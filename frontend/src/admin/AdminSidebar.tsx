import React from 'react';
import {
  LayoutDashboard,
  Users,
  BookOpen,
  PlusCircle,
  FolderTree,
  Briefcase,
  Layers,
  Sparkles,
  CreditCard,
  ShoppingBag,
  TicketPercent,
  TrendingUp,
  Award,
  Bell,
  Settings,
  History,
  ChevronLeft,
  ChevronRight,
  ExternalLink,
  ShieldCheck,
  X,
} from 'lucide-react';

export interface AdminSidebarProps {
  currentPath: string;
  onNavigate: (path: string) => void;
  collapsed: boolean;
  onToggleCollapse: () => void;
  mobileOpen: boolean;
  onCloseMobile: () => void;
}

interface NavItem {
  name: string;
  path: string;
  icon: React.ElementType;
  badge?: string;
}

export const ADMIN_NAV_ITEMS: NavItem[] = [
  { name: 'Dashboard', path: '/admin', icon: LayoutDashboard },
  { name: 'Users', path: '/admin/users', icon: Users },
  { name: 'Courses', path: '/admin/courses', icon: BookOpen },
  { name: 'Add Course', path: '/admin/courses/new', icon: PlusCircle },
  { name: 'Categories', path: '/admin/categories', icon: FolderTree },
  { name: 'Careers', path: '/admin/careers', icon: Briefcase },
  { name: 'Projects', path: '/admin/projects', icon: Layers },
  { name: 'Job Opportunities', path: '/admin/opportunities', icon: Sparkles },
  { name: 'Payments', path: '/admin/payments', icon: CreditCard },
  { name: 'Orders', path: '/admin/orders', icon: ShoppingBag },
  { name: 'Discounts', path: '/admin/discounts', icon: TicketPercent },
  { name: 'Course Progress', path: '/admin/progress', icon: TrendingUp },
  { name: 'Certificates', path: '/admin/certificates', icon: Award },
  { name: 'Notifications', path: '/admin/notifications', icon: Bell },
  { name: 'Activity Logs', path: '/admin/activity', icon: History },
  { name: 'Settings', path: '/admin/settings', icon: Settings },
];

