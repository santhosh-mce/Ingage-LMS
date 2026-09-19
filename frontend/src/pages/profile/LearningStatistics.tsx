import React from 'react';
import { BookOpen, CheckCircle2, Clock, TrendingUp } from 'lucide-react';
import { UserEnrollmentRecord } from '../../api/paymentApi';

interface LearningStatisticsProps {
  enrollments: UserEnrollmentRecord[];
  loading?: boolean;
}

export const LearningStatistics: React.FC<LearningStatisticsProps> = ({ enrollments, loading }) => {
  if (loading) {
    return (
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        {[1, 2, 3].map((i) => (
          <div key={i} className="bg-white rounded-2xl border border-gray-100 p-5 shadow-xs animate-pulse">
            <div className="h-4 bg-gray-200 rounded w-1/2 mb-3"></div>
            <div className="h-8 bg-gray-200 rounded w-1/3"></div>
          </div>
        ))}
      </div>
    );
  }

  const enrolledCount = enrollments.length;
  const completedCount = enrollments.filter(
    (e) => e.status === 'COMPLETED' || (e.progressPercentage && e.progressPercentage >= 100)
  ).length;
  const inProgressCount = enrollments.filter(
    (e) => e.status === 'ACTIVE' && (!e.progressPercentage || e.progressPercentage < 100)
  ).length;

  return (
    <div>
      <div className="flex items-center justify-between mb-4">
        <h2 className="text-lg font-bold text-gray-900 tracking-tight flex items-center gap-2">
          <TrendingUp className="w-5 h-5 text-[#8DB600]" />
          <span>Learning Statistics</span>
        </h2>
        <span className="text-xs text-gray-500 font-medium">Real-time enrollment tracking</span>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        {/* Card 1: Courses Enrolled */}
        <div className="bg-white rounded-2xl border border-gray-100 p-5 shadow-xs hover:border-lime-200 transition-all">
          <div className="flex items-center justify-between">
            <span className="text-xs font-bold uppercase tracking-wider text-gray-500">
              Courses Enrolled
            </span>
            <div className="w-8 h-8 rounded-xl bg-lime-50 text-lime-700 flex items-center justify-center">
              <BookOpen className="w-4 h-4" />
            </div>
          </div>
          <div className="mt-2 text-2xl sm:text-3xl font-extrabold text-gray-900 tracking-tight">
            {enrolledCount}
          </div>
          <p className="text-[11px] text-gray-400 mt-1">Total courses in your curriculum</p>
        </div>

        {/* Card 2: In Progress */}
        <div className="bg-white rounded-2xl border border-gray-100 p-5 shadow-xs hover:border-amber-200 transition-all">
          <div className="flex items-center justify-between">
            <span className="text-xs font-bold uppercase tracking-wider text-gray-500">
              In Progress
            </span>
            <div className="w-8 h-8 rounded-xl bg-amber-50 text-amber-600 flex items-center justify-center">
              <Clock className="w-4 h-4" />
            </div>
          </div>
          <div className="mt-2 text-2xl sm:text-3xl font-extrabold text-gray-900 tracking-tight">
            {inProgressCount}
          </div>
          <p className="text-[11px] text-gray-400 mt-1">Active courses currently learning</p>
        </div>

        {/* Card 3: Completed */}
        <div className="bg-white rounded-2xl border border-gray-100 p-5 shadow-xs hover:border-emerald-200 transition-all">
          <div className="flex items-center justify-between">
            <span className="text-xs font-bold uppercase tracking-wider text-gray-500">
              Courses Completed
            </span>
            <div className="w-8 h-8 rounded-xl bg-emerald-50 text-emerald-600 flex items-center justify-center">
              <CheckCircle2 className="w-4 h-4" />
            </div>
          </div>
          <div className="mt-2 text-2xl sm:text-3xl font-extrabold text-gray-900 tracking-tight">
            {completedCount}
          </div>
          <p className="text-[11px] text-gray-400 mt-1">Finished courses with 100% progress</p>
        </div>
      </div>
    </div>
  );
};
