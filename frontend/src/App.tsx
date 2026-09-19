/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import React, { useState, useEffect } from 'react';
import { Header } from './components/Header';
import { Footer } from './components/Footer';
import { ScreenNavigator } from './components/ScreenNavigator';
import { AuthModal } from './components/AuthModal';
import { ProtectedRoute } from './components/ProtectedRoute';
import { UserProfile, AuthMode } from './types';
import { getCurrentUser, logoutUser, getAccessibleImageUrl } from './api/authApi';
import { AuthProvider, useAuth } from './context/AuthContext';

// Protected Route Checker
export const isProtectedRoute = (path: string): boolean => {
  if (path.startsWith('/admin')) return true;
  if (path.startsWith('/profile')) return true;
  if (path === '/my-learning') return true;
  if (path === '/learning-roles' || path === '/my-learning-roles' || path === '/my-roles') return true;
  if (path.startsWith('/learn')) return true;
  if (path === '/my-projects' || path === '/projects-dashboard') return true;
  if (path.startsWith('/internship')) return true;
  if (path === '/jobs' || path === '/opportunities') return true;
  if (path.startsWith('/checkout') || path.startsWith('/buy')) return true;
  if (path === '/user/payments' || path === '/my-payments') return true;
  return false;
};

// Route Human-friendly Titles
export const getRouteTitle = (path: string): string => {
  if (path.startsWith('/admin')) return 'Ingage Admin Control Center';
  if (path.startsWith('/profile/certificates')) return 'Certificates & Credentials';
  if (path.startsWith('/profile/downloads')) return 'Download Center';
  if (path.startsWith('/profile/tickets')) return 'My Support Tickets';
  if (path.startsWith('/profile/help')) return 'Help Center';
  if (path.startsWith('/profile/settings')) return 'Account Settings';
  if (path.startsWith('/profile/reset-password')) return 'Password Reset';
  if (path.startsWith('/profile')) return 'Student Profile';
  if (path === '/my-learning') return 'My Learning Dashboard';
  if (path === '/learning-roles' || path === '/my-learning-roles' || path === '/my-roles') return 'My Learning Roles';
  if (path.startsWith('/learn')) return 'Course Learning Player';
  if (path === '/my-projects' || path === '/projects-dashboard') return 'My Active Projects';
  if (path.startsWith('/internship')) return 'Internship Program';
  if (path === '/jobs' || path === '/opportunities') return 'Opportunities & Job Board';
  if (path.startsWith('/checkout') || path.startsWith('/buy')) return 'Course Enrollment & Checkout';
  if (path === '/user/payments' || path === '/my-payments') return 'My Payment History';
  if (path === '/payment/success') return 'Payment Confirmation';
  if (path === '/payment/failed') return 'Payment Status';
  return 'Protected Workspace';
};

// Core Public & LMS Pages
import { LandingPage } from './pages/LandingPage';
import { CareersPage } from './pages/CareersPage';
import { CareerDetailPage } from './pages/CareerDetailPage';
import { LearningPlayerPage } from './pages/LearningPlayerPage';
import { CourseLearnPage } from './pages/CourseLearnPage';
import { ProjectsPage } from './pages/ProjectsPage';
import { JobsPage } from './pages/JobsPage';
import { OpportunitiesPage } from './pages/OpportunitiesPage';
import { EmployersPage } from './pages/EmployersPage';
import { MyLearningPage } from './pages/MyLearningPage';
import { MyLearningRolesPage } from './pages/MyLearningRolesPage';
import { InternshipPage } from './pages/InternshipPage';
import { InternshipProgramPage } from './pages/InternshipProgramPage';
import { MyProjectsPage } from './pages/MyProjectsPage';
import { ProfilePage, ProfileTab } from './pages/ProfilePage';
import { VerifyOtpPage } from './pages/VerifyOtpPage';
import { CoursesPage } from './pages/CoursesPage';
import { CourseDetailPage } from './pages/CourseDetailPage';
import { PaymentSuccessPage } from './pages/PaymentSuccessPage';
import { PaymentFailedPage } from './pages/PaymentFailedPage';
import { UserPaymentsPage } from './pages/UserPaymentsPage';
import { CertificateVerificationPage } from './pages/CertificateVerificationPage';
import { OAuthCallbackPage } from './pages/OAuthCallbackPage';

