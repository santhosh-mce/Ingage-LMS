import React, { useState, useEffect } from 'react';
import { getAdminOrders } from '../../api/adminApi';
import {
  ShoppingBag,
  Search,
  RefreshCw,
  Calendar,
  CheckCircle2,
  Clock,
  Tag,
} from 'lucide-react';

export const AdminOrdersPage: React.FC<{ onNavigate: (path: string) => void }> = ({ onNavigate }) => {
  const [orders, setOrders] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');

  const fetchOrders = async () => {
    setLoading(true);
    try {
      const data = await getAdminOrders();
      setOrders(data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchOrders();
  }, []);

  const filtered = orders.filter((o) => {
    if (search.trim()) {
      const q = search.toLowerCase();
      const matchName = o.userName?.toLowerCase().includes(q);
      const matchEmail = o.userEmail?.toLowerCase().includes(q);
      const matchCourse = o.courseName?.toLowerCase().includes(q);
      const matchOrder = o.orderNumber?.toLowerCase().includes(q);
      return matchName || matchEmail || matchCourse || matchOrder;
    }
    return true;
  });

  return (
    <div className="space-y-6 animate-fade-in">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-white tracking-tight flex items-center gap-2">
            <ShoppingBag className="w-6 h-6 text-lime-400" />
            Order Management
          </h1>
          <p className="text-xs text-slate-400">
            Student checkout orders, applied coupons, and settlement timelines.
          </p>
        </div>

        <button
          onClick={fetchOrders}
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
            placeholder="Search by student, order #, or course..."
            className="w-full pl-10 pr-4 py-2 bg-slate-950 border border-slate-800 rounded-xl text-xs text-white placeholder-slate-400 focus:outline-none focus:border-lime-400"
          />
        </div>
      </div>

      <div className="bg-slate-900 border border-slate-800 rounded-3xl shadow-xl overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs text-slate-300">
            <thead className="bg-slate-950/80 uppercase text-[10px] text-slate-400 tracking-wider border-b border-slate-800">
              <tr>
                <th className="px-4 py-3.5">Order Number</th>
                <th className="px-4 py-3.5">Student</th>
                <th className="px-4 py-3.5">Course</th>
                <th className="px-4 py-3.5">Amount</th>
                <th className="px-4 py-3.5">Discount</th>
                <th className="px-4 py-3.5">Final Amount</th>
                <th className="px-4 py-3.5">Coupon</th>
                <th className="px-4 py-3.5">Status</th>
                <th className="px-4 py-3.5">Created Date</th>
                <th className="px-4 py-3.5 text-right">Settled Date</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-800/60">
              {loading && orders.length === 0 ? (
                <tr>
                  <td colSpan={10} className="px-4 py-12 text-center text-slate-400">
                    <RefreshCw className="w-6 h-6 animate-spin mx-auto text-lime-400 mb-2" />
                    Querying orders...
                  </td>
                </tr>
              ) : filtered.length === 0 ? (
                <tr>
                  <td colSpan={10} className="px-4 py-12 text-center text-slate-400">
                    No orders found.
                  </td>
                </tr>
              ) : (
                filtered.map((o) => (
                  <tr key={o.id} className="hover:bg-slate-800/40 transition-colors">
                    <td className="px-4 py-3.5 font-mono font-bold text-white">
                      {o.orderNumber}
                    </td>
                    <td className="px-4 py-3.5">
                      <div className="font-bold text-white">{o.userName}</div>
                      <div className="text-[10px] text-slate-400">{o.userEmail}</div>
                    </td>
                    <td className="px-4 py-3.5 font-semibold text-slate-200 max-w-xs truncate">
                      {o.courseName}
                    </td>
                    <td className="px-4 py-3.5 text-slate-400">₹{o.originalAmount}</td>
                    <td className="px-4 py-3.5 text-rose-400">₹{o.discountAmount || 0}</td>
                    <td className="px-4 py-3.5 font-bold text-lime-400">₹{o.finalAmount}</td>
                    <td className="px-4 py-3.5">
                      {o.couponCode ? (
                        <span className="text-[10px] font-bold px-2 py-0.5 rounded bg-lime-500/10 text-lime-400 border border-lime-500/20 font-mono">
                          {o.couponCode}
                        </span>
                      ) : (
                        <span className="text-slate-500">—</span>
                      )}
                    </td>
                    <td className="px-4 py-3.5">
                      <span
                        className={`text-[10px] font-bold px-2 py-0.5 rounded-full uppercase tracking-wider ${
                          o.status === 'PAID'
                            ? 'bg-emerald-500/10 text-emerald-400 border border-emerald-500/20'
                            : o.status === 'PENDING'
                            ? 'bg-amber-500/10 text-amber-400 border border-amber-500/20'
                            : 'bg-rose-500/10 text-rose-400 border border-rose-500/20'
                        }`}
                      >
                        {o.status}
                      </span>
                    </td>
                    <td className="px-4 py-3.5 text-slate-400 text-[11px]">
                      {new Date(o.createdDate).toLocaleDateString()}
                    </td>
                    <td className="px-4 py-3.5 text-right text-slate-400 text-[11px]">
                      {o.paidDate ? new Date(o.paidDate).toLocaleDateString() : '—'}
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
