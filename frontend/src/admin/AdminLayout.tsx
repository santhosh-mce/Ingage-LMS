import React, { useState } from 'react';
import { AdminSidebar } from './AdminSidebar';
import { UserProfile } from '../types';
import {
  Menu,
  Bell,
  Search,
  LogOut,
  ExternalLink,
  ShieldCheck,
  Sparkles,
} from 'lucide-react';

export interface AdminLayoutProps {
  currentPath: string;
  onNavigate: (path: string) => void;
  currentUser: UserProfile | null;
  onLogout: () => void;
  children: React.ReactNode;
}

export const AdminLayout: React.FC<AdminLayoutProps> = ({
  currentPath,
  onNavigate,
  currentUser,
  onLogout,
  children,
}) => {
  const [collapsed, setCollapsed] = useState(false);
  const [mobileOpen, setMobileOpen] = useState(false);

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 flex flex-col font-sans selection:bg-lime-400 selection:text-slate-950">
      {/* Responsive Admin Sidebar */}
      <AdminSidebar
        currentPath={currentPath}
        onNavigate={onNavigate}
        collapsed={collapsed}
        onToggleCollapse={() => setCollapsed(!collapsed)}
        mobileOpen={mobileOpen}
        onCloseMobile={() => setMobileOpen(false)}
      />

      {/* Main Container */}
      <div
        className={`flex-1 flex flex-col transition-all duration-300 ease-in-out ${
          collapsed ? 'md:ml-20' : 'md:ml-64'
        }`}
      >
        {/* Top Bar Header */}
        <header className="sticky top-0 z-20 h-16 bg-slate-900/80 backdrop-blur-md border-b border-slate-800/80 px-4 sm:px-6 flex items-center justify-between gap-4">
          {/* Mobile Menu & Page Context */}
          <div className="flex items-center gap-3">
            <button
              onClick={() => setMobileOpen(true)}
              className="md:hidden p-2 rounded-xl text-slate-400 hover:text-white hover:bg-slate-800 transition-colors cursor-pointer"
              aria-label="Open navigation drawer"
            >
              <Menu className="w-5 h-5" />
            </button>

            <div className="hidden sm:flex items-center gap-2 text-xs text-slate-400">
              <span className="font-semibold text-slate-300">Admin Console</span>
              <span>/</span>
              <span className="text-lime-400 font-medium capitalize">
                {currentPath.replace('/admin', '').replace(/^\//, '').replace(/-/g, ' ') || 'Dashboard'}
              </span>
            </div>
          </div>

          {/* Quick Search & Actions */}
          <div className="flex items-center gap-3">
            {/* Quick Link to Learner Site */}
            <button
              onClick={() => onNavigate('/')}
              className="hidden lg:flex items-center gap-2 px-3 py-1.5 rounded-xl bg-slate-800/70 hover:bg-slate-800 text-xs font-medium text-slate-300 hover:text-white border border-slate-700/60 transition-all cursor-pointer"
            >
              <ExternalLink className="w-3.5 h-3.5 text-lime-400" />
              <span>Learner View</span>
            </button>

            {/* Notification Indicator */}
            <button
              onClick={() => onNavigate('/admin/notifications')}
              className="p-2 rounded-xl text-slate-400 hover:text-white hover:bg-slate-800 transition-colors relative cursor-pointer"
              title="Notifications"
            >
              <Bell className="w-4 h-4" />
              <span className="w-2 h-2 rounded-full bg-lime-400 absolute top-2 right-2 animate-pulse"></span>
            </button>

            <div className="h-6 w-px bg-slate-800 hidden sm:block"></div>

            {/* Admin Profile Pill */}
            <div className="flex items-center gap-3">
              <div className="flex items-center gap-2.5">
                <div className="w-8 h-8 rounded-xl bg-lime-500/20 border border-lime-500/40 flex items-center justify-center font-bold text-lime-400 text-xs">
                  {currentUser?.name ? currentUser.name.charAt(0).toUpperCase() : 'A'}
                </div>
                <div className="hidden md:flex flex-col">
                  <span className="text-xs font-semibold text-white leading-tight">
                    {currentUser?.name || 'Administrator'}
                  </span>
                  <span className="text-[10px] text-lime-400 font-medium">
                    {currentUser?.role || 'ADMIN'}
                  </span>
                </div>
              </div>

              {/* Logout button */}
              <button
                onClick={onLogout}
                className="p-2 rounded-xl text-slate-400 hover:text-rose-400 hover:bg-rose-500/10 transition-colors cursor-pointer"
                title="Sign Out"
              >
                <LogOut className="w-4 h-4" />
              </button>
            </div>
          </div>
        </header>

        {/* Dynamic Admin Page View */}
        <main className="flex-1 p-4 sm:p-6 lg:p-8 bg-slate-950 max-w-7xl w-full mx-auto">
          {children}
        </main>
      </div>
    </div>
  );
};
