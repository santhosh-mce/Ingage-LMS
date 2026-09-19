import React, { useState, useEffect } from 'react';
import { User as UserIcon, Pencil, Mail, Shield, CheckCircle, Camera } from 'lucide-react';
import { UserProfile } from '../../types';
import { getAccessibleImageUrl } from '../../api/authApi';

interface ProfileHeaderProps {
  user: UserProfile | null;
  onEditProfile: () => void;
  onChangePhoto?: () => void;
}

export const ProfileHeader: React.FC<ProfileHeaderProps> = ({ user, onEditProfile, onChangePhoto }) => {
  const [imageError, setImageError] = useState(false);

  useEffect(() => {
    setImageError(false);
  }, [user?.avatarUrl]);

  const displayAvatar = getAccessibleImageUrl(user?.avatarUrl);

  const initials = user?.name
    ? user.name
        .split(' ')
        .map((n) => n[0])
        .join('')
        .slice(0, 2)
        .toUpperCase()
    : 'U';

  const roleDisplay = user?.role
    ? user.role.charAt(0).toUpperCase() + user.role.slice(1).toLowerCase()
    : 'Learner';

  return (
    <div className="bg-white rounded-2xl border border-gray-100 p-6 sm:p-8 shadow-xs relative overflow-hidden">
      {/* Decorative subtle background accent */}
      <div className="absolute top-0 right-0 -mt-8 -mr-8 w-40 h-40 bg-lime-50 rounded-full blur-2xl pointer-events-none opacity-60" />

      <div className="flex flex-col sm:flex-row items-center sm:items-start justify-between gap-6 relative z-10">
        {/* Left: Avatar & Main Identity */}
        <div className="flex flex-col sm:flex-row items-center sm:items-center gap-5 text-center sm:text-left">
          {/* Avatar with Camera edit button */}
          <div className="relative group shrink-0">
            {displayAvatar && !imageError ? (
              <img
                src={displayAvatar}
                alt={user?.name || 'User avatar'}
                onError={() => setImageError(true)}
                className="w-24 h-24 sm:w-28 sm:h-28 rounded-2xl object-cover ring-4 ring-lime-50 shadow-md"
              />
            ) : (
              <div className="w-24 h-24 sm:w-28 sm:h-28 rounded-2xl bg-gradient-to-br from-lime-100 to-lime-200 border-2 border-lime-300/60 flex items-center justify-center text-lime-900 font-extrabold text-2xl sm:text-3xl shadow-xs ring-4 ring-lime-50">
                {initials}
              </div>
            )}

            {/* Change Photo Camera Overlay Button */}
            {onChangePhoto && (
              <button
                id="change-profile-photo-btn"
                type="button"
                onClick={onChangePhoto}
                className="absolute -top-1.5 -left-1.5 bg-slate-900 hover:bg-slate-950 text-white p-2 rounded-xl shadow-md transition-all cursor-pointer hover:scale-105 border-2 border-white focus:outline-none focus:ring-2 focus:ring-lime-400"
                title="Change Profile Photo"
                aria-label="Change Profile Photo"
              >
                <Camera className="w-3.5 h-3.5 text-lime-400" />
              </button>
            )}

            <div
              className="absolute -bottom-1 -right-1 bg-[#8DB600] text-white p-1 rounded-full ring-2 ring-white shadow-xs"
              title="Verified Learner"
            >
              <CheckCircle className="w-4 h-4" />
            </div>
          </div>

          {/* User Details */}
          <div>
            <div className="flex flex-wrap items-center justify-center sm:justify-start gap-2.5">
              <h1 className="text-2xl sm:text-3xl font-extrabold text-gray-900 tracking-tight">
                {user?.name || 'Learner'}
              </h1>
              <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-bold uppercase tracking-wider bg-lime-100 text-lime-800 border border-lime-200">
                <Shield className="w-3 h-3 text-lime-700" />
                {roleDisplay}
              </span>
            </div>

            <div className="flex flex-wrap items-center justify-center sm:justify-start gap-3 mt-2 text-sm text-gray-500">
              <span className="flex items-center gap-1.5">
                <Mail className="w-4 h-4 text-gray-400" />
                {user?.email || 'No email associated'}
              </span>
            </div>
          </div>
        </div>

        {/* Right: Edit Profile Action Button */}
        <div className="w-full sm:w-auto shrink-0">
          <button
            id="edit-profile-open-btn"
            onClick={onEditProfile}
            className="w-full sm:w-auto inline-flex items-center justify-center gap-2 px-5 py-2.5 rounded-xl bg-white hover:bg-lime-50 border border-gray-200 hover:border-lime-300 text-gray-700 hover:text-lime-800 font-semibold text-sm shadow-xs transition-all cursor-pointer focus:outline-none focus:ring-2 focus:ring-lime-300"
          >
            <Pencil className="w-4 h-4 text-gray-500 group-hover:text-lime-700" />
            <span>Edit Profile</span>
          </button>
        </div>
      </div>
    </div>
  );
};
