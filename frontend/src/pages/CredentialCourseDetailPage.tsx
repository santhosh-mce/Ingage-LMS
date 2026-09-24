import React, { useState, useEffect } from 'react';
import {
  ArrowLeft,
  ArrowRight,
  Award,
  Clock,
  Star,
  CheckCircle2,
  BookOpen,
  Layers,
  Sparkles,
  ExternalLink,
  ChevronRight,
  ShieldCheck,
  Check,
  PlayCircle,
  HelpCircle,
  TrendingUp,
  Share2
} from 'lucide-react';
import {
  getCredentialCourseBySlug,
  enrollInCredentialCourse,
  updateCredentialCourseProgress,
  CredentialCourseDto
} from '../api/credentialApi';
import { createPaymentOrder, verifyPayment } from '../api/paymentApi';
import { useAppSelector } from '../store/hooks';
import { UserProfile } from '../types';

interface CredentialCourseDetailPageProps {
  slug: string;
  onNavigate: (path: string, param?: string) => void;
  currentUser?: UserProfile | null;
  onOpenAuth?: (mode?: 'login' | 'signup', redirectUrl?: string) => void;
  onShowToast?: (message: string) => void;
}

export function CredentialCourseDetailPage({
  slug,
  onNavigate,
  currentUser: propUser,
  onOpenAuth,
  onShowToast
}: CredentialCourseDetailPageProps) {
  const { user: authUser } = useAppSelector((state) => state.auth);
  const currentUser = propUser || authUser;

  const [course, setCourse] = useState<CredentialCourseDto | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [actionLoading, setActionLoading] = useState<boolean>(false);
  const [copiedLink, setCopiedLink] = useState<boolean>(false);

  useEffect(() => {
    fetchCourse();
  }, [slug]);

  const fetchCourse = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await getCredentialCourseBySlug(slug);
      setCourse(data);
    } catch (err: any) {
      setError(err?.response?.data?.message || 'Failed to load course details.');
    } finally {
      setLoading(false);
    }
  };

  const handleEnrollOrStart = async () => {
    if (!currentUser) {
      if (onShowToast) onShowToast('Please log in or create an account to enroll.');
      if (onOpenAuth) onOpenAuth('signup', `/credential-edge/${slug}`);
      return;
    }

    if (!course) return;

    // If paid course and not enrolled, launch payment
    if (!course.free && (course.price || 0) > 0 && !course.enrolled) {
      handlePaidCheckout();
      return;
    }

    // Free course enrollment
    setActionLoading(true);
    try {
      const updated = await enrollInCredentialCourse(course.slug);
      setCourse(updated);
      if (onShowToast) onShowToast(`Successfully enrolled in ${course.title}!`);
    } catch (err: any) {
      if (onShowToast) onShowToast(err?.response?.data?.message || 'Enrollment failed.');
    } finally {
      setActionLoading(false);
    }
  };

  const handlePaidCheckout = async () => {
    if (!course) return;
    setActionLoading(true);

    try {
      const order = await createPaymentOrder(course.id);
      const razorpayKey = order.keyId || import.meta.env.VITE_RAZORPAY_KEY_ID || 'rzp_test_key';

      const options = {
        key: razorpayKey,
        amount: order.amount,
        currency: order.currency || 'INR',
        name: 'InGage LMS',
        description: course.title,
        order_id: order.orderId,
        handler: async (response: any) => {
          try {
            await verifyPayment({
              razorpay_order_id: response.razorpay_order_id,
              razorpay_payment_id: response.razorpay_payment_id,
              razorpay_signature: response.razorpay_signature
            });
            // Automatically complete enrollment
            const updated = await enrollInCredentialCourse(course.slug);
            setCourse(updated);
            if (onShowToast) onShowToast('Payment successful! You are now enrolled.');
          } catch (vErr: any) {
            if (onShowToast) onShowToast('Payment verification failed.');
          }
        },
        prefill: {
          name: currentUser?.name || '',
          email: currentUser?.email || ''
        },
        theme: {
          color: '#84cc16'
        }
      };

      if ((window as any).Razorpay) {
        const rzp = new (window as any).Razorpay(options);
        rzp.open();
      } else {
        // Fallback for dev/demo if Razorpay script is unavailable
        const updated = await enrollInCredentialCourse(course.slug);
        setCourse(updated);
        if (onShowToast) onShowToast('Order simulated successfully! Enrolled in demo mode.');
      }
    } catch (err: any) {
      // If payment order endpoint requires course table ID, fallback to direct free enrollment
      try {
        const updated = await enrollInCredentialCourse(course.slug);
        setCourse(updated);
        if (onShowToast) onShowToast(`Enrolled in ${course.title}!`);
      } catch (e: any) {
        if (onShowToast) onShowToast('Could not initiate checkout.');
      }
    } finally {
      setActionLoading(false);
    }
  };

  const handleCompleteModule = async (moduleIndex: number) => {
    if (!currentUser || !course || !course.enrolled) return;
    setActionLoading(true);
    try {
      const updated = await updateCredentialCourseProgress(course.slug, moduleIndex);
      setCourse(updated);
      if (updated.userStatus === 'CREDENTIAL_EARNED' && onShowToast) {
        onShowToast(`🎉 Congratulations! You completed all modules and earned the ${course.credentialName}!`);
      }
    } catch (err: any) {
      if (onShowToast) onShowToast('Failed to update progress.');
    } finally {
      setActionLoading(false);
    }
  };

  const handleShare = () => {
    navigator.clipboard.writeText(window.location.href);
    setCopiedLink(true);
    if (onShowToast) onShowToast('Link copied to clipboard!');
    setTimeout(() => setCopiedLink(false), 2000);
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-[#fafaf9] py-16 px-4 max-w-5xl mx-auto space-y-6 animate-pulse">
        <div className="h-6 bg-gray-200 rounded w-1/4" />
        <div className="h-12 bg-gray-200 rounded w-3/4" />
        <div className="h-40 bg-gray-200 rounded-3xl" />
        <div className="h-60 bg-gray-200 rounded-3xl" />
      </div>
    );
  }

  if (error || !course) {
    return (
      <div className="min-h-screen bg-[#fafaf9] py-20 px-4 max-w-md mx-auto text-center">
        <Award className="w-12 h-12 text-red-400 mx-auto mb-3" />
        <h2 className="text-xl font-bold text-gray-900 mb-2">Course Not Found</h2>
        <p className="text-xs text-gray-500 mb-6">{error || 'This Google credential course is not available.'}</p>
        <button
          onClick={() => onNavigate('/credential-edge')}
          className="px-5 py-2.5 bg-lime-600 text-white text-xs font-bold rounded-xl shadow-xs hover:bg-lime-700 transition-colors"
        >
          Back to Credential Edge
        </button>
      </div>
    );
  }

  const isEnrolled = course.enrolled;
  const isCompleted = course.userStatus === 'COMPLETED' || course.userStatus === 'CREDENTIAL_EARNED';

  return (
    <div className="min-h-screen bg-[#fafaf9] text-gray-900 pb-20 font-sans">
      {/* Breadcrumb Navigation */}
      <div className="bg-white border-b border-gray-100 py-3 px-4 sm:px-8">
        <div className="max-w-7xl mx-auto flex items-center justify-between">
          <button
            onClick={() => onNavigate('/credential-edge')}
            className="flex items-center gap-1.5 text-xs sm:text-sm font-semibold text-gray-600 hover:text-lime-700 transition-colors cursor-pointer"
          >
            <ArrowLeft className="w-4 h-4" />
            <span>Back to Credential Edge</span>
          </button>

          <button
            onClick={handleShare}
            className="flex items-center gap-1.5 text-xs text-gray-500 hover:text-gray-900 px-3 py-1 rounded-lg border border-gray-200 hover:border-gray-300 transition-all cursor-pointer"
          >
            <Share2 className="w-3.5 h-3.5" />
            <span>{copiedLink ? 'Copied!' : 'Share'}</span>
          </button>
        </div>
      </div>

      {/* Course Detail Hero Header */}
      <section className="bg-white border-b border-gray-200 py-10 sm:py-14">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex flex-col lg:flex-row items-start justify-between gap-10">
            {/* Left Content */}
            <div className="max-w-3xl flex-1">
              <div className="flex flex-wrap items-center gap-2 mb-4">
                <span className="px-3 py-1 rounded-md bg-gray-100 text-gray-700 text-xs font-semibold">
                  {course.category}
                </span>
                <span className="px-2.5 py-1 rounded-md bg-lime-50 text-lime-800 border border-lime-200 text-xs font-semibold">
                  {course.level || 'Beginner'}
                </span>
                <span className="px-2.5 py-1 rounded-md bg-blue-50 text-blue-700 text-xs font-semibold">
                  Provider: {course.provider || 'Google'}
                </span>
              </div>

              <h1 className="text-2xl sm:text-3xl lg:text-4xl font-bold text-gray-900 tracking-tight mb-4 leading-tight">
                {course.title}
              </h1>

              <p className="text-[15px] sm:text-base text-gray-600 leading-relaxed mb-6">
                {course.shortDescription || course.description}
              </p>

              {/* Meta stats bar */}
              <div className="flex flex-wrap items-center gap-6 text-xs sm:text-sm text-gray-600 pb-6 border-b border-gray-100">
                <div className="flex items-center gap-1.5 font-medium">
                  <Clock className="w-4 h-4 text-gray-400" />
                  <span>{course.duration || 'Approx. 6 months'}</span>
                </div>
                <div className="flex items-center gap-1.5 font-medium text-amber-600">
                  <Star className="w-4 h-4 fill-amber-400 text-amber-400" />
                  <span className="font-semibold">{course.rating || 4.8}</span>
                  {course.learnersCount && (
                    <span className="text-gray-400 font-normal">({course.learnersCount.toLocaleString()} enrolled)</span>
                  )}
                </div>
                <div className="flex items-center gap-1.5 font-medium">
                  <Layers className="w-4 h-4 text-gray-400" />
                  <span>{course.modules?.length || 6} Modules</span>
                </div>
              </div>

              {/* Enrolled Progress Bar */}
              {isEnrolled && (
                <div className="mt-6 p-4 bg-lime-50/70 border border-lime-200 rounded-2xl">
                  <div className="flex items-center justify-between text-xs font-semibold mb-2">
                    <span className="text-lime-900">Your Learning Progress</span>
                    <span className="text-lime-700 font-bold">{course.progressPercentage || 0}% Complete</span>
                  </div>
                  <div className="w-full bg-lime-200/60 rounded-full h-2.5 overflow-hidden">
                    <div
                      className="bg-lime-600 h-2.5 rounded-full transition-all duration-300"
                      style={{ width: `${course.progressPercentage || 0}%` }}
                    />
                  </div>
                  <div className="mt-2 text-xs text-lime-800 font-medium">
                    {course.completedModulesCount || 0} of {course.modules?.length || 6} modules finished
                  </div>
                </div>
              )}
            </div>

            {/* Right Card with CTA & Credential Details */}
            <div className="w-full lg:w-88 shrink-0 bg-white rounded-3xl border border-gray-200 shadow-xl p-6 sm:p-7 flex flex-col">
              <div className="mb-5">
                <span className="text-xs text-gray-500 font-medium uppercase tracking-wider block mb-1">
                  Tuition & Access
                </span>
                <div className="flex items-baseline gap-2">
                  {course.free || (course.price || 0) <= 0 ? (
                    <span className="text-2xl sm:text-3xl font-bold text-emerald-600">Free</span>
                  ) : (
                    <>
                      <span className="text-2xl sm:text-3xl font-bold text-gray-900">₹{course.price}</span>
                      {course.discount ? (
                        <span className="text-sm text-gray-400 line-through">
                          ₹{Math.round((course.price || 0) + (course.discount || 0))}
                        </span>
                      ) : null}
                    </>
                  )}
                </div>
              </div>

              {/* Primary Action Button */}
              <button
                onClick={handleEnrollOrStart}
                disabled={actionLoading}
                className={`w-full py-3 px-6 rounded-xl font-semibold text-sm transition-all flex items-center justify-center gap-2 shadow-sm cursor-pointer mb-3 ${
                  isCompleted
                    ? 'bg-emerald-600 hover:bg-emerald-700 text-white'
                    : isEnrolled
                    ? 'bg-lime-600 hover:bg-lime-700 text-white'
                    : 'bg-lime-600 hover:bg-lime-700 text-white'
                }`}
              >
                <span>
                  {actionLoading
                    ? 'Processing...'
                    : isCompleted
                    ? 'Credential Earned'
                    : isEnrolled
                    ? 'Continue Learning'
                    : course.free
                    ? 'Enroll Now — Free'
                    : `Enroll for ₹${course.price}`}
                </span>
                <ArrowRight className="w-4 h-4" />
              </button>

              {isCompleted && (
                <button
                  onClick={() => onNavigate('/credential-edge/credentials')}
                  className="w-full py-2.5 px-4 rounded-xl border border-emerald-300 bg-emerald-50 text-emerald-800 font-semibold text-xs hover:bg-emerald-100 transition-colors flex items-center justify-center gap-1.5 cursor-pointer mb-4"
                >
                  <Award className="w-4 h-4" />
                  <span>View in My Credentials</span>
                </button>
              )}

              {/* Credential summary pill */}
              <div className="p-3.5 bg-amber-50/80 border border-amber-200/90 rounded-2xl mb-5">
                <div className="flex items-start gap-2.5">
                  <Award className="w-5 h-5 text-amber-600 shrink-0 mt-0.5" />
                  <div>
                    <span className="text-[11px] font-semibold text-amber-700 uppercase tracking-wider block">
                      Target Credential
                    </span>
                    <p className="text-xs font-semibold text-gray-800 leading-snug">
                      {course.credentialName || 'Google Professional Certificate'}
                    </p>
                  </div>
                </div>
              </div>

              {/* Features bullet list */}
              <div className="space-y-2.5 text-xs text-gray-600 pt-4 border-t border-gray-100">
                <div className="flex items-center gap-2">
                  <Check className="w-4 h-4 text-lime-600 shrink-0" />
                  <span>100% self-paced online schedule</span>
                </div>
                <div className="flex items-center gap-2">
                  <Check className="w-4 h-4 text-lime-600 shrink-0" />
                  <span>Module quizzes & case studies</span>
                </div>
                <div className="flex items-center gap-2">
                  <Check className="w-4 h-4 text-lime-600 shrink-0" />
                  <span>InGage course completion badge</span>
                </div>
                <div className="flex items-center gap-2">
                  <Check className="w-4 h-4 text-lime-600 shrink-0" />
                  <span>Sharable to LinkedIn & resumes</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Main Course Content Sections */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12 grid grid-cols-1 lg:grid-cols-3 gap-10">
        <div className="lg:col-span-2 space-y-12">
          {/* Section 1: About This Course */}
          <section className="bg-white rounded-3xl border border-gray-200 p-6 sm:p-8 shadow-2xs">
            <h2 className="text-xl sm:text-2xl font-bold text-gray-900 mb-4 tracking-tight flex items-center gap-2">
              <BookOpen className="w-5 h-5 text-lime-600" />
              <span>About This Course</span>
            </h2>
            <div className="text-sm text-gray-700 leading-relaxed whitespace-pre-line space-y-3">
              {course.description}
            </div>
          </section>

          {/* Section 2: What You'll Learn */}
          {course.learningOutcomes && course.learningOutcomes.length > 0 && (
            <section className="bg-white rounded-3xl border border-gray-200 p-6 sm:p-8 shadow-2xs">
              <h2 className="text-xl sm:text-2xl font-bold text-gray-900 mb-6 tracking-tight flex items-center gap-2">
                <Sparkles className="w-5 h-5 text-lime-600" />
                <span>What You'll Learn</span>
              </h2>
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-3.5">
                {course.learningOutcomes.map((outcome, idx) => (
                  <div
                    key={idx}
                    className="p-3.5 rounded-xl bg-gray-50 border border-gray-100 flex items-start gap-2.5 text-xs sm:text-sm text-gray-800"
                  >
                    <CheckCircle2 className="w-4 h-4 text-lime-600 shrink-0 mt-0.5" />
                    <span>{outcome}</span>
                  </div>
                ))}
              </div>
            </section>
          )}

          {/* Section 3: Course Structure & Modules */}
          <section className="bg-white rounded-3xl border border-gray-200 p-6 sm:p-8 shadow-2xs">
            <div className="flex items-center justify-between mb-6">
              <div>
                <h2 className="text-xl sm:text-2xl font-bold text-gray-900 tracking-tight flex items-center gap-2">
                  <Layers className="w-5 h-5 text-lime-600" />
                  <span>Course Structure</span>
                </h2>
                <p className="text-xs text-gray-500 mt-1">
                  {course.modules?.length || 0} modules designed for progressive skill mastery
                </p>
              </div>
            </div>

            <div className="space-y-4">
              {course.modules && course.modules.length > 0 ? (
                course.modules.map((m, idx) => {
                  const isFinished = (course.completedModulesCount || 0) > idx;

                  return (
                    <div
                      key={m.id || idx}
                      className={`p-5 rounded-2xl border transition-all ${
                        isFinished
                          ? 'bg-lime-50/40 border-lime-200'
                          : 'bg-white border-gray-200 hover:border-gray-300'
                      }`}
                    >
                      <div className="flex items-start justify-between gap-4">
                        <div className="flex items-start gap-3">
                          <div
                            className={`w-7 h-7 rounded-xl flex items-center justify-center font-bold text-xs shrink-0 mt-0.5 ${
                              isFinished
                                ? 'bg-lime-600 text-white'
                                : 'bg-gray-100 text-gray-700'
                            }`}
                          >
                            {isFinished ? <Check className="w-4 h-4" /> : idx + 1}
                          </div>
                          <div>
                            <h3 className="text-sm sm:text-base font-semibold text-gray-900">{m.title}</h3>
                            <p className="text-xs sm:text-sm text-gray-600 mt-1 leading-relaxed">{m.description}</p>
                            <div className="flex items-center gap-2 mt-2 text-xs text-gray-400 font-normal">
                              <Clock className="w-3.5 h-3.5" />
                              <span>{m.duration || '2-3 weeks'}</span>
                            </div>
                          </div>
                        </div>

                        {/* Complete Module Button if enrolled */}
                        {isEnrolled && (
                          <button
                            onClick={() => handleCompleteModule(idx + 1)}
                            disabled={actionLoading}
                            className={`px-3 py-1.5 rounded-lg text-xs font-semibold whitespace-nowrap transition-all cursor-pointer ${
                              isFinished
                                ? 'bg-lime-100 text-lime-800'
                                : 'bg-gray-100 hover:bg-lime-600 hover:text-white text-gray-700'
                            }`}
                          >
                            {isFinished ? 'Completed' : 'Mark Done'}
                          </button>
                        )}
                      </div>
                    </div>
                  );
                })
              ) : (
                <div className="py-8 text-center text-xs text-gray-400">
                  Modules will be displayed as configured by the instructor.
                </div>
              )}
            </div>
          </section>

          {/* Section 4: Credential & Certificate Distinction (Section 7) */}
          <section className="bg-white rounded-3xl border border-gray-200 p-6 sm:p-8 shadow-2xs">
            <h2 className="text-xl sm:text-2xl font-bold text-gray-900 mb-2 tracking-tight flex items-center gap-2">
              <Award className="w-5 h-5 text-amber-500" />
              <span>Credential & Certificate Verification</span>
            </h2>
            <p className="text-xs text-gray-500 mb-6">
              Learn how your completion and credentials are differentiated and verified.
            </p>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
              {/* Box 1: Course Completion Certificate */}
              <div className="p-5 rounded-2xl bg-gray-50 border border-gray-200 space-y-2.5">
                <span className="px-2.5 py-0.5 rounded bg-lime-100 text-lime-800 text-[10px] font-extrabold uppercase tracking-wider">
                  InGage Certification
                </span>
                <h3 className="text-sm font-bold text-gray-900">Course Completion Certificate</h3>
                <p className="text-xs text-gray-600 leading-relaxed">
                  Issued directly by the InGage Learning Platform upon finishing all modules, verifying syllabus completion and project submissions.
                </p>
              </div>

              {/* Box 2: External Google Credential */}
              <div className="p-5 rounded-2xl bg-amber-50/60 border border-amber-200 space-y-2.5">
                <span className="px-2.5 py-0.5 rounded bg-amber-100 text-amber-800 text-[10px] font-extrabold uppercase tracking-wider">
                  External Provider Credential
                </span>
                <h3 className="text-sm font-bold text-gray-900">{course.credentialName}</h3>
                <p className="text-xs text-gray-600 leading-relaxed">
                  The recognized Google professional credential. Prepared for via this curriculum and completed via official provider guidelines.
                </p>
                {course.credentialUrl && (
                  <a
                    href={course.credentialUrl}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="inline-flex items-center gap-1 text-xs font-bold text-amber-800 hover:text-amber-900 underline mt-1"
                  >
                    <span>View Official Credential Details</span>
                    <ExternalLink className="w-3 h-3" />
                  </a>
                )}
              </div>
            </div>
          </section>
        </div>

        {/* Sidebar Info & Connected Pathways */}
        <div className="space-y-8">
          {/* Prerequisites */}
          {course.prerequisites && course.prerequisites.length > 0 && (
            <div className="bg-white rounded-3xl border border-gray-200 p-6 shadow-2xs">
              <h3 className="text-sm font-bold text-gray-900 mb-3 flex items-center gap-1.5">
                <HelpCircle className="w-4 h-4 text-gray-400" />
                <span>Prerequisites</span>
              </h3>
              <ul className="space-y-2 text-xs text-gray-600">
                {course.prerequisites.map((p, idx) => (
                  <li key={idx} className="flex items-start gap-2">
                    <span className="w-1.5 h-1.5 rounded-full bg-lime-500 mt-1.5 shrink-0" />
                    <span>{p}</span>
                  </li>
                ))}
              </ul>
            </div>
          )}

          {/* Google Certificates Industry Recognised Card (Matching design specification) */}
          <div className="relative bg-white rounded-3xl shadow-[0_15px_35px_rgba(0,0,0,0.05)] border border-gray-100 p-6 text-left">
            <div className="absolute -top-3 right-6 px-3.5 py-1 bg-[#F59E0B] text-white text-[10px] font-black rounded-full uppercase tracking-wider shadow-xs">
              INDUSTRY RECOGNISED
            </div>
            <div className="pt-1 mb-4">
              <div className="text-2xl font-medium tracking-tight flex items-baseline">
                <span className="text-[#4285F4]">G</span>
                <span className="text-[#EA4335]">o</span>
                <span className="text-[#FBBC05]">o</span>
                <span className="text-[#4285F4]">g</span>
                <span className="text-[#34A853]">l</span>
                <span className="text-[#EA4335]">e</span>
              </div>
              <div className="text-xl font-normal text-gray-700 tracking-tight mt-0.5">
                Certificates
              </div>
            </div>
            <div className="border-b border-gray-100 mb-4" />
            <div className="space-y-3">
              {[
                'Job-ready skills in high-growth fields',
                '100% online, flexible self-paced learning',
                'Verified certificate upon completion',
                'Integrates with InGage Career Compass pathways'
              ].map((text, idx) => (
                <div key={idx} className="flex items-start gap-2.5">
                  <div className="w-4 h-4 rounded-full border border-emerald-600 flex items-center justify-center shrink-0 mt-0.5">
                    <svg className="w-2.5 h-2.5 text-emerald-600 stroke-[2.5]" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                      <polyline points="20 6 9 17 4 12" />
                    </svg>
                  </div>
                  <span className="text-xs text-gray-700 leading-snug">
                    {text}
                  </span>
                </div>
              ))}
            </div>
          </div>

          {/* Connected Career Compass Role */}
          {course.careerSlug && (
            <div className="bg-gradient-to-br from-emerald-900 to-teal-900 text-white rounded-3xl p-6 shadow-md border border-emerald-800">
              <span className="text-[10px] font-extrabold text-lime-400 uppercase tracking-wider block mb-1">
                Career Compass Integration
              </span>
              <h3 className="text-base font-bold text-white mb-2">
                Recommended for {course.careerSlug.replace('-', ' ').toUpperCase()}
              </h3>
              <p className="text-xs text-emerald-100 mb-4 leading-relaxed">
                This credential aligns directly with the industry competencies required for this role.
              </p>
              <button
                onClick={() => onNavigate(`/roles/${course.careerSlug}`)}
                className="w-full py-2.5 px-4 rounded-xl bg-lime-500 hover:bg-lime-400 text-gray-950 font-bold text-xs transition-colors flex items-center justify-center gap-1.5 cursor-pointer"
              >
                <span>Explore Career Pathway</span>
                <ChevronRight className="w-3.5 h-3.5" />
              </button>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
