package com.lms.Backend.admin.service;

import com.lms.Backend.certificate.entity.CertificateStatus;
import com.lms.Backend.certificate.repository.CertificateRepository;
import com.lms.Backend.course.entity.Course;
import com.lms.Backend.course.entity.CourseStatus;
import com.lms.Backend.course.repository.CourseRepository;
import com.lms.Backend.discount.repository.DiscountRepository;
import com.lms.Backend.learning.entity.Enrollment;
import com.lms.Backend.learning.entity.EnrollmentStatus;
import com.lms.Backend.learning.repository.EnrollmentRepository;
import com.lms.Backend.payment.entity.Payment;
import com.lms.Backend.payment.entity.PaymentStatus;
import com.lms.Backend.payment.repository.PaymentRepository;
import com.lms.Backend.user.entity.User;
import com.lms.Backend.user.entity.UserRole;
import com.lms.Backend.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminDashboardService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CertificateRepository certificateRepository;
    private final PaymentRepository paymentRepository;
    private final DiscountRepository discountRepository;

    public AdminDashboardService(
        UserRepository userRepository,
        CourseRepository courseRepository,
        EnrollmentRepository enrollmentRepository,
        CertificateRepository certificateRepository,
        PaymentRepository paymentRepository,
        DiscountRepository discountRepository
    ) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.certificateRepository = certificateRepository;
        this.paymentRepository = paymentRepository;
        this.discountRepository = discountRepository;
    }

    public Map<String, Object> getOverviewMetrics() {
        long totalUsers = userRepository.count();
        long totalAdmins = userRepository.countByRole(UserRole.ADMIN);
        long totalCourses = courseRepository.count();
        long publishedCourses = courseRepository.countByStatus(CourseStatus.PUBLISHED);
        long draftCourses = courseRepository.countByStatus(CourseStatus.DRAFT);
        long totalEnrollments = enrollmentRepository.count();
        long activeLearners = enrollmentRepository.countDistinctActiveLearners();
        long completedCourses = enrollmentRepository.countByStatus(EnrollmentStatus.COMPLETED);
        long certificatesIssued = certificateRepository.countByStatus(CertificateStatus.ACTIVE);
        Double totalRev = paymentRepository.getTotalRevenue();
        double totalRevenue = totalRev != null ? totalRev : 0.0;

        Instant startOfMonth = YearMonth.now().atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        Double monthRev = paymentRepository.getRevenueSince(startOfMonth);
        double thisMonthRevenue = monthRev != null ? monthRev : 0.0;

        long totalPayments = paymentRepository.countByPaymentStatus(PaymentStatus.PAID);
        long activeDiscounts = discountRepository.countByActiveTrue();

        Map<String, Object> metrics = new LinkedHashMap<>();
        metrics.put("totalUsers", totalUsers);
        metrics.put("totalAdmins", totalAdmins);
        metrics.put("totalCourses", totalCourses);
        metrics.put("publishedCourses", publishedCourses);
        metrics.put("draftCourses", draftCourses);
        metrics.put("totalEnrollments", totalEnrollments);
        metrics.put("activeLearners", activeLearners);
        metrics.put("completedCourses", completedCourses);
        metrics.put("certificatesIssued", certificatesIssued);
        metrics.put("totalRevenue", totalRevenue);
        metrics.put("thisMonthRevenue", thisMonthRevenue);
        metrics.put("totalPayments", totalPayments);
        metrics.put("activeDiscounts", activeDiscounts);
        return metrics;
    }

    public Map<String, Object> getAnalytics() {
        Map<String, Object> analytics = new HashMap<>();

        // 1. User Growth (Daily, Weekly, Monthly, Yearly)
        List<User> allUsers = userRepository.findAllByOrderByCreatedAtDesc();
        analytics.put("userGrowth", calculateUserGrowth(allUsers));

        // 2. Course Enrollment breakdown
        List<Course> courses = courseRepository.findAllByOrderByIdDesc();
        List<Map<String, Object>> courseEnrollments = new ArrayList<>();
        for (Course c : courses) {
            long total = enrollmentRepository.countByCourseId(c.getId());
            long completed = enrollmentRepository.countByCourseIdAndStatus(c.getId(), EnrollmentStatus.COMPLETED);
            long active = enrollmentRepository.countByCourseIdAndStatus(c.getId(), EnrollmentStatus.ACTIVE);
            Double avgProgress = enrollmentRepository.getAverageProgressByCourseId(c.getId());
            Double rev = paymentRepository.getRevenueByCourseId(c.getId());

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("courseId", c.getId());
            item.put("courseName", c.getTitle());
            item.put("totalEnrolled", total);
            item.put("completedUsers", completed);
            item.put("activeUsers", active);
            item.put("averageProgress", avgProgress != null ? Math.round(avgProgress * 10.0) / 10.0 : 0.0);
            item.put("revenue", rev != null ? rev : 0.0);
            courseEnrollments.add(item);
        }
        analytics.put("courseEnrollments", courseEnrollments);

        // 3. Revenue Breakdown
        analytics.put("revenueAnalytics", calculateRevenueAnalytics(courses));

        // 4. Course Completion Breakdown
        long totalEnrolled = enrollmentRepository.count();
        long completed = enrollmentRepository.countByStatus(EnrollmentStatus.COMPLETED);
        long inProgress = enrollmentRepository.countByStatus(EnrollmentStatus.ACTIVE);
        long cancelled = enrollmentRepository.countByStatus(EnrollmentStatus.CANCELLED);

        Map<String, Object> completionData = new LinkedHashMap<>();
        completionData.put("enrolled", totalEnrolled);
        completionData.put("inProgress", inProgress);
        completionData.put("completed", completed);
        completionData.put("cancelled", cancelled);
        analytics.put("courseCompletion", completionData);

        return analytics;
    }

    private Map<String, Object> calculateUserGrowth(List<User> users) {
        LocalDate today = LocalDate.now();
        ZoneId zone = ZoneId.systemDefault();

        // Daily: past 7 days
        Map<String, Long> daily = new LinkedHashMap<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate d = today.minusDays(i);
            daily.put(d.format(DateTimeFormatter.ofPattern("EEE (MM/dd)")), 0L);
        }

        // Weekly: past 4 weeks
        Map<String, Long> weekly = new LinkedHashMap<>();
        for (int i = 3; i >= 0; i--) {
            LocalDate start = today.minusWeeks(i);
            weekly.put("Week " + (4 - i) + " (" + start.format(DateTimeFormatter.ofPattern("MM/dd")) + ")", 0L);
        }

        // Monthly: past 6 months
        Map<String, Long> monthly = new LinkedHashMap<>();
        for (int i = 5; i >= 0; i--) {
            YearMonth ym = YearMonth.now().minusMonths(i);
            monthly.put(ym.format(DateTimeFormatter.ofPattern("MMM yyyy")), 0L);
        }

        // Yearly: past 3 years
        Map<String, Long> yearly = new LinkedHashMap<>();
        for (int i = 2; i >= 0; i--) {
            int yr = today.getYear() - i;
            yearly.put(String.valueOf(yr), 0L);
        }

        for (User u : users) {
            if (u.getCreatedAt() == null) continue;
            LocalDate createdDate = u.getCreatedAt().atZone(zone).toLocalDate();
            YearMonth ym = YearMonth.from(createdDate);

            // Match daily
            long daysAgo = ChronoUnit.DAYS.between(createdDate, today);
            if (daysAgo >= 0 && daysAgo <= 6) {
                String key = createdDate.format(DateTimeFormatter.ofPattern("EEE (MM/dd)"));
                if (daily.containsKey(key)) {
                    daily.put(key, daily.get(key) + 1);
                }
            }

            // Match weekly
            long weeksAgo = ChronoUnit.WEEKS.between(createdDate, today);
            if (weeksAgo >= 0 && weeksAgo <= 3) {
                int idx = 3 - (int) weeksAgo;
                LocalDate start = today.minusWeeks((int) weeksAgo);
                String key = "Week " + (idx + 1) + " (" + start.format(DateTimeFormatter.ofPattern("MM/dd")) + ")";
                if (weekly.containsKey(key)) {
                    weekly.put(key, weekly.get(key) + 1);
                }
            }

            // Match monthly
            String mKey = ym.format(DateTimeFormatter.ofPattern("MMM yyyy"));
            if (monthly.containsKey(mKey)) {
                monthly.put(mKey, monthly.get(mKey) + 1);
            }

            // Match yearly
            String yKey = String.valueOf(createdDate.getYear());
            if (yearly.containsKey(yKey)) {
                yearly.put(yKey, yearly.get(yKey) + 1);
            }
        }

        Map<String, Object> res = new HashMap<>();
        res.put("daily", convertMapToList(daily));
        res.put("weekly", convertMapToList(weekly));
        res.put("monthly", convertMapToList(monthly));
        res.put("yearly", convertMapToList(yearly));
        return res;
    }

    private Map<String, Object> calculateRevenueAnalytics(List<Course> courses) {
        Double totalRev = paymentRepository.getTotalRevenue();
        double totalRevenue = totalRev != null ? totalRev : 0.0;

        Instant startOfToday = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant();
        Double dailyRev = paymentRepository.getRevenueSince(startOfToday);
        double todayRevenue = dailyRev != null ? dailyRev : 0.0;

        Instant startOfMonth = YearMonth.now().atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        Double monthRev = paymentRepository.getRevenueSince(startOfMonth);
        double monthlyRevenue = monthRev != null ? monthRev : 0.0;

        List<Map<String, Object>> courseWise = new ArrayList<>();
        for (Course c : courses) {
            Double rev = paymentRepository.getRevenueByCourseId(c.getId());
            if (rev != null && rev > 0) {
                Map<String, Object> item = new HashMap<>();
                item.put("courseName", c.getTitle());
                item.put("revenue", rev);
                courseWise.add(item);
            }
        }

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("totalRevenue", totalRevenue);
        res.put("monthlyRevenue", monthlyRevenue);
        res.put("todayRevenue", todayRevenue);
        res.put("courseWiseRevenue", courseWise);
        return res;
    }

    private List<Map<String, Object>> convertMapToList(Map<String, Long> map) {
        List<Map<String, Object>> list = new ArrayList<>();
        map.forEach((k, v) -> {
            Map<String, Object> entry = new HashMap<>();
            entry.put("label", k);
            entry.put("count", v);
            list.add(entry);
        });
        return list;
    }

    public List<Map<String, Object>> getLatestCourses() {
        List<Course> courses = courseRepository.findTop5ByOrderByIdDesc();
        List<Map<String, Object>> result = new ArrayList<>();
        int index = 1;
        for (Course c : courses) {
            long enrolled = enrollmentRepository.countByCourseId(c.getId());
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("index", index++);
            item.put("courseId", c.getId());
            item.put("title", c.getTitle());
            item.put("category", c.getCategory() != null ? c.getCategory() : "—");
            item.put("instructor", c.getInstructor() != null ? c.getInstructor() : "—");
            item.put("price", c.getPrice());
            item.put("status", c.getStatus() != null ? c.getStatus().name() : "DRAFT");
            item.put("published", c.isPublished());
            item.put("enrolled", enrolled);
            result.add(item);
        }
        return result;
    }

    public List<Map<String, Object>> getRecentActivity() {
        // Combine recent enrollments and recent user registrations into one feed
        List<Map<String, Object>> activities = new ArrayList<>();

        // Recent enrollments (up to 5)
        List<Enrollment> recentEnrollments = enrollmentRepository.findTop10ByOrderByEnrolledAtDesc();
        int count = 0;
        for (Enrollment e : recentEnrollments) {
            if (count >= 5) break;
            String userName = e.getUser() != null ? e.getUser().getName() : "A learner";
            String courseName = e.getCourse() != null ? e.getCourse().getTitle() : "a course";
            Map<String, Object> activity = new LinkedHashMap<>();
            activity.put("type", "enrollment");
            activity.put("icon", "graduation");
            activity.put("color", "green");
            activity.put("title", "New enrollment");
            activity.put("description", userName + " – " + courseName);
            activity.put("timestamp", e.getEnrolledAt() != null ? e.getEnrolledAt().toString() : null);
            activities.add(activity);
            count++;
        }

        // Recent user registrations (up to 3)
        List<User> recentUsers = userRepository.findTop10ByOrderByCreatedAtDesc();
        int userCount = 0;
        for (User u : recentUsers) {
            if (userCount >= 3) break;
            Map<String, Object> activity = new LinkedHashMap<>();
            activity.put("type", "registration");
            activity.put("icon", "user");
            activity.put("color", "blue");
            activity.put("title", "New user registered");
            activity.put("description", u.getName());
            activity.put("timestamp", u.getCreatedAt() != null ? u.getCreatedAt().toString() : null);
            activities.add(activity);
            userCount++;
        }

        // Sort by timestamp descending
        activities.sort((a, b) -> {
            String ta = (String) a.get("timestamp");
            String tb = (String) b.get("timestamp");
            if (ta == null && tb == null) return 0;
            if (ta == null) return 1;
            if (tb == null) return -1;
            return tb.compareTo(ta);
        });

        return activities.size() > 6 ? activities.subList(0, 6) : activities;
    }
}
