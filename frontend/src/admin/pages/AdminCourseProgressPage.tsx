import React, { useState, useEffect } from 'react';
import { getAdminProgress } from '../../api/adminApi';
import {
  GraduationCap,
  Search,
  CheckCircle2,
  ExternalLink,
  RefreshCw,
} from 'lucide-react';

export const AdminCourseProgressPage: React.FC<{ onNavigate: (path: string) => void }> = ({
  onNavigate,
}) => {
  const [progressList, setProgressList] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');

  const fetchProgress = async () => {
    setLoading(true);
    try {
      const data = await getAdminProgress(search, undefined, statusFilter);
      setProgressList(data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchProgress();
  }, [search, statusFilter]);

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 tracking-tight flex items-center gap-2">
            <GraduationCap className="w-6 h-6 text-green-600" />
            Course Progress & Enrollments
          </h1>
          <p className="text-sm text-slate-500 mt-0.5">
            Real-time curriculum progression, lesson completion counters, and automated certificate issuances.
          </p>
        </div>

        <button
          onClick={fetchProgress}
          className="flex items-center gap-2 px-4 py-2 bg-white hover:bg-slate-50 text-slate-700 rounded-xl border border-slate-200 text-sm font-semibold transition-colors cursor-pointer shadow-xs self-start sm:self-auto"
        >
          <RefreshCw className={`w-4 h-4 ${loading ? 'animate-spin text-green-600' : 'text-slate-500'}`} />
          <span>Refresh</span>
        </button>
      </div>

      {/* Search and Filters */}
      <div className="p-4 bg-white border border-slate-200 rounded-2xl flex flex-col sm:flex-row gap-3 items-center justify-between shadow-xs">
        <div className="relative w-full sm:w-80">
          <Search className="w-4 h-4 text-slate-400 absolute left-3.5 top-3" />
          <input
            type="text"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Search by student, email, or course..."
            className="w-full pl-10 pr-4 py-2 bg-slate-50 border border-slate-200 rounded-xl text-sm text-slate-900 placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-green-500/20 focus:border-green-600 transition"
          />
        </div>

        <div className="flex items-center gap-1 self-start sm:self-auto bg-slate-100 p-1 rounded-xl border border-slate-200/60">
          {['ALL', 'IN_PROGRESS', 'COMPLETED'].map((st) => (
            <button
              key={st}
              onClick={() => setStatusFilter(st)}
              className={`px-3 py-1 rounded-lg text-xs font-semibold uppercase tracking-wider transition-all cursor-pointer ${
                statusFilter === st
                  ? 'bg-green-600 text-white shadow-xs'
                  : 'text-slate-600 hover:text-slate-900'
              }`}
            >
              {st.replace('_', ' ')}
            </button>
          ))}
        </div>
      </div>

      {/* Progress Table */}
      <div className="bg-white border border-slate-200 rounded-2xl shadow-xs overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-sm">
            <thead className="bg-slate-50 uppercase text-[11px] text-slate-500 font-semibold tracking-wider border-b border-slate-200">
              <tr>
                <th className="px-5 py-3.5">Student</th>
                <th className="px-4 py-3.5">Course</th>
                <th className="px-4 py-3.5">Progress</th>
                <th className="px-4 py-3.5">Lessons Completed</th>
                <th className="px-4 py-3.5">Status</th>
                <th className="px-4 py-3.5">Enrolled Date</th>
                <th className="px-4 py-3.5">Completion Date</th>
                <th className="px-4 py-3.5">Certificate</th>
                <th className="px-4 py-3.5 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 text-slate-700">
              {loading && progressList.length === 0 ? (
                <tr>
                  <td colSpan={9} className="px-4 py-12 text-center text-slate-400">
                    <RefreshCw className="w-6 h-6 animate-spin mx-auto text-green-600 mb-2" />
                    Querying learner progress...
                  </td>
                </tr>
              ) : progressList.length === 0 ? (
                <tr>
                  <td colSpan={9} className="px-4 py-12 text-center text-slate-400">
                    No student progress records found.
                  </td>
                </tr>
              ) : (
                progressList.map((p) => (
                  <tr key={p.enrollmentId} className="hover:bg-slate-50/80 transition-colors">
                    <td className="px-5 py-3.5 font-bold text-slate-900">
                      <div>{p.userName}</div>
                      <div className="text-xs text-slate-500 font-normal">{p.userEmail}</div>
                    </td>
                    <td className="px-4 py-3.5 font-semibold text-slate-900 max-w-xs truncate">
                      {p.courseTitle}
                    </td>
                    <td className="px-4 py-3.5">
                      <div className="flex items-center gap-2">
                        <div className="w-16 bg-slate-100 rounded-full h-1.5 overflow-hidden">
                          <div
                            className="bg-green-600 h-full rounded-full"
                            style={{ width: `${p.progress}%` }}
                          />
                        </div>
                        <span className="text-xs font-bold text-slate-900">{p.progress}%</span>
                      </div>
                    </td>
                    <td className="px-4 py-3.5 font-bold text-slate-900 text-xs">
                      {p.lessonsCompleted}{' '}
                      <span className="text-slate-400 font-normal">/ {p.lessonsTotal}</span>
                    </td>
                    <td className="px-4 py-3.5">
                      <span
                        className={`text-[11px] font-semibold px-2.5 py-1 rounded-full ${
                          p.status === 'COMPLETED'
                            ? 'bg-green-100 text-green-800 border border-green-200'
                            : 'bg-amber-100 text-amber-800 border border-amber-200'
                        }`}
                      >
                        {p.status}
                      </span>
                    </td>
                    <td className="px-4 py-3.5 text-slate-500 text-xs">
                      {p.enrollmentDate ? new Date(p.enrollmentDate).toLocaleDateString() : '—'}
                    </td>
                    <td className="px-4 py-3.5 text-slate-500 text-xs">
                      {p.completionDate ? new Date(p.completionDate).toLocaleDateString() : '—'}
                    </td>
                    <td className="px-4 py-3.5">
                      {p.certificateNumber ? (
                        <span className="font-mono text-green-700 font-bold text-xs">
                          {p.certificateNumber}
                        </span>
                      ) : (
                        <span className="text-slate-400">—</span>
                      )}
                    </td>
                    <td className="px-4 py-3.5 text-right space-x-2 whitespace-nowrap">
                      {p.verificationCode && (
                        <a
                          href={`/certificate/verify/${p.verificationCode}`}
                          target="_blank"
                          rel="noreferrer"
                          className="inline-flex items-center gap-1 px-2.5 py-1 rounded-lg bg-slate-100 hover:bg-green-50 text-slate-700 hover:text-green-700 text-xs font-medium transition"
                        >
                          <ExternalLink className="w-3 h-3" />
                          <span>Verify</span>
                        </a>
                      )}
                      <button
                        onClick={() => onNavigate(`/admin/users/${p.userId}`)}
                        className="px-2.5 py-1 rounded-lg bg-slate-100 hover:bg-slate-200 text-slate-700 text-xs font-medium transition cursor-pointer"
                      >
                        User Dossier
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
