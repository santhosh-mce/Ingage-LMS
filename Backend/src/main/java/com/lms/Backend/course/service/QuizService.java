package com.lms.Backend.course.service;

import com.lms.Backend.admin.service.AdminProgressService;
import com.lms.Backend.course.dto.QuizQuestionDto;
import com.lms.Backend.course.dto.QuizSubmitRequest;
import com.lms.Backend.course.dto.QuizSubmitResponse;
import com.lms.Backend.course.entity.CourseLesson;
import com.lms.Backend.course.entity.LessonType;
import com.lms.Backend.course.entity.QuizQuestion;
import com.lms.Backend.course.repository.CourseLessonRepository;
import com.lms.Backend.course.repository.QuizQuestionRepository;
import com.lms.Backend.learning.repository.EnrollmentRepository;
import com.lms.Backend.user.entity.User;
import com.lms.Backend.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class QuizService {

    private final QuizQuestionRepository quizQuestionRepository;
    private final CourseLessonRepository courseLessonRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AdminProgressService adminProgressService;

    // Track user answered questions in memory: "userId_lessonId" -> Set of answered question IDs
    private final Map<String, java.util.Set<Long>> userCorrectQuestions = new ConcurrentHashMap<>();

    public QuizService(
        QuizQuestionRepository quizQuestionRepository,
        CourseLessonRepository courseLessonRepository,
        UserRepository userRepository,
        EnrollmentRepository enrollmentRepository,
        AdminProgressService adminProgressService
    ) {
        this.quizQuestionRepository = quizQuestionRepository;
        this.courseLessonRepository = courseLessonRepository;
        this.userRepository = userRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.adminProgressService = adminProgressService;
    }

    @Transactional
    public List<QuizQuestionDto> getQuizQuestionsForLesson(Long courseId, Long lessonId, Principal principal) {
        CourseLesson lesson = courseLessonRepository.findById(lessonId)
            .orElseThrow(() -> new IllegalArgumentException("Lesson not found"));

        if (!lesson.getSection().getCourse().getId().equals(courseId)) {
            throw new IllegalArgumentException("Lesson does not belong to the specified course");
        }

        List<QuizQuestion> questions = quizQuestionRepository.findByLessonIdOrderByDisplayOrderAscIdAsc(lessonId);

        // If no questions seeded yet for this quiz lesson, seed standard high-quality questions
        if (questions.isEmpty()) {
            questions = seedDefaultQuestions(lesson);
        }

        // Return client-safe DTO without exposing correct answers
        return questions.stream()
            .map(q -> new QuizQuestionDto(
                q.getId(),
                lessonId,
                q.getQuestionText(),
                q.getOptions(),
                q.getDisplayOrder()
            ))
            .collect(Collectors.toList());
    }

    @Transactional
    public QuizSubmitResponse submitQuizAnswer(
        Long courseId,
        Long lessonId,
        QuizSubmitRequest request,
        Principal principal
    ) {
        if (principal == null) {
            throw new IllegalArgumentException("Authentication required to submit quiz answers");
        }

        User user = userRepository.findByEmailIgnoreCase(principal.getName())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        CourseLesson lesson = courseLessonRepository.findById(lessonId)
            .orElseThrow(() -> new IllegalArgumentException("Lesson not found"));

        if (!lesson.getSection().getCourse().getId().equals(courseId)) {
            throw new IllegalArgumentException("Lesson does not belong to the specified course");
        }

        QuizQuestion question = quizQuestionRepository.findById(request.getQuestionId())
            .orElseThrow(() -> new IllegalArgumentException("Quiz question not found"));

        if (!question.getLesson().getId().equals(lessonId)) {
            throw new IllegalArgumentException("Question does not belong to this quiz lesson");
        }

        List<QuizQuestion> allQuestions = quizQuestionRepository.findByLessonIdOrderByDisplayOrderAscIdAsc(lessonId);
        int totalQuestions = allQuestions.size();

        boolean isCorrect = request.getSelectedOptionIndex() != null
            && request.getSelectedOptionIndex().equals(question.getCorrectOptionIndex());

        String userKey = user.getId().toString() + "_" + lessonId;
        userCorrectQuestions.putIfAbsent(userKey, java.util.concurrent.ConcurrentHashMap.newKeySet());
        java.util.Set<Long> userAnswers = userCorrectQuestions.get(userKey);

        if (isCorrect) {
            userAnswers.add(question.getId());
        }

        int answeredCount = userAnswers.size();
        boolean quizCompleted = totalQuestions > 0 && answeredCount >= totalQuestions;

        // If all questions in this quiz lesson answered correctly, complete lesson in progress system
        if (quizCompleted) {
            try {
                adminProgressService.markLessonComplete(user.getId(), lessonId, null);
            } catch (Exception ignored) {}
        }

        return new QuizSubmitResponse(
            isCorrect,
            question.getExplanation(),
            question.getCorrectOptionIndex(),
            quizCompleted,
            totalQuestions,
            answeredCount,
            isCorrect ? "Correct answer!" : "Incorrect answer. Please review and try again."
        );
    }

    private List<QuizQuestion> seedDefaultQuestions(CourseLesson lesson) {
        List<QuizQuestion> list = new ArrayList<>();

        list.add(new QuizQuestion(
            lesson,
            "Which technology is primarily used for building user interfaces in this course?",
            Arrays.asList("React", "PostgreSQL", "Docker", "Linux"),
            0,
            "Correct! React is used to build modular, component-driven user interfaces.",
            0
        ));

        list.add(new QuizQuestion(
            lesson,
            "What architectural solution is used for predictable global application state management?",
            Arrays.asList("Local variables", "Redux Toolkit", "Browser cookies only", "URL hashes"),
            1,
            "Correct! Redux Toolkit provides centralized and predictable state management.",
            1
        ));

        list.add(new QuizQuestion(
            lesson,
            "Which HTTP status code signifies partial content streaming for video chunks?",
            Arrays.asList("200 OK", "301 Moved", "206 Partial Content", "404 Not Found"),
            2,
            "Correct! HTTP 206 Partial Content enables byte-range protected video streaming.",
            2
        ));

        return quizQuestionRepository.saveAll(list);
    }
}
