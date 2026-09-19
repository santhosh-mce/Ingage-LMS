import React, { useState, useEffect } from 'react';
import {
  User,
  Settings,
  BookOpen,
  Award,
  Shield,
  TrendingUp,
  RefreshCw,
  AlertCircle,
  Clock,
  CheckCircle2,
  ExternalLink
} from 'lucide-react';
import { UserProfile } from '../types';
import { useAppDispatch, useAppSelector } from '../store/hooks';
import { logoutUserThunk } from '../store/slices/authSlice';
import { getMyEnrollments, UserEnrollmentRecord } from '../api/paymentApi';

import { ProfileHeader } from './profile/ProfileHeader';
import { PersonalInformation } from './profile/PersonalInformation';
import { EditProfileModal } from './profile/EditProfileModal';
import { ProfileImageModal } from './profile/ProfileImageModal';
import { LearningStatistics } from './profile/LearningStatistics';
import { LearningProgress } from './profile/LearningProgress';
import { EnrolledCourses } from './profile/EnrolledCourses';
import { AccountSettings } from './profile/AccountSettings';

export type ProfileTab =
  | 'profile'
  | 'certificates'
  | 'downloads'
  | 'tickets'
  | 'help'
  | 'settings'
  | 'reset-password';

interface ProfilePageProps {
  initialTab?: ProfileTab;
  currentUser?: UserProfile | null;
  onNavigate: (path: string, param?: string) => void;
  onShowToast?: (msg: string, type?: 'success' | 'info' | 'error') => void;
}