// Admin Pages & Layout
import { AdminLayout } from './admin/AdminLayout';
import { AdminDashboardPage } from './admin/pages/AdminDashboardPage';
import { AdminUsersPage } from './admin/pages/AdminUsersPage';
import { AdminUserDetailsPage } from './admin/pages/AdminUserDetailsPage';
import { AdminCoursesPage } from './admin/pages/AdminCoursesPage';
import { AdminAddCoursePage } from './admin/pages/AdminAddCoursePage';
import { AdminCourseAnalyticsPage } from './admin/pages/AdminCourseAnalyticsPage';
import { AdminPaymentsPage } from './admin/pages/AdminPaymentsPage';
import { AdminOrdersPage } from './admin/pages/AdminOrdersPage';
import { AdminDiscountsPage } from './admin/pages/AdminDiscountsPage';
import { AdminCourseProgressPage } from './admin/pages/AdminCourseProgressPage';
import { AdminCertificatesPage } from './admin/pages/AdminCertificatesPage';
import { AdminCareersPage } from './admin/pages/AdminCareersPage';
import { AdminProjectsPage } from './admin/pages/AdminProjectsPage';
import { AdminActivityLogsPage } from './admin/pages/AdminActivityLogsPage';
import { AdminSettingsPage } from './admin/pages/AdminSettingsPage';
import { AdminUnauthorizedPage } from './admin/pages/AdminUnauthorizedPage';

