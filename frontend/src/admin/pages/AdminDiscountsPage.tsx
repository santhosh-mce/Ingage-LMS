import React, { useState, useEffect } from 'react';
import {
  getAdminDiscounts,
  createAdminDiscount,
  toggleAdminDiscountStatus,
  deleteAdminDiscount,
  getDiscountAnalytics,
} from '../../api/adminApi';
import {
  TicketPercent,
  Plus,
  Tag,
  Calendar,
  CheckCircle2,
  XCircle,
  Trash2,
  TrendingUp,
  IndianRupee,
  RefreshCw,
  Hash,
} from 'lucide-react';

export interface AdminDiscountsPageProps {
  onNavigate: (path: string) => void;
  onShowToast?: (msg: string) => void;
}

export const AdminDiscountsPage: React.FC<AdminDiscountsPageProps> = ({
  onNavigate,
  onShowToast,
}) => {
  const [discounts, setDiscounts] = useState<any[]>([]);
  const [analytics, setAnalytics] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);

  // Form State
  const [couponCode, setCouponCode] = useState('');
  const [discountType, setDiscountType] = useState<'PERCENTAGE' | 'FIXED_AMOUNT'>('PERCENTAGE');
  const [discountValue, setDiscountValue] = useState<number>(20);
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');
  const [usageLimit, setUsageLimit] = useState<number>(100);
  const [perUserLimit, setPerUserLimit] = useState<number>(1);
  const [minPurchaseAmount, setMinPurchaseAmount] = useState<number>(499);
  const [maxDiscount, setMaxDiscount] = useState<number>(2000);
  const [active, setActive] = useState(true);

  const fetchData = async () => {
    setLoading(true);
    try {
      const [disc, anal] = await Promise.all([
        getAdminDiscounts(),
        getDiscountAnalytics(),
      ]);
      setDiscounts(disc);
      setAnalytics(anal);
    } catch (err) {
      console.error(err);
      if (onShowToast) onShowToast('Failed to load discount coupons.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleCreateCoupon = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!couponCode.trim()) {
      alert('Please enter a coupon code.');
      return;
    }

    try {
      await createAdminDiscount({
        couponCode: couponCode.trim().toUpperCase(),
        discountType,
        discountValue,
        startDate: startDate ? new Date(startDate).toISOString() : new Date().toISOString(),
        endDate: endDate ? new Date(endDate).toISOString() : null,
        usageLimit,
        perUserLimit,
        minPurchaseAmount,
        maxDiscount: discountType === 'PERCENTAGE' ? maxDiscount : null,
        active,
      });

      if (onShowToast) onShowToast(`Coupon ${couponCode.toUpperCase()} created successfully!`);
      setIsCreateModalOpen(false);
      setCouponCode('');
      fetchData();
    } catch (err: any) {
      alert(err.response?.data?.message || 'Failed to create coupon.');
    }
  };

  const handleToggleStatus = async (d: any) => {
    try {
      await toggleAdminDiscountStatus(d.id, !d.active);
      setDiscounts(
        discounts.map((item) =>
          item.id === d.id ? { ...item, active: !d.active, status: !d.active ? 'ACTIVE' : 'INACTIVE' } : item
        )
      );
      if (onShowToast) onShowToast(`Coupon ${d.couponCode} is now ${!d.active ? 'Active' : 'Inactive'}`);
    } catch {
      if (onShowToast) onShowToast('Failed to update status.');
    }
  };

  const handleDelete = async (d: any) => {
    if (!window.confirm(`Delete coupon ${d.couponCode}?`)) return;
    try {
      await deleteAdminDiscount(d.id);
      setDiscounts(discounts.filter((item) => item.id !== d.id));
      if (onShowToast) onShowToast('Coupon deleted.');
    } catch {
      if (onShowToast) onShowToast('Failed to delete coupon.');
    }
  };

  return (
    <div className="space-y-8 animate-fade-in">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-white tracking-tight flex items-center gap-2">
            <TicketPercent className="w-6 h-6 text-lime-400" />
            Discount & Promotional Coupon Engine
          </h1>
          <p className="text-xs text-slate-400">
            Create coupon codes, enforce usage caps, min cart rules, and view real-time redemption ROI.
          </p>
        </div>

        <div className="flex items-center gap-3">
          <button
            onClick={fetchData}
            className="p-2.5 bg-slate-800 hover:bg-slate-700 text-slate-300 rounded-xl border border-slate-700 transition-colors cursor-pointer"
            title="Refresh Discounts"
          >
            <RefreshCw className={`w-4 h-4 ${loading ? 'animate-spin text-lime-400' : ''}`} />
          </button>

          <button
            onClick={() => setIsCreateModalOpen(true)}
            className="flex items-center gap-2 px-4 py-2.5 bg-lime-500 hover:bg-lime-400 text-slate-950 font-bold text-xs rounded-xl transition-all shadow-lg shadow-lime-500/20 cursor-pointer"
          >
            <Plus className="w-4 h-4" />
            <span>Create Coupon</span>
          </button>
        </div>
      </div>

      {/* Analytics KPI Row */}
      <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-6 gap-3 sm:gap-4">
        <div className="p-4 bg-slate-900 border border-slate-800 rounded-2xl shadow-lg space-y-1">
          <span className="text-[11px] text-slate-400 font-medium">Total Coupons</span>
          <p className="text-xl font-bold text-white">{analytics?.totalCoupons || 0}</p>
        </div>
        <div className="p-4 bg-slate-900 border border-emerald-500/20 bg-emerald-500/5 rounded-2xl shadow-lg space-y-1">
          <span className="text-[11px] text-emerald-400 font-medium">Active Coupons</span>
          <p className="text-xl font-bold text-emerald-400">{analytics?.activeCoupons || 0}</p>
        </div>
        <div className="p-4 bg-slate-900 border border-amber-500/20 bg-amber-500/5 rounded-2xl shadow-lg space-y-1">
          <span className="text-[11px] text-amber-400 font-medium">Expired Coupons</span>
          <p className="text-xl font-bold text-amber-400">{analytics?.expiredCoupons || 0}</p>
        </div>
        <div className="p-4 bg-slate-900 border border-slate-800 rounded-2xl shadow-lg space-y-1">
          <span className="text-[11px] text-slate-400 font-medium">Total Redemptions</span>
          <p className="text-xl font-bold text-cyan-400">{analytics?.totalUses || 0}</p>
        </div>
        <div className="p-4 bg-slate-900 border border-rose-500/20 bg-rose-500/5 rounded-2xl shadow-lg space-y-1">
          <span className="text-[11px] text-rose-400 font-medium">Discount Given</span>
          <p className="text-xl font-bold text-rose-400">₹{Number(analytics?.totalDiscountGiven || 0).toLocaleString()}</p>
        </div>
        <div className="p-4 bg-slate-900 border border-lime-500/20 bg-lime-500/5 rounded-2xl shadow-lg space-y-1">
          <span className="text-[11px] text-lime-400 font-medium">Revenue Driven</span>
          <p className="text-xl font-bold text-lime-400">₹{Number(analytics?.revenueGenerated || 0).toLocaleString()}</p>
        </div>
      </div>

      {/* Coupons Table */}
      <div className="bg-slate-900 border border-slate-800 rounded-3xl shadow-xl overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs text-slate-300">
            <thead className="bg-slate-950/80 uppercase text-[10px] text-slate-400 tracking-wider border-b border-slate-800">
              <tr>
                <th className="px-4 py-3.5">Coupon Code</th>
                <th className="px-4 py-3.5">Discount Value</th>
                <th className="px-4 py-3.5">Usage / Limit</th>
                <th className="px-4 py-3.5">Min Purchase</th>
                <th className="px-4 py-3.5">Validity</th>
                <th className="px-4 py-3.5">Discount Given</th>
                <th className="px-4 py-3.5">Revenue Driven</th>
                <th className="px-4 py-3.5">Status</th>
                <th className="px-4 py-3.5 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-800/60">
              {loading && discounts.length === 0 ? (
                <tr>
                  <td colSpan={9} className="px-4 py-12 text-center text-slate-400">
                    <RefreshCw className="w-6 h-6 animate-spin mx-auto text-lime-400 mb-2" />
                    Querying discounts from database...
                  </td>
                </tr>
              ) : discounts.length === 0 ? (
                <tr>
                  <td colSpan={9} className="px-4 py-12 text-center text-slate-400">
                    No discount coupons found. Click &quot;Create Coupon&quot; to add one.
                  </td>
                </tr>
              ) : (
                discounts.map((d) => (
                  <tr key={d.id} className="hover:bg-slate-800/40 transition-colors">
                    <td className="px-4 py-3.5 font-mono font-bold text-lime-400 text-sm">
                      {d.couponCode}
                    </td>
                    <td className="px-4 py-3.5 font-bold text-white">
                      {d.discountType === 'PERCENTAGE' ? `${d.discountValue}% OFF` : `₹${d.discountValue} FLAT`}
                      {d.maxDiscount && (
                        <span className="block text-[10px] text-slate-400 font-normal">
                          Max: ₹{d.maxDiscount}
                        </span>
                      )}
                    </td>
                    <td className="px-4 py-3.5">
                      <span className="font-bold text-white">{d.usedCount}</span>
                      <span className="text-slate-400"> / {d.usageLimit || '∞'}</span>
                    </td>
                    <td className="px-4 py-3.5 text-slate-300">
                      ₹{d.minPurchaseAmount || 0}
                    </td>
                    <td className="px-4 py-3.5 text-slate-400 text-[11px]">
                      {d.endDate ? (
                        <span>Until {new Date(d.endDate).toLocaleDateString()}</span>
                      ) : (
                        <span className="text-emerald-400 font-medium">No Expiration</span>
                      )}
                    </td>
                    <td className="px-4 py-3.5 font-bold text-rose-400">
                      ₹{Number(d.totalDiscountGiven || 0).toLocaleString()}
                    </td>
                    <td className="px-4 py-3.5 font-bold text-lime-400">
                      ₹{Number(d.revenueGenerated || 0).toLocaleString()}
                    </td>
                    <td className="px-4 py-3.5">
                      <span
                        className={`text-[10px] font-bold px-2 py-0.5 rounded-full uppercase tracking-wider ${
                          d.status === 'ACTIVE'
                            ? 'bg-emerald-500/10 text-emerald-400 border border-emerald-500/20'
                            : d.status === 'EXPIRED'
                            ? 'bg-amber-500/10 text-amber-400 border border-amber-500/20'
                            : 'bg-rose-500/10 text-rose-400 border border-rose-500/20'
                        }`}
                      >
                        {d.status}
                      </span>
                    </td>
                    <td className="px-4 py-3.5 text-right space-x-2 whitespace-nowrap">
                      <button
                        onClick={() => handleToggleStatus(d)}
                        className={`p-1.5 rounded-lg transition-colors cursor-pointer ${
                          d.active
                            ? 'bg-rose-500/10 hover:bg-rose-500/20 text-rose-400'
                            : 'bg-emerald-500/10 hover:bg-emerald-500/20 text-emerald-400'
                        }`}
                        title={d.active ? 'Deactivate Coupon' : 'Activate Coupon'}
                      >
                        {d.active ? <XCircle className="w-3.5 h-3.5" /> : <CheckCircle2 className="w-3.5 h-3.5" />}
                      </button>

                      <button
                        onClick={() => handleDelete(d)}
                        className="p-1.5 rounded-lg bg-slate-800 hover:bg-rose-500/20 text-slate-400 hover:text-rose-400 transition-colors cursor-pointer"
                        title="Delete Coupon"
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

      {/* Modal: Create Coupon */}
      {isCreateModalOpen && (
        <div className="fixed inset-0 bg-slate-950/80 backdrop-blur-xs flex items-center justify-center p-4 z-50">
          <form
            onSubmit={handleCreateCoupon}
            className="bg-slate-900 border border-slate-800 rounded-3xl p-6 sm:p-8 max-w-lg w-full space-y-4 shadow-2xl max-h-[90vh] overflow-y-auto"
          >
            <div className="flex items-center justify-between border-b border-slate-800 pb-3">
              <h3 className="text-base font-bold text-white flex items-center gap-2">
                <TicketPercent className="w-5 h-5 text-lime-400" />
                Create New Promotional Discount
              </h3>
            </div>

            <div className="space-y-3">
              <div>
                <label className="text-xs font-semibold text-slate-300">Coupon Code *</label>
                <input
                  type="text"
                  required
                  value={couponCode}
                  onChange={(e) => setCouponCode(e.target.value.toUpperCase())}
                  placeholder="e.g. SUMMER50, WELCOME20"
                  className="w-full p-2.5 bg-slate-950 border border-slate-800 rounded-xl text-xs text-white focus:outline-none focus:border-lime-400 uppercase font-mono font-bold mt-1"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="text-xs font-semibold text-slate-300">Discount Type</label>
                  <select
                    value={discountType}
                    onChange={(e: any) => setDiscountType(e.target.value)}
                    className="w-full p-2.5 bg-slate-950 border border-slate-800 rounded-xl text-xs text-white focus:outline-none focus:border-lime-400 mt-1"
                  >
                    <option value="PERCENTAGE">PERCENTAGE (%)</option>
                    <option value="FIXED_AMOUNT">FIXED AMOUNT (₹)</option>
                  </select>
                </div>

                <div>
                  <label className="text-xs font-semibold text-slate-300">Discount Value *</label>
                  <input
                    type="number"
                    required
                    min={1}
                    value={discountValue}
                    onChange={(e) => setDiscountValue(Number(e.target.value))}
                    className="w-full p-2.5 bg-slate-950 border border-slate-800 rounded-xl text-xs text-white focus:outline-none focus:border-lime-400 font-bold mt-1"
                  />
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="text-xs font-semibold text-slate-300">Start Date</label>
                  <input
                    type="date"
                    value={startDate}
                    onChange={(e) => setStartDate(e.target.value)}
                    className="w-full p-2.5 bg-slate-950 border border-slate-800 rounded-xl text-xs text-white focus:outline-none focus:border-lime-400 mt-1"
                  />
                </div>

                <div>
                  <label className="text-xs font-semibold text-slate-300">End / Expiration Date</label>
                  <input
                    type="date"
                    value={endDate}
                    onChange={(e) => setEndDate(e.target.value)}
                    className="w-full p-2.5 bg-slate-950 border border-slate-800 rounded-xl text-xs text-white focus:outline-none focus:border-lime-400 mt-1"
                  />
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="text-xs font-semibold text-slate-300">Total Usage Limit</label>
                  <input
                    type="number"
                    min={1}
                    value={usageLimit}
                    onChange={(e) => setUsageLimit(Number(e.target.value))}
                    className="w-full p-2.5 bg-slate-950 border border-slate-800 rounded-xl text-xs text-white focus:outline-none focus:border-lime-400 mt-1"
                  />
                </div>

                <div>
                  <label className="text-xs font-semibold text-slate-300">Per-User Limit</label>
                  <input
                    type="number"
                    min={1}
                    value={perUserLimit}
                    onChange={(e) => setPerUserLimit(Number(e.target.value))}
                    className="w-full p-2.5 bg-slate-950 border border-slate-800 rounded-xl text-xs text-white focus:outline-none focus:border-lime-400 mt-1"
                  />
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="text-xs font-semibold text-slate-300">Min Purchase Amount (₹)</label>
                  <input
                    type="number"
                    min={0}
                    value={minPurchaseAmount}
                    onChange={(e) => setMinPurchaseAmount(Number(e.target.value))}
                    className="w-full p-2.5 bg-slate-950 border border-slate-800 rounded-xl text-xs text-white focus:outline-none focus:border-lime-400 mt-1"
                  />
                </div>

                {discountType === 'PERCENTAGE' && (
                  <div>
                    <label className="text-xs font-semibold text-slate-300">Max Discount Cap (₹)</label>
                    <input
                      type="number"
                      min={0}
                      value={maxDiscount}
                      onChange={(e) => setMaxDiscount(Number(e.target.value))}
                      className="w-full p-2.5 bg-slate-950 border border-slate-800 rounded-xl text-xs text-white focus:outline-none focus:border-lime-400 mt-1"
                    />
                  </div>
                )}
              </div>

              <label className="flex items-center gap-2 text-xs font-semibold text-slate-300 cursor-pointer pt-1">
                <input
                  type="checkbox"
                  checked={active}
                  onChange={(e) => setActive(e.target.checked)}
                  className="w-4 h-4 rounded text-lime-500 bg-slate-950 border-slate-700"
                />
                <span>Set as Active immediately</span>
              </label>
            </div>

            <div className="flex justify-end gap-2 pt-4 border-t border-slate-800">
              <button
                type="button"
                onClick={() => setIsCreateModalOpen(false)}
                className="px-4 py-2 bg-slate-800 hover:bg-slate-700 text-slate-300 rounded-xl text-xs font-semibold cursor-pointer"
              >
                Cancel
              </button>
              <button
                type="submit"
                className="px-5 py-2 bg-lime-500 hover:bg-lime-400 text-slate-950 font-bold rounded-xl text-xs cursor-pointer shadow-md shadow-lime-500/20"
              >
                Create Coupon
              </button>
            </div>
          </form>
        </div>
      )}
    </div>
  );
};
