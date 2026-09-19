import React, { useState, useEffect, useRef } from 'react';
import {
  ChevronLeft,
  ChevronRight,
  Play,
  Pause,
  Volume2,
  VolumeX,
  Maximize,
  Minimize,
  CheckCircle2,
  Clock,
  BookOpen,
  User,
  Check,
  AlertCircle,
  Sparkles,
  Lock,
  FileText,
  ShieldCheck,
  ShieldAlert,
  Loader2
} from 'lucide-react';
import { getCourseById, CourseDto } from '../api/courseApi';
import { getCourseContent, CourseContentDetail } from '../api/paymentApi';
import { markLessonComplete } from '../api/adminApi';
import { getCourseEnrollmentStatus, CourseEnrollmentStatusDto } from '../api/careerApi';
import { useAppSelector } from '../store/hooks';

interface CourseLearnPageProps {
  courseId: number;
  onNavigate: (path: string, param?: string) => void;
  onShowToast?: (msg: string, type?: 'success' | 'info' | 'error') => void;
}

interface FlattenedLesson {
  id: number;
  title: string;
  description?: string;
  duration?: string;
  lessonType: string;
  sectionId: number;
  sectionTitle: string;
  isCompleted: boolean;
  locked: boolean;
  freePreview?: boolean;
}

type WatermarkPosition = 'top-left' | 'top-right' | 'bottom-left' | 'bottom-right' | 'center';