export const AdminSidebar: React.FC<AdminSidebarProps> = ({
  currentPath,
  onNavigate,
  collapsed,
  onToggleCollapse,
  mobileOpen,
  onCloseMobile,
}) => {
  const isItemActive = (path: string) => {
    if (path === '/admin') return currentPath === '/admin';
    return currentPath.startsWith(path);
  };

  const sidebarContent = (
    <div className="flex flex-col h-full bg-slate-900 text-slate-300 border-r border-slate-800 select-none">
      {/* Brand Header */}
      <div className="h-16 flex items-center justify-between px-4 border-b border-slate-800 shrink-0">
        <div className="flex items-center gap-3 overflow-hidden">
          <div className="w-9 h-9 rounded-xl bg-lime-500/10 border border-lime-500/30 flex items-center justify-center text-lime-400 shrink-0 font-bold text-lg shadow-sm">
            <ShieldCheck className="w-5 h-5" />
          </div>
          {!collapsed && (
            <div className="flex flex-col min-w-0">
              <span className="font-bold text-white text-base tracking-tight truncate flex items-center gap-1.5">
                Ingage <span className="text-lime-400 text-xs px-1.5 py-0.5 rounded bg-lime-500/10 border border-lime-500/20 uppercase font-semibold">Admin</span>
              </span>
              <span className="text-[11px] text-slate-400 truncate">LMS Management Suite</span>
            </div>
          )}
        </div>

        {/* Mobile close button */}
        <button
          onClick={onCloseMobile}
          className="md:hidden p-1.5 rounded-lg text-slate-400 hover:text-white hover:bg-slate-800 transition-colors"
          aria-label="Close sidebar"
        >
          <X className="w-5 h-5" />
        </button>
      </div>

      {/* Navigation List */}
      <div className="flex-1 overflow-y-auto px-3 py-4 space-y-1 scrollbar-thin scrollbar-thumb-slate-700">
        <div className={`px-2 mb-2 text-[10px] font-semibold text-slate-400 uppercase tracking-wider ${collapsed ? 'text-center' : ''}`}>
          {collapsed ? '•••' : 'Platform Operations'}
        </div>

        {ADMIN_NAV_ITEMS.map((item) => {
          const active = isItemActive(item.path);
          const Icon = item.icon;

          return (
            <button
              key={item.path}
              onClick={() => {
                onNavigate(item.path);
                onCloseMobile();
              }}
              title={collapsed ? item.name : undefined}
              className={`w-full flex items-center gap-3 px-3 py-2.5 rounded-xl text-sm font-medium transition-all group relative cursor-pointer ${
                active
                  ? 'bg-lime-500 text-slate-950 font-semibold shadow-md shadow-lime-500/20'
                  : 'text-slate-300 hover:text-white hover:bg-slate-800/80'
              }`}
            >
              <Icon className={`w-5 h-5 shrink-0 transition-transform group-hover:scale-110 ${active ? 'text-slate-950' : 'text-slate-400 group-hover:text-lime-400'}`} />
              
              {!collapsed && (
                <span className="truncate flex-1 text-left">{item.name}</span>
              )}

              {!collapsed && item.badge && (
                <span className={`text-[11px] px-2 py-0.5 rounded-full font-bold uppercase tracking-wider shrink-0 ${
                  active ? 'bg-slate-950 text-lime-400' : 'bg-slate-800 text-slate-300'
                }`}>
                  {item.badge}
                </span>
              )}

              {/* Collapsed tooltip badge on hover */}
              {collapsed && (
                <div className="hidden group-hover:block absolute left-full ml-3 px-2.5 py-1 bg-slate-800 text-white text-xs font-semibold rounded-md shadow-xl border border-slate-700 whitespace-nowrap z-50 pointer-events-none">
                  {item.name}
                </div>
              )}
            </button>
          );
        })}
      </div>

      {/* Footer Exit / Collapse Toggle */}
      <div className="p-3 border-t border-slate-800 space-y-2 shrink-0">
        <button
          onClick={() => onNavigate('/')}
          className={`w-full flex items-center gap-2.5 px-3 py-2 rounded-xl text-xs font-medium text-slate-400 hover:text-white hover:bg-slate-800 transition-colors cursor-pointer ${
            collapsed ? 'justify-center' : ''
          }`}
          title="Return to Learner View"
        >
          <ExternalLink className="w-4 h-4 shrink-0 text-slate-400" />
          {!collapsed && <span>Learner Portal</span>}
        </button>

        {/* Desktop Collapse Toggle */}
        <button
          onClick={onToggleCollapse}
          className="hidden md:flex w-full items-center justify-center p-2 rounded-xl text-slate-400 hover:text-white hover:bg-slate-800 transition-colors cursor-pointer"
          title={collapsed ? 'Expand Sidebar' : 'Collapse Sidebar'}
        >
          {collapsed ? <ChevronRight className="w-4 h-4" /> : <ChevronLeft className="w-4 h-4" />}
        </button>
      </div>
    </div>
  );

  return (
    <>
      {/* Desktop Fixed Sidebar */}
      <aside
        className={`hidden md:block fixed top-0 left-0 bottom-0 z-30 transition-all duration-300 ease-in-out ${
          collapsed ? 'w-20' : 'w-64'
        }`}
      >
        {sidebarContent}
      </aside>

      {/* Mobile Drawer Overlay */}
      {mobileOpen && (
        <div
          className="fixed inset-0 bg-slate-950/70 backdrop-blur-xs z-40 md:hidden animate-fade-in"
          onClick={onCloseMobile}
        />
      )}
      <aside
        className={`fixed top-0 left-0 bottom-0 z-50 w-72 transform transition-transform duration-300 ease-in-out md:hidden ${
          mobileOpen ? 'translate-x-0' : '-translate-x-full'
        }`}
      >
        {sidebarContent}
      </aside>
    </>
  );
};