function AppContent() {
  const {
    currentUser,
    isAuthenticated,
    isAuthLoading,
    login,
    logout,
  } = useAuth();

  const [currentPath, setCurrentPath] = useState<string>(() => {
    const rawPath = typeof window !== 'undefined' ? (window.location.pathname || '/') : '/';
    const token = typeof window !== 'undefined' ? localStorage.getItem('ingage_token') : null;

    // If an unauthenticated user with NO token directly visits a protected route, redirect to '/'
    if (isProtectedRoute(rawPath) && !token) {
      try {
        sessionStorage.setItem('ingage_redirect_after_auth', rawPath);
        window.history.replaceState({}, '', '/');
      } catch {
        // noop
      }
      return '/';
    }
    return rawPath;
  });

  const [roleParam, setRoleParam] = useState<string>(() => {
    if (typeof window !== 'undefined') {
      const p = window.location.pathname || '';
      if (p.startsWith('/roles/')) return p.slice(7).split('?')[0].split('#')[0] || 'data-analyst';
      if (p.startsWith('/learn/')) return p.slice(7).split('?')[0].split('#')[0] || 'data-analyst';
      if (p.startsWith('/courses/')) return p.slice(9).split('?')[0].split('#')[0] || '';
    }
    return 'data-analyst';
  });

  const [isAuthModalOpen, setIsAuthModalOpen] = useState<boolean>(() => {
    const p = typeof window !== 'undefined' ? (window.location.pathname || '/') : '/';
    const token = typeof window !== 'undefined' ? localStorage.getItem('ingage_token') : null;

    if (isProtectedRoute(p) && !token) {
      return true;
    }
    return p === '/signup' || p === '/login' || p === '/forgot-password';
  });

  const [authModalMode, setAuthModalMode] = useState<AuthMode>(() => {
    const p = typeof window !== 'undefined' ? (window.location.pathname || '/') : '/';
    const token = typeof window !== 'undefined' ? localStorage.getItem('ingage_token') : null;

    if (isProtectedRoute(p) && !token) {
      return 'login';
    }
    if (p === '/forgot-password') return 'forgot-password';
    return p === '/login' ? 'login' : 'signup';
  });

  const [toastMessage, setToastMessage] = useState<string | null>(null);

  // Check if initial direct access was intercepted for an unauthenticated visitor
  useEffect(() => {
    const token = localStorage.getItem('ingage_token');
    const savedRedirect = sessionStorage.getItem('ingage_redirect_after_auth');
    if (!token && savedRedirect) {
      showToast(`Please log in to access ${getRouteTitle(savedRedirect)}.`);
    }
  }, []);

  // Keep state synchronized with browser URL
  useEffect(() => {
    const handlePopState = () => {
      const path = window.location.pathname || '/';
      const token = localStorage.getItem('ingage_token');

      // If user navigates history to a protected route while completely unauthenticated and not loading
      if (isProtectedRoute(path) && !token && !isAuthenticated && !isAuthLoading) {
        try {
          sessionStorage.setItem('ingage_redirect_after_auth', path);
          window.history.replaceState({}, '', '/');
        } catch {}
        setCurrentPath('/');
        setAuthModalMode('login');
        setIsAuthModalOpen(true);
        showToast(`Please sign in to access ${getRouteTitle(path)}.`);
        return;
      }

      setCurrentPath(path);
      if (path.startsWith('/roles/')) {
        setRoleParam(path.replace('/roles/', ''));
      } else if (path.startsWith('/learn/')) {
        setRoleParam(path.replace('/learn/', ''));
      } else if (path === '/signup') {
        setAuthModalMode('signup');
        setIsAuthModalOpen(true);
      } else if (path === '/login') {
        setAuthModalMode('login');
        setIsAuthModalOpen(true);
      } else if (path === '/forgot-password') {
        setAuthModalMode('forgot-password');
        setIsAuthModalOpen(true);
      }
    };

    window.addEventListener('popstate', handlePopState);
    return () => window.removeEventListener('popstate', handlePopState);
  }, [isAuthenticated, isAuthLoading]);

  // Sync auth modal if path changes
  useEffect(() => {
    if (currentPath === '/signup') {
      setAuthModalMode('signup');
      setIsAuthModalOpen(true);
    } else if (currentPath === '/login') {
      const urlParams = new URLSearchParams(window.location.search);
      const errorMsg = urlParams.get('error');
      if (errorMsg) {
        showToast(decodeURIComponent(errorMsg));
      }
      setAuthModalMode('login');
      setIsAuthModalOpen(true);
    } else if (currentPath === '/forgot-password') {
      setAuthModalMode('forgot-password');
      setIsAuthModalOpen(true);
    }
  }, [currentPath]);

  // Remember intended destination for post-auth redirection
  const [redirectAfterAuth, setRedirectAfterAuth] = useState<string | null>(() => {
    try {
      return sessionStorage.getItem('ingage_redirect_after_auth');
    } catch {
      return null;
    }
  });



  const showToast = (msg: string) => {
    setToastMessage(msg);
    setTimeout(() => {
      setToastMessage(null);
    }, 4000);
  };

  const handleOpenAuth = (mode: AuthMode = 'signup', redirectUrl?: string) => {
    if (redirectUrl) {
      setRedirectAfterAuth(redirectUrl);
      try {
        sessionStorage.setItem('ingage_redirect_after_auth', redirectUrl);
      } catch {
        // ignore
      }
    }
    setAuthModalMode(mode);
    setIsAuthModalOpen(true);
  };

  const handleRequireAuth = (intendedPath: string, pageTitle?: string) => {
    setRedirectAfterAuth(intendedPath);
    try {
      sessionStorage.setItem('ingage_redirect_after_auth', intendedPath);
    } catch {
      // ignore
    }
    setAuthModalMode('login');
    setIsAuthModalOpen(true);
    if (pageTitle) {
      showToast(`Please sign in or create an account to access ${pageTitle}.`);
    }
  };

  const handleAuthSuccess = (user: UserProfile) => {
    const token = localStorage.getItem('ingage_token') || '';
    login(token, user);

    // Check if user was intercepted trying to reach a protected page or action
    const targetRedirect = redirectAfterAuth || sessionStorage.getItem('ingage_redirect_after_auth');
    if (
      targetRedirect &&
      targetRedirect !== '/login' &&
      targetRedirect !== '/signup' &&
      targetRedirect !== '/forgot-password' &&
      targetRedirect !== '/'
    ) {
      setRedirectAfterAuth(null);
      try {
        sessionStorage.removeItem('ingage_redirect_after_auth');
      } catch {
        // ignore
      }
      showToast(`Welcome ${user.name}! Redirecting to ${getRouteTitle(targetRedirect)}...`);
      navigate(targetRedirect);
      return;
    }

    showToast(`Welcome ${user.name}! You are now signed in.`);

    // If on /login or /signup, return to home
    if (currentPath === '/login' || currentPath === '/signup' || currentPath === '/forgot-password') {
      navigate('/');
    }
  };

  const handleLogout = async () => {
    await logout();
    setRedirectAfterAuth(null);
    showToast('You have signed out successfully.');
    if (isProtectedRoute(currentPath)) {
      navigate('/');
    }
  };

  const handleCloseAuth = () => {
    setIsAuthModalOpen(false);
    if (currentPath === '/login' || currentPath === '/signup' || currentPath === '/forgot-password') {
      navigate('/');
    }
  };

  const navigate = (path: string, param?: string) => {
    let targetPath = path;
    if (param) {
      setRoleParam(param);
    }

    if (path === '/signup') {
      setAuthModalMode('signup');
      setIsAuthModalOpen(true);
      setCurrentPath(path);
      try {
        window.history.pushState({}, '', path);
      } catch {
        // noop
      }
      return;
    }

    if (path === '/login') {
      setAuthModalMode('login');
      setIsAuthModalOpen(true);
      setCurrentPath(path);
      try {
        window.history.pushState({}, '', path);
      } catch {
        // noop
      }
      return;
    }

    if (path === '/forgot-password') {
      setAuthModalMode('forgot-password');
      setIsAuthModalOpen(true);
      setCurrentPath(path);
      try {
        window.history.pushState({}, '', path);
      } catch {
        // noop
      }
      return;
    }
    
    if (path.startsWith('/roles/')) {
      const p = path.split('/')[2] || param || 'data-analyst';
      setRoleParam(p);
      targetPath = `/roles/${p}`;
    } else if (path.startsWith('/learn/')) {
      const p = path.split('/')[2] || param || 'data-analyst';
      setRoleParam(p);
      targetPath = `/learn/${p}`;
    } else if (path.startsWith('/courses/')) {
      const p = path.split('/courses/')[1] || param || '';
      if (p) {
        setRoleParam(p);
      }
    } else if (path === '/payment/success' && param) {
      setRoleParam(param);
    }

    // Intercept protected routes if user has no token / not authenticated
    const token = localStorage.getItem('ingage_token');
    if (isProtectedRoute(targetPath) && !token && !isAuthenticated && !isAuthLoading) {
      setRedirectAfterAuth(targetPath);
      try {
        sessionStorage.setItem('ingage_redirect_after_auth', targetPath);
        window.history.replaceState({}, '', '/');
      } catch {
        // ignore
      }
      setCurrentPath('/');
      showToast(`Please sign in to access ${getRouteTitle(targetPath)}.`);
      handleOpenAuth('login', targetPath);
      return;
    }

    setCurrentPath(targetPath);
    try {
      window.history.pushState({}, '', targetPath);
    } catch {
      // Graceful fallback if pushState is restricted in iframe
    }
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  // Render the matching view
  const renderCurrentPage = () => {
    // Admin Routes
    if (currentPath.startsWith('/admin')) {
      if (isAuthLoading) {
        return (
          <div className="min-h-screen bg-slate-950 flex flex-col items-center justify-center p-8 space-y-4">
            <div className="w-8 h-8 border-2 border-lime-500 border-t-transparent rounded-full animate-spin"></div>
            <p className="text-sm font-semibold text-slate-400">Verifying administrator credentials...</p>
          </div>
        );
      }
      if (!currentUser) {
        return <LandingPage onNavigate={navigate} />;
      }
      if (currentUser.role !== 'ADMIN') {
        return <AdminUnauthorizedPage onNavigate={navigate} />;
      }

      let adminContent: React.ReactNode = null;
      if (currentPath === '/admin' || currentPath === '/admin/dashboard') {
        adminContent = <AdminDashboardPage onNavigate={navigate} />;
      } else if (currentPath === '/admin/users') {
        adminContent = <AdminUsersPage onNavigate={navigate} />;
      } else if (currentPath.startsWith('/admin/users/')) {
        const userId = parseInt(currentPath.replace('/admin/users/', ''), 10) || 1;
        adminContent = <AdminUserDetailsPage userId={userId} onNavigate={navigate} />;
      } else if (currentPath === '/admin/courses/new') {
        adminContent = <AdminAddCoursePage onNavigate={navigate} />;
      } else if (currentPath.startsWith('/admin/courses/') && currentPath.endsWith('/edit')) {
        const parts = currentPath.split('/');
        const courseId = parseInt(parts[3], 10);
        adminContent = <AdminAddCoursePage courseId={courseId} onNavigate={navigate} />;
      } else if (currentPath.startsWith('/admin/courses/') && currentPath.endsWith('/analytics')) {
        const parts = currentPath.split('/');
        const courseId = parseInt(parts[3], 10);
        adminContent = <AdminCourseAnalyticsPage courseId={courseId} onNavigate={navigate} />;
      } else if (currentPath === '/admin/courses') {
        adminContent = <AdminCoursesPage onNavigate={navigate} />;
      } else if (currentPath === '/admin/payments') {
        adminContent = <AdminPaymentsPage onNavigate={navigate} />;
      } else if (currentPath === '/admin/orders') {
        adminContent = <AdminOrdersPage onNavigate={navigate} />;
      } else if (currentPath === '/admin/discounts') {
        adminContent = <AdminDiscountsPage onNavigate={navigate} />;
      } else if (currentPath === '/admin/progress') {
        adminContent = <AdminCourseProgressPage onNavigate={navigate} />;
      } else if (currentPath === '/admin/certificates') {
        adminContent = <AdminCertificatesPage onNavigate={navigate} />;
      } else if (currentPath === '/admin/careers') {
        adminContent = <AdminCareersPage onNavigate={navigate} />;
      } else if (currentPath === '/admin/projects') {
        adminContent = <AdminProjectsPage onNavigate={navigate} />;
      } else if (currentPath === '/admin/activity') {
        adminContent = <AdminActivityLogsPage onNavigate={navigate} />;
      } else if (currentPath === '/admin/settings') {
        adminContent = <AdminSettingsPage onNavigate={navigate} />;
      } else {
        adminContent = <AdminDashboardPage onNavigate={navigate} />;
      }

      return (
        <AdminLayout
          currentPath={currentPath}
          onNavigate={navigate}
          currentUser={currentUser}
          onLogout={handleLogout}
        >
          {adminContent}
        </AdminLayout>
      );
    }

    // Public Certificate Verification
    if (currentPath.startsWith('/certificate/verify/') || currentPath.startsWith('/verify/certificate/')) {
      const code = currentPath.split('/certificate/verify/')[1] || currentPath.split('/verify/certificate/')[1] || '';
      return <CertificateVerificationPage code={code} onNavigate={navigate} />;
    }

    // Immediate fallback: Never render protected view if user is not authenticated and not loading
    if (isProtectedRoute(currentPath) && !currentUser && !isAuthLoading) {
      return <LandingPage onNavigate={navigate} />;
    }

    if (currentPath === '/oauth/callback') {
      return <OAuthCallbackPage onNavigate={navigate} onShowToast={showToast} />;
    }

    if (currentPath.startsWith('/verify-otp') || currentPath.startsWith('/login/verify-otp')) {
      return <VerifyOtpPage onNavigate={navigate} onOpenAuth={handleOpenAuth} onShowToast={showToast} />;
    }
    if (currentPath === '/' || currentPath === '/signup' || currentPath === '/login' || currentPath.startsWith('/reset-password')) {
      return <LandingPage onNavigate={navigate} />;
    }
    if (currentPath === '/careers') {
      return <CareersPage onNavigate={navigate} />;
    }
    if (currentPath === '/courses') {
      return <CoursesPage onNavigate={navigate} onShowToast={showToast} />;
    }
    if (currentPath.startsWith('/courses/')) {
      const courseId = currentPath.slice(9).split('?')[0].split('#')[0] || roleParam;
      return (
        <CourseDetailPage
          courseId={courseId}
          onNavigate={navigate}
          currentUser={currentUser}
          onOpenAuth={handleOpenAuth}
          onShowToast={showToast}
        />
      );
    }
    if (currentPath === '/payment/success') {
      return <PaymentSuccessPage dataJson={roleParam} onNavigate={navigate} />;
    }
    if (currentPath === '/payment/failed') {
      return <PaymentFailedPage onNavigate={navigate} />;
    }
    if (currentPath === '/user/payments' || currentPath === '/my-payments') {
      return (
        <ProtectedRoute
          isAuthenticated={isAuthenticated}
          isLoading={isAuthLoading}
          intendedPath={currentPath}
          pageTitle="My Payment History"
          onRequireAuth={handleRequireAuth}
          onNavigate={navigate}
        >
          <UserPaymentsPage onNavigate={navigate} currentUser={currentUser} />
        </ProtectedRoute>
      );
    }
    if (currentPath.startsWith('/roles/')) {
      const roleSlug = currentPath.slice(7).split('?')[0].split('#')[0] || roleParam || 'data-analyst';
      return (
        <CareerDetailPage
          roleId={roleSlug}
          onNavigate={navigate}
          currentUser={currentUser}
          onOpenAuth={handleOpenAuth}
          onShowToast={showToast}
        />
      );
    }
    if (currentPath.startsWith('/learn')) {
      const rawParam = currentPath.startsWith('/learn/')
        ? (currentPath.slice(7).split('?')[0].split('#')[0] || roleParam || '5')
        : roleParam || '5';

      // If param is numeric (e.g. /learn/5), route to CourseLearnPage
      if (!isNaN(Number(rawParam))) {
        return (
          <ProtectedRoute
            isAuthenticated={isAuthenticated}
            isLoading={isAuthLoading}
            intendedPath={currentPath}
            pageTitle="Course Learning Player"
            onRequireAuth={handleRequireAuth}
            onNavigate={navigate}
          >
            <CourseLearnPage courseId={Number(rawParam)} onNavigate={navigate} onShowToast={showToast} />
          </ProtectedRoute>
        );
      }

      // Legacy fallback for career role tracks (e.g. /learn/data-analyst)
      return (
        <ProtectedRoute
          isAuthenticated={isAuthenticated}
          isLoading={isAuthLoading}
          intendedPath={currentPath}
          pageTitle="Interactive Course Player"
          onRequireAuth={handleRequireAuth}
          onNavigate={navigate}
        >
          <LearningPlayerPage roleId={rawParam} onNavigate={navigate} onShowToast={showToast} />
        </ProtectedRoute>
      );
    }
    if (currentPath === '/projects') {
      return (
        <ProjectsPage
          onNavigate={navigate}
          onShowToast={showToast}
          currentUser={currentUser}
          onOpenAuth={handleOpenAuth}
        />
      );
    }
    if (currentPath === '/jobs' || currentPath === '/opportunities') {
      return (
        <ProtectedRoute
          isAuthenticated={isAuthenticated}
          isLoading={isAuthLoading}
          intendedPath={currentPath}
          pageTitle="Opportunities"
          onRequireAuth={handleRequireAuth}
          onNavigate={navigate}
        >
          <OpportunitiesPage
            onNavigate={navigate}
            onShowToast={showToast}
            currentUser={currentUser}
            onOpenAuth={handleOpenAuth}
          />
        </ProtectedRoute>
      );
    }
    if (currentPath === '/my-learning') {
      return (
        <ProtectedRoute
          isAuthenticated={isAuthenticated}
          isLoading={isAuthLoading}
          intendedPath={currentPath}
          pageTitle="My Learning Dashboard"
          onRequireAuth={handleRequireAuth}
          onNavigate={navigate}
        >
          <MyLearningPage onNavigate={navigate} onShowToast={showToast} />
        </ProtectedRoute>
      );
    }
    if (currentPath === '/learning-roles' || currentPath === '/my-learning-roles' || currentPath === '/my-roles') {
      return (
        <ProtectedRoute
          isAuthenticated={isAuthenticated}
          isLoading={isAuthLoading}
          intendedPath={currentPath}
          pageTitle="My Learning Roles"
          onRequireAuth={handleRequireAuth}
          onNavigate={navigate}
        >
          <MyLearningRolesPage onNavigate={navigate} onShowToast={showToast} />
        </ProtectedRoute>
      );
    }
    if (currentPath === '/internship-program' || currentPath === '/internship-overview' || currentPath === '/internship-intro') {
      return (
        <ProtectedRoute
          isAuthenticated={isAuthenticated}
          isLoading={isAuthLoading}
          intendedPath={currentPath}
          pageTitle="Internship Program Overview"
          onRequireAuth={handleRequireAuth}
          onNavigate={navigate}
        >
          <InternshipProgramPage onNavigate={navigate} onShowToast={showToast} />
        </ProtectedRoute>
      );
    }
    if (currentPath.startsWith('/internship')) {
      return (
        <ProtectedRoute
          isAuthenticated={isAuthenticated}
          isLoading={isAuthLoading}
          intendedPath={currentPath}
          pageTitle="Active Internship Program"
          onRequireAuth={handleRequireAuth}
          onNavigate={navigate}
        >
          <InternshipPage onNavigate={navigate} onShowToast={showToast} />
        </ProtectedRoute>
      );
    }
    if (currentPath === '/my-projects' || currentPath === '/projects-dashboard') {
      return (
        <ProtectedRoute
          isAuthenticated={isAuthenticated}
          isLoading={isAuthLoading}
          intendedPath={currentPath}
          pageTitle="My Projects"
          onRequireAuth={handleRequireAuth}
          onNavigate={navigate}
        >
          <MyProjectsPage onNavigate={navigate} onShowToast={showToast} currentUser={currentUser} />
        </ProtectedRoute>
      );
    }
    if (currentPath.startsWith('/profile')) {
      let initialTab: ProfileTab = 'profile';
      if (currentPath === '/profile/certificates') initialTab = 'certificates';
      else if (currentPath === '/profile/downloads') initialTab = 'downloads';
      else if (currentPath === '/profile/tickets') initialTab = 'tickets';
      else if (currentPath === '/profile/help') initialTab = 'help';
      else if (currentPath === '/profile/settings') initialTab = 'settings';
      else if (currentPath === '/profile/reset-password') initialTab = 'reset-password';

      return (
        <ProtectedRoute
          isAuthenticated={isAuthenticated}
          isLoading={isAuthLoading}
          intendedPath={currentPath}
          pageTitle={getRouteTitle(currentPath)}
          onRequireAuth={handleRequireAuth}
          onNavigate={navigate}
        >
          <ProfilePage
            initialTab={initialTab}
            currentUser={currentUser}
            onNavigate={navigate}
            onShowToast={showToast}
          />
        </ProtectedRoute>
      );
    }
    if (currentPath === '/employers') {
      return <EmployersPage onNavigate={navigate} />;
    }

    // Default fallback to LandingPage
    return <LandingPage onNavigate={navigate} />;
  };

  const isLearningPlayer = currentPath.startsWith('/learn/');
  const isAdminRoute = currentPath.startsWith('/admin');

  return (
    <div
      className={`min-h-screen ${
        isAdminRoute ? 'bg-slate-950 text-slate-100' : 'bg-white text-gray-900'
      } flex flex-col font-sans selection:bg-lime-200 selection:text-lime-900 relative`}
    >
      {/* Toast Notification (Positioned below the sticky navbar) */}
      {toastMessage && (
        <div
          id="global-toast-notification"
          className="fixed top-22 right-4 sm:right-6 z-50 bg-gray-900 text-white text-xs sm:text-sm px-4 py-2.5 rounded-xl shadow-2xl border border-gray-700 flex items-center gap-2.5 animate-in slide-in-from-top-4 fade-in duration-200"
        >
          <span className="w-2 h-2 rounded-full bg-lime-400 shrink-0 animate-pulse"></span>
          <span className="font-medium">{toastMessage}</span>
          <button
            onClick={() => setToastMessage(null)}
            className="ml-2 text-gray-400 hover:text-white transition-colors cursor-pointer text-xs"
            aria-label="Dismiss toast"
          >
            ✕
          </button>
        </div>
      )}

      {/* Global Header (Hidden on Admin Routes) */}
      {!isAdminRoute && (
        <Header
          currentPath={currentPath}
          onNavigate={navigate}
          currentUser={currentUser}
          onOpenAuth={handleOpenAuth}
          onLogout={handleLogout}
        />
      )}

      {/* Main Page Content */}
      <main className="flex-1">{renderCurrentPage()}</main>

      {/* Global Footer (Hidden on Admin Routes) */}
      {!isAdminRoute && <Footer onNavigate={navigate} />}

      {/* Floating Screen Navigator (Bottom Right Screen directory) */}
      <ScreenNavigator currentPath={currentPath} onNavigate={navigate} currentUser={currentUser} />

      {/* Global Authentication Modal (Create Account / Log In / Forgot Password) */}
      <AuthModal
        isOpen={isAuthModalOpen}
        initialMode={authModalMode}
        onClose={handleCloseAuth}
        onSuccess={handleAuthSuccess}
        onNavigate={navigate}
      />
    </div>
  );
}

export default function App() {
  return (
    <AuthProvider>
      <AppContent />
    </AuthProvider>
  );
}

