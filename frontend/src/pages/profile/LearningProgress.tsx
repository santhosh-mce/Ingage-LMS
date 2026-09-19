import React from 'react';
import { PlayCircle, ArrowRight, BookOpen, Clock, Sparkles } from 'lucide-react';
import { UserEnrollmentRecord } from '../../api/paymentApi';

interface LearningProgressProps {
  enrollments: UserEnrollmentRecord[];
  onNavigate: (path: string) => void;
  loading?: boolean;
}

export const LearningProgress: React.FC<LearningProgressProps> = ({
  enrollments,
  onNavigate,
  loading,
}) => {
  if (loading) {
    return (
      <div className="bg-white rounded-2xl border border-gray-100 p-6 sm:p-8 shadow-xs space-y-4">
        <div className="h-5 bg-gray-200 rounded w-1/4 mb-4"></div>
        {[1, 2].map((i) => (
          <div key={i} className="p-4 rounded-xl border border-gray-100 animate-pulse space-y-2">
            <div className="h-4 bg-gray-200 rounded w-1/3"></div>
            <div className="h-2 bg-gray-200 rounded w-full"></div>
          </div>
        ))}
      </div>
    );
  }

  // Active or in-progress courses
  const inProgressCourses = enrollments.filter(
    (e) => e.status === 'ACTIVE' || (e.progressPercentage !== undefined && e.progressPercentage < 100)
  );

  return (
    <div className="bg-white rounded-2xl border border-gray-100 p-6 sm:p-8 shadow-xs">
      <div className="flex items-center justify-between mb-6 pb-4 border-b border-gray-100">
        <div>
          <h2 className="text-lg font-bold text-gray-900 tracking-tight flex items-center gap-2">
            <PlayCircle className="w-5 h-5 text-[#8DB600]" />
            <span>Learning Progress</span>
          </h2>
          <p className="text-xs sm:text-sm text-gray-500 mt-0.5">
            Pick up right where you left off
          </p>
        </div>
        {inProgressCourses.length > 0 && (
          <span className="text-xs bg-lime-100 text-lime-800 font-bold px-2.5 py-1 rounded-full">
            {inProgressCourses.length} in progress
          </span>
        )}
      </div>

      {inProgressCourses.length === 0 ? (
        <div className="py-8 px-4 rounded-xl bg-gray-50/70 border border-dashed border-gray-200 text-center">
          <BookOpen className="w-10 h-10 text-gray-400 mx-auto mb-2" />
          <p className="text-sm font-bold text-gray-800">No active progress right now</p>
          <p className="text-xs text-gray-500 mt-1 mb-4">
            Enroll in an interactive course and begin your skill journey!
          </p>
          <button
            onClick={() => onNavigate('/courses')}
            className="inline-flex items-center gap-2 px-5 py-2.5 rounded-xl bg-[#8DB600] hover:bg-[#7ba000] text-gray-900 font-bold text-xs shadow-xs transition-all cursor-pointer"
          >
            <span>Browse Courses</span>
            <ArrowRight className="w-3.5 h-3.5" />
          </button>
        </div>
      ) : (
        <div className="space-y-4">
          {inProgressCourses.map((enr) => {
            const progress = enr.progressPercentage || 0;
            return (
              <div
                key={enr.id}
                className="p-4 sm:p-5 rounded-xl border border-gray-100 hover:border-lime-300 hover:shadow-xs bg-white transition-all flex flex-col sm:flex-row sm:items-center justify-between gap-4"
              >
                {/* Course Details & Progress bar */}
                <div className="flex-1 min-w-0">
                  <div className="flex items-center gap-2 mb-1.5 flex-wrap">
                    <span className="text-[11px] font-bold uppercase tracking-wider text-lime-800 bg-lime-50 px-2 py-0.5 rounded-md border border-lime-100">
                      {enr.category || 'Course'}
                    </span>
                    {enr.duration && (
                      <span className="text-xs text-gray-400 flex items-center gap-1">
                        <Clock className="w-3 h-3" />
                        {enr.duration}
                      </span>
                    )}
                  </div>

                  <h3 className="font-bold text-gray-900 text-sm sm:text-base line-clamp-1">
                    {enr.courseTitle || `Course #${enr.courseId}`}
                  </h3>

                  {/* Progress bar */}
                  <div className="mt-3">
                    <div className="flex items-center justify-between text-xs mb-1.5 font-medium">
                      <span className="text-gray-500">Progress</span>
                      <span className="font-bold text-lime-700">{progress}%</span>
                    </div>
                    <div className="w-full bg-gray-100 h-2.5 rounded-full overflow-hidden">
                      <div
                        className="bg-[#8DB600] h-full rounded-full transition-all duration-500"
                        style={{ width: `${Math.max(progress, 4)}%` }}
                      />
                    </div>
                  </div>
                </div>

                {/* Continue button */}
                <div className="shrink-0 sm:self-center">
                  <button
                    onClick={() => onNavigate(`/courses/${enr.courseId}`)}
                    className="w-full sm:w-auto inline-flex items-center justify-center gap-2 px-4 py-2.5 rounded-xl bg-[#8DB600] hover:bg-[#7ba000] text-gray-900 font-bold text-xs sm:text-sm shadow-xs transition-all cursor-pointer"
                  >
                    <span>Continue Learning</span>
                    <ArrowRight className="w-4 h-4" />
                  </button>
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
};
