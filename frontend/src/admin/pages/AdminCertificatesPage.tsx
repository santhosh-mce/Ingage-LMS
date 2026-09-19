import React, { useState, useEffect } from 'react';
import { getAdminCertificates, getCertificateDownloadUrl } from '../../api/adminApi';
import {
  Award,
  Search,
  Download,
  ExternalLink,
  RefreshCw,
  Calendar,
  CheckCircle2,
  FileCheck,
} from 'lucide-react';

export const AdminCertificatesPage: React.FC<{ onNavigate: (path: string) => void }> = ({ onNavigate }) => {
  const [certificates, setCertificates] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');

  const fetchCerts = async () => {
    setLoading(true);
    try {
      const data = await getAdminCertificates();
      setCertificates(data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCerts();
  }, []);

  const filtered = certificates.filter((c) => {
    if (search.trim()) {
      const q = search.toLowerCase();
      const matchName = c.userName?.toLowerCase().includes(q);
      const matchEmail = c.userEmail?.toLowerCase().includes(q);
      const matchCourse = c.courseTitle?.toLowerCase().includes(q);
      const matchNum = c.certificateNumber?.toLowerCase().includes(q) || c.verificationCode?.toLowerCase().includes(q);
      return matchName || matchEmail || matchCourse || matchNum;
    }
    return true;
  });

  return (
    <div className="space-y-6 animate-fade-in">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-white tracking-tight flex items-center gap-2">
            <Award className="w-6 h-6 text-lime-400" />
            Issued Credentials & Certificate Authority
          </h1>
          <p className="text-xs text-slate-400">
            Immutable completion records generated via OpenPDF with unique cryptographic verification codes.
          </p>
        </div>

        <button
          onClick={fetchCerts}
          className="p-2.5 bg-slate-800 hover:bg-slate-700 text-slate-300 rounded-xl border border-slate-700 transition-colors cursor-pointer self-start sm:self-auto"
        >
          <RefreshCw className={`w-4 h-4 ${loading ? 'animate-spin text-lime-400' : ''}`} />
        </button>
      </div>

      <div className="p-4 bg-slate-900 border border-slate-800 rounded-2xl flex items-center gap-3">
        <div className="relative w-full sm:w-80">
          <Search className="w-4 h-4 text-slate-400 absolute left-3.5 top-3" />
          <input
            type="text"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Search certificate #, student, or course..."
            className="w-full pl-10 pr-4 py-2 bg-slate-950 border border-slate-800 rounded-xl text-xs text-white placeholder-slate-400 focus:outline-none focus:border-lime-400"
          />
        </div>
      </div>

      <div className="bg-slate-900 border border-slate-800 rounded-3xl shadow-xl overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs text-slate-300">
            <thead className="bg-slate-950/80 uppercase text-[10px] text-slate-400 tracking-wider border-b border-slate-800">
              <tr>
                <th className="px-4 py-3.5">Certificate Number</th>
                <th className="px-4 py-3.5">Student</th>
                <th className="px-4 py-3.5">Course Completed</th>
                <th className="px-4 py-3.5">Verification Code</th>
                <th className="px-4 py-3.5">Status</th>
                <th className="px-4 py-3.5">Issue Date</th>
                <th className="px-4 py-3.5 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-800/60">
              {loading && certificates.length === 0 ? (
                <tr>
                  <td colSpan={7} className="px-4 py-12 text-center text-slate-400">
                    <RefreshCw className="w-6 h-6 animate-spin mx-auto text-lime-400 mb-2" />
                    Querying issued credentials...
                  </td>
                </tr>
              ) : filtered.length === 0 ? (
                <tr>
                  <td colSpan={7} className="px-4 py-12 text-center text-slate-400">
                    No certificate records match.
                  </td>
                </tr>
              ) : (
                filtered.map((c) => (
                  <tr key={c.id} className="hover:bg-slate-800/40 transition-colors">
                    <td className="px-4 py-3.5 font-mono font-bold text-lime-400 text-sm">
                      {c.certificateNumber}
                    </td>
                    <td className="px-4 py-3.5">
                      <div className="font-bold text-white">{c.userName}</div>
                      <div className="text-[10px] text-slate-400">{c.userEmail}</div>
                    </td>
                    <td className="px-4 py-3.5 font-semibold text-slate-200 max-w-xs truncate">
                      {c.courseTitle}
                    </td>
                    <td className="px-4 py-3.5 font-mono text-slate-400 text-[11px]">
                      {c.verificationCode}
                    </td>
                    <td className="px-4 py-3.5">
                      <span className="text-[10px] font-bold px-2 py-0.5 rounded-full bg-emerald-500/10 text-emerald-400 border border-emerald-500/20">
                        {c.status}
                      </span>
                    </td>
                    <td className="px-4 py-3.5 text-slate-400 text-[11px]">
                      {new Date(c.issueDate).toLocaleDateString()}
                    </td>
                    <td className="px-4 py-3.5 text-right space-x-2 whitespace-nowrap">
                      {/* Download PDF button */}
                      <a
                        href={getCertificateDownloadUrl(c.id, false)}
                        download
                        className="inline-flex items-center gap-1 px-3 py-1 rounded-lg bg-lime-500 hover:bg-lime-400 text-slate-950 font-bold text-[11px] transition-colors cursor-pointer"
                      >
                        <Download className="w-3 h-3" />
                        <span>PDF</span>
                      </a>

                      {/* Public verify page */}
                      <a
                        href={c.verificationUrl}
                        target="_blank"
                        rel="noreferrer"
                        className="inline-flex items-center gap-1 px-2.5 py-1 rounded-lg bg-slate-800 hover:bg-slate-700 text-slate-300 text-[11px] transition-colors"
                      >
                        <ExternalLink className="w-3 h-3" />
                        <span>Public Check</span>
                      </a>
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
