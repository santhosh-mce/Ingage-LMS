import React, { useState } from 'react';
import { BookOpen, Clock, ArrowRight, CheckCircle2, PlayCircle, Layers } from 'lucide-react';
import { UserEnrollmentRecord } from '../../api/paymentApi';

interface EnrolledCoursesProps {
  enrollments: UserEnrollmentRecord[];
  onNavigate: (path: string) => void;
  loading?: boolean;
}

export const EnrolledCourses: React.FC<EnrolledCoursesProps> = ({
  enrollments,
  onNavigate,
  loading,
}) => {
  const [imageErrors, setImageErrors] = useState<Record<number, boolean>>({});

  const handleImageError = (id: number) => {
    setImageErrors((prev) => ({ ...prev, [id]: true }));
  };

  if (loading) {
    return (
      <div className="bg-white rounded-2xl border border-gray-100 p-6 sm:p-8 shadow-xs">
        <div className="h-5 bg-gray-200 rounded w-1/4 mb-6"></div>
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-5">
          {[1, 2, 3].map((i) => (
            <div key={i} className="rounded-2xl border border-gray-100 overflow-hidden animate-pulse">
              <div className="h-40 bg-gray-200"></div>
              <div className="p-4 space-y-3">
                <div className="h-4 bg-gray-200 rounded w-3/4"></div>
                <div className="h-3 bg-gray-200 rounded w-1/2"></div>
                <div className="h-2 bg-gray-200 rounded w-full"></div>
              </div>
            </div>
          ))}
        </div>
      </div>
    );
  }

  return (
    <div className="bg-white rounded-2xl border border-gray-100 p-6 sm:p-8 shadow-xs">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 mb-6 pb-4 border-b border-gray-100">
        <div>
          <h2 className="text-lg font-bold text-gray-900 tracking-tight flex items-center gap-2">
            <BookOpen className="w-5 h-5 text-[#8DB600]" />
            <span>Enrolled Courses</span>
          </h2>
          <p className="text-xs sm:text-sm text-gray-500 mt-0.5">
            All your curriculum courses and certifications
          </p>
        </div>

        <div className="flex items-center gap-3">
          <span className="text-xs bg-lime-100 text-lime-800 font-bold px-3 py-1 rounded-full">
            {enrollments.length} {enrollments.length === 1 ? 'Course' : 'Courses'}
          </span>
          <button
            onClick={() => onNavigate('/courses')}
            className="text-xs sm:text-sm font-semibold text-[#8DB600] hover:text-[#7ba000] flex items-center gap-1 cursor-pointer transition-colors"
          >
            <span>Browse Catalog</span>
            <ArrowRight className="w-4 h-4" />
          </button>
        </div>
      </div>

      {enrollments.length === 0 ? (
        <div className="py-12 px-4 rounded-xl bg-gray-50/70 border border-dashed border-gray-200 text-center">
          <BookOpen className="w-12 h-12 text-gray-400 mx-auto mb-3" />
          <h3 className="text-base font-bold text-gray-800">No course enrollments yet</h3>
          <p className="text-xs sm:text-sm text-gray-500 mt-1 max-w-md mx-auto mb-5">
            Expand your career opportunities with our hands-on curriculum and industry certifications.
          </p>
          <button
            onClick={() => onNavigate('/courses')}
            className="inline-flex items-center gap-2 px-6 py-2.5 rounded-xl bg-[#8DB600] hover:bg-[#7ba000] text-gray-900 font-bold text-sm shadow-xs transition-all cursor-pointer"
          >
            <span>Explore Courses</span>
            <ArrowRight className="w-4 h-4" />
          </button>
        </div>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-5">
          {enrollments.map((enr) => {
            const isCompleted =
              enr.status === 'COMPLETED' || (enr.progressPercentage !== undefined && enr.progressPercentage >= 100);
            const progress = enr.progressPercentage || 0;
            const hasError = imageErrors[enr.id];

            return (
              <div
                key={enr.id}
                className="group bg-white rounded-2xl border border-gray-200/90 shadow-xs hover:shadow-md hover:border-lime-300 transition-all duration-300 flex flex-col overflow-hidden"
              >
                {/* Course Thumbnail */}
                <div className="relative w-full h-40 bg-gray-100 overflow-hidden shrink-0">
                  {enr.thumbnail && !hasError ? (
                    <img
                      src={enr.thumbnail}
                      alt={enr.courseTitle}
                      onError={() => handleImageError(enr.id)}
                      className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500"
                      loading="lazy"
                    />
                  ) : (
                    <div className="w-full h-full flex flex-col items-center justify-center bg-gradient-to-br from-lime-50 to-emerald-100 text-lime-800">
                      <BookOpen className="w-10 h-10 mb-1 opacity-80" />
                      <span className="text-[11px] font-bold uppercase tracking-wider">
                        {enr.category || 'Course'}
                      </span>
                    </div>
                  )}

                  {/* Status Badge */}
                  <div className="absolute top-3 left-3">
                    {isCompleted ? (
                      <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-[11px] font-bold bg-emerald-600 text-white shadow-xs">
                        <CheckCircle2 className="w-3 h-3" />
                        Completed
                      </span>
                    ) : (
                      <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-[11px] font-bold bg-[#8DB600] text-gray-900 shadow-xs">
                        <PlayCircle className="w-3 h-3" />
                        Active
                      </span>
                    )}
                  </div>
                </div>

                {/* Content */}
                <div className="p-4 sm:p-5 flex-1 flex flex-col justify-between">
                  <div>
                    <div className="flex items-center justify-between text-xs text-gray-500 mb-1.5">
                      <span className="font-semibold text-lime-800 bg-lime-50 px-2 py-0.5 rounded-md border border-lime-100">
                        {enr.category || 'General'}
                      </span>
                      {enr.duration && (
                        <span className="flex items-center gap-1">
                          <Clock className="w-3 h-3 text-gray-400" />
                          {enr.duration}
                        </span>
                      )}
                    </div>

                    <h3 className="font-bold text-gray-900 text-sm sm:text-base line-clamp-2 group-hover:text-lime-800 transition-colors">
                      {enr.courseTitle || `Course #${enr.courseId}`}
                    </h3>
                  </div>

                  {/* Progress bar */}
                  <div className="mt-4 pt-3 border-t border-gray-100">
                    <div className="flex items-center justify-between text-xs mb-1.5 font-medium">
                      <span className="text-gray-500">Progress</span>
                      <span className="font-bold text-lime-700">{progress}%</span>
                    </div>
                    <div className="w-full bg-gray-100 h-2 rounded-full overflow-hidden mb-4">
                      <div
                        className="bg-[#8DB600] h-full rounded-full transition-all duration-500"
                        style={{ width: `${Math.max(progress, 3)}%` }}
                      />
                    </div>

                    <button
                      onClick={() => onNavigate(`/courses/${enr.courseId}`)}
                      className="w-full inline-flex items-center justify-center gap-2 py-2 rounded-xl bg-gray-50 hover:bg-[#8DB600] text-gray-800 hover:text-gray-900 font-bold text-xs transition-colors cursor-pointer border border-gray-200 hover:border-[#8DB600]"
                    >
                      <span>{isCompleted ? 'Review Course' : 'Continue Learning'}</span>
                      <ArrowRight className="w-3.5 h-3.5" />
                    </button>
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
};