export const ProfilePage: React.FC<ProfilePageProps> = ({
  initialTab = 'profile',
  currentUser: propUser,
  onNavigate,
  onShowToast,
}) => {
  const dispatch = useAppDispatch();
  const { user: authUser } = useAppSelector((state) => state.auth);
  const activeUser = propUser || authUser;

  const [activeTab, setActiveTab] = useState<ProfileTab>(initialTab);
  const [isEditProfileOpen, setIsEditProfileOpen] = useState(false);
  const [isPhotoModalOpen, setIsPhotoModalOpen] = useState(false);

  // Enrollments & Learning Data
  const [enrollments, setEnrollments] = useState<UserEnrollmentRecord[]>([]);
  const [loadingEnrollments, setLoadingEnrollments] = useState<boolean>(true);
  const [enrollmentError, setEnrollmentError] = useState<string | null>(null);

  const fetchEnrollmentData = () => {
    let isMounted = true;
    setLoadingEnrollments(true);
    setEnrollmentError(null);

    getMyEnrollments()
      .then((data) => {
        if (isMounted) {
          setEnrollments(data || []);
        }
      })
      .catch((err) => {
        console.warn('Failed to load user enrollments in ProfilePage:', err);
        if (isMounted) {
          setEnrollmentError('Could not load course enrollments. Please try again.');
        }
      })
      .finally(() => {
        if (isMounted) {
          setLoadingEnrollments(false);
        }
      });

    return () => {
      isMounted = false;
    };
  };

  useEffect(() => {
    fetchEnrollmentData();
  }, []);

  useEffect(() => {
    if (initialTab) {
      setActiveTab(initialTab);
    }
  }, [initialTab]);

  const handleLogout = async () => {
    try {
      await dispatch(logoutUserThunk()).unwrap();
      if (onShowToast) {
        onShowToast('Logged out successfully.', 'info');
      }
      onNavigate('/');
    } catch {
      onNavigate('/');
    }
  };

  return (
    <div className="w-full bg-[#f8fafc] min-h-[calc(100vh-4.5rem)] pb-16">
      <div className="w-full px-4 sm:px-8 lg:px-12 xl:px-16 2xl:px-20 pt-8 space-y-6 max-w-7xl mx-auto">
        {/* Navigation Breadcrumb / Tabs Bar */}
        <div className="flex items-center justify-between flex-wrap gap-4 pb-2">
          <div className="flex items-center gap-1.5 p-1 bg-gray-200/70 rounded-2xl">
            <button
              id="profile-tab-overview"
              onClick={() => setActiveTab('profile')}
              className={`inline-flex items-center gap-2 px-4 py-2 rounded-xl text-xs sm:text-sm font-bold transition-all cursor-pointer ${
                activeTab === 'profile'
                  ? 'bg-white text-gray-900 shadow-xs'
                  : 'text-gray-600 hover:text-gray-900 hover:bg-white/50'
              }`}
            >
              <User className="w-4 h-4" />
              <span>Profile & Learning</span>
            </button>

            <button
              id="profile-tab-settings"
              onClick={() => setActiveTab('settings')}
              className={`inline-flex items-center gap-2 px-4 py-2 rounded-xl text-xs sm:text-sm font-bold transition-all cursor-pointer ${
                activeTab === 'settings'
                  ? 'bg-white text-gray-900 shadow-xs'
                  : 'text-gray-600 hover:text-gray-900 hover:bg-white/50'
              }`}
            >
              <Settings className="w-4 h-4" />
              <span>Account Settings</span>
            </button>
          </div>

          <div className="flex items-center gap-2">
            <button
              onClick={fetchEnrollmentData}
              disabled={loadingEnrollments}
              className="p-2 text-gray-500 hover:text-gray-900 hover:bg-gray-100 rounded-xl transition-colors cursor-pointer disabled:opacity-50"
              title="Refresh learning data"
              aria-label="Refresh data"
            >
              <RefreshCw className={`w-4 h-4 ${loadingEnrollments ? 'animate-spin text-[#8DB600]' : ''}`} />
            </button>
          </div>
        </div>

        {/* Global Error Banner if fetching fails */}
        {enrollmentError && (
          <div className="p-4 rounded-2xl bg-rose-50 border border-rose-200 text-rose-800 text-sm flex items-center justify-between gap-3">
            <div className="flex items-center gap-2">
              <AlertCircle className="w-5 h-5 text-rose-600 shrink-0" />
              <span>{enrollmentError}</span>
            </div>
            <button
              onClick={fetchEnrollmentData}
              className="px-3 py-1 bg-rose-100 hover:bg-rose-200 text-rose-900 rounded-lg text-xs font-bold transition-colors cursor-pointer shrink-0"
            >
              Retry
            </button>
          </div>
        )}

        {/* 1. Main Profile & Learning Tab Content */}
        {activeTab === 'profile' && (
          <div className="space-y-6">
            {/* Profile Header */}
            <ProfileHeader
              user={activeUser}
              onEditProfile={() => setIsEditProfileOpen(true)}
              onChangePhoto={() => setIsPhotoModalOpen(true)}
            />

            {/* Learning Statistics */}
            <LearningStatistics
              enrollments={enrollments}
              loading={loadingEnrollments}
            />

            {/* Personal Information */}
            <PersonalInformation user={activeUser} />

            {/* Learning Progress Section */}
            <LearningProgress
              enrollments={enrollments}
              onNavigate={onNavigate}
              loading={loadingEnrollments}
            />

            {/* Enrolled Courses Section */}
            <EnrolledCourses
              enrollments={enrollments}
              onNavigate={onNavigate}
              loading={loadingEnrollments}
            />
          </div>
        )}

        {/* 2. Account Settings Tab Content */}
        {activeTab === 'settings' && (
          <AccountSettings
            user={activeUser}
            onLogout={handleLogout}
            onNavigate={onNavigate}
          />
        )}

        {/* 3. Other sub-tabs fallback (e.g. certificates, help) */}
        {activeTab !== 'profile' && activeTab !== 'settings' && (
          <div className="bg-white rounded-2xl border border-gray-100 p-8 text-center space-y-4">
            <Award className="w-12 h-12 text-[#8DB600] mx-auto" />
            <h3 className="text-lg font-bold text-gray-900 capitalize">
              {activeTab.replace('-', ' ')}
            </h3>
            <p className="text-sm text-gray-500 max-w-md mx-auto">
              View your achievements and verification credentials through the official certificate verification hub.
            </p>
            <div className="flex items-center justify-center gap-3 pt-2">
              <button
                onClick={() => setActiveTab('profile')}
                className="px-5 py-2 rounded-xl bg-gray-100 hover:bg-gray-200 text-gray-800 font-bold text-xs transition-colors cursor-pointer"
              >
                Back to Profile
              </button>
              <button
                onClick={() => onNavigate('/certificates/verify')}
                className="inline-flex items-center gap-1.5 px-5 py-2 rounded-xl bg-[#8DB600] hover:bg-[#7ba000] text-gray-900 font-bold text-xs shadow-xs transition-colors cursor-pointer"
              >
                <span>Verify Credentials</span>
                <ExternalLink className="w-3.5 h-3.5" />
              </button>
            </div>
          </div>
        )}
      </div>

      {/* Edit Profile Modal */}
      <EditProfileModal
        isOpen={isEditProfileOpen}
        onClose={() => setIsEditProfileOpen(false)}
        currentUser={activeUser}
        onSuccessToast={(msg) => onShowToast && onShowToast(msg, 'success')}
      />

      {/* Profile Image Modal */}
      <ProfileImageModal
        isOpen={isPhotoModalOpen}
        onClose={() => setIsPhotoModalOpen(false)}
        currentUser={activeUser}
        onSuccessToast={(msg) => onShowToast && onShowToast(msg, 'success')}
      />
    </div>
  );
};
