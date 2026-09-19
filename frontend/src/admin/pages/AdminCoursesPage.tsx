import React, { useState, useEffect } from 'react';
import {
  getAdminCourses,
  publishAdminCourse,
  unpublishAdminCourse,
  archiveAdminCourse,
  deleteAdminCourse,
} from '../../api/adminApi';
import {
  BookOpen,
  Plus,
  Search,
  CheckCircle2,
  XCircle,
  Archive,
  Trash2,
  Edit2,
  BarChart2,
  Eye,
  RefreshCw,
  Tag,
  IndianRupee,
} from 'lucide-react';

export interface AdminCoursesPageProps {
  onNavigate: (path: string) => void;
  onShowToast?: (msg: string) => void;
}

export const AdminCoursesPage: React.FC<AdminCoursesPageProps> = ({
  onNavigate,
  onShowToast,
}) => {
  const [courses, setCourses] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');

  const fetchCourses = async () => {
    setLoading(true);
    try {
      const data = await getAdminCourses(searchQuery, statusFilter);
      setCourses(data);
    } catch (err) {
      console.error('Failed to load courses', err);
      if (onShowToast) onShowToast('Failed to load courses.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCourses();
  }, [searchQuery, statusFilter]);

  const handlePublish = async (course: any) => {
    try {
      const res = await publishAdminCourse(course.id);
      if (res.success) {
        if (onShowToast) onShowToast(`Published ${course.title}!`);
        fetchCourses();
      } else {
        if (onShowToast) onShowToast(`Publish failed: ${res.errors?.join(', ') || res.message}`);
      }
    } catch (err: any) {
      if (onShowToast) onShowToast(err.response?.data?.message || 'Publishing failed.');
    }
  };

  const handleUnpublish = async (course: any) => {
    try {
      await unpublishAdminCourse(course.id);
      if (onShowToast) onShowToast(`Unpublished ${course.title}`);
      fetchCourses();
    } catch {
      if (onShowToast) onShowToast('Failed to unpublish course.');
    }
  };

  const handleArchive = async (course: any) => {
    if (!window.confirm(`Archive course "${course.title}"? Enrolled learners will maintain access, but the course will be hidden from public discovery.`)) {
      return;
    }
    try {
      const res = await archiveAdminCourse(course.id);
      if (onShowToast) onShowToast(res.message || 'Course archived.');
      fetchCourses();
    } catch {
      if (onShowToast) onShowToast('Failed to archive course.');
    }
  };

  const handleDelete = async (course: any) => {
    if (!window.confirm(`Are you sure you want to delete or archive "${course.title}"? If students are enrolled, it will be safely archived to preserve records.`)) {
      return;
    }
    try {
      const res = await deleteAdminCourse(course.id);
      if (onShowToast) onShowToast(res.message || 'Course updated.');
      fetchCourses();
    } catch {
      if (onShowToast) onShowToast('Failed to delete course.');
    }
  };

  return (
    <div className="space-y-6 animate-fade-in">
      {/* Header & Add Button */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-white tracking-tight flex items-center gap-2">
            <BookOpen className="w-6 h-6 text-lime-400" />
            Course Catalog & Curriculum Management
          </h1>
          <p className="text-xs text-slate-400">
            Publish, edit pricing, manage sections and lessons, and monitor student completion rates.
          </p>
        </div>

        <div className="flex items-center gap-3 self-start sm:self-auto">
          <button
            onClick={fetchCourses}
            className="p-2.5 bg-slate-800 hover:bg-slate-700 text-slate-300 hover:text-white rounded-xl border border-slate-700 transition-colors cursor-pointer"
            title="Refresh Courses"
          >
            <RefreshCw className={`w-4 h-4 ${loading ? 'animate-spin text-lime-400' : ''}`} />
          </button>

          <button
            onClick={() => onNavigate('/admin/courses/new')}
            className="flex items-center gap-2 px-4 py-2.5 bg-lime-500 hover:bg-lime-400 text-slate-950 font-bold text-xs rounded-xl transition-all shadow-lg shadow-lime-500/20 cursor-pointer"
          >
            <Plus className="w-4 h-4" />
            <span>Add New Course</span>
          </button>
        </div>
      </div>

      {/* Filter & Search Bar */}
      <div className="p-4 bg-slate-900 border border-slate-800 rounded-2xl flex flex-col sm:flex-row gap-3 items-center justify-between shadow-lg">
        <div className="relative w-full sm:w-80">
          <Search className="w-4 h-4 text-slate-400 absolute left-3.5 top-3" />
          <input
            type="text"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            placeholder="Search courses by title..."
            className="w-full pl-10 pr-4 py-2 bg-slate-950 border border-slate-800 rounded-xl text-xs text-white placeholder-slate-400 focus:outline-none focus:border-lime-400"
          />
        </div>

        {/* Status Filter Tabs */}
        <div className="flex items-center gap-1.5 self-start sm:self-auto bg-slate-950 p-1 rounded-xl border border-slate-800">
          {['ALL', 'PUBLISHED', 'DRAFT', 'UNPUBLISHED', 'ARCHIVED'].map((st) => (
            <button
              key={st}
              onClick={() => setStatusFilter(st)}
              className={`px-3 py-1 rounded-lg text-[10px] font-bold tracking-wider uppercase transition-all cursor-pointer ${
                statusFilter === st
                  ? 'bg-lime-500 text-slate-950 shadow-sm'
                  : 'text-slate-400 hover:text-white'
              }`}
            >
              {st}
            </button>
          ))}
        </div>
      </div>

      {/* Courses Table */}
      <div className="bg-slate-900 border border-slate-800 rounded-3xl shadow-xl overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs text-slate-300">
            <thead className="bg-slate-950/80 uppercase text-[10px] text-slate-400 tracking-wider border-b border-slate-800">
              <tr>
                <th className="px-4 py-3.5">Course Name</th>
                <th className="px-4 py-3.5">Category</th>
                <th className="px-4 py-3.5">Price</th>
                <th className="px-4 py-3.5">Final Price</th>
                <th className="px-4 py-3.5">Status</th>
                <th className="px-4 py-3.5 text-center">Students</th>
                <th className="px-4 py-3.5">Completion Rate</th>
                <th className="px-4 py-3.5">Revenue</th>
                <th className="px-4 py-3.5 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-800/60">
              {loading && courses.length === 0 ? (
                <tr>
                  <td colSpan={9} className="px-4 py-12 text-center text-slate-400">
                    <RefreshCw className="w-6 h-6 animate-spin mx-auto text-lime-400 mb-2" />
                    Querying courses...
                  </td>
                </tr>
              ) : courses.length === 0 ? (
                <tr>
                  <td colSpan={9} className="px-4 py-12 text-center text-slate-400">
                    No courses found matching selected filters.
                  </td>
                </tr>
              ) : (
                courses.map((c) => (
                  <tr key={c.id} className="hover:bg-slate-800/40 transition-colors">
                    <td className="px-4 py-3.5 font-bold text-white max-w-[200px]">
                      <div className="flex items-center gap-2.5">
                        {c.thumbnail ? (
                          <img
                            src={c.thumbnail}
                            alt=""
                            className="w-10 h-7 object-cover rounded-md shrink-0 bg-slate-800"
                            onError={(e) => {
                              (e.target as HTMLElement).style.display = 'none';
                            }}
                          />
                        ) : (
                          <div className="w-10 h-7 bg-slate-800 rounded-md shrink-0 flex items-center justify-center text-slate-400">
                            <BookOpen className="w-4 h-4" />
                          </div>
                        )}
                        <span className="truncate">{c.title}</span>
                      </div>
                    </td>
                    <td className="px-4 py-3.5 text-slate-400">
                      {c.category || 'General'}
                    </td>
                    <td className="px-4 py-3.5 text-slate-400">
                      {c.price === 0 ? <span className="text-lime-400 font-semibold">Free</span> : `₹${c.price}`}
                    </td>
                    <td className="px-4 py-3.5 font-bold text-white">
                      {c.finalPrice === 0 ? <span className="text-lime-400">Free</span> : `₹${c.finalPrice}`}
                      {c.discountType && (
                        <span className="ml-1 text-[10px] text-emerald-400">
                          ({c.discountType === 'PERCENTAGE' ? `${c.discountValue}%` : `₹${c.discountValue}`})
                        </span>
                      )}
                    </td>
                    <td className="px-4 py-3.5">
                      <span
                        className={`text-[10px] font-bold px-2 py-0.5 rounded-full uppercase tracking-wider ${
                          c.status === 'PUBLISHED'
                            ? 'bg-emerald-500/10 text-emerald-400 border border-emerald-500/20'
                            : c.status === 'DRAFT'
                            ? 'bg-amber-500/10 text-amber-400 border border-amber-500/20'
                            : c.status === 'ARCHIVED'
                            ? 'bg-slate-800 text-slate-400 border border-slate-700'
                            : 'bg-rose-500/10 text-rose-400 border border-rose-500/20'
                        }`}
                      >
                        {c.status}
                      </span>
                    </td>
                    <td className="px-4 py-3.5 text-center font-bold text-slate-200">
                      {c.students || 0}
                    </td>
                    <td className="px-4 py-3.5">
                      <div className="flex items-center gap-2">
                        <div className="w-16 bg-slate-800 rounded-full h-1.5 overflow-hidden">
                          <div
                            className="bg-lime-400 h-full rounded-full"
                            style={{ width: `${Math.min(100, c.completionRate || 0)}%` }}
                          ></div>
                        </div>
                        <span className="text-[11px] text-slate-400">{c.completionRate}%</span>
                      </div>
                    </td>
                    <td className="px-4 py-3.5 font-bold text-lime-400">
                      ₹{Number(c.revenue || 0).toLocaleString()}
                    </td>
                    <td className="px-4 py-3.5 text-right space-x-1.5 whitespace-nowrap">
                      {/* Analytics */}
                      <button
                        onClick={() => onNavigate(`/admin/courses/${c.id}/analytics`)}
                        className="p-1.5 rounded-lg bg-slate-800 hover:bg-slate-700 text-slate-300 hover:text-white transition-colors cursor-pointer"
                        title="Course Analytics"
                      >
                        <BarChart2 className="w-3.5 h-3.5 text-purple-400" />
                      </button>

                      {/* Edit */}
                      <button
                        onClick={() => onNavigate(`/admin/courses/${c.id}/edit`)}
                        className="p-1.5 rounded-lg bg-slate-800 hover:bg-slate-700 text-slate-300 hover:text-white transition-colors cursor-pointer"
                        title="Edit Course & Content"
                      >
                        <Edit2 className="w-3.5 h-3.5" />
                      </button>

                      {/* Publish / Unpublish */}
                      {c.status === 'PUBLISHED' ? (
                        <button
                          onClick={() => handleUnpublish(c)}
                          className="p-1.5 rounded-lg bg-amber-500/10 hover:bg-amber-500/20 text-amber-400 transition-colors cursor-pointer"
                          title="Unpublish Course"
                        >
                          <XCircle className="w-3.5 h-3.5" />
                        </button>
                      ) : (
                        <button
                          onClick={() => handlePublish(c)}
                          className="p-1.5 rounded-lg bg-emerald-500/10 hover:bg-emerald-500/20 text-emerald-400 transition-colors cursor-pointer"
                          title="Validate & Publish"
                        >
                          <CheckCircle2 className="w-3.5 h-3.5" />
                        </button>
                      )}

                      {/* Archive */}
                      {c.status !== 'ARCHIVED' && (
                        <button
                          onClick={() => handleArchive(c)}
                          className="p-1.5 rounded-lg bg-slate-800 hover:bg-slate-700 text-slate-400 hover:text-white transition-colors cursor-pointer"
                          title="Archive Course"
                        >
                          <Archive className="w-3.5 h-3.5" />
                        </button>
                      )}

                      {/* Delete */}
                      <button
                        onClick={() => handleDelete(c)}
                        className="p-1.5 rounded-lg bg-slate-800 hover:bg-rose-500/20 text-slate-400 hover:text-rose-400 transition-colors cursor-pointer"
                        title="Delete Course"
                      >
                        <Trash2 className="w-3.5 h-3.5" />
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