export function CourseLearnPage({ courseId, onNavigate, onShowToast }: CourseLearnPageProps) {
  const { user: authUser } = useAppSelector((state) => state.auth);

  const [course, setCourse] = useState<CourseDto | null>(null);
  const [content, setContent] = useState<CourseContentDetail | null>(null);
  const [enrollmentStatus, setEnrollmentStatus] = useState<CourseEnrollmentStatusDto | null>(null);
  const [activeLessonId, setActiveLessonId] = useState<number | null>(null);
  const [completedLessonIds, setCompletedLessonIds] = useState<number[]>([]);
  const [activeTab, setActiveTab] = useState<'overview' | 'notes'>('overview');

  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [videoError, setVideoError] = useState<string | null>(null);
  const [isVideoBuffering, setIsVideoBuffering] = useState<boolean>(false);
  const [isSubmittingCompletion, setIsSubmittingCompletion] = useState<boolean>(false);

  // Video container & element refs
  const videoContainerRef = useRef<HTMLDivElement | null>(null);
  const videoRef = useRef<HTMLVideoElement | null>(null);
  const [isPlaying, setIsPlaying] = useState<boolean>(false);
  const [currentTime, setCurrentTime] = useState<number>(0);
  const [duration, setDuration] = useState<number>(0);
  const [volume, setVolume] = useState<number>(1);
  const [isMuted, setIsMuted] = useState<boolean>(false);
  const [playbackSpeed, setPlaybackSpeed] = useState<number>(1);
  const [isFullscreen, setIsFullscreen] = useState<boolean>(false);

  // Dynamic security watermark state
  const [watermarkPos, setWatermarkPos] = useState<WatermarkPosition>('top-right');

  // Backend API base URL for protected video streaming endpoint
  const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8000/api';
  const authToken = typeof window !== 'undefined' ? (localStorage.getItem('ingage_token') || '') : '';

  // Download deterrence: Prevent Save / View Source keyboard shortcuts
  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if ((e.ctrlKey || e.metaKey) && (e.key === 's' || e.key === 'S' || e.key === 'u' || e.key === 'U')) {
        e.preventDefault();
      }
    };
    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, []);

  // Track fullscreen state to keep overlay watermark & custom controls functional in fullscreen
  useEffect(() => {
    const handleFullscreenChange = () => {
      setIsFullscreen(!!document.fullscreenElement);
    };
    document.addEventListener('fullscreenchange', handleFullscreenChange);
    return () => document.removeEventListener('fullscreenchange', handleFullscreenChange);
  }, []);

  // Subtle watermark position shifting every 15 seconds to deter screen recordings
  useEffect(() => {
    const positions: WatermarkPosition[] = ['top-right', 'bottom-left', 'top-left', 'bottom-right', 'center'];
    let idx = 0;
    const interval = setInterval(() => {
      idx = (idx + 1) % positions.length;
      setWatermarkPos(positions[idx]);
    }, 15000);
    return () => clearInterval(interval);
  }, []);

  const loadCourseData = async () => {
    setLoading(true);
    setError(null);
    try {
      const [courseRes, contentRes] = await Promise.all([
        getCourseById(courseId),
        getCourseContent(courseId),
      ]);

      setCourse(courseRes);
      setContent(contentRes);

      // Fetch enrollment status if logged in
      try {
        const enrollRes = await getCourseEnrollmentStatus(courseId);
        setEnrollmentStatus(enrollRes);
      } catch {
        // Continue if unauthenticated or error
      }

      // Initialize active lesson to first lesson in first section
      if (contentRes.sections && contentRes.sections.length > 0) {
        const firstSec = contentRes.sections[0];
        if (firstSec.lessons && firstSec.lessons.length > 0) {
          setActiveLessonId(firstSec.lessons[0].id);
        }
      }

      // Restore locally completed lessons for this course
      try {
        const stored = localStorage.getItem(`ingage_completed_lessons_${courseId}`);
        if (stored) {
          setCompletedLessonIds(JSON.parse(stored));
        }
      } catch {
        // noop
      }
    } catch (err: any) {
      setError(err?.response?.data?.message || err?.message || 'Unable to load this course lesson.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadCourseData();
  }, [courseId]);

  // Flatten curriculum lessons for easy previous/next navigation
  const allLessons: FlattenedLesson[] = React.useMemo(() => {
    if (!content || !content.sections) return [];
    const list: FlattenedLesson[] = [];
    content.sections.forEach((sec) => {
      sec.lessons.forEach((les) => {
        list.push({
          id: les.id,
          title: les.title,
          description: les.description,
          duration: les.duration,
          lessonType: les.lessonType,
          sectionId: sec.id,
          sectionTitle: sec.title,
          isCompleted: completedLessonIds.includes(les.id),
          locked: les.locked,
          freePreview: les.freePreview,
        });
      });
    });
    return list;
  }, [content, completedLessonIds]);

  const currentIndex = allLessons.findIndex((l) => l.id === activeLessonId);
  const activeLesson = allLessons[currentIndex] || allLessons[0] || null;

  const isUserEnrolled = Boolean(content?.isEnrolled || enrollmentStatus?.enrolled || enrollmentStatus?.courseAccess);
  const canPlayActiveLesson = Boolean(activeLesson && (isUserEnrolled || activeLesson.freePreview));
  const isLockedForUser = Boolean(activeLesson && !canPlayActiveLesson);

  // Compute authenticated video streaming URL
  // Uses protected backend endpoint: /api/courses/{courseId}/lessons/{lessonId}/video?token={jwt}
  const videoStreamUrl = activeLesson && canPlayActiveLesson
    ? `${API_BASE_URL}/courses/${courseId}/lessons/${activeLesson.id}/video${authToken ? `?token=${encodeURIComponent(authToken)}` : ''}`
    : '';

  // Reset video error on active lesson change
  useEffect(() => {
    setVideoError(null);
    setIsPlaying(false);
    setIsVideoBuffering(false);
  }, [activeLesson?.id]);

  // Restore saved video position for the active lesson
  useEffect(() => {
    if (!activeLesson || !videoRef.current) return;

    try {
      const key = `ingage_video_pos_${courseId}_${activeLesson.id}`;
      const savedTime = localStorage.getItem(key);
      if (savedTime && Number(savedTime) > 0) {
        const timeNum = Number(savedTime);
        videoRef.current.currentTime = timeNum;
        setCurrentTime(timeNum);
      } else {
        videoRef.current.currentTime = 0;
        setCurrentTime(0);
      }
    } catch {
      // noop
    }
  }, [activeLesson?.id, courseId]);

  // Save video position periodically as user watches
  const handleTimeUpdate = () => {
    if (!videoRef.current || !activeLesson) return;
    const cur = videoRef.current.currentTime;
    setCurrentTime(cur);
    setDuration(videoRef.current.duration || 0);

    // Save timestamp to localStorage
    try {
      const key = `ingage_video_pos_${courseId}_${activeLesson.id}`;
      localStorage.setItem(key, String(cur));
    } catch {
      // noop
    }
  };

  const togglePlay = () => {
    if (!videoRef.current) return;
    if (isPlaying) {
      videoRef.current.pause();
      setIsPlaying(false);
    } else {
      videoRef.current.play().then(() => setIsPlaying(true)).catch(() => {});
    }
  };

  const handleSeek = (e: React.ChangeEvent<HTMLInputElement>) => {
    const time = Number(e.target.value);
    if (videoRef.current) {
      videoRef.current.currentTime = time;
      setCurrentTime(time);
    }
  };

  const toggleMute = () => {
    if (!videoRef.current) return;
    const newMuted = !isMuted;
    videoRef.current.muted = newMuted;
    setIsMuted(newMuted);
  };

  const handleVolumeChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const val = Number(e.target.value);
    setVolume(val);
    if (videoRef.current) {
      videoRef.current.volume = val;
      videoRef.current.muted = val === 0;
      setIsMuted(val === 0);
    }
  };

  const handleSpeedChange = (speed: number) => {
    setPlaybackSpeed(speed);
    if (videoRef.current) {
      videoRef.current.playbackRate = speed;
    }
  };

  const handleFullscreen = () => {
    if (!videoContainerRef.current) return;
    if (document.fullscreenElement) {
      document.exitFullscreen().catch(() => {});
    } else {
      videoContainerRef.current.requestFullscreen().catch(() => {});
    }
  };

  const handleVideoError = () => {
    setIsPlaying(false);
    setIsVideoBuffering(false);
    setVideoError('Video stream access restricted. Please ensure you are logged in and enrolled in this course.');
  };

  const formatTime = (secs: number) => {
    if (isNaN(secs) || secs < 0) return '0:00';
    const m = Math.floor(secs / 60);
    const s = Math.floor(secs % 60);
    return `${m}:${s < 10 ? '0' : ''}${s}`;
  };

  // Mark lesson as complete via backend API
  const handleMarkComplete = async () => {
    if (!activeLesson) return;
    setIsSubmittingCompletion(true);
    try {
      await markLessonComplete(activeLesson.id);

      const updated = [...completedLessonIds];
      if (!updated.includes(activeLesson.id)) {
        updated.push(activeLesson.id);
        setCompletedLessonIds(updated);
        localStorage.setItem(`ingage_completed_lessons_${courseId}`, JSON.stringify(updated));
      }

      if (onShowToast) {
        onShowToast(`"${activeLesson.title}" marked as completed!`, 'success');
      }

      // Refresh enrollment status from backend for updated course progress %
      try {
        const enrollRes = await getCourseEnrollmentStatus(courseId);
        setEnrollmentStatus(enrollRes);
      } catch {
        // noop
      }
    } catch {
      // Graceful local update
      const updated = [...completedLessonIds];
      if (!updated.includes(activeLesson.id)) {
        updated.push(activeLesson.id);
        setCompletedLessonIds(updated);
        localStorage.setItem(`ingage_completed_lessons_${courseId}`, JSON.stringify(updated));
      }
      if (onShowToast) {
        onShowToast('Lesson progress updated.', 'success');
      }
    } finally {
      setIsSubmittingCompletion(false);
    }
  };

  // Navigation handlers
  const handlePreviousLesson = () => {
    if (currentIndex > 0) {
      const prev = allLessons[currentIndex - 1];
      setActiveLessonId(prev.id);
    }
  };

  const handleNextLesson = () => {
    if (currentIndex < allLessons.length - 1) {
      const next = allLessons[currentIndex + 1];
      setActiveLessonId(next.id);
    }
  };

  // Safe course progress calculation
  const totalLessonsCount = allLessons.length;
  const completedCount = completedLessonIds.length;
  const progressPercent = enrollmentStatus?.progress !== undefined && enrollmentStatus.progress > 0
    ? enrollmentStatus.progress
    : totalLessonsCount > 0
    ? Math.round((completedCount / totalLessonsCount) * 100)
    : 0;

  const isCurrentCompleted = activeLesson ? completedLessonIds.includes(activeLesson.id) : false;

  // Real authenticated user information for dynamic watermark
  const watermarkName = authUser?.name || 'Verified Learner';
  const watermarkEmail = authUser?.email || '';

  return (
    <div className="w-full bg-[#f8fafc] min-h-[calc(100vh-4.5rem)] text-gray-900 pb-16 flex flex-col select-text">
      {/* 1. Dedicated Course Player Navigation Bar */}
      <div className="bg-white border-b border-gray-200 sticky top-0 z-30 shadow-2xs">
        <div className="w-full px-4 sm:px-6 lg:px-8 py-3 flex items-center justify-between gap-4">
          <div className="flex items-center gap-3">
            <button
              id="back-to-course-btn"
              onClick={() => onNavigate(`/courses/${courseId}`)}
              className="inline-flex items-center gap-1 px-3 py-1.5 rounded-xl text-xs sm:text-sm font-semibold text-gray-600 hover:text-gray-900 hover:bg-gray-100 transition-colors cursor-pointer"
            >
              <ChevronLeft className="w-4 h-4" />
              <span>Course Overview</span>
            </button>
            <div className="h-4 w-px bg-gray-200 hidden sm:block" />
            <span className="text-xs sm:text-sm font-bold text-gray-900 truncate max-w-xs sm:max-w-md lg:max-w-lg">
              {course?.title || 'Course Learning'}
            </span>
          </div>

          {/* Course Overall Progress Indicator */}
          <div className="flex items-center gap-3">
            <div className="hidden sm:flex flex-col items-end">
              <span className="text-[11px] font-bold text-gray-500 uppercase tracking-wider">
                Course Progress
              </span>
              <span className="text-xs font-black text-gray-900">
                {progressPercent}% Complete
              </span>
            </div>

            <div className="w-24 sm:w-32 bg-gray-200 rounded-full h-2 overflow-hidden">
              <div
                className="h-full bg-[#8DB600] rounded-full transition-all duration-500"
                style={{ width: `${progressPercent}%` }}
              />
            </div>
          </div>
        </div>
      </div>

      {/* 2. Main Content Area */}
      <div className="w-full px-4 sm:px-6 lg:px-8 py-6 max-w-7xl mx-auto flex-1 flex flex-col">
        {/* Loading State */}
        {loading && (
          <div className="w-full space-y-6 animate-pulse">
            <div className="aspect-video w-full max-w-4xl mx-auto bg-gray-200 rounded-3xl" />
            <div className="h-6 w-1/3 bg-gray-200 rounded" />
            <div className="h-4 w-1/2 bg-gray-100 rounded" />
          </div>
        )}

        {/* Error State */}
        {!loading && error && (
          <div className="p-8 bg-rose-50 border border-rose-200 rounded-3xl flex flex-col items-center justify-center text-center space-y-3 max-w-lg mx-auto my-12">
            <AlertCircle className="w-10 h-10 text-rose-600" />
            <h3 className="font-bold text-gray-900">Unable to load this lesson</h3>
            <p className="text-xs text-rose-700">{error}</p>
            <button
              onClick={loadCourseData}
              className="px-5 py-2 rounded-xl bg-rose-600 text-white font-bold text-xs hover:bg-rose-700 transition-colors cursor-pointer"
            >
              Try Again
            </button>
          </div>
        )}

        {/* Main 2-Column Responsive Layout */}
        {!loading && !error && activeLesson && (
          <div className="grid grid-cols-1 lg:grid-cols-12 gap-8 items-start">
            {/* Left Column (8 cols): Responsive Video Player & Lesson Details */}
            <div className="lg:col-span-8 space-y-6">
              {/* Free Preview Notification Banner when user is not enrolled */}
              {activeLesson.freePreview && !isUserEnrolled && (
                <div className="bg-lime-50 border border-lime-200 rounded-2xl px-4 py-3 flex flex-col sm:flex-row items-start sm:items-center justify-between gap-3 text-xs text-lime-900 shadow-2xs">
                  <div className="flex items-center gap-2">
                    <Sparkles className="w-4 h-4 text-[#8DB600] shrink-0" />
                    <span>
                      <strong>Free Preview Lesson:</strong> You are viewing an unlocked preview lesson. Enroll in this course to access all modules and protected video walkthroughs.
                    </span>
                  </div>
                  <button
                    onClick={() => onNavigate(`/courses/${courseId}`)}
                    className="px-3.5 py-1.5 rounded-xl bg-[#8DB600] hover:bg-[#7ba000] text-gray-950 font-bold text-xs transition-colors shrink-0 cursor-pointer"
                  >
                    Buy / Enroll Now
                  </button>
                </div>
              )}

              {/* Responsive Video Container with Security Features & Dynamic Watermark */}
              <div
                ref={videoContainerRef}
                onContextMenu={(e) => e.preventDefault()}
                className="relative bg-black rounded-3xl overflow-hidden shadow-xl border border-gray-900 group select-none"
              >
                {/* When lesson is protected and user is not enrolled, show locked screen directly */}
                {isLockedForUser ? (
                  <div className="w-full aspect-video bg-black flex flex-col items-center justify-center p-6 text-center z-30 space-y-4">
                    <div className="w-14 h-14 rounded-2xl bg-amber-500/20 border border-amber-500/30 flex items-center justify-center text-amber-400">
                      <Lock className="w-7 h-7" />
                    </div>
                    <div className="space-y-1.5 max-w-md">
                      <h4 className="text-white font-bold text-base sm:text-lg">
                        Protected Video Content
                      </h4>
                      <p className="text-gray-400 text-xs sm:text-sm">
                        This video lesson requires an active course enrollment. Please enroll to unlock all curriculum modules and certifications.
                      </p>
                    </div>
                    <div className="flex items-center gap-3 pt-2">
                      <button
                        onClick={() => onNavigate(`/courses/${courseId}`)}
                        className="px-5 py-2.5 rounded-xl bg-[#8DB600] text-gray-950 font-bold text-xs hover:bg-[#7ba000] transition-colors cursor-pointer"
                      >
                        Course Overview &amp; Enroll
                      </button>
                    </div>
                  </div>
                ) : (
                  /* HTML5 Video Element with Backend Protected Stream URL */
                  <video
                    ref={videoRef}
                    src={videoStreamUrl}
                    controlsList="nodownload"
                    disablePictureInPicture
                    onContextMenu={(e) => e.preventDefault()}
                    onTimeUpdate={handleTimeUpdate}
                    onLoadedMetadata={handleTimeUpdate}
                    onWaiting={() => setIsVideoBuffering(true)}
                    onPlaying={() => setIsVideoBuffering(false)}
                    onError={handleVideoError}
                    onEnded={() => {
                      setIsPlaying(false);
                      handleMarkComplete();
                    }}
                    playsInline
                    className="w-full aspect-video object-contain bg-black cursor-pointer"
                    onClick={togglePlay}
                  />
                )}

                {/* Buffering Indicator */}
                {isVideoBuffering && !videoError && (
                  <div className="absolute inset-0 flex items-center justify-center bg-black/40 pointer-events-none z-10">
                    <Loader2 className="w-10 h-10 text-[#8DB600] animate-spin" />
                  </div>
                )}

                {/* Video Playback / Authorization Error Overlay */}
                {videoError && (
                  <div className="absolute inset-0 bg-black/90 flex flex-col items-center justify-center p-6 text-center z-30 space-y-4">
                    <div className="w-14 h-14 rounded-2xl bg-rose-500/20 border border-rose-500/30 flex items-center justify-center text-rose-400">
                      <Lock className="w-7 h-7" />
                    </div>
                    <div className="space-y-1.5 max-w-md">
                      <h4 className="text-white font-bold text-base sm:text-lg">
                        Protected Video Content
                      </h4>
                      <p className="text-gray-400 text-xs sm:text-sm">
                        {videoError}
                      </p>
                    </div>
                    <div className="flex items-center gap-3 pt-2">
                      <button
                        onClick={() => onNavigate(`/courses/${courseId}`)}
                        className="px-4 py-2 rounded-xl bg-[#8DB600] text-gray-950 font-bold text-xs hover:bg-[#7ba000] transition-colors cursor-pointer"
                      >
                        Course Overview &amp; Enroll
                      </button>
                    </div>
                  </div>
                )}

                {/* Dynamic Forensic Watermark Overlay (Works in Standard & Fullscreen) */}
                {!videoError && (
                  <div
                    className={`absolute pointer-events-none select-none transition-all duration-1000 ease-in-out z-20 ${
                      watermarkPos === 'top-left'
                        ? 'top-4 left-4 text-left'
                        : watermarkPos === 'top-right'
                        ? 'top-4 right-4 text-right'
                        : watermarkPos === 'bottom-left'
                        ? 'bottom-20 left-4 text-left'
                        : watermarkPos === 'bottom-right'
                        ? 'bottom-20 right-4 text-right'
                        : 'top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 text-center'
                    }`}
                  >
                    <div className="bg-black/30 backdrop-blur-[2px] px-3 py-1.5 rounded-lg border border-white/10 opacity-30 hover:opacity-40 transition-opacity">
                      <p className="text-[11px] sm:text-xs font-mono font-bold text-white/90 leading-tight tracking-wide">
                        {watermarkName}
                      </p>
                      {watermarkEmail && (
                        <p className="text-[9px] sm:text-[10px] font-mono text-white/70 leading-tight mt-0.5">
                          {watermarkEmail}
                        </p>
                      )}
                    </div>
                  </div>
                )}

                {/* Custom Video Control Overlay Bar */}
                {!videoError && (
                  <div className="absolute inset-x-0 bottom-0 bg-gradient-to-t from-black/95 via-black/60 to-transparent p-4 space-y-2 opacity-95 group-hover:opacity-100 transition-opacity z-25">
                    {/* Progress Seek Bar (Supports HTTP 206 Partial Content Range Requests) */}
                    <input
                      type="range"
                      min={0}
                      max={duration || 100}
                      value={currentTime}
                      onChange={handleSeek}
                      className="w-full h-1.5 bg-white/30 rounded-lg appearance-none cursor-pointer accent-[#8DB600]"
                      title="Seek video progress"
                    />

                    {/* Controls Row */}
                    <div className="flex items-center justify-between text-white text-xs gap-3">
                      <div className="flex items-center gap-3">
                        <button
                          onClick={togglePlay}
                          className="p-2 rounded-lg bg-white/15 hover:bg-white/30 text-white transition-colors cursor-pointer"
                          title={isPlaying ? 'Pause (Space)' : 'Play (Space)'}
                        >
                          {isPlaying ? <Pause className="w-4 h-4" /> : <Play className="w-4 h-4 fill-white" />}
                        </button>

                        {/* Time display */}
                        <span className="font-mono text-white/90 text-xs">
                          {formatTime(currentTime)} / {formatTime(duration)}
                        </span>

                        {/* Volume & Mute */}
                        <div className="flex items-center gap-1.5 hidden sm:flex">
                          <button
                            onClick={toggleMute}
                            className="p-1 text-white/80 hover:text-white cursor-pointer"
                            title={isMuted ? 'Unmute' : 'Mute'}
                          >
                            {isMuted || volume === 0 ? (
                              <VolumeX className="w-4 h-4 text-rose-400" />
                            ) : (
                              <Volume2 className="w-4 h-4" />
                            )}
                          </button>
                          <input
                            type="range"
                            min={0}
                            max={1}
                            step={0.05}
                            value={isMuted ? 0 : volume}
                            onChange={handleVolumeChange}
                            className="w-16 h-1 bg-white/30 rounded-lg appearance-none cursor-pointer accent-[#8DB600]"
                            title="Volume"
                          />
                        </div>
                      </div>

                      {/* Right side controls: Speed & Fullscreen */}
                      <div className="flex items-center gap-2">
                        <select
                          value={playbackSpeed}
                          onChange={(e) => handleSpeedChange(Number(e.target.value))}
                          className="bg-white/15 border border-white/20 text-white text-xs rounded-lg px-2 py-1 focus:outline-none cursor-pointer"
                          title="Playback Speed"
                        >
                          <option value={0.75} className="text-gray-900">0.75x</option>
                          <option value={1} className="text-gray-900">1x</option>
                          <option value={1.25} className="text-gray-900">1.25x</option>
                          <option value={1.5} className="text-gray-900">1.5x</option>
                          <option value={2} className="text-gray-900">2x</option>
                        </select>

                        <button
                          onClick={handleFullscreen}
                          className="p-1.5 rounded-lg hover:bg-white/20 text-white transition-colors cursor-pointer"
                          title={isFullscreen ? 'Exit Fullscreen' : 'Fullscreen'}
                        >
                          {isFullscreen ? <Minimize className="w-4 h-4" /> : <Maximize className="w-4 h-4" />}
                        </button>
                      </div>
                    </div>
                  </div>
                )}
              </div>

              {/* Lesson Metadata & Navigation Control Bar */}
              <div className="bg-white rounded-3xl border border-gray-200/90 p-6 shadow-xs space-y-5">
                <div className="flex flex-col sm:flex-row sm:items-start justify-between gap-4">
                  <div className="space-y-1.5">
                    <div className="flex items-center gap-2 flex-wrap">
                      <span className="px-2.5 py-0.5 rounded-full text-xs font-bold bg-lime-100 text-lime-900 border border-lime-200">
                        {course?.category || 'Curriculum'}
                      </span>
                      <span className="text-xs font-semibold text-gray-500">
                        {activeLesson.sectionTitle}
                      </span>
                    </div>

                    <h1 className="text-xl sm:text-2xl font-black text-gray-950 tracking-tight">
                      {activeLesson.title}
                    </h1>

                    <div className="flex items-center gap-4 text-xs text-gray-500 font-medium">
                      <span className="flex items-center gap-1">
                        <Clock className="w-3.5 h-3.5 text-gray-400" />
                        <span>{activeLesson.duration || '12m'}</span>
                      </span>
                      <span className="flex items-center gap-1">
                        <User className="w-3.5 h-3.5 text-gray-400" />
                        <span>Instructor: {course?.instructor || 'Faculty'}</span>
                      </span>
                      <span className="flex items-center gap-1 text-emerald-700 bg-emerald-50 px-2 py-0.5 rounded-md border border-emerald-100 font-semibold">
                        <ShieldCheck className="w-3.5 h-3.5" />
                        <span>Protected Stream</span>
                      </span>
                    </div>
                  </div>

                  {/* Mark As Complete Button */}
                  <button
                    id="mark-lesson-complete-btn"
                    onClick={handleMarkComplete}
                    disabled={isSubmittingCompletion}
                    className={`inline-flex items-center gap-2 px-5 py-2.5 rounded-xl text-xs sm:text-sm font-bold shadow-2xs transition-all cursor-pointer disabled:opacity-50 shrink-0 ${
                      isCurrentCompleted
                        ? 'bg-emerald-100 text-emerald-900 border border-emerald-300'
                        : 'bg-[#8DB600] hover:bg-[#7ba000] text-gray-950'
                    }`}
                  >
                    <CheckCircle2 className={`w-4 h-4 ${isCurrentCompleted ? 'text-emerald-700' : 'text-gray-950'}`} />
                    <span>{isCurrentCompleted ? 'Completed ✓' : 'Mark as Complete'}</span>
                  </button>
                </div>

                {/* Previous / Next Lesson Control Strip */}
                <div className="pt-4 border-t border-gray-100 flex items-center justify-between gap-3">
                  <button
                    onClick={handlePreviousLesson}
                    disabled={currentIndex <= 0}
                    className="inline-flex items-center gap-1.5 px-4 py-2 rounded-xl border border-gray-200 bg-white hover:bg-gray-50 text-gray-700 font-semibold text-xs disabled:opacity-40 disabled:cursor-not-allowed transition-colors cursor-pointer"
                  >
                    <ChevronLeft className="w-4 h-4" />
                    <span>Previous Lesson</span>
                  </button>

                  <span className="text-xs font-bold text-gray-400">
                    Lesson {currentIndex + 1} of {allLessons.length}
                  </span>

                  <button
                    onClick={handleNextLesson}
                    disabled={currentIndex >= allLessons.length - 1}
                    className="inline-flex items-center gap-1.5 px-4 py-2 rounded-xl bg-gray-900 hover:bg-black text-white font-bold text-xs disabled:opacity-40 disabled:cursor-not-allowed transition-colors cursor-pointer"
                  >
                    <span>Next Lesson</span>
                    <ChevronRight className="w-4 h-4" />
                  </button>
                </div>
              </div>

              {/* Lesson Tabs: Overview vs Notes */}
              <div className="bg-white rounded-3xl border border-gray-200/90 p-6 shadow-xs space-y-4">
                <div className="flex items-center gap-3 border-b border-gray-100 pb-3">
                  <button
                    onClick={() => setActiveTab('overview')}
                    className={`pb-2 text-xs sm:text-sm font-bold border-b-2 transition-all cursor-pointer ${
                      activeTab === 'overview'
                        ? 'border-[#8DB600] text-gray-950'
                        : 'border-transparent text-gray-400 hover:text-gray-700'
                    }`}
                  >
                    Lesson Overview
                  </button>
                  <button
                    onClick={() => setActiveTab('notes')}
                    className={`pb-2 text-xs sm:text-sm font-bold border-b-2 transition-all cursor-pointer ${
                      activeTab === 'notes'
                        ? 'border-[#8DB600] text-gray-950'
                        : 'border-transparent text-gray-400 hover:text-gray-700'
                    }`}
                  >
                    Notes &amp; Resources
                  </button>
                </div>

                {activeTab === 'overview' && (
                  <div className="space-y-4 text-xs sm:text-sm text-gray-600 leading-relaxed">
                    <p>{activeLesson.description || 'Welcome to this lesson. Watch the video above to master the foundational concepts.'}</p>
                    {course?.description && (
                      <div className="p-4 bg-gray-50 rounded-2xl border border-gray-100 space-y-1">
                        <span className="text-[11px] font-bold text-gray-400 uppercase tracking-wider">
                          Course Information
                        </span>
                        <p className="text-gray-700 font-medium">{course.description}</p>
                      </div>
                    )}
                  </div>
                )}

                {activeTab === 'notes' && (
                  <div className="space-y-3 text-xs sm:text-sm text-gray-700">
                    <div className="p-4 bg-lime-50/60 rounded-2xl border border-lime-200/80 space-y-2">
                      <div className="flex items-center gap-2 font-bold text-lime-900">
                        <Sparkles className="w-4 h-4 text-[#8DB600]" />
                        <span>Key Learning Takeaway</span>
                      </div>
                      <p className="text-lime-950/90 leading-relaxed text-xs">
                        Follow along with the hands-on concepts demonstrated in this lesson. Practice the queries and visualizations in your local development environment.
                      </p>
                    </div>

                    <div className="p-4 bg-gray-50 rounded-2xl border border-gray-100 flex items-center justify-between">
                      <div className="flex items-center gap-3">
                        <FileText className="w-5 h-5 text-gray-400" />
                        <div>
                          <p className="font-bold text-gray-900 text-xs">Lesson Reference Guide</p>
                          <p className="text-[11px] text-gray-500">PDF Guide • Available for enrolled learners</p>
                        </div>
                      </div>
                      <span className="px-3 py-1 bg-white border border-gray-200 rounded-lg text-[11px] font-bold text-gray-700">
                        Included
                      </span>
                    </div>
                  </div>
                )}
              </div>
            </div>

            {/* Right Column (4 cols): Course Curriculum Sidebar */}
            <div className="lg:col-span-4 bg-white rounded-3xl border border-gray-200/90 p-5 sm:p-6 shadow-xs space-y-5 sticky top-20">
              {/* Curriculum Header */}
              <div className="space-y-2 pb-4 border-b border-gray-100">
                <div className="flex items-center justify-between">
                  <h2 className="text-base font-bold text-gray-950 flex items-center gap-2">
                    <BookOpen className="w-4 h-4 text-[#8DB600]" />
                    <span>Course Curriculum</span>
                  </h2>
                  <span className="text-xs font-extrabold text-[#8DB600]">
                    {completedCount} / {totalLessonsCount}
                  </span>
                </div>
                <p className="text-xs text-gray-500">
                  Select any lesson below to switch or review.
                </p>
              </div>

              {/* Sections & Lessons Accordion */}
              <div className="space-y-4 max-h-[calc(100vh-22rem)] overflow-y-auto pr-1">
                {content?.sections && content.sections.map((section) => (
                  <div key={section.id} className="space-y-2">
                    {/* Section Header */}
                    <div className="px-2 py-1 text-xs font-bold text-gray-400 uppercase tracking-wider">
                      {section.title}
                    </div>

                    {/* Lessons list */}
                    <div className="space-y-1.5">
                      {section.lessons.map((lesson) => {
                        const isCurrent = lesson.id === activeLesson.id;
                        const isDone = completedLessonIds.includes(lesson.id);

                        return (
                          <button
                            key={lesson.id}
                            onClick={() => setActiveLessonId(lesson.id)}
                            className={`w-full text-left p-3 rounded-2xl border transition-all cursor-pointer flex items-center justify-between gap-3 ${
                              isCurrent
                                ? 'bg-lime-50/80 border-[#8DB600] text-gray-950 shadow-2xs'
                                : 'bg-gray-50/70 border-gray-100 hover:bg-gray-100 text-gray-700'
                            }`}
                          >
                            <div className="flex items-start gap-2.5 min-w-0">
                              {/* Status Icon */}
                              {isDone ? (
                                <div className="w-5 h-5 rounded-full bg-emerald-100 text-emerald-700 flex items-center justify-center shrink-0 mt-0.5">
                                  <Check className="w-3 h-3 stroke-[3]" />
                                </div>
                              ) : isCurrent ? (
                                <div className="w-5 h-5 rounded-full bg-[#8DB600] text-gray-950 flex items-center justify-center shrink-0 mt-0.5">
                                  <Play className="w-2.5 h-2.5 fill-current ml-0.5" />
                                </div>
                              ) : (
                                <div className="w-5 h-5 rounded-full border border-gray-300 flex items-center justify-center shrink-0 mt-0.5 text-gray-400">
                                  <span className="w-1.5 h-1.5 rounded-full bg-gray-300" />
                                </div>
                              )}

                              <div className="min-w-0">
                                <div className="flex items-center gap-1.5 flex-wrap">
                                  <p className={`text-xs font-bold line-clamp-1 ${isCurrent ? 'text-gray-950' : 'text-gray-800'}`}>
                                    {lesson.title}
                                  </p>
                                  {lesson.freePreview && !isUserEnrolled && (
                                    <span className="text-[9px] uppercase font-bold text-lime-700 bg-lime-100 px-1.5 py-0.5 rounded-full shrink-0">
                                      Free Preview
                                    </span>
                                  )}
                                  {lesson.locked && !isUserEnrolled && !lesson.freePreview && (
                                    <Lock className="w-3 h-3 text-gray-400 shrink-0" />
                                  )}
                                </div>
                                <span className="text-[11px] text-gray-400">
                                  {lesson.duration || '10m'} • {lesson.lessonType}
                                </span>
                              </div>
                            </div>
                          </button>
                        );
                      })}
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
