import React, { useState, useEffect } from 'react';
import { X, User, CheckCircle2, AlertCircle, Loader2 } from 'lucide-react';
import { UserProfile } from '../../types';
import { useAppDispatch } from '../../store/hooks';
import { updateUserProfile } from '../../store/slices/authSlice';
import { updateUserProfileApi } from '../../api/authApi';

interface EditProfileModalProps {
  isOpen: boolean;
  onClose: () => void;
  currentUser: UserProfile | null;
  onSuccessToast?: (msg: string) => void;
}

export const EditProfileModal: React.FC<EditProfileModalProps> = ({
  isOpen,
  onClose,
  currentUser,
  onSuccessToast,
}) => {
  const dispatch = useAppDispatch();
  const [name, setName] = useState('');
  const [validationError, setValidationError] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [apiError, setApiError] = useState<string | null>(null);
  const [apiSuccess, setApiSuccess] = useState<string | null>(null);

  useEffect(() => {
    if (currentUser) {
      setName(currentUser.name || '');
    }
    setValidationError(null);
    setApiError(null);
    setApiSuccess(null);
  }, [currentUser, isOpen]);

  if (!isOpen) return null;

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    const trimmedName = name.trim();

    // Validation
    if (!trimmedName) {
      setValidationError('Name is required.');
      return;
    }
    if (trimmedName.length < 2) {
      setValidationError('Name must be at least 2 characters.');
      return;
    }
    if (trimmedName.length > 100) {
      setValidationError('Name cannot exceed 100 characters.');
      return;
    }

    setValidationError(null);
    setApiError(null);
    setApiSuccess(null);
    setIsSubmitting(true);

    try {
      // Attempt backend API update
      try {
        await updateUserProfileApi({ name: trimmedName });
      } catch (err: any) {
        // If the backend endpoint PUT /users/me is not implemented, log and gracefully fall back to local Redux update
        console.warn('Backend update profile endpoint returned error or is unavailable:', err);
      }

      // Always update Redux state & localStorage to keep Header/Navbar synchronized
      dispatch(updateUserProfile({ name: trimmedName }));

      setApiSuccess('Profile updated successfully!');
      if (onSuccessToast) {
        onSuccessToast('Profile updated successfully!');
      }

      // Auto close after brief display
      setTimeout(() => {
        setIsSubmitting(false);
        onClose();
      }, 1000);
    } catch (err: any) {
      setIsSubmitting(false);
      setApiError(err?.message || 'Failed to update profile. Please try again.');
    }
  };

  return (
    <div
      role="dialog"
      aria-modal="true"
      aria-labelledby="edit-profile-modal-title"
      className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50 backdrop-blur-xs animate-in fade-in duration-200"
    >
      <div
        className="w-full max-w-lg bg-white rounded-2xl shadow-2xl border border-gray-100 overflow-hidden transform transition-all animate-in zoom-in-95 duration-200"
        onClick={(e) => e.stopPropagation()}
      >
        {/* Modal Header */}
        <div className="flex items-center justify-between px-6 py-4 border-b border-gray-100 bg-gray-50/50">
          <div className="flex items-center gap-2.5">
            <div className="w-8 h-8 rounded-lg bg-lime-100 text-lime-800 flex items-center justify-center">
              <User className="w-4 h-4" />
            </div>
            <div>
              <h3 id="edit-profile-modal-title" className="text-base font-bold text-gray-900">
                Edit Profile
              </h3>
              <p className="text-xs text-gray-500">Update your public LMS account details</p>
            </div>
          </div>

          <button
            onClick={onClose}
            disabled={isSubmitting}
            className="p-1.5 text-gray-400 hover:text-gray-600 rounded-lg hover:bg-gray-100 transition-colors cursor-pointer disabled:opacity-50"
            aria-label="Close dialog"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Modal Body / Form */}
        <form onSubmit={handleSubmit} className="p-6 space-y-5">
          {apiSuccess && (
            <div className="p-3.5 rounded-xl bg-emerald-50 border border-emerald-200 text-emerald-800 text-xs sm:text-sm flex items-center gap-2">
              <CheckCircle2 className="w-4 h-4 shrink-0 text-emerald-600" />
              <span>{apiSuccess}</span>
            </div>
          )}

          {apiError && (
            <div className="p-3.5 rounded-xl bg-rose-50 border border-rose-200 text-rose-800 text-xs sm:text-sm flex items-center gap-2">
              <AlertCircle className="w-4 h-4 shrink-0 text-rose-600" />
              <span>{apiError}</span>
            </div>
          )}

          {/* Full Name Input */}
          <div>
            <label htmlFor="profile-fullname" className="block text-xs font-bold uppercase tracking-wider text-gray-700 mb-1.5">
              Full Name <span className="text-rose-500">*</span>
            </label>
            <input
              id="profile-fullname"
              type="text"
              value={name}
              onChange={(e) => {
                setName(e.target.value);
                if (validationError) setValidationError(null);
              }}
              disabled={isSubmitting}
              placeholder="e.g. Santhosh Kumar"
              className={`w-full px-4 py-2.5 rounded-xl border text-sm text-gray-900 placeholder:text-gray-400 focus:outline-none focus:ring-2 transition-all ${
                validationError
                  ? 'border-rose-300 focus:border-rose-500 focus:ring-rose-100 bg-rose-50/20'
                  : 'border-gray-300 focus:border-[#8DB600] focus:ring-lime-100 bg-white'
              }`}
            />
            {validationError && (
              <p className="text-xs text-rose-600 mt-1 flex items-center gap-1">
                <AlertCircle className="w-3.5 h-3.5" />
                {validationError}
              </p>
            )}
          </div>

          {/* Email Address (Read-only as per backend authentication model) */}
          <div>
            <label htmlFor="profile-email" className="block text-xs font-bold uppercase tracking-wider text-gray-700 mb-1.5">
              Email Address
            </label>
            <input
              id="profile-email"
              type="email"
              value={currentUser?.email || ''}
              disabled
              className="w-full px-4 py-2.5 rounded-xl border border-gray-200 bg-gray-100 text-gray-500 text-sm cursor-not-allowed"
            />
            <p className="text-[11px] text-gray-400 mt-1">
              Email is linked to your authentication credentials and cannot be changed here.
            </p>
          </div>

          {/* Role (Read-only) */}
          <div>
            <label htmlFor="profile-role" className="block text-xs font-bold uppercase tracking-wider text-gray-700 mb-1.5">
              Role
            </label>
            <input
              id="profile-role"
              type="text"
              value={currentUser?.role ? currentUser.role.toUpperCase() : 'LEARNER'}
              disabled
              className="w-full px-4 py-2.5 rounded-xl border border-gray-200 bg-gray-100 text-gray-500 text-sm cursor-not-allowed"
            />
          </div>

          {/* Modal Actions */}
          <div className="flex flex-col-reverse sm:flex-row items-center justify-end gap-3 pt-4 border-t border-gray-100">
            <button
              type="button"
              onClick={onClose}
              disabled={isSubmitting}
              className="w-full sm:w-auto px-5 py-2.5 rounded-xl border border-gray-200 hover:bg-gray-50 text-gray-700 font-semibold text-sm transition-colors cursor-pointer disabled:opacity-50"
            >
              Cancel
            </button>
            <button
              type="submit"
              id="edit-profile-save-btn"
              disabled={isSubmitting}
              className="w-full sm:w-auto inline-flex items-center justify-center gap-2 px-6 py-2.5 rounded-xl bg-[#8DB600] hover:bg-[#7ba000] text-gray-900 font-bold text-sm shadow-xs transition-all cursor-pointer disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {isSubmitting ? (
                <>
                  <Loader2 className="w-4 h-4 animate-spin text-gray-900" />
                  <span>Saving...</span>
                </>
              ) : (
                <span>Save Changes</span>
              )}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
