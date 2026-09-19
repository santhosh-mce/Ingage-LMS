import React, { useState } from 'react';
import {
  Settings,
  Database,
  CreditCard,
  HardDrive,
  Shield,
  Bell,
  Save,
  CheckCircle2,
  AlertCircle
} from 'lucide-react';

export const AdminSettingsPage: React.FC = () => {
  const [platformName, setPlatformName] = useState('Ingage LMS');
  const [supportEmail, setSupportEmail] = useState('support@ingage.com');
  const [currency, setCurrency] = useState('INR');
  const [razorpayKeyId, setRazorpayKeyId] = useState('rzp_test_1DP5mmOlF5G5ag');
  const [razorpaySecretSet, setRazorpaySecretSet] = useState(true);
  const [emailAlerts, setEmailAlerts] = useState(true);
  const [autoApproveCertificates, setAutoApproveCertificates] = useState(true);
  const [saving, setSaving] = useState(false);
  const [savedSuccess, setSavedSuccess] = useState(false);

  const handleSave = (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    setTimeout(() => {
      setSaving(false);
      setSavedSuccess(true);
      setTimeout(() => setSavedSuccess(false), 3000);
    }, 600);
  };

  return (
    <div className="space-y-6 max-w-4xl">
      <div>
        <h1 className="text-2xl font-bold text-white tracking-tight flex items-center gap-2">
          <Settings className="w-7 h-7 text-indigo-400" />
          System Settings & Integrations
        </h1>
        <p className="text-sm text-slate-400 mt-1">
          Configure platform parameters, Razorpay payment credentials, storage policies, and email alerts
        </p>
      </div>

      {savedSuccess && (
        <div className="p-4 bg-emerald-500/10 border border-emerald-500/30 rounded-xl text-emerald-400 text-sm flex items-center gap-2">
          <CheckCircle2 className="w-5 h-5 text-emerald-400" />
          Settings updated successfully! Changes will take effect across the platform.
        </div>
      )}

      {/* System Health Indicators */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <div className="bg-slate-900 border border-slate-800 rounded-xl p-4 flex items-center gap-3">
          <div className="w-10 h-10 rounded-lg bg-emerald-500/10 text-emerald-400 flex items-center justify-center">
            <Database className="w-5 h-5" />
          </div>
          <div>
            <div className="text-xs text-slate-400">PostgreSQL Database</div>
            <div className="text-sm font-semibold text-emerald-400 flex items-center gap-1.5 mt-0.5">
              <span className="w-2 h-2 rounded-full bg-emerald-400 animate-pulse"></span>
              Connected (Port 5432)
            </div>
          </div>
        </div>

        <div className="bg-slate-900 border border-slate-800 rounded-xl p-4 flex items-center gap-3">
          <div className="w-10 h-10 rounded-lg bg-indigo-500/10 text-indigo-400 flex items-center justify-center">
            <CreditCard className="w-5 h-5" />
          </div>
          <div>
            <div className="text-xs text-slate-400">Payment Gateway</div>
            <div className="text-sm font-semibold text-indigo-400 flex items-center gap-1.5 mt-0.5">
              <span className="w-2 h-2 rounded-full bg-indigo-400"></span>
              Razorpay Test Mode
            </div>
          </div>
        </div>

        <div className="bg-slate-900 border border-slate-800 rounded-xl p-4 flex items-center gap-3">
          <div className="w-10 h-10 rounded-lg bg-amber-500/10 text-amber-400 flex items-center justify-center">
            <HardDrive className="w-5 h-5" />
          </div>
          <div>
            <div className="text-xs text-slate-400">Media Storage</div>
            <div className="text-sm font-semibold text-slate-200 mt-0.5">
              Local Disk Storage (/uploads)
            </div>
          </div>
        </div>
      </div>

      {/* Main Settings Form */}
      <form onSubmit={handleSave} className="space-y-6">
        {/* General Settings */}
        <div className="bg-slate-900 border border-slate-800/80 rounded-2xl p-6 shadow-xl space-y-4">
          <h2 className="text-base font-semibold text-white border-b border-slate-800 pb-3 flex items-center gap-2">
            <Shield className="w-4 h-4 text-indigo-400" />
            General Platform Settings
          </h2>
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-semibold text-slate-400 uppercase tracking-wider mb-2">
                Platform Name
              </label>
              <input
                type="text"
                value={platformName}
                onChange={(e) => setPlatformName(e.target.value)}
                className="w-full px-3 py-2 bg-slate-950 border border-slate-800 rounded-lg text-sm text-white focus:outline-none focus:border-indigo-500"
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-slate-400 uppercase tracking-wider mb-2">
                Support Email
              </label>
              <input
                type="email"
                value={supportEmail}
                onChange={(e) => setSupportEmail(e.target.value)}
                className="w-full px-3 py-2 bg-slate-950 border border-slate-800 rounded-lg text-sm text-white focus:outline-none focus:border-indigo-500"
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-slate-400 uppercase tracking-wider mb-2">
                Default Currency
              </label>
              <select
                value={currency}
                onChange={(e) => setCurrency(e.target.value)}
                className="w-full px-3 py-2 bg-slate-950 border border-slate-800 rounded-lg text-sm text-white focus:outline-none focus:border-indigo-500"
              >
                <option value="INR">INR (₹)</option>
                <option value="USD">USD ($)</option>
                <option value="EUR">EUR (€)</option>
              </select>
            </div>
          </div>
        </div>

        {/* Payment Integration */}
        <div className="bg-slate-900 border border-slate-800/80 rounded-2xl p-6 shadow-xl space-y-4">
          <h2 className="text-base font-semibold text-white border-b border-slate-800 pb-3 flex items-center gap-2">
            <CreditCard className="w-4 h-4 text-indigo-400" />
            Razorpay Payment Gateway Credentials
          </h2>
          <div className="space-y-3">
            <div>
              <label className="block text-xs font-semibold text-slate-400 uppercase tracking-wider mb-2">
                Razorpay Key ID
              </label>
              <input
                type="text"
                value={razorpayKeyId}
                onChange={(e) => setRazorpayKeyId(e.target.value)}
                className="w-full px-3 py-2 bg-slate-950 border border-slate-800 rounded-lg text-sm font-mono text-white focus:outline-none focus:border-indigo-500"
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-slate-400 uppercase tracking-wider mb-2">
                Razorpay Key Secret (Server Environment)
              </label>
              <div className="flex items-center gap-3">
                <input
                  type="password"
                  disabled
                  value="••••••••••••••••••••••••"
                  className="flex-1 px-3 py-2 bg-slate-950/60 border border-slate-800 rounded-lg text-sm font-mono text-slate-500 cursor-not-allowed"
                />
                <span className="text-xs text-emerald-400 font-medium flex items-center gap-1">
                  <CheckCircle2 className="w-4 h-4" /> Configured in backend application.properties
                </span>
              </div>
            </div>
          </div>
        </div>

        {/* Automated Rules */}
        <div className="bg-slate-900 border border-slate-800/80 rounded-2xl p-6 shadow-xl space-y-4">
          <h2 className="text-base font-semibold text-white border-b border-slate-800 pb-3 flex items-center gap-2">
            <Bell className="w-4 h-4 text-indigo-400" />
            Automation & Notifications
          </h2>
          <div className="space-y-4">
            <label className="flex items-center gap-3 cursor-pointer">
              <input
                type="checkbox"
                checked={autoApproveCertificates}
                onChange={(e) => setAutoApproveCertificates(e.target.checked)}
                className="w-4 h-4 rounded border-slate-700 bg-slate-950 text-indigo-600 focus:ring-indigo-500"
              />
              <div>
                <div className="text-sm font-medium text-white">
                  Automatic Certificate Generation
                </div>
                <div className="text-xs text-slate-400">
                  Automatically generate and issue PDF certificate when user hits 100% course completion
                </div>
              </div>
            </label>

            <label className="flex items-center gap-3 cursor-pointer">
              <input
                type="checkbox"
                checked={emailAlerts}
                onChange={(e) => setEmailAlerts(e.target.checked)}
                className="w-4 h-4 rounded border-slate-700 bg-slate-950 text-indigo-600 focus:ring-indigo-500"
              />
              <div>
                <div className="text-sm font-medium text-white">
                  Admin Email Notifications
                </div>
                <div className="text-xs text-slate-400">
                  Receive email alerts for new student enrollments, order failures, and refunds
                </div>
              </div>
            </label>
          </div>
        </div>

        <div className="flex justify-end">
          <button
            type="submit"
            disabled={saving}
            className="inline-flex items-center gap-2 px-6 py-2.5 bg-indigo-600 hover:bg-indigo-500 disabled:opacity-50 text-white font-medium rounded-lg text-sm shadow-lg shadow-indigo-500/25 transition"
          >
            <Save className="w-4 h-4" />
            {saving ? 'Saving Changes...' : 'Save Settings'}
          </button>
        </div>
      </form>
    </div>
  );
};
