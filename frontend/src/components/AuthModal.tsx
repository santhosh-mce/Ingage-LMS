import React, { useState, useEffect, useRef } from 'react';
import { X, Mail, Lock, User, Eye, EyeOff, Check, AlertCircle, ArrowRight, ShieldCheck, Sparkles } from 'lucide-react';
import { AuthMode, UserProfile } from '../types';
import { getAuthErrorMessage, loginUser, registerUser } from '../api/authApi';
import { SuccessModal } from './SuccessModal';
import { useAppDispatch } from '../store/hooks';
import { setCredentials } from '../store/slices/authSlice';

interface SuccessConfig {
  title: string;
  subtitle: string;
  successTitle: string;
  message: string;
}

interface AuthModalProps {
  isOpen: boolean;
  initialMode?: AuthMode;
  onClose: () => void;
  onSuccess: (user: UserProfile) => void;
  onNavigate?: (path: string) => void;
}

export function AuthModal({
  isOpen,
  initialMode = 'signup',
  onClose,
  onSuccess,
  onNavigate,
}: AuthModalProps) {
  const [mode, setMode] = useState<AuthMode>(initialMode === 'forgot-password' ? 'login' : initialMode);
  
  // Form state
  const [fullName, setFullName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [agreeTerms, setAgreeTerms] = useState(false);
  const [rememberMe, setRememberMe] = useState(true);

  // UI state
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const dispatch = useAppDispatch();
  const [error, setError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);
  const [showTermsModal, setShowTermsModal] = useState(false);
  const [successConfig, setSuccessConfig] = useState<SuccessConfig | null>(null);

  // Timeout ref for clean auto-redirect teardown
  const successTimerRef = useRef<NodeJS.Timeout | null>(null);

  const handleModalClose = () => {
    if (successTimerRef.current) {
      clearTimeout(successTimerRef.current);
      successTimerRef.current = null;
    }
    setSuccessConfig(null);
    onClose();
  };

  // Sync mode when initialMode changes or modal opens
  useEffect(() => {
    if (isOpen) {
      setMode(initialMode === 'forgot-password' ? 'login' : initialMode);
      setError(null);
      setSuccessMessage(null);
      setSuccessConfig(null);
    }
  }, [isOpen, initialMode]);

  // Cleanup timers on unmount
  useEffect(() => {
    return () => {
      if (successTimerRef.current) {
        clearTimeout(successTimerRef.current);
      }
    };
  }, []);

  // Lock body scroll when modal is open
  useEffect(() => {
    if (isOpen) {
      document.body.style.overflow = 'hidden';
    } else {
      document.body.style.overflow = 'unset';
    }
    return () => {
      document.body.style.overflow = 'unset';
    };
  }, [isOpen]);

  // Handle ESC key
  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'Escape' && isOpen) {
        handleModalClose();
      }
    };
    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [isOpen]);

  if (!isOpen) return null;

  // Password strength calculation
  const getPasswordStrength = () => {
    if (!password) return { score: 0, text: '', color: '' };
    let score = 0;
    if (password.length >= 8) score += 1;
    if (/[A-Z]/.test(password)) score += 1;
    if (/[0-9]/.test(password)) score += 1;
    if (/[^A-Za-z0-9]/.test(password)) score += 1;

    if (score <= 1) return { score: 25, text: 'Weak', color: 'bg-red-500' };
    if (score === 2) return { score: 50, text: 'Fair', color: 'bg-amber-500' };
    if (score === 3) return { score: 75, text: 'Good', color: 'bg-blue-500' };
    return { score: 100, text: 'Strong', color: 'bg-lime-600' };
  };

  const strength = getPasswordStrength();

  // Submission handler
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);

    // Basic validation
    if (!email || !email.includes('@')) {
      setError('Please enter a valid email address.');
      return;
    }

    if (!password || password.length < 8) {
      setError('Password must be at least 8 characters.');
      return;
    }

    if (mode === 'signup') {
      if (!fullName.trim()) {
        setError('Please enter your full name.');
        return;
      }
      if (password !== confirmPassword) {
        setError('Passwords do not match. Please verify.');
        return;
      }
      if (!agreeTerms) {
        setError('Please agree to the Terms of Service and Privacy Policy.');
        return;
      }
    }

    setIsLoading(true);

    try {
      if (mode === 'signup') {
        const response = await registerUser({ name: fullName.trim(), email: email.trim(), password });
        
        // Show Signup Success Modal
        setSuccessConfig({
          title: 'Create Your Account',
          subtitle: 'Start your learning journey today',
          successTitle: 'Account Created!',
          message: 'Welcome to Ingage LMS. Setting up your profile...',
        });
        setError(null);
        setSuccessMessage(null);

        // Perform auto-login in background during setup state
        try {
          const loginRes = await loginUser({ email: email.trim(), password });
          if (loginRes.token) {
            const user: UserProfile = {
              id: loginRes.userId || response.userId || '',
              name: loginRes.name || fullName.trim(),
              email: loginRes.email || email.trim(),
              role: loginRes.role || 'STUDENT',
              enrolledPaths: [],
            };

            // Immediately persist session so any concurrent navigation or refresh has full auth state
            localStorage.setItem('ingage_token', loginRes.token);
            localStorage.setItem('ingage_user', JSON.stringify(user));
            dispatch(setCredentials({ token: loginRes.token, user }));

            // Hold success modal during short setup state, then redirect to app
            successTimerRef.current = setTimeout(() => {
              setSuccessConfig(null);
              onSuccess(user);
              onClose();
            }, 2200);
            return;
          }
        } catch {
          // If auto-login fails, smoothly transition to login mode after the short setup state
          successTimerRef.current = setTimeout(() => {
            setSuccessConfig(null);
            setMode('login');
            setPassword('');
            setConfirmPassword('');
            setSuccessMessage('Account created successfully! Please log in.');
          }, 2200);
          return;
        }
        return;
      }

      // Login Flow
      const response = await loginUser({ email: email.trim(), password });

      if (response.token) {
        const user: UserProfile = {
          id: response.userId || '',
          name: response.name || '',
          email: response.email || '',
          role: response.role || 'STUDENT',
          enrolledPaths: [],
        };

        // Immediately persist session so any concurrent navigation or refresh has full auth state
        localStorage.setItem('ingage_token', response.token);
        localStorage.setItem('ingage_user', JSON.stringify(user));
        dispatch(setCredentials({ token: response.token, user }));

        // Show Login Success Modal matching reference image
        setSuccessConfig({
          title: 'Welcome Back!',
          subtitle: 'Great to see you again',
          successTitle: 'Login Successful!',
          message: 'Welcome back to Ingage LMS. Setting up your dashboard...',
        });
        setError(null);
        setSuccessMessage(null);

        // Hold success modal during short setup state, then redirect to dashboard
        successTimerRef.current = setTimeout(() => {
          setSuccessConfig(null);
          onSuccess(user);
          onClose();
        }, 2000);
        return;
      }
    } catch (requestError) {
      setError(getAuthErrorMessage(requestError, 'Unable to authenticate. Please try again.'));
    } finally {
      setIsLoading(false);
    }
  };

  // Social Auth Handlers
  const handleSocialAuth = (provider: 'Google' | 'LinkedIn') => {
    setIsLoading(true);
    const apiBaseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8000/api';
    window.location.assign(`${apiBaseUrl}/oauth2/authorization/${provider.toLowerCase()}`);
  };

  // Quick demo autofill
  const handleQuickDemo = (roleType: 'learner' | 'pro') => {
    if (roleType === 'learner') {
      setFullName('Jordan Vance');
      setEmail('jordan.vance@example.com');
      setPassword('P@ssword2026!');
      setConfirmPassword('P@ssword2026!');
      setAgreeTerms(true);
    } else {
      setFullName('Elena Rostova');
      setEmail('elena.rostova@techcorp.io');
      setPassword('IngageElite2026!');
      setConfirmPassword('IngageElite2026!');
      setAgreeTerms(true);
    }
  };

  if (successConfig) {
    return (
      <SuccessModal
        isOpen={isOpen}
        title={successConfig.title}
        subtitle={successConfig.subtitle}
        successTitle={successConfig.successTitle}
        message={successConfig.message}
        onClose={handleModalClose}
      />
    );
  }

  return (
    <>
      <div
        id="auth-modal-backdrop"
        className="fixed inset-0 z-50 flex items-center justify-center p-4 sm:p-6 bg-black/60 backdrop-blur-xs transition-opacity animate-in fade-in duration-200"
        onClick={(e) => {
          if (e.target === e.currentTarget) handleModalClose();
        }}
      >
        {/* Modal Container */}
        <div
          id="auth-modal-card"
          className="relative w-full max-w-[460px] bg-white rounded-2xl shadow-2xl border border-gray-100 overflow-hidden flex flex-col max-h-[90vh] transition-all transform scale-100"
        >
          {/* Header with Close Button */}
          <div className="pt-6 px-5 sm:px-8 pb-4 flex items-start justify-between gap-3">
            <div>
              <h2 id="auth-modal-title" className="text-xl sm:text-2xl font-bold text-gray-900 tracking-tight">
                {mode === 'signup' ? 'Create Your Account' : 'Welcome Back'}
              </h2>
              <p id="auth-modal-subtitle" className="text-xs sm:text-sm text-gray-500 mt-1">
                {mode === 'signup' ? 'Start your learning journey today' : 'Log in to continue your learning journey'}
              </p>
            </div>

            <button
              id="auth-modal-close-btn"
              onClick={handleModalClose}
              className="text-gray-400 hover:text-gray-600 p-1.5 rounded-lg hover:bg-gray-100 transition-colors shrink-0 cursor-pointer"
              aria-label="Close modal"
            >
              <X className="w-5 h-5" />
            </button>
          </div>

          {/* Full-width Divider below Header */}
          <div className="w-full border-b border-gray-100" />

          {/* Scrollable Form Content */}
          <div className="px-4 sm:px-8 pb-6 overflow-y-auto space-y-4 flex-1">
              {/* Status Alert Messages */}
            {error && (
              <div className="p-3.5 rounded-xl bg-red-50 border border-red-200 text-red-700 text-xs sm:text-sm flex flex-col gap-2 animate-in fade-in duration-200">
                <div className="flex items-start gap-2.5">
                  <AlertCircle className="w-4 h-4 mt-0.5 shrink-0 text-red-600" />
                  <span className="font-medium">{error}</span>
                </div>
              </div>
            )}

            {/* Success Message Banner */}
            {successMessage && (
              <div className="p-3.5 rounded-xl bg-lime-50 border border-lime-200 text-lime-800 text-xs sm:text-sm flex items-center gap-2.5">
                <Check className="w-4 h-4 shrink-0 text-lime-600" />
                <span>{successMessage}</span>
              </div>
            )}

            <div className="space-y-3 pt-1">
              {/* Google Button */}
              <button
                id="social-google-btn"
                type="button"
                onClick={() => handleSocialAuth('Google')}
                disabled={isLoading}
                className="w-full py-3 px-4 rounded-xl border border-gray-200 hover:border-gray-300 hover:bg-gray-50/90 active:bg-gray-100 transition-all flex items-center justify-center gap-3 text-sm font-semibold text-gray-700 shadow-2xs group cursor-pointer"
              >
                <svg className="w-5 h-5 shrink-0" viewBox="0 0 24 24">
                  <path
                    fill="#4285F4"
                    d="M23.745 12.27c0-.7-.06-1.4-.19-2.07H12v4.51h6.6c-.29 1.52-1.14 2.82-2.4 3.68v3.05h3.88c2.27-2.09 3.66-5.17 3.66-9.17z"
                  />
                  <path
                    fill="#34A853"
                    d="M12 24c3.24 0 5.95-1.08 7.93-2.91l-3.88-3.05c-1.08.72-2.45 1.16-4.05 1.16-3.12 0-5.77-2.1-6.72-4.93H1.25v3.15C3.26 21.36 7.33 24 12 24z"
                  />
                  <path
                    fill="#FBBC05"
                    d="M5.28 14.27c-.25-.72-.38-1.49-.38-2.27s.14-1.55.38-2.27V6.58H1.25C.45 8.18 0 9.99 0 12s.45 3.82 1.25 5.42l4.03-3.15z"
                  />
                  <path
                    fill="#EA4335"
                    d="M12 4.75c1.77 0 3.35.61 4.6 1.8l3.42-3.42C17.95 1.19 15.24 0 12 0 7.33 0 3.26 2.64 1.25 6.58l4.03 3.15c.95-2.83 3.6-4.98 6.72-4.98z"
                  />
                </svg>
                <span>Continue with Google</span>
              </button>

              {/* LinkedIn Button */}
              <button
                id="social-linkedin-btn"
                type="button"
                onClick={() => handleSocialAuth('LinkedIn')}
                disabled={isLoading}
                className="w-full py-3 px-4 rounded-xl border border-gray-200 hover:border-gray-300 hover:bg-gray-50/90 active:bg-gray-100 transition-all flex items-center justify-center gap-3 text-sm font-semibold text-gray-700 shadow-2xs group cursor-pointer"
              >
                <svg className="w-5 h-5 shrink-0" viewBox="0 0 24 24">
                  <path
                    fill="#0A66C2"
                    d="M20.447 20.452h-3.554v-5.569c0-1.328-.027-3.037-1.852-3.037-1.853 0-2.136 1.445-2.136 2.939v5.667H9.351V9h3.414v1.561h.046c.477-.9 1.637-1.85 3.37-1.85 3.601 0 4.267 2.37 4.267 5.455v6.286zM5.337 7.433c-1.144 0-2.063-.926-2.063-2.065 0-1.138.92-2.063 2.063-2.063 1.14 0 2.064.925 2.064 2.063 0 1.139-.925 2.065-2.064 2.065zm1.782 13.019H3.555V9h3.564v11.452zM22.225 0H1.771C.792 0 0 .774 0 1.729v20.542C0 23.227.792 24 1.771 24h20.451c.979 0 1.778-.773 1.778-1.729V1.73C24 .774 23.205 0 22.222 0h.003z"
                  />
                </svg>
                <span>Continue with LinkedIn</span>
              </button>

              {/* Divider */}
              <div className="relative my-4 flex items-center justify-center">
                <div className="w-full border-t border-gray-200"></div>
                <span className="absolute bg-white px-3 text-xs text-gray-500 font-medium tracking-wide">
                  {mode === 'signup' ? 'Or sign up with email' : 'Or log in with email'}
                </span>
              </div>
            </div>

            {/* Form */}
            <form onSubmit={handleSubmit} className="space-y-4">
              {/* Full Name field (Only in Signup) */}
              {mode === 'signup' && (
                <div>
                  <label className="block text-sm font-semibold text-gray-800 mb-1.5" htmlFor="auth-fullname">
                    Full Name
                  </label>
                  <div className="relative">
                    <div className="absolute inset-y-0 left-0 pl-3.5 flex items-center pointer-events-none text-gray-400">
                      <User className="w-5 h-5" />
                    </div>
                    <input
                      id="auth-fullname"
                      type="text"
                      value={fullName}
                      onChange={(e) => setFullName(e.target.value)}
                      placeholder="Enter your full name"
                      className="w-full pl-11 pr-4 py-2.5 rounded-xl border border-gray-200 focus:border-lime-500 focus:ring-2 focus:ring-lime-100 text-sm text-gray-900 placeholder:text-gray-400 transition-all outline-none"
                      required
                    />
                  </div>
                </div>
              )}

              {/* Email Address field */}
              <div>
                <label className="block text-sm font-semibold text-gray-800 mb-1.5" htmlFor="auth-email">
                  Email Address
                </label>
                <div className="relative">
                  <div className="absolute inset-y-0 left-0 pl-3.5 flex items-center pointer-events-none text-gray-400">
                    <Mail className="w-5 h-5" />
                  </div>
                  <input
                    id="auth-email"
                    type="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    placeholder="your.email@example.com"
                    className="w-full pl-11 pr-4 py-2.5 rounded-xl border border-gray-200 focus:border-lime-500 focus:ring-2 focus:ring-lime-100 text-sm text-gray-900 placeholder:text-gray-400 transition-all outline-none"
                    required
                  />
                </div>
              </div>

              {/* Password field */}
              <div>
                <label className="block text-sm font-semibold text-gray-800 mb-1.5" htmlFor="auth-password">
                  Password
                </label>
                <div className="relative">
                  <div className="absolute inset-y-0 left-0 pl-3.5 flex items-center pointer-events-none text-gray-400">
                    <Lock className="w-5 h-5" />
                  </div>
                  <input
                    id="auth-password"
                    type={showPassword ? 'text' : 'password'}
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    placeholder={mode === 'signup' ? 'Create a strong password' : 'Enter your password'}
                    className="w-full pl-11 pr-11 py-2.5 rounded-xl border border-gray-200 focus:border-lime-500 focus:ring-2 focus:ring-lime-100 text-sm text-gray-900 placeholder:text-gray-400 transition-all outline-none"
                    required
                  />
                  <button
                    type="button"
                    onClick={() => setShowPassword(!showPassword)}
                    className="absolute inset-y-0 right-0 pr-3.5 flex items-center text-gray-400 hover:text-gray-600 transition-colors"
                    tabIndex={-1}
                  >
                    {showPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
                  </button>
                </div>

                {/* Password Strength Indicator in signup */}
                {mode === 'signup' && password.length > 0 && (
                  <div className="mt-2 space-y-1">
                    <div className="h-1.5 w-full bg-gray-100 rounded-full overflow-hidden">
                      <div
                        className={`h-full ${strength.color} transition-all duration-300`}
                        style={{ width: `${strength.score}%` }}
                      ></div>
                    </div>
                    <div className="flex justify-between items-center text-[11px] text-gray-500">
                      <span>Must be 8+ characters</span>
                      <span className="font-semibold text-gray-700">{strength.text}</span>
                    </div>
                  </div>
                )}
              </div>

              {/* Confirm Password field (Only in Signup) */}
              {mode === 'signup' && (
                <div>
                  <label className="block text-sm font-semibold text-gray-800 mb-1.5" htmlFor="auth-confirm-password">
                    Confirm Password
                  </label>
                  <div className="relative">
                    <div className="absolute inset-y-0 left-0 pl-3.5 flex items-center pointer-events-none text-gray-400">
                      <Lock className="w-5 h-5" />
                    </div>
                    <input
                      id="auth-confirm-password"
                      type={showConfirmPassword ? 'text' : 'password'}
                      value={confirmPassword}
                      onChange={(e) => setConfirmPassword(e.target.value)}
                      placeholder="Re-enter your password"
                      className="w-full pl-11 pr-11 py-2.5 rounded-xl border border-gray-200 focus:border-lime-500 focus:ring-2 focus:ring-lime-100 text-sm text-gray-900 placeholder:text-gray-400 transition-all outline-none"
                      required
                    />
                    <button
                      type="button"
                      onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                      className="absolute inset-y-0 right-0 pr-3.5 flex items-center text-gray-400 hover:text-gray-600 transition-colors"
                      tabIndex={-1}
                    >
                      {showConfirmPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
                    </button>
                  </div>
                  {confirmPassword && password !== confirmPassword && (
                    <p className="text-[11px] text-red-500 mt-1">Passwords do not match</p>
                  )}
                </div>
              )}

              {/* Remember Me (Only in Login) */}
              {mode === 'login' && (
                <div className="flex items-center justify-between text-xs sm:text-sm">
                  <label className="flex items-center gap-2 cursor-pointer select-none text-gray-600 hover:text-gray-800">
                    <input
                      id="auth-remember-me"
                      type="checkbox"
                      checked={rememberMe}
                      onChange={(e) => setRememberMe(e.target.checked)}
                      className="w-4 h-4 rounded text-lime-600 focus:ring-lime-500 border-gray-300 accent-lime-600 cursor-pointer"
                    />
                    <span>Remember me</span>
                  </label>
                </div>
              )}

              {/* Terms of Service Checkbox (Only in Signup) */}
              {mode === 'signup' && (
                <div className="pt-1">
                  <label className="flex items-start gap-2.5 cursor-pointer select-none text-xs sm:text-sm text-gray-600">
                    <input
                      id="auth-terms-checkbox"
                      type="checkbox"
                      checked={agreeTerms}
                      onChange={(e) => setAgreeTerms(e.target.checked)}
                      className="w-4 h-4 mt-0.5 rounded text-lime-600 focus:ring-lime-500 border-gray-300 accent-lime-600 shrink-0 cursor-pointer"
                      required
                    />
                    <span>
                      I agree to the{' '}
                      <button
                        type="button"
                        onClick={(e) => {
                          e.preventDefault();
                          setShowTermsModal(true);
                        }}
                        className="font-medium text-lime-700 hover:underline inline"
                      >
                        Terms of Service
                      </button>{' '}
                      and{' '}
                      <button
                        type="button"
                        onClick={(e) => {
                          e.preventDefault();
                          setShowTermsModal(true);
                        }}
                        className="font-medium text-lime-700 hover:underline inline"
                      >
                        Privacy Policy
                      </button>
                    </span>
                  </label>
                </div>
              )}

              {/* Primary Action Button */}
              <button
                id="auth-submit-btn"
                type="submit"
                disabled={isLoading}
                className="w-full py-3.5 px-4 bg-lime-600 hover:bg-lime-700 active:bg-lime-800 text-white font-semibold rounded-xl transition-all shadow-sm flex items-center justify-center gap-2 cursor-pointer disabled:opacity-75 disabled:cursor-not-allowed text-sm sm:text-base mt-2"
              >
                {isLoading ? (
                  <div className="flex items-center gap-2">
                    <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
                    <span>Processing...</span>
                  </div>
                ) : (
                  <>
                    <span>
                      {mode === 'signup' && 'Create Account'}
                      {mode === 'login' && 'Log In'}
                    </span>
                    <ArrowRight className="w-4 h-4" />
                  </>
                )}
              </button>
            </form>

            {/* Quick Demo Fill Helper Bar */}
            <div className="pt-2 border-t border-gray-100">
              <div className="flex items-center justify-between text-[11px] text-gray-500 mb-1.5">
                <span className="flex items-center gap-1 font-medium text-gray-600">
                  <Sparkles className="w-3 h-3 text-amber-500" />
                  Quick Test Autofill:
                </span>
                <div className="flex items-center gap-2">
                  <button
                    type="button"
                    onClick={() => handleQuickDemo('learner')}
                    className="px-2 py-0.5 rounded bg-gray-100 hover:bg-lime-100 hover:text-lime-800 text-gray-700 font-medium transition-colors cursor-pointer"
                  >
                    Learner
                  </button>
                  <button
                    type="button"
                    onClick={() => handleQuickDemo('pro')}
                    className="px-2 py-0.5 rounded bg-gray-100 hover:bg-lime-100 hover:text-lime-800 text-gray-700 font-medium transition-colors cursor-pointer"
                  >
                    Enterprise Pro
                  </button>
                </div>
              </div>
            </div>

            {/* Mode Switch Footers */}
            <div className="text-center pt-2">
              {mode === 'signup' && (
                <p className="text-xs sm:text-sm text-gray-600">
                  Already have an account?{' '}
                  <button
                    id="auth-switch-to-login"
                    type="button"
                    onClick={() => {
                      setMode('login');
                      setError(null);
                    }}
                    className="font-bold text-lime-700 hover:text-lime-800 hover:underline ml-1 cursor-pointer"
                  >
                    Log In
                  </button>
                </p>
              )}

              {mode === 'login' && (
                <div className="space-y-2">
                  <p className="text-xs sm:text-sm text-gray-600">
                    Don't have an account?{' '}
                    <button
                      id="auth-switch-to-signup"
                      type="button"
                      onClick={() => {
                        setMode('signup');
                        setError(null);
                      }}
                      className="font-bold text-lime-700 hover:text-lime-800 hover:underline ml-1 cursor-pointer"
                    >
                      Sign Up
                    </button>
                  </p>
                  <p className="text-[11px] text-gray-400">
                    By logging in, you agree to our{' '}
                    <button
                      type="button"
                      onClick={() => setShowTermsModal(true)}
                      className="hover:underline text-gray-500 cursor-pointer"
                    >
                      Terms of Service
                    </button>{' '}
                    and{' '}
                    <button
                      type="button"
                      onClick={() => setShowTermsModal(true)}
                      className="hover:underline text-gray-500 cursor-pointer"
                    >
                      Privacy Policy
                    </button>
                  </p>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>

      {/* Terms & Privacy Policy Quick Dialog */}
      {showTermsModal && (
        <div
          className="fixed inset-0 z-60 flex items-center justify-center p-4 bg-black/60 backdrop-blur-xs animate-in fade-in duration-150"
          onClick={() => setShowTermsModal(false)}
        >
          <div
            className="bg-white rounded-2xl max-w-lg w-full p-6 shadow-2xl border border-gray-100 max-h-[80vh] flex flex-col"
            onClick={(e) => e.stopPropagation()}
          >
            <div className="flex items-center justify-between pb-3 border-b border-gray-100">
              <div className="flex items-center gap-2">
                <ShieldCheck className="w-5 h-5 text-lime-600" />
                <h3 className="font-bold text-gray-900 text-lg">Terms of Service &amp; Privacy</h3>
              </div>
              <button
                onClick={() => setShowTermsModal(false)}
                className="text-gray-400 hover:text-gray-600 p-1 rounded-lg hover:bg-gray-100 cursor-pointer"
              >
                <X className="w-4 h-4" />
              </button>
            </div>
            <div className="py-4 text-xs sm:text-sm text-gray-600 overflow-y-auto space-y-3 leading-relaxed">
              <p>
                <strong>1. Ingage LMS Platform Access:</strong> By registering with Ingage LMS, you gain access to interactive job-role curriculums, sequential gated assessments (requiring 70% passing threshold), and career-readiness analytics.
              </p>
              <p>
                <strong>2. Academic Integrity &amp; Verified Certificates:</strong> Quiz completions, code reviews, and capstone milestones must represent your original work. Verified certificates are backed by digital credential IDs.
              </p>
              <p>
                <strong>3. Privacy &amp; GDPR Compliance:</strong> Your personal data is encrypted in transit and at rest with TLS 1.3 and AES-256. We never sell your personal contact information to third-party advertisers.
              </p>
            </div>
            <div className="pt-3 border-t border-gray-100 flex justify-end">
              <button
                type="button"
                onClick={() => {
                  setAgreeTerms(true);
                  setShowTermsModal(false);
                }}
                className="px-4 py-2 bg-lime-600 hover:bg-lime-700 text-white rounded-xl text-sm font-semibold transition-colors cursor-pointer"
              >
                I Understand &amp; Agree
              </button>
            </div>
          </div>
        </div>
      )}
    </>
  );
}
