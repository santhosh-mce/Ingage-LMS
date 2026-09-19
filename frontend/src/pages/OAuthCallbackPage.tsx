import React, { useEffect, useState, useRef } from 'react';
import { useAuth } from '../context/AuthContext';
import { getCurrentUser, getAccessibleImageUrl } from '../api/authApi';
import { UserProfile } from '../types';

interface OAuthCallbackPageProps {
  onNavigate: (path: string) => void;
  onShowToast: (msg: string) => void;
}

export const OAuthCallbackPage: React.FC<OAuthCallbackPageProps> = ({
  onNavigate,
  onShowToast,
}) => {
  const { login } = useAuth();
  const [status, setStatus] = useState<'loading' | 'success' | 'error'>('loading');
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const processedRef = useRef<boolean>(false);

  useEffect(() => {
    if (processedRef.current) return;
    processedRef.current = true;

    const urlParams = new URLSearchParams(window.location.search);
    const token = urlParams.get('token');
    const error = urlParams.get('error');

    if (error) {
      const decodedError = decodeURIComponent(error);
      setStatus('error');
      setErrorMessage(decodedError);
      onShowToast(`Social sign-in error: ${decodedError}`);
      return;
    }

    if (!token) {
      setStatus('error');
      setErrorMessage('No authentication token received from identity provider.');
      onShowToast('Social sign-in failed: missing authentication token.');
      return;
    }

    // Persist token in local storage
    localStorage.setItem('ingage_token', token);

    // Fetch user profile using existing API client
    getCurrentUser()
      .then((user) => {
        const rawAvatar = user.avatarUrl || user.profileImage || user.avatar;
        const resolvedAvatar = rawAvatar ? getAccessibleImageUrl(rawAvatar) : undefined;
        const profile: UserProfile = {
          id: user.userId,
          name: user.name,
          email: user.email,
          role: user.role,
          avatarUrl: resolvedAvatar,
          enrolledPaths: [],
        };

        // Update Redux state and persistent storage
        login(token, profile);
        setStatus('success');

        // Check for saved post-auth redirect
        const savedRedirect = sessionStorage.getItem('ingage_redirect_after_auth');
        const targetPath =
          savedRedirect &&
          savedRedirect !== '/login' &&
          savedRedirect !== '/signup' &&
          savedRedirect !== '/forgot-password' &&
          savedRedirect !== '/'
            ? savedRedirect
            : user.role === 'ADMIN'
            ? '/admin'
            : '/';

        if (savedRedirect) {
          try {
            sessionStorage.removeItem('ingage_redirect_after_auth');
          } catch {
            // ignore
          }
        }

        onShowToast(`Welcome back, ${user.name}!`);

        // Smooth redirect to destination
        setTimeout(() => {
          onNavigate(targetPath);
        }, 600);
      })
      .catch((err) => {
        localStorage.removeItem('ingage_token');
        localStorage.removeItem('ingage_user');
        setStatus('error');
        const msg =
          err?.response?.data?.message ||
          err?.response?.data?.error ||
          'Failed to verify your identity. Please try signing in again.';
        setErrorMessage(msg);
        onShowToast('Social sign-in could not be completed.');
      });
  }, [login, onNavigate, onShowToast]);

  return (
    <div className="min-h-screen bg-slate-950 flex flex-col items-center justify-center p-6 text-white select-none">
      <div className="relative w-full max-w-md p-8 rounded-2xl bg-slate-900/90 border border-slate-800 shadow-2xl backdrop-blur-xl flex flex-col items-center text-center overflow-hidden">
        {/* Ambient Top Glow */}
        <div className="absolute top-0 left-1/2 -translate-x-1/2 w-48 h-1 bg-gradient-to-r from-transparent via-lime-400 to-transparent" />

        {status === 'loading' && (
          <>
            <div className="relative mb-6">
              <div className="w-16 h-16 rounded-full border-2 border-slate-700 border-t-lime-400 animate-spin" />
              <div className="absolute inset-0 flex items-center justify-center">
                <div className="w-8 h-8 rounded-full bg-lime-400/10 flex items-center justify-center">
                  <div className="w-3 h-3 rounded-full bg-lime-400 animate-pulse" />
                </div>
              </div>
            </div>
            <h2 className="text-xl font-bold tracking-tight text-white mb-2">
              Authenticating with Google
            </h2>
            <p className="text-sm text-slate-400 max-w-xs">
              Verifying your security credentials and preparing your learning dashboard...
            </p>
          </>
        )}

        {status === 'success' && (
          <>
            <div className="w-16 h-16 mb-6 rounded-full bg-lime-500/20 border border-lime-500/40 flex items-center justify-center text-lime-400">
              <svg
                className="w-8 h-8"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
                strokeWidth={2.5}
              >
                <path strokeLinecap="round" strokeLinejoin="round" d="M5 13l4 4L19 7" />
              </svg>
            </div>
            <h2 className="text-xl font-bold tracking-tight text-white mb-2">
              Authentication Successful
            </h2>
            <p className="text-sm text-slate-400 max-w-xs">
              Redirecting you to Ingage LMS...
            </p>
          </>
        )}

        {status === 'error' && (
          <>
            <div className="w-16 h-16 mb-6 rounded-full bg-red-500/20 border border-red-500/40 flex items-center justify-center text-red-400">
              <svg
                className="w-8 h-8"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
                strokeWidth={2}
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"
                />
              </svg>
            </div>
            <h2 className="text-xl font-bold tracking-tight text-white mb-2">
              Sign-in Unsuccessful
            </h2>
            <p className="text-sm text-slate-400 max-w-xs mb-6">
              {errorMessage || 'Unable to authenticate with Google. Please try again.'}
            </p>
            <button
              onClick={() => onNavigate('/login')}
              className="w-full py-2.5 px-4 rounded-xl bg-lime-500 hover:bg-lime-400 text-slate-950 font-semibold text-sm transition shadow-lg shadow-lime-500/20"
            >
              Return to Sign In
            </button>
          </>
        )}
      </div>
    </div>
  );
};
