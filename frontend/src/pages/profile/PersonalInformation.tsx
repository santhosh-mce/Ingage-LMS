import React from 'react';
import { User, Mail, Shield, Phone, KeyRound, CheckCircle2 } from 'lucide-react';
import { UserProfile } from '../../types';

interface PersonalInformationProps {
  user: UserProfile | null;
}

export const PersonalInformation: React.FC<PersonalInformationProps> = ({ user }) => {
  return (
    <div className="bg-white rounded-2xl border border-gray-100 p-6 sm:p-8 shadow-xs">
      <div className="flex items-center justify-between mb-6 pb-4 border-b border-gray-100">
        <div>
          <h2 className="text-lg font-bold text-gray-900 tracking-tight">
            Personal Information
          </h2>
          <p className="text-xs sm:text-sm text-gray-500 mt-0.5">
            Your verified account details and credentials
          </p>
        </div>
        <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-semibold bg-emerald-50 text-emerald-700 border border-emerald-200">
          <CheckCircle2 className="w-3.5 h-3.5" />
          Active Account
        </span>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
        {/* Full Name */}
        <div className="p-4 rounded-xl bg-gray-50/60 border border-gray-100">
          <div className="flex items-center gap-2 text-xs font-semibold text-gray-500 uppercase tracking-wider mb-1">
            <User className="w-4 h-4 text-gray-400" />
            <span>Full Name</span>
          </div>
          <p className="text-sm sm:text-base font-bold text-gray-900 break-words">
            {user?.name ? user.name : <span className="text-gray-400 font-normal italic">Not provided</span>}
          </p>
        </div>

        {/* Email Address */}
        <div className="p-4 rounded-xl bg-gray-50/60 border border-gray-100">
          <div className="flex items-center gap-2 text-xs font-semibold text-gray-500 uppercase tracking-wider mb-1">
            <Mail className="w-4 h-4 text-gray-400" />
            <span>Email Address</span>
          </div>
          <p className="text-sm sm:text-base font-bold text-gray-900 break-words">
            {user?.email ? user.email : <span className="text-gray-400 font-normal italic">Not provided</span>}
          </p>
        </div>

        {/* User Role */}
        <div className="p-4 rounded-xl bg-gray-50/60 border border-gray-100">
          <div className="flex items-center gap-2 text-xs font-semibold text-gray-500 uppercase tracking-wider mb-1">
            <Shield className="w-4 h-4 text-gray-400" />
            <span>Account Role</span>
          </div>
          <p className="text-sm sm:text-base font-bold text-gray-900 capitalize">
            {user?.role ? user.role.toLowerCase() : 'Learner'}
          </p>
        </div>

        {/* Account ID */}
        <div className="p-4 rounded-xl bg-gray-50/60 border border-gray-100 sm:col-span-2 lg:col-span-2">
          <div className="flex items-center gap-2 text-xs font-semibold text-gray-500 uppercase tracking-wider mb-1">
            <KeyRound className="w-4 h-4 text-gray-400" />
            <span>Learner ID</span>
          </div>
          <p className="text-xs sm:text-sm font-mono text-gray-700 break-all select-all">
            {user?.id ? user.id : <span className="text-gray-400 font-sans italic">Not provided</span>}
          </p>
        </div>

        {/* Phone Number */}
        <div className="p-4 rounded-xl bg-gray-50/60 border border-gray-100">
          <div className="flex items-center gap-2 text-xs font-semibold text-gray-500 uppercase tracking-wider mb-1">
            <Phone className="w-4 h-4 text-gray-400" />
            <span>Phone Number</span>
          </div>
          <p className="text-sm sm:text-base font-medium text-gray-700">
            <span className="text-gray-400 font-normal italic">Not provided</span>
          </p>
        </div>
      </div>
    </div>
  );
};
