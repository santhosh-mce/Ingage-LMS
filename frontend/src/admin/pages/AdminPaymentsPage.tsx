import React, { useState, useEffect } from 'react';
import { getAdminPayments } from '../../api/adminApi';
import {
  CreditCard,
  Search,
  CheckCircle2,
  Clock,
  XCircle,
  RefreshCw,
  IndianRupee,
  Calendar,
} from 'lucide-react';

export const AdminPaymentsPage: React.FC<{ onNavigate: (path: string) => void }> = ({ onNavigate }) => {
  const [payments, setPayments] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');

  const fetchPayments = async () => {
    setLoading(true);
    try {
      const data = await getAdminPayments();
      setPayments(data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPayments();
  }, []);

  const filtered = payments.filter((p) => {
    if (statusFilter !== 'ALL' && p.paymentStatus !== statusFilter) return false;
    if (search.trim()) {
      const q = search.toLowerCase();
      const matchName = p.userName?.toLowerCase().includes(q);
      const matchEmail = p.userEmail?.toLowerCase().includes(q);
      const matchCourse = p.courseName?.toLowerCase().includes(q);
      const matchId = p.paymentNumber?.toLowerCase().includes(q) || p.razorpayPaymentId?.toLowerCase().includes(q);
      return matchName || matchEmail || matchCourse || matchId;
    }
    return true;
  });

  const totalCollected = payments
    .filter((p) => p.paymentStatus === 'PAID')
    .reduce((acc, p) => acc + (Number(p.finalAmount) || 0), 0);

  return (
    <div className="space-y-6 animate-fade-in">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-white tracking-tight flex items-center gap-2">
            <CreditCard className="w-6 h-6 text-lime-400" />
            Razorpay Payment Management
          </h1>
          <p className="text-xs text-slate-400">
            Real-time transaction logs verified via backend HMAC-SHA256 signatures.
          </p>
        </div>

        <div className="flex items-center gap-4">
          <div className="text-right">
            <span className="text-[10px] uppercase font-bold text-slate-400">Total Settled</span>
            <p className="text-lg font-bold text-lime-400">₹{totalCollected.toLocaleString()}</p>
          </div>
          <button
            onClick={fetchPayments}
            className="p-2.5 bg-slate-800 hover:bg-slate-700 text-slate-300 rounded-xl border border-slate-700 transition-colors cursor-pointer"
            title="Refresh Transactions"
          >
            <RefreshCw className={`w-4 h-4 ${loading ? 'animate-spin text-lime-400' : ''}`} />
          </button>
        </div>
      </div>

      {/* Filter Bar */}
      <div className="p-4 bg-slate-900 border border-slate-800 rounded-2xl flex flex-col sm:flex-row gap-3 items-center justify-between shadow-lg">
        <div className="relative w-full sm:w-80">
          <Search className="w-4 h-4 text-slate-400 absolute left-3.5 top-3" />
          <input
            type="text"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Search by student, course, or payment ID..."
            className="w-full pl-10 pr-4 py-2 bg-slate-950 border border-slate-800 rounded-xl text-xs text-white placeholder-slate-400 focus:outline-none focus:border-lime-400"
          />
        </div>

        {/* Status filters */}
        <div className="flex items-center gap-1.5 self-start sm:self-auto bg-slate-950 p-1 rounded-xl border border-slate-800">
          {['ALL', 'PAID', 'PENDING', 'FAILED', 'REFUNDED'].map((st) => (
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

      {/* Payments Table */}
      <div className="bg-slate-900 border border-slate-800 rounded-3xl shadow-xl overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs text-slate-300">
            <thead className="bg-slate-950/80 uppercase text-[10px] text-slate-400 tracking-wider border-b border-slate-800">
              <tr>
                <th className="px-4 py-3.5">Payment ID</th>
                <th className="px-4 py-3.5">Order ID</th>
                <th className="px-4 py-3.5">Student</th>
                <th className="px-4 py-3.5">Course</th>
                <th className="px-4 py-3.5">Original</th>
                <th className="px-4 py-3.5">Discount</th>
                <th className="px-4 py-3.5">Final Paid</th>
                <th className="px-4 py-3.5">Gateway / Ref</th>
                <th className="px-4 py-3.5">Status</th>
                <th className="px-4 py-3.5 text-right">Date</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-800/60">
              {loading && payments.length === 0 ? (
                <tr>
                  <td colSpan={10} className="px-4 py-12 text-center text-slate-400">
                    <RefreshCw className="w-6 h-6 animate-spin mx-auto text-lime-400 mb-2" />
                    Querying payment records...
                  </td>
                </tr>
              ) : filtered.length === 0 ? (
                <tr>
                  <td colSpan={10} className="px-4 py-12 text-center text-slate-400">
                    No payment records match criteria.
                  </td>
                </tr>
              ) : (
                filtered.map((p) => (
                  <tr key={p.id} className="hover:bg-slate-800/40 transition-colors">
                    <td className="px-4 py-3.5 font-mono text-slate-300 font-bold">
                      {p.paymentNumber}
                    </td>
                    <td className="px-4 py-3.5 font-mono text-slate-400 text-[11px]">
                      {p.orderNumber || '—'}
                    </td>
                    <td className="px-4 py-3.5">
                      <div className="font-bold text-white">{p.userName}</div>
                      <div className="text-[10px] text-slate-400">{p.userEmail}</div>
                    </td>
                    <td className="px-4 py-3.5 font-semibold text-slate-200 max-w-xs truncate">
                      {p.courseName}
                    </td>
                    <td className="px-4 py-3.5 text-slate-400">
                      ₹{p.amount}
                    </td>
                    <td className="px-4 py-3.5 text-rose-400">
                      ₹{p.discount || 0}
                    </td>
                    <td className="px-4 py-3.5 font-bold text-lime-400">
                      ₹{p.finalAmount}
                    </td>
                    <td className="px-4 py-3.5 font-mono text-[10px] text-slate-400 truncate max-w-[120px]">
                      {p.razorpayPaymentId || p.paymentMethod || 'Razorpay'}
                    </td>
                    <td className="px-4 py-3.5">
                      <span
                        className={`text-[10px] font-bold px-2 py-0.5 rounded-full uppercase tracking-wider ${
                          p.paymentStatus === 'PAID'
                            ? 'bg-emerald-500/10 text-emerald-400 border border-emerald-500/20'
                            : p.paymentStatus === 'PENDING'
                            ? 'bg-amber-500/10 text-amber-400 border border-amber-500/20'
                            : 'bg-rose-500/10 text-rose-400 border border-rose-500/20'
                        }`}
                      >
                        {p.paymentStatus}
                      </span>
                    </td>
                    <td className="px-4 py-3.5 text-right text-slate-400 text-[11px] whitespace-nowrap">
                      {new Date(p.paymentDate).toLocaleDateString()}
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
