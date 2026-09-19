import React, { useState, useEffect } from 'react';
import { getDashboardOverview, getDashboardAnalytics } from '../../api/adminApi';
import {
  Users,
  ShieldCheck,
  BookOpen,
  CheckCircle2,
  FileText,
  GraduationCap,
  Activity,
  Award,
  IndianRupee,
  Calendar,
  CreditCard,
  TicketPercent,
  TrendingUp,
  RefreshCw,
  PieChart,
  BarChart3,
  Layers,
} from 'lucide-react';

export const AdminDashboardPage: React.FC<{ onNavigate: (path: string) => void }> = ({ onNavigate }) => {
  const [loading, setLoading] = useState(true);
  const [overview, setOverview] = useState<any>(null);
  const [analytics, setAnalytics] = useState<any>(null);
  const [growthTimeframe, setGrowthTimeframe] = useState<'daily' | 'weekly' | 'monthly' | 'yearly'>('daily');

  const fetchData = async () => {
    setLoading(true);
    try {
      const [over, anal] = await Promise.all([
        getDashboardOverview(),
        getDashboardAnalytics(),
      ]);
      setOverview(over);
      setAnalytics(anal);
    } catch (err) {
      console.error('Failed to load dashboard metrics', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  if (loading && !overview) {
    return (
      <div className="flex flex-col items-center justify-center min-h-[60vh] space-y-4">
        <RefreshCw className="w-8 h-8 text-lime-400 animate-spin" />
        <p className="text-sm text-slate-400">Loading database statistics...</p>
      </div>
    );
  }

  const statCards = [
    { label: 'Total Users', value: overview?.totalUsers ?? 0, icon: Users, color: 'text-sky-400', bg: 'bg-sky-500/10', border: 'border-sky-500/20', path: '/admin/users' },
    { label: 'Total Admins', value: overview?.totalAdmins ?? 0, icon: ShieldCheck, color: 'text-purple-400', bg: 'bg-purple-500/10', border: 'border-purple-500/20', path: '/admin/users' },
    { label: 'Total Courses', value: overview?.totalCourses ?? 0, icon: BookOpen, color: 'text-indigo-400', bg: 'bg-indigo-500/10', border: 'border-indigo-500/20', path: '/admin/courses' },
    { label: 'Published Courses', value: overview?.publishedCourses ?? 0, icon: CheckCircle2, color: 'text-emerald-400', bg: 'bg-emerald-500/10', border: 'border-emerald-500/20', path: '/admin/courses' },
    { label: 'Draft Courses', value: overview?.draftCourses ?? 0, icon: FileText, color: 'text-amber-400', bg: 'bg-amber-500/10', border: 'border-amber-500/20', path: '/admin/courses' },
    { label: 'Total Enrollments', value: overview?.totalEnrollments ?? 0, icon: GraduationCap, color: 'text-lime-400', bg: 'bg-lime-500/10', border: 'border-lime-500/20', path: '/admin/progress' },
    { label: 'Active Learners', value: overview?.activeLearners ?? 0, icon: Activity, color: 'text-cyan-400', bg: 'bg-cyan-500/10', border: 'border-cyan-500/20', path: '/admin/users' },
    { label: 'Completed Courses', value: overview?.completedCourses ?? 0, icon: CheckCircle2, color: 'text-teal-400', bg: 'bg-teal-500/10', border: 'border-teal-500/20', path: '/admin/progress' },
    { label: 'Certificates Issued', value: overview?.certificatesIssued ?? 0, icon: Award, color: 'text-amber-300', bg: 'bg-amber-500/10', border: 'border-amber-500/20', path: '/admin/certificates' },
    { label: 'Total Revenue', value: `₹${Number(overview?.totalRevenue || 0).toLocaleString()}`, icon: IndianRupee, color: 'text-lime-400', bg: 'bg-lime-500/10', border: 'border-lime-500/20', path: '/admin/payments' },
    { label: 'This Month Revenue', value: `₹${Number(overview?.thisMonthRevenue || 0).toLocaleString()}`, icon: Calendar, color: 'text-emerald-400', bg: 'bg-emerald-500/10', border: 'border-emerald-500/20', path: '/admin/payments' },
    { label: 'Total Payments', value: overview?.totalPayments ?? 0, icon: CreditCard, color: 'text-blue-400', bg: 'bg-blue-500/10', border: 'border-blue-500/20', path: '/admin/payments' },
    { label: 'Active Discounts', value: overview?.activeDiscounts ?? 0, icon: TicketPercent, color: 'text-rose-400', bg: 'bg-rose-500/10', border: 'border-rose-500/20', path: '/admin/discounts' },
  ];

  const userGrowthData: Array<{ label: string; count: number }> = analytics?.userGrowth?.[growthTimeframe] || [];
  const maxGrowth = Math.max(...userGrowthData.map(d => d.count), 5);

  const courseCompletion = analytics?.courseCompletion || { enrolled: 0, inProgress: 0, completed: 0, cancelled: 0 };
  const totalCompletedSum = (courseCompletion.completed || 0) + (courseCompletion.inProgress || 0);
  const completionPercentage = totalCompletedSum > 0 ? Math.round(((courseCompletion.completed || 0) / totalCompletedSum) * 100) : 0;

  return (
    <div className="space-y-8 animate-fade-in">
      {/* Overview Top Bar */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-white tracking-tight flex items-center gap-2">
            Operations & Performance Dashboard
          </h1>
          <p className="text-xs text-slate-400">
            Real-time PostgreSQL metrics and learner engagement analytics.
          </p>
        </div>

        <button
          onClick={fetchData}
          disabled={loading}
          className="flex items-center gap-2 px-3.5 py-2 bg-slate-800 hover:bg-slate-700 text-slate-200 rounded-xl text-xs font-semibold transition-all border border-slate-700 cursor-pointer self-start sm:self-auto"
        >
          <RefreshCw className={`w-3.5 h-3.5 ${loading ? 'animate-spin text-lime-400' : ''}`} />
          <span>Sync Data</span>
        </button>
      </div>

      {/* 13 Database Metric Cards */}
      <div className="grid grid-cols-1 min-[420px]:grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-3.5 sm:gap-4">
        {statCards.map((card, idx) => {
          const Icon = card.icon;
          return (
            <div
              key={idx}
              onClick={() => onNavigate(card.path)}
              className={`p-4 rounded-2xl bg-slate-900/90 border ${card.border} hover:border-slate-600 transition-all hover:translate-y-[-2px] cursor-pointer shadow-lg group relative overflow-hidden`}
            >
              <div className="flex items-center justify-between mb-2">
                <span className="text-xs font-medium text-slate-400 truncate pr-1">
                  {card.label}
                </span>
                <div className={`w-7 h-7 rounded-lg ${card.bg} ${card.color} flex items-center justify-center shrink-0`}>
                  <Icon className="w-4 h-4" />
                </div>
              </div>
              <div className="text-xl sm:text-2xl font-bold text-white tracking-tight">
                {card.value}
              </div>
            </div>
          );
        })}
      </div>

      {/* Analytics Charts Section */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* User Growth Chart (2 cols) */}
        <div className="lg:col-span-2 bg-slate-900 border border-slate-800 rounded-3xl p-4 sm:p-6 shadow-xl space-y-6">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3">
            <div>
              <h2 className="text-base font-bold text-white flex items-center gap-2">
                <TrendingUp className="w-4 h-4 text-lime-400" />
                User Registration Growth
              </h2>
              <p className="text-xs text-slate-400">Chronological student acquisition</p>
            </div>

            {/* Timeframe Switcher */}
            <div className="flex items-center bg-slate-950 p-1 rounded-xl border border-slate-800 text-xs overflow-x-auto max-w-full">
              {(['daily', 'weekly', 'monthly', 'yearly'] as const).map((t) => (
                <button
                  key={t}
                  onClick={() => setGrowthTimeframe(t)}
                  className={`px-2.5 sm:px-3 py-1 rounded-lg font-semibold uppercase text-[10px] tracking-wider transition-all cursor-pointer whitespace-nowrap ${
                    growthTimeframe === t
                      ? 'bg-lime-500 text-slate-950 shadow-sm'
                      : 'text-slate-400 hover:text-white'
                  }`}
                >
                  {t}
                </button>
              ))}
            </div>
          </div>

          {/* SVG Bar / Curve Chart */}
          <div className="h-64 w-full flex items-end gap-2 pt-6 pb-2 px-2">
            {userGrowthData.length === 0 ? (
              <div className="w-full h-full flex items-center justify-center text-xs text-slate-400">
                No user growth records in selected window.
              </div>
            ) : (
              userGrowthData.map((d, i) => {
                const heightPercent = maxGrowth > 0 ? (d.count / maxGrowth) * 100 : 0;
                return (
                  <div key={i} className="flex-1 flex flex-col items-center h-full justify-end group relative">
                    {/* Hover tooltip */}
                    <div className="hidden group-hover:block absolute -top-8 px-2 py-1 bg-slate-800 text-lime-400 text-[11px] font-bold rounded-md shadow-md border border-slate-700 whitespace-nowrap z-10 pointer-events-none">
                      {d.count} user{d.count !== 1 ? 's' : ''}
                    </div>

                    <div
                      style={{ height: `${Math.max(6, heightPercent)}%` }}
                      className="w-full max-w-[36px] bg-gradient-to-t from-lime-600 to-lime-400 rounded-t-lg transition-all group-hover:brightness-110 shadow-sm shadow-lime-500/20"
                    ></div>
                    <span className="text-[10px] text-slate-400 truncate w-full text-center mt-2.5">
                      {d.label}
                    </span>
                  </div>
                );
              })
            )}
          </div>
        </div>

        {/* Course Completion Distribution (1 col) */}
        <div className="bg-slate-900 border border-slate-800 rounded-3xl p-6 shadow-xl space-y-6 flex flex-col justify-between">
          <div>
            <h2 className="text-base font-bold text-white flex items-center gap-2">
              <PieChart className="w-4 h-4 text-emerald-400" />
              Completion Funnel
            </h2>
            <p className="text-xs text-slate-400">Enrolled vs In-Progress vs Completed</p>
          </div>

          <div className="flex flex-col items-center justify-center py-6 space-y-4">
            <div className="relative w-36 h-36 flex items-center justify-center">
              <svg className="w-full h-full transform -rotate-90" viewBox="0 0 36 36">
                <path
                  className="text-slate-800"
                  strokeWidth="3.5"
                  stroke="currentColor"
                  fill="none"
                  d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831"
                />
                <path
                  className="text-lime-400 transition-all duration-1000 ease-out"
                  strokeDasharray={`${completionPercentage}, 100`}
                  strokeWidth="3.5"
                  strokeLinecap="round"
                  stroke="currentColor"
                  fill="none"
                  d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831"
                />
              </svg>
              <div className="absolute flex flex-col items-center">
                <span className="text-2xl font-bold text-white">{completionPercentage}%</span>
                <span className="text-[10px] font-semibold text-slate-400 uppercase">Completed</span>
              </div>
            </div>

            <div className="w-full space-y-2 pt-2 text-xs">
              <div className="flex items-center justify-between p-2 rounded-xl bg-slate-950/60 border border-slate-800">
                <span className="flex items-center gap-2 text-slate-300">
                  <span className="w-2.5 h-2.5 rounded-full bg-lime-400"></span>
                  Completed
                </span>
                <span className="font-bold text-white">{courseCompletion.completed}</span>
              </div>
              <div className="flex items-center justify-between p-2 rounded-xl bg-slate-950/60 border border-slate-800">
                <span className="flex items-center gap-2 text-slate-300">
                  <span className="w-2.5 h-2.5 rounded-full bg-cyan-400"></span>
                  In Progress
                </span>
                <span className="font-bold text-white">{courseCompletion.inProgress}</span>
              </div>
              <div className="flex items-center justify-between p-2 rounded-xl bg-slate-950/60 border border-slate-800">
                <span className="flex items-center gap-2 text-slate-300">
                  <span className="w-2.5 h-2.5 rounded-full bg-slate-600"></span>
                  Total Enrollments
                </span>
                <span className="font-bold text-white">{courseCompletion.enrolled}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Course Enrollment Breakdown Table */}
      <div className="bg-slate-900 border border-slate-800 rounded-3xl p-6 shadow-xl space-y-4">
        <div className="flex items-center justify-between">
          <div>
            <h2 className="text-base font-bold text-white flex items-center gap-2">
              <BarChart3 className="w-4 h-4 text-purple-400" />
              Course-Wise Learner Metrics
            </h2>
            <p className="text-xs text-slate-400">Enrolled students, average progress, and generated revenue</p>
          </div>
          <button
            onClick={() => onNavigate('/admin/courses')}
            className="text-xs font-semibold text-lime-400 hover:text-lime-300 transition-colors cursor-pointer"
          >
            Manage Courses →
          </button>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs text-slate-300">
            <thead className="bg-slate-950/80 uppercase text-[10px] text-slate-400 tracking-wider border-b border-slate-800">
              <tr>
                <th className="px-4 py-3">Course Title</th>
                <th className="px-4 py-3">Total Enrolled</th>
                <th className="px-4 py-3">Active Learners</th>
                <th className="px-4 py-3">Completed</th>
                <th className="px-4 py-3">Avg Progress</th>
                <th className="px-4 py-3">Revenue</th>
                <th className="px-4 py-3 text-right">Action</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-800/60">
              {analytics?.courseEnrollments?.length === 0 ? (
                <tr>
                  <td colSpan={7} className="px-4 py-8 text-center text-slate-400">
                    No courses available in database.
                  </td>
                </tr>
              ) : (
                analytics?.courseEnrollments?.map((c: any) => (
                  <tr key={c.courseId} className="hover:bg-slate-800/40 transition-colors">
                    <td className="px-4 py-3.5 font-semibold text-white max-w-xs truncate">
                      {c.courseName}
                    </td>
                    <td className="px-4 py-3.5 font-bold text-slate-200">
                      {c.totalEnrolled}
                    </td>
                    <td className="px-4 py-3.5 text-cyan-400 font-medium">
                      {c.activeUsers}
                    </td>
                    <td className="px-4 py-3.5 text-emerald-400 font-medium">
                      {c.completedUsers}
                    </td>
                    <td className="px-4 py-3.5">
                      <div className="flex items-center gap-2">
                        <div className="w-20 bg-slate-800 rounded-full h-1.5 overflow-hidden">
                          <div
                            className="bg-lime-400 h-full rounded-full"
                            style={{ width: `${Math.min(100, c.averageProgress || 0)}%` }}
                          ></div>
                        </div>
                        <span className="text-[11px] text-slate-400">{c.averageProgress}%</span>
                      </div>
                    </td>
                    <td className="px-4 py-3.5 font-bold text-lime-400">
                      ₹{Number(c.revenue || 0).toLocaleString()}
                    </td>
                    <td className="px-4 py-3.5 text-right">
                      <button
                        onClick={() => onNavigate(`/admin/courses/${c.courseId}/analytics`)}
                        className="px-2.5 py-1 rounded-lg bg-slate-800 hover:bg-slate-700 text-slate-200 text-[11px] font-medium transition-colors cursor-pointer"
                      >
                        Analytics
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};
