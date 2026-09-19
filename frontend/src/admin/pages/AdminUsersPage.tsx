import React, { useState, useEffect } from 'react';
import {
  getAdminUsers,
  updateAdminUserStatus,
  updateAdminUserRole,
  deleteOrDeactivateAdminUser,
} from '../../api/adminApi';
import {
  Users,
  Search,
  CheckCircle2,
  XCircle,
  Eye,
  Edit2,
  Trash2,
  Shield,
  CreditCard,
  GraduationCap,
  RefreshCw,
  MoreHorizontal,
  Phone,
} from 'lucide-react';

export interface AdminUsersPageProps {
  onNavigate: (path: string) => void;
  onShowToast?: (msg: string) => void;
}

export const AdminUsersPage: React.FC<AdminUsersPageProps> = ({
  onNavigate,
  onShowToast,
}) => {
  const [users, setUsers] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');
  const [roleFilter, setRoleFilter] = useState('ALL');
  const [editingRoleUser, setEditingRoleUser] = useState<any>(null);
  const [newRole, setNewRole] = useState('');

  const fetchUsers = async () => {
    setLoading(true);
    try {
      const data = await getAdminUsers(searchQuery);
      setUsers(data);
    } catch (err) {
      console.error('Failed to fetch users', err);
      if (onShowToast) onShowToast('Failed to load user directory.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUsers();
  }, [searchQuery]);

  const handleToggleStatus = async (user: any) => {
    const nextStatus = !user.active;
    try {
      await updateAdminUserStatus(user.id, nextStatus);
      setUsers(users.map((u) => (u.id === user.id ? { ...u, active: nextStatus, status: nextStatus ? 'ACTIVE' : 'INACTIVE' } : u)));
      if (onShowToast) onShowToast(`User ${user.name} is now ${nextStatus ? 'Activated' : 'Deactivated'}.`);
    } catch {
      if (onShowToast) onShowToast('Failed to update user status.');
    }
  };

  const handleSaveRole = async () => {
    if (!editingRoleUser || !newRole) return;
    try {
      await updateAdminUserRole(editingRoleUser.id, newRole);
      setUsers(users.map((u) => (u.id === editingRoleUser.id ? { ...u, role: newRole } : u)));
      if (onShowToast) onShowToast(`Updated role for ${editingRoleUser.name} to ${newRole}`);
      setEditingRoleUser(null);
    } catch {
      if (onShowToast) onShowToast('Failed to update role.');
    }
  };

  const handleDelete = async (user: any) => {
    if (!window.confirm(`Are you sure you want to remove or deactivate user ${user.name}? Active learning and payment histories will be preserved.`)) {
      return;
    }
    try {
      const res = await deleteOrDeactivateAdminUser(user.id);
      if (onShowToast) onShowToast(res.message || 'Operation completed.');
      fetchUsers();
    } catch {
      if (onShowToast) onShowToast('Failed to process user action.');
    }
  };

  const filteredUsers = users.filter((u) => {
    if (roleFilter !== 'ALL' && u.role !== roleFilter) return false;
    return true;
  });

  return (
    <div className="space-y-6 animate-fade-in">
      {/* Page Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-white tracking-tight flex items-center gap-2">
            <Users className="w-6 h-6 text-lime-400" />
            User Management
          </h1>
          <p className="text-xs text-slate-400">
            Inspect all registered platform learners, roles, and administrative statuses.
          </p>
        </div>

        <button
          onClick={fetchUsers}
          className="flex items-center gap-2 px-3.5 py-2 bg-slate-800 hover:bg-slate-700 text-slate-200 text-xs font-semibold rounded-xl border border-slate-700 transition-colors cursor-pointer self-start sm:self-auto"
        >
          <RefreshCw className={`w-3.5 h-3.5 ${loading ? 'animate-spin text-lime-400' : ''}`} />
          <span>Refresh List</span>
        </button>
      </div>

      {/* Filter & Search Bar */}
      <div className="p-4 bg-slate-900 border border-slate-800 rounded-2xl flex flex-col sm:flex-row gap-3 items-center justify-between shadow-lg">
        <div className="relative w-full sm:w-80">
          <Search className="w-4 h-4 text-slate-400 absolute left-3.5 top-3" />
          <input
            type="text"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            placeholder="Search by name or email..."
            className="w-full pl-10 pr-4 py-2 bg-slate-950 border border-slate-800 rounded-xl text-xs text-white placeholder-slate-400 focus:outline-none focus:border-lime-400"
          />
        </div>

        {/* Role Filter tabs */}
        <div className="flex items-center gap-1.5 self-start sm:self-auto bg-slate-950 p-1 rounded-xl border border-slate-800 overflow-x-auto max-w-full">
          {['ALL', 'STUDENT', 'LEARNER', 'ADMIN', 'EMPLOYER'].map((r) => (
            <button
              key={r}
              onClick={() => setRoleFilter(r)}
              className={`px-3 py-1 rounded-lg text-[10px] font-bold tracking-wider uppercase transition-all cursor-pointer ${
                roleFilter === r
                  ? 'bg-lime-500 text-slate-950 shadow-sm'
                  : 'text-slate-400 hover:text-white'
              }`}
            >
              {r}
            </button>
          ))}
        </div>
      </div>

      {/* Users Table */}
      <div className="bg-slate-900 border border-slate-800 rounded-3xl shadow-xl overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs text-slate-300">
            <thead className="bg-slate-950/80 uppercase text-[10px] text-slate-400 tracking-wider border-b border-slate-800">
              <tr>
                <th className="px-4 py-3.5">Name</th>
                <th className="px-4 py-3.5">Email</th>
                <th className="px-4 py-3.5">Role</th>
                <th className="px-4 py-3.5">Registration</th>
                <th className="px-4 py-3.5">Last Login</th>
                <th className="px-4 py-3.5 text-center">Courses</th>
                <th className="px-4 py-3.5 text-center">Payments</th>
                <th className="px-4 py-3.5">Total Spent</th>
                <th className="px-4 py-3.5">Status</th>
                <th className="px-4 py-3.5 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-800/60">
              {loading && users.length === 0 ? (
                <tr>
                  <td colSpan={10} className="px-4 py-12 text-center text-slate-400">
                    <RefreshCw className="w-6 h-6 animate-spin mx-auto text-lime-400 mb-2" />
                    Querying PostgreSQL user records...
                  </td>
                </tr>
              ) : filteredUsers.length === 0 ? (
                <tr>
                  <td colSpan={10} className="px-4 py-12 text-center text-slate-400">
                    No users matching criteria.
                  </td>
                </tr>
              ) : (
                filteredUsers.map((u) => (
                  <tr key={u.id} className="hover:bg-slate-800/40 transition-colors">
                    <td className="px-4 py-3.5 font-bold text-white flex items-center gap-2">
                      <div className="w-7 h-7 rounded-lg bg-slate-800 flex items-center justify-center text-lime-400 font-semibold text-xs shrink-0">
                        {u.name ? u.name.charAt(0).toUpperCase() : 'U'}
                      </div>
                      <span className="truncate max-w-[130px]">{u.name}</span>
                    </td>
                    <td className="px-4 py-3.5 text-slate-400 font-mono text-[11px] truncate max-w-[180px]">
                      {u.email}
                    </td>
                    <td className="px-4 py-3.5">
                      <span
                        className={`text-[10px] px-2 py-0.5 rounded-full font-bold uppercase tracking-wider ${
                          u.role === 'ADMIN'
                            ? 'bg-purple-500/10 text-purple-400 border border-purple-500/30'
                            : u.role === 'EMPLOYER'
                            ? 'bg-amber-500/10 text-amber-400 border border-amber-500/30'
                            : 'bg-lime-500/10 text-lime-400 border border-lime-500/30'
                        }`}
                      >
                        {u.role}
                      </span>
                    </td>
                    <td className="px-4 py-3.5 text-slate-400 text-[11px]">
                      {u.registrationDate ? new Date(u.registrationDate).toLocaleDateString() : '—'}
                    </td>
                    <td className="px-4 py-3.5 text-slate-400 text-[11px]">
                      {u.lastLogin ? new Date(u.lastLogin).toLocaleDateString() : '—'}
                    </td>
                    <td className="px-4 py-3.5 text-center">
                      <span className="font-bold text-white">{u.enrolledCourses || 0}</span>
                      <span className="text-slate-400 text-[10px]"> ({u.completedCourses || 0} done)</span>
                    </td>
                    <td className="px-4 py-3.5 text-center font-bold text-slate-200">
                      {u.paymentCount || 0}
                    </td>
                    <td className="px-4 py-3.5 font-bold text-lime-400">
                      ₹{Number(u.totalSpent || 0).toLocaleString()}
                    </td>
                    <td className="px-4 py-3.5">
                      <span
                        className={`inline-flex items-center gap-1 text-[10px] font-bold px-2 py-0.5 rounded-full ${
                          u.active
                            ? 'bg-emerald-500/10 text-emerald-400 border border-emerald-500/20'
                            : 'bg-rose-500/10 text-rose-400 border border-rose-500/20'
                        }`}
                      >
                        {u.active ? <CheckCircle2 className="w-2.5 h-2.5" /> : <XCircle className="w-2.5 h-2.5" />}
                        {u.status}
                      </span>
                    </td>
                    <td className="px-4 py-3.5 text-right space-x-1.5 whitespace-nowrap">
                      {/* View details */}
                      <button
                        onClick={() => onNavigate(`/admin/users/${u.id}`)}
                        className="p-1.5 rounded-lg bg-slate-800 hover:bg-slate-700 text-slate-300 hover:text-white transition-colors cursor-pointer"
                        title="View Complete Dossier"
                      >
                        <Eye className="w-3.5 h-3.5" />
                      </button>

                      {/* Edit Role */}
                      <button
                        onClick={() => {
                          setEditingRoleUser(u);
                          setNewRole(u.role);
                        }}
                        className="p-1.5 rounded-lg bg-slate-800 hover:bg-slate-700 text-slate-300 hover:text-white transition-colors cursor-pointer"
                        title="Edit Role"
                      >
                        <Edit2 className="w-3.5 h-3.5" />
                      </button>

                      {/* Toggle status */}
                      <button
                        onClick={() => handleToggleStatus(u)}
                        className={`p-1.5 rounded-lg transition-colors cursor-pointer ${
                          u.active
                            ? 'bg-rose-500/10 hover:bg-rose-500/20 text-rose-400'
                            : 'bg-emerald-500/10 hover:bg-emerald-500/20 text-emerald-400'
                        }`}
                        title={u.active ? 'Deactivate User' : 'Activate User'}
                      >
                        {u.active ? <XCircle className="w-3.5 h-3.5" /> : <CheckCircle2 className="w-3.5 h-3.5" />}
                      </button>

                      {/* Delete / Deactivate */}
                      <button
                        onClick={() => handleDelete(u)}
                        className="p-1.5 rounded-lg bg-slate-800 hover:bg-rose-500/20 text-slate-400 hover:text-rose-400 transition-colors cursor-pointer"
                        title="Remove User"
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

      {/* Edit Role Modal */}
      {editingRoleUser && (
        <div className="fixed inset-0 bg-slate-950/80 backdrop-blur-xs flex items-center justify-center p-4 z-50">
          <div className="bg-slate-900 border border-slate-800 rounded-3xl p-6 max-w-sm w-full space-y-4 shadow-2xl">
            <h3 className="text-base font-bold text-white">
              Edit User Role: {editingRoleUser.name}
            </h3>
            <p className="text-xs text-slate-400">
              Select new role authorization level for this account.
            </p>

            <select
              value={newRole}
              onChange={(e) => setNewRole(e.target.value)}
              className="w-full p-2.5 bg-slate-950 border border-slate-800 rounded-xl text-xs text-white focus:outline-none focus:border-lime-400"
            >
              <option value="STUDENT">STUDENT</option>
              <option value="LEARNER">LEARNER</option>
              <option value="ADMIN">ADMIN</option>
              <option value="EMPLOYER">EMPLOYER</option>
            </select>

            <div className="flex gap-2 justify-end pt-2">
              <button
                onClick={() => setEditingRoleUser(null)}
                className="px-4 py-2 bg-slate-800 hover:bg-slate-700 text-slate-300 rounded-xl text-xs font-semibold cursor-pointer"
              >
                Cancel
              </button>
              <button
                onClick={handleSaveRole}
                className="px-4 py-2 bg-lime-500 hover:bg-lime-400 text-slate-950 font-bold rounded-xl text-xs cursor-pointer shadow-md shadow-lime-500/20"
              >
                Save Role
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
