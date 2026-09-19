import React, { useState, useEffect } from 'react';
import { getAdminProgress } from '../../api/adminApi';
import {
  TrendingUp,
  Search,
  CheckCircle2,
  Clock,
  Award,
  ExternalLink,
  RefreshCw,
  BookOpen,
} from 'lucide-react';

export const AdminCourseProgressPage: React.FC<{ onNavigate: (path: string) => void }> = ({ onNavigate }) => {
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
    <div className="space-y-6 animate-fade-in">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-white tracking-tight flex items-center gap-2">
            <TrendingUp className="w-6 h-6 text-lime-400" />
            Global Learner Course Progress Tracker
          </h1>
          <p className="text-xs text-slate-400">
            Real-time curriculum progression, lesson completion counters, and automated certificate issuances.
          </p>
        </div>

        <button
          onClick={fetchProgress}
          className="p-2.5 bg-slate-800 hover:bg-slate-700 text-slate-300 rounded-xl border border-slate-700 transition-colors cursor-pointer self-start sm:self-auto"
        >
          <RefreshCw className={`w-4 h-4 ${loading ? 'animate-spin text-lime-400' : ''}`} />
        </button>
      </div>

      {/* Search and Filters */}
      <div className="p-4 bg-slate-900 border border-slate-800 rounded-2xl flex flex-col sm:flex-row gap-3 items-center justify-between shadow-lg">
        <div className="relative w-full sm:w-80">
          <Search className="w-4 h-4 text-slate-400 absolute left-3.5 top-3" />
          <input
            type="text"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Search by student, email, or course..."
            className="w-full pl-10 pr-4 py-2 bg-slate-950 border border-slate-800 rounded-xl text-xs text-white placeholder-slate-400 focus:outline-none focus:border-lime-400"
          />
        </div>

        <div className="flex items-center gap-1.5 self-start sm:self-auto bg-slate-950 p-1 rounded-xl border border-slate-800">
          {['ALL', 'IN_PROGRESS', 'COMPLETED'].map((st) => (
            <button
              key={st}
              onClick={() => setStatusFilter(st)}
              className={`px-3 py-1 rounded-lg text-[10px] font-bold tracking-wider uppercase transition-all cursor-pointer ${
                statusFilter === st
                  ? 'bg-lime-500 text-slate-950 shadow-sm'
                  : 'text-slate-400 hover:text-white'
              }`}
            >
              {st.replace('_', ' ')}
            </button>
          ))}
        </div>
      </div>

      {/* Progress Table */}
      <div className="bg-slate-900 border border-slate-800 rounded-3xl shadow-xl overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs text-slate-300">
            <thead className="bg-slate-950/80 uppercase text-[10px] text-slate-400 tracking-wider border-b border-slate-800">
              <tr>
                <th className="px-4 py-3.5">Student</th>
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
            <tbody className="divide-y divide-slate-800/60">
              {loading && progressList.length === 0 ? (
                <tr>
                  <td colSpan={9} className="px-4 py-12 text-center text-slate-400">
                    <RefreshCw className="w-6 h-6 animate-spin mx-auto text-lime-400 mb-2" />
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
                  <tr key={p.enrollmentId} className="hover:bg-slate-800/40 transition-colors">
                    <td className="px-4 py-3.5 font-bold text-white">
                      <div>{p.userName}</div>
                      <div className="text-[10px] text-slate-400">{p.userEmail}</div>
                    </td>
                    <td className="px-4 py-3.5 font-semibold text-slate-200 max-w-xs truncate">
                      {p.courseTitle}
                    </td>
                    <td className="px-4 py-3.5">
                      <div className="flex items-center gap-2">
                        <div className="w-16 bg-slate-800 rounded-full h-1.5 overflow-hidden">
                          <div
                            className="bg-lime-400 h-full rounded-full"
                            style={{ width: `${p.progress}%` }}
                          ></div>
                        </div>
                        <span className="text-[11px] font-bold text-white">{p.progress}%</span>
                      </div>
                    </td>
                    <td className="px-4 py-3.5 font-bold text-slate-200">
                      {p.lessonsCompleted} <span className="text-slate-400 font-normal">/ {p.lessonsTotal}</span>
                    </td>
                    <td className="px-4 py-3.5">
                      <span
                        className={`text-[10px] font-bold px-2 py-0.5 rounded-full ${
                          p.status === 'COMPLETED'
                            ? 'bg-emerald-500/10 text-emerald-400 border border-emerald-500/20'
                            : 'bg-cyan-500/10 text-cyan-400 border border-cyan-500/20'
                        }`}
                      >
                        {p.status}
                      </span>
                    </td>
                    <td className="px-4 py-3.5 text-slate-400 text-[11px]">
                      {new Date(p.enrollmentDate).toLocaleDateString()}
                    </td>
                    <td className="px-4 py-3.5 text-slate-400 text-[11px]">
                      {p.completionDate ? new Date(p.completionDate).toLocaleDateString() : '—'}
                    </td>
                    <td className="px-4 py-3.5">
                      {p.certificateNumber ? (
                        <span className="font-mono text-lime-400 font-bold text-[11px]">
                          {p.certificateNumber}
                        </span>
                      ) : (
                        <span className="text-slate-500">—</span>
                      )}
                    </td>
                    <td className="px-4 py-3.5 text-right space-x-2 whitespace-nowrap">
                      {p.verificationCode && (
                        <a
                          href={`/certificate/verify/${p.verificationCode}`}
                          target="_blank"
                          rel="noreferrer"
                          className="inline-flex items-center gap-1 px-2 py-1 rounded-lg bg-slate-800 hover:bg-slate-700 text-slate-300 text-[11px]"
                        >
                          <ExternalLink className="w-3 h-3" />
                          <span>Verify</span>
                        </a>
                      )}
                      <button
                        onClick={() => onNavigate(`/admin/users/${p.userId}`)}
                        className="px-2 py-1 rounded-lg bg-slate-800 hover:bg-slate-700 text-slate-300 text-[11px]"
                      >
                        Dossier
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
