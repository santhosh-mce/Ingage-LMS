import React, { useState, useEffect } from 'react';
import {
  createAdminCourse,
  updateAdminCourse,
  getAdminCourseById,
  publishAdminCourse,
  addCourseSection,
  updateCourseSection,
  deleteCourseSection,
  addCourseLesson,
  updateCourseLesson,
  deleteCourseLesson,
  uploadMediaFile,
  getCourseCategories,
} from '../../api/adminApi';
import {
  BookOpen,
  ArrowLeft,
  Save,
  CheckCircle2,
  Plus,
  Trash2,
  Edit,
  Video,
  FileText,
  HelpCircle,
  Upload,
  Layers,
  Sparkles,
  AlertCircle,
  Eye,
  RefreshCw,
  FolderPlus,
} from 'lucide-react';

export interface AdminAddCoursePageProps {
  courseId?: string | number;
  onNavigate: (path: string) => void;
  onShowToast?: (msg: string) => void;
}

export const AdminAddCoursePage: React.FC<AdminAddCoursePageProps> = ({
  courseId,
  onNavigate,
  onShowToast,
}) => {
  const isEditing = Boolean(courseId);

  // Form State
  const [title, setTitle] = useState('');
  const [slug, setSlug] = useState('');
  const [shortDescription, setShortDescription] = useState('');
  const [description, setDescription] = useState('');
  const [category, setCategory] = useState('Data Science & AI');
  const [level, setLevel] = useState('Intermediate');
  const [language, setLanguage] = useState('English');
  const [duration, setDuration] = useState('12 hours');
  const [instructor, setInstructor] = useState('Ingage Senior Architect');
  const [thumbnail, setThumbnail] = useState('');
  const [banner, setBanner] = useState('');
  const [isFree, setIsFree] = useState(false);
  const [price, setPrice] = useState<number>(1999);
  const [discountType, setDiscountType] = useState<'PERCENTAGE' | 'FIXED_AMOUNT'>('PERCENTAGE');
  const [discountValue, setDiscountValue] = useState<number>(20);
  const [currency, setCurrency] = useState('INR');

  // Categories list
  const [categories, setCategories] = useState<any[]>([]);

  // Sections & Lessons builder
  const [sections, setSections] = useState<any[]>([]);
  const [savedCourseId, setSavedCourseId] = useState<number | string | null>(courseId || null);

  // Active Modals
  const [isAddingSection, setIsAddingSection] = useState(false);
  const [newSectionTitle, setNewSectionTitle] = useState('');
  const [newSectionDesc, setNewSectionDesc] = useState('');

  const [activeSectionForLesson, setActiveSectionForLesson] = useState<any>(null);
  const [lessonForm, setLessonForm] = useState({
    title: '',
    description: '',
    lessonType: 'VIDEO',
    contentUrl: '',
    duration: '10m',
    durationSeconds: 600,
    freePreview: false,
    required: true,
  });

  const [uploading, setUploading] = useState(false);
  const [uploadProgress, setUploadProgress] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);
  const [validationErrors, setValidationErrors] = useState<string[]>([]);

  // Calculate final price automatically
  const calculateFinalPrice = (): number => {
    if (isFree || price <= 0) return 0;
    if (!discountValue || discountValue <= 0) return price;

    if (discountType === 'PERCENTAGE') {
      const discounted = price - (price * (discountValue / 100));
      return Math.max(0, Math.round(discounted * 100) / 100);
    } else {
      return Math.max(0, price - discountValue);
    }
  };

  const finalPrice = calculateFinalPrice();

  useEffect(() => {
    getCourseCategories().then(setCategories).catch(() => {});

    if (courseId) {
      getAdminCourseById(courseId).then((data) => {
        setTitle(data.title || '');
        setSlug(data.slug || '');
        setShortDescription(data.shortDescription || '');
        setDescription(data.description || '');
        setCategory(data.category || 'Data Science & AI');
        setLevel(data.level || 'Intermediate');
        setLanguage(data.language || 'English');
        setDuration(data.duration || '12 hours');
        setInstructor(data.instructor || 'Ingage Lead Instructor');
        setThumbnail(data.thumbnail || '');
        setBanner(data.banner || '');
        setPrice(data.price || 0);
        setIsFree(data.price === 0);
        if (data.discountType) setDiscountType(data.discountType);
        if (data.discountValue) setDiscountValue(data.discountValue);
        setSections(data.sections || []);
        setSavedCourseId(courseId);
      }).catch((err) => {
        console.error('Failed to load course details', err);
      });
    }
  }, [courseId]);

  const handleFileUpload = async (e: React.ChangeEvent<HTMLInputElement>, target: 'thumbnail' | 'banner' | 'lessonVideo') => {
    const file = e.target.files?.[0];
    if (!file) return;

    setUploading(true);
    setUploadProgress(`Uploading ${file.name}...`);
    try {
      const categoryType = target === 'lessonVideo' ? 'video' : 'thumbnail';
      const res = await uploadMediaFile(file, categoryType);
      if (target === 'thumbnail') setThumbnail(res.url);
      else if (target === 'banner') setBanner(res.url);
      else if (target === 'lessonVideo') setLessonForm((prev) => ({ ...prev, contentUrl: res.url }));
      if (onShowToast) onShowToast('File uploaded successfully!');
    } catch (err) {
      console.error(err);
      if (onShowToast) onShowToast('Upload failed.');
    } finally {
      setUploading(false);
      setUploadProgress(null);
    }
  };

  const handleSaveDraft = async () => {
    if (!title.trim()) {
      alert('Please enter a course title.');
      return;
    }

    setSaving(true);
    try {
      const payload = {
        title: title.trim(),
        slug: slug.trim() || undefined,
        shortDescription,
        description,
        category,
        level,
        language,
        duration,
        instructor,
        thumbnail,
        banner,
        price: isFree ? 0 : price,
        discountType: isFree ? null : discountType,
        discountValue: isFree ? 0 : discountValue,
        status: 'DRAFT',
      };

      let result;
      if (savedCourseId) {
        result = await updateAdminCourse(savedCourseId, payload);
      } else {
        result = await createAdminCourse(payload);
        setSavedCourseId(result.id);
      }

      if (onShowToast) onShowToast('Course draft saved successfully!');
    } catch (err: any) {
      if (onShowToast) onShowToast('Failed to save draft.');
    } finally {
      setSaving(false);
    }
  };

  const handlePublish = async () => {
    setValidationErrors([]);
    const errors: string[] = [];

    if (!title.trim()) errors.push('Course title is required');
    if (!description.trim()) errors.push('Course description is required');
    if (!category.trim()) errors.push('Category is required');
    if (!thumbnail.trim()) errors.push('Course thumbnail is required');
    if (!isFree && (price == null || price <= 0)) errors.push('Valid price is required for paid courses');
    if (sections.length === 0) errors.push('Course must have at least one section/module');
    const totalLessons = sections.reduce((acc, s) => acc + (s.lessons?.length || 0), 0);
    if (totalLessons === 0) errors.push('Course must contain at least one lesson before publishing');

    if (errors.length > 0) {
      setValidationErrors(errors);
      return;
    }

    setSaving(true);
    try {
      // First save latest course info
      const payload = {
        title: title.trim(),
        slug: slug.trim() || undefined,
        shortDescription,
        description,
        category,
        level,
        language,
        duration,
        instructor,
        thumbnail,
        banner,
        price: isFree ? 0 : price,
        discountType: isFree ? null : discountType,
        discountValue: isFree ? 0 : discountValue,
      };

      let activeId = savedCourseId;
      if (activeId) {
        await updateAdminCourse(activeId, payload);
      } else {
        const created = await createAdminCourse(payload);
        activeId = created.id;
        setSavedCourseId(activeId);
      }

      // Then call publish
      const publishRes = await publishAdminCourse(activeId!);
      if (publishRes.success) {
        if (onShowToast) onShowToast('Course successfully published live!');
        onNavigate('/admin/courses');
      } else {
        setValidationErrors(publishRes.errors || [publishRes.message]);
      }
    } catch (err: any) {
      if (onShowToast) onShowToast('Publish failed: ' + (err.response?.data?.message || err.message));
    } finally {
      setSaving(false);
    }
  };

  // Section builder actions
  const handleAddSection = async () => {
    if (!newSectionTitle.trim()) return;
    if (!savedCourseId) {
      alert('Please save the course draft first before adding curriculum sections.');
      return;
    }

    try {
      const section = await addCourseSection(savedCourseId, {
        title: newSectionTitle.trim(),
        description: newSectionDesc.trim(),
      });
      setSections([...sections, { ...section, lessons: [] }]);
      setNewSectionTitle('');
      setNewSectionDesc('');
      setIsAddingSection(false);
      if (onShowToast) onShowToast('Section created!');
    } catch {
      if (onShowToast) onShowToast('Failed to add section.');
    }
  };

  const handleDeleteSection = async (secId: number) => {
    if (!window.confirm('Delete this section and all lessons in it?')) return;
    try {
      await deleteCourseSection(secId);
      setSections(sections.filter((s) => s.id !== secId));
      if (onShowToast) onShowToast('Section deleted.');
    } catch {
      if (onShowToast) onShowToast('Failed to delete section.');
    }
  };

  // Lesson builder actions
  const handleAddLesson = async () => {
    if (!activeSectionForLesson || !lessonForm.title.trim()) return;

    try {
      const lesson = await addCourseLesson(activeSectionForLesson.id, {
        title: lessonForm.title.trim(),
        description: lessonForm.description.trim(),
        lessonType: lessonForm.lessonType,
        contentUrl: lessonForm.contentUrl,
        duration: lessonForm.duration,
        durationSeconds: lessonForm.durationSeconds,
        freePreview: lessonForm.freePreview,
        required: lessonForm.required,
      });

      setSections(
        sections.map((s) => {
          if (s.id === activeSectionForLesson.id) {
            return { ...s, lessons: [...(s.lessons || []), lesson] };
          }
          return s;
        })
      );

      setActiveSectionForLesson(null);
      setLessonForm({
        title: '',
        description: '',
        lessonType: 'VIDEO',
        contentUrl: '',
        duration: '10m',
        durationSeconds: 600,
        freePreview: false,
        required: true,
      });
      if (onShowToast) onShowToast('Lesson added to curriculum!');
    } catch {
      if (onShowToast) onShowToast('Failed to add lesson.');
    }
  };

  const handleDeleteLesson = async (secId: number, lesId: number) => {
    if (!window.confirm('Delete this lesson?')) return;
    try {
      await deleteCourseLesson(lesId);
      setSections(
        sections.map((s) => {
          if (s.id === secId) {
            return { ...s, lessons: s.lessons.filter((l: any) => l.id !== lesId) };
          }
          return s;
        })
      );
      if (onShowToast) onShowToast('Lesson deleted.');
    } catch {
      if (onShowToast) onShowToast('Failed to delete lesson.');
    }
  };

  return (
    <div className="space-y-8 animate-fade-in pb-16">
      {/* Top Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div className="flex items-center gap-3">
          <button
            onClick={() => onNavigate('/admin/courses')}
            className="p-2 rounded-xl bg-slate-900 border border-slate-800 text-slate-400 hover:text-white transition-colors cursor-pointer"
          >
            <ArrowLeft className="w-4 h-4" />
          </button>
          <div>
            <h1 className="text-2xl font-bold text-white tracking-tight">
              {isEditing ? 'Edit Course Curriculum & Pricing' : 'Course Creation Studio'}
            </h1>
            <p className="text-xs text-slate-400">
              Configure course details, pricing discounts, and multi-module lesson content.
            </p>
          </div>
        </div>

        {/* Action Buttons */}
        <div className="flex items-center gap-2.5">
          <button
            onClick={handleSaveDraft}
            disabled={saving}
            className="flex items-center gap-2 px-4 py-2.5 rounded-xl bg-slate-800 hover:bg-slate-700 text-slate-200 text-xs font-semibold border border-slate-700 transition-all cursor-pointer"
          >
            <Save className="w-4 h-4" />
            <span>Save Draft</span>
          </button>

          <button
            onClick={handlePublish}
            disabled={saving}
            className="flex items-center gap-2 px-5 py-2.5 rounded-xl bg-lime-500 hover:bg-lime-400 text-slate-950 text-xs font-bold transition-all shadow-lg shadow-lime-500/20 cursor-pointer"
          >
            <CheckCircle2 className="w-4 h-4" />
            <span>Publish Live</span>
          </button>
        </div>
      </div>

      {/* Validation Errors Notice */}
      {validationErrors.length > 0 && (
        <div className="p-4 rounded-2xl bg-rose-500/10 border border-rose-500/30 text-rose-300 text-xs space-y-1">
          <div className="font-bold flex items-center gap-2 text-rose-400">
            <AlertCircle className="w-4 h-4" />
            <span>Cannot publish course. Please address the following issues:</span>
          </div>
          <ul className="list-disc list-inside space-y-0.5 pl-2 text-rose-200">
            {validationErrors.map((err, i) => (
              <li key={i}>{err}</li>
            ))}
          </ul>
        </div>
      )}

      {/* Basic Information Card */}
      <div className="bg-slate-900 border border-slate-800 rounded-3xl p-6 sm:p-8 shadow-xl space-y-6">
        <h2 className="text-base font-bold text-white flex items-center gap-2">
          <BookOpen className="w-4 h-4 text-lime-400" />
          1. Basic Course Information
        </h2>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div className="md:col-span-2 space-y-1">
            <label className="text-xs font-semibold text-slate-300">Course Title *</label>
            <input
              type="text"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              placeholder="e.g. Master Enterprise Python, Docker & Microservices"
              className="w-full p-2.5 bg-slate-950 border border-slate-800 rounded-xl text-xs text-white focus:outline-none focus:border-lime-400"
            />
          </div>

          <div className="space-y-1">
            <label className="text-xs font-semibold text-slate-300">Slug (URL identifier)</label>
            <input
              type="text"
              value={slug}
              onChange={(e) => setSlug(e.target.value)}
              placeholder="e.g. enterprise-python-docker (auto-generated if blank)"
              className="w-full p-2.5 bg-slate-950 border border-slate-800 rounded-xl text-xs text-white focus:outline-none focus:border-lime-400 font-mono"
            />
          </div>

          <div className="space-y-1">
            <label className="text-xs font-semibold text-slate-300">Category *</label>
            <select
              value={category}
              onChange={(e) => setCategory(e.target.value)}
              className="w-full p-2.5 bg-slate-950 border border-slate-800 rounded-xl text-xs text-white focus:outline-none focus:border-lime-400"
            >
              {categories.map((c) => (
                <option key={c.id} value={c.name}>{c.name}</option>
              ))}
              <option value="Data Science & AI">Data Science & AI</option>
              <option value="Software Development">Software Development</option>
              <option value="Cloud & DevOps">Cloud & DevOps</option>
              <option value="Design & UI/UX">Design & UI/UX</option>
              <option value="Business & Product">Business & Product</option>
            </select>
          </div>

          <div className="space-y-1">
            <label className="text-xs font-semibold text-slate-300">Level</label>
            <select
              value={level}
              onChange={(e) => setLevel(e.target.value)}
              className="w-full p-2.5 bg-slate-950 border border-slate-800 rounded-xl text-xs text-white focus:outline-none focus:border-lime-400"
            >
              <option value="Beginner">Beginner</option>
              <option value="Intermediate">Intermediate</option>
              <option value="Advanced">Advanced</option>
              <option value="All Levels">All Levels</option>
            </select>
          </div>

          <div className="space-y-1">
            <label className="text-xs font-semibold text-slate-300">Language</label>
            <input
              type="text"
              value={language}
              onChange={(e) => setLanguage(e.target.value)}
              placeholder="e.g. English"
              className="w-full p-2.5 bg-slate-950 border border-slate-800 rounded-xl text-xs text-white focus:outline-none focus:border-lime-400"
            />
          </div>

          <div className="space-y-1">
            <label className="text-xs font-semibold text-slate-300">Estimated Duration</label>
            <input
              type="text"
              value={duration}
              onChange={(e) => setDuration(e.target.value)}
              placeholder="e.g. 14 hours / 6 weeks"
              className="w-full p-2.5 bg-slate-950 border border-slate-800 rounded-xl text-xs text-white focus:outline-none focus:border-lime-400"
            />
          </div>

          <div className="space-y-1">
            <label className="text-xs font-semibold text-slate-300">Lead Instructor</label>
            <input
              type="text"
              value={instructor}
              onChange={(e) => setInstructor(e.target.value)}
              placeholder="Instructor Name"
              className="w-full p-2.5 bg-slate-950 border border-slate-800 rounded-xl text-xs text-white focus:outline-none focus:border-lime-400"
            />
          </div>

          <div className="md:col-span-2 space-y-1">
            <label className="text-xs font-semibold text-slate-300">Short Description</label>
            <input
              type="text"
              value={shortDescription}
              onChange={(e) => setShortDescription(e.target.value)}
              placeholder="Brief 1-2 sentence hook for course cards..."
              className="w-full p-2.5 bg-slate-950 border border-slate-800 rounded-xl text-xs text-white focus:outline-none focus:border-lime-400"
            />
          </div>

          <div className="md:col-span-2 space-y-1">
            <label className="text-xs font-semibold text-slate-300">Full Description *</label>
            <textarea
              rows={4}
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              placeholder="Comprehensive syllabus, target audience, and prerequisites..."
              className="w-full p-2.5 bg-slate-950 border border-slate-800 rounded-xl text-xs text-white focus:outline-none focus:border-lime-400"
            />
          </div>

          {/* Thumbnail & Banner Uploaders */}
          <div className="space-y-1">
            <label className="text-xs font-semibold text-slate-300">Thumbnail Image URL *</label>
            <div className="flex gap-2">
              <input
                type="text"
                value={thumbnail}
                onChange={(e) => setThumbnail(e.target.value)}
                placeholder="https://... or upload image"
                className="w-full p-2.5 bg-slate-950 border border-slate-800 rounded-xl text-xs text-white focus:outline-none focus:border-lime-400"
              />
              <label className="px-3 py-2 bg-slate-800 hover:bg-slate-700 text-slate-300 rounded-xl text-xs font-semibold cursor-pointer shrink-0 flex items-center gap-1">
                <Upload className="w-3.5 h-3.5" />
                <span>Upload</span>
                <input
                  type="file"
                  accept="image/*"
                  onChange={(e) => handleFileUpload(e, 'thumbnail')}
                  className="hidden"
                />
              </label>
            </div>
            {thumbnail && (
              <img src={thumbnail} alt="Thumbnail preview" className="w-24 h-16 object-cover rounded-lg mt-2 border border-slate-700" />
            )}
          </div>

          <div className="space-y-1">
            <label className="text-xs font-semibold text-slate-300">Course Banner Image URL</label>
            <div className="flex gap-2">
              <input
                type="text"
                value={banner}
                onChange={(e) => setBanner(e.target.value)}
                placeholder="Header cover photo URL"
                className="w-full p-2.5 bg-slate-950 border border-slate-800 rounded-xl text-xs text-white focus:outline-none focus:border-lime-400"
              />
              <label className="px-3 py-2 bg-slate-800 hover:bg-slate-700 text-slate-300 rounded-xl text-xs font-semibold cursor-pointer shrink-0 flex items-center gap-1">
                <Upload className="w-3.5 h-3.5" />
                <span>Upload</span>
                <input
                  type="file"
                  accept="image/*"
                  onChange={(e) => handleFileUpload(e, 'banner')}
                  className="hidden"
                />
              </label>
            </div>
          </div>
        </div>
      </div>

      {/* Pricing & Discount Engine */}
      <div className="bg-slate-900 border border-slate-800 rounded-3xl p-6 sm:p-8 shadow-xl space-y-6">
        <div className="flex items-center justify-between">
          <h2 className="text-base font-bold text-white flex items-center gap-2">
            <Sparkles className="w-4 h-4 text-lime-400" />
            2. Pricing & Promotional Discount
          </h2>

          <label className="flex items-center gap-2 text-xs font-bold text-slate-300 cursor-pointer">
            <input
              type="checkbox"
              checked={isFree}
              onChange={(e) => setIsFree(e.target.checked)}
              className="w-4 h-4 rounded text-lime-500 focus:ring-lime-400 bg-slate-950 border-slate-700"
            />
            <span>Mark as FREE Course</span>
          </label>
        </div>

        {!isFree && (
          <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
            <div className="space-y-1">
              <label className="text-xs font-semibold text-slate-300">Base Price (₹) *</label>
              <input
                type="number"
                min={0}
                value={price}
                onChange={(e) => setPrice(Math.max(0, Number(e.target.value)))}
                className="w-full p-2.5 bg-slate-950 border border-slate-800 rounded-xl text-xs text-white focus:outline-none focus:border-lime-400 font-bold"
              />
            </div>

            <div className="space-y-1">
              <label className="text-xs font-semibold text-slate-300">Discount Type</label>
              <select
                value={discountType}
                onChange={(e: any) => setDiscountType(e.target.value)}
                className="w-full p-2.5 bg-slate-950 border border-slate-800 rounded-xl text-xs text-white focus:outline-none focus:border-lime-400"
              >
                <option value="PERCENTAGE">PERCENTAGE (%)</option>
                <option value="FIXED_AMOUNT">FIXED AMOUNT (₹)</option>
              </select>
            </div>

            <div className="space-y-1">
              <label className="text-xs font-semibold text-slate-300">Discount Value</label>
              <input
                type="number"
                min={0}
                value={discountValue}
                onChange={(e) => setDiscountValue(Math.max(0, Number(e.target.value)))}
                className="w-full p-2.5 bg-slate-950 border border-slate-800 rounded-xl text-xs text-white focus:outline-none focus:border-lime-400"
              />
            </div>
          </div>
        )}

        {/* Live Calculation Preview Banner */}
        <div className="p-4 bg-slate-950/80 border border-slate-800 rounded-2xl flex flex-wrap items-center justify-between gap-4">
          <div className="space-y-0.5">
            <span className="text-[11px] font-semibold text-slate-400 uppercase tracking-wider">
              Automatic Price Calculation
            </span>
            <div className="flex items-center gap-3 text-xs">
              <span className="text-slate-400">Original Price: <b className="text-white">₹{isFree ? 0 : price}</b></span>
              {!isFree && discountValue > 0 && (
                <span className="text-rose-400">
                  Discount: {discountType === 'PERCENTAGE' ? `${discountValue}%` : `₹${discountValue}`}
                </span>
              )}
            </div>
          </div>

          <div className="flex items-center gap-2">
            <span className="text-xs text-slate-400">Student Pays:</span>
            <span className="text-xl font-bold text-lime-400">
              {isFree || finalPrice === 0 ? 'FREE' : `₹${finalPrice.toLocaleString()}`}
            </span>
          </div>
        </div>
      </div>

      {/* Course Content Builder (Sections & Lessons) */}
      <div className="bg-slate-900 border border-slate-800 rounded-3xl p-6 sm:p-8 shadow-xl space-y-6">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <div>
            <h2 className="text-base font-bold text-white flex items-center gap-2">
              <Layers className="w-4 h-4 text-purple-400" />
              3. Curriculum & Content Builder
            </h2>
            <p className="text-xs text-slate-400">
              Organize course into modules and lessons (Video, Text, PDF, Quiz, Assignment)
            </p>
          </div>

          <button
            onClick={() => {
              if (!savedCourseId) {
                handleSaveDraft();
              }
              setIsAddingSection(true);
            }}
            className="flex items-center gap-1.5 px-3.5 py-2 bg-slate-800 hover:bg-slate-700 text-slate-200 text-xs font-semibold rounded-xl border border-slate-700 transition-colors cursor-pointer self-start sm:self-auto"
          >
            <FolderPlus className="w-4 h-4 text-lime-400" />
            <span>Add Module / Section</span>
          </button>
        </div>

        {/* Sections List */}
        <div className="space-y-4">
          {sections.length === 0 ? (
            <div className="py-10 text-center text-xs text-slate-400 border-2 border-dashed border-slate-800 rounded-2xl space-y-2">
              <Layers className="w-8 h-8 text-slate-400 mx-auto" />
              <p>No curriculum sections created yet.</p>
              <p className="text-slate-400">Click &quot;Add Module / Section&quot; to begin building lessons.</p>
            </div>
          ) : (
            sections.map((sec, secIdx) => (
              <div key={sec.id} className="bg-slate-950/70 border border-slate-800 rounded-2xl p-4 sm:p-5 space-y-4">
                {/* Section Header */}
                <div className="flex items-center justify-between gap-2 border-b border-slate-800/80 pb-3">
                  <div className="flex items-center gap-2.5">
                    <span className="w-6 h-6 rounded-lg bg-slate-800 text-lime-400 font-bold text-xs flex items-center justify-center">
                      {secIdx + 1}
                    </span>
                    <div>
                      <h3 className="text-sm font-bold text-white">{sec.title}</h3>
                      {sec.description && <p className="text-[11px] text-slate-400">{sec.description}</p>}
                    </div>
                  </div>

                  <div className="flex items-center gap-2">
                    <button
                      onClick={() => {
                        setActiveSectionForLesson(sec);
                      }}
                      className="flex items-center gap-1 px-3 py-1.5 rounded-lg bg-lime-500/10 hover:bg-lime-500/20 text-lime-400 text-xs font-semibold transition-colors cursor-pointer"
                    >
                      <Plus className="w-3.5 h-3.5" />
                      <span>Add Lesson</span>
                    </button>

                    <button
                      onClick={() => handleDeleteSection(sec.id)}
                      className="p-1.5 text-slate-400 hover:text-rose-400 transition-colors"
                      title="Delete Section"
                    >
                      <Trash2 className="w-3.5 h-3.5" />
                    </button>
                  </div>
                </div>

                {/* Lessons in Section */}
                <div className="space-y-2 pl-2 sm:pl-4">
                  {sec.lessons?.length === 0 ? (
                    <p className="text-[11px] text-slate-400 italic py-2">No lessons inside this module yet.</p>
                  ) : (
                    sec.lessons?.map((les: any, lesIdx: number) => (
                      <div
                        key={les.id}
                        className="flex items-center justify-between p-2.5 bg-slate-900 border border-slate-800/80 rounded-xl text-xs hover:border-slate-700 transition-all"
                      >
                        <div className="flex items-center gap-2.5 overflow-hidden">
                          {les.lessonType === 'VIDEO' ? (
                            <Video className="w-4 h-4 text-sky-400 shrink-0" />
                          ) : les.lessonType === 'QUIZ' ? (
                            <HelpCircle className="w-4 h-4 text-purple-400 shrink-0" />
                          ) : (
                            <FileText className="w-4 h-4 text-emerald-400 shrink-0" />
                          )}
                          <span className="font-semibold text-slate-200 truncate">
                            {lesIdx + 1}. {les.title}
                          </span>
                          {les.duration && (
                            <span className="text-[10px] text-slate-400 shrink-0">({les.duration})</span>
                          )}
                          {les.freePreview && (
                            <span className="text-[10px] px-1.5 py-0.5 rounded bg-lime-500/10 text-lime-400 border border-lime-500/20 uppercase font-bold shrink-0">
                              Free Preview
                            </span>
                          )}
                        </div>

                        <button
                          onClick={() => handleDeleteLesson(sec.id, les.id)}
                          className="text-slate-400 hover:text-rose-400 p-1 transition-colors"
                          title="Delete Lesson"
                        >
                          <Trash2 className="w-3 h-3" />
                        </button>
                      </div>
                    ))
                  )}
                </div>
              </div>
            ))
          )}
        </div>
      </div>

      {/* Modal: Add Section */}
      {isAddingSection && (
        <div className="fixed inset-0 bg-slate-950/80 backdrop-blur-xs flex items-center justify-center p-4 z-50">
          <div className="bg-slate-900 border border-slate-800 rounded-3xl p-6 max-w-md w-full space-y-4 shadow-2xl">
            <h3 className="text-base font-bold text-white">Add Curriculum Module / Section</h3>
            <div className="space-y-3">
              <div>
                <label className="text-xs font-semibold text-slate-300">Section Title *</label>
                <input
                  type="text"
                  value={newSectionTitle}
                  onChange={(e) => setNewSectionTitle(e.target.value)}
                  placeholder="e.g. Module 1: Architecture & System Setup"
                  className="w-full p-2.5 bg-slate-950 border border-slate-800 rounded-xl text-xs text-white focus:outline-none focus:border-lime-400 mt-1"
                />
              </div>
              <div>
                <label className="text-xs font-semibold text-slate-300">Module Description</label>
                <input
                  type="text"
                  value={newSectionDesc}
                  onChange={(e) => setNewSectionDesc(e.target.value)}
                  placeholder="Brief objectives of this module"
                  className="w-full p-2.5 bg-slate-950 border border-slate-800 rounded-xl text-xs text-white focus:outline-none focus:border-lime-400 mt-1"
                />
              </div>
            </div>

            <div className="flex justify-end gap-2 pt-2">
              <button
                onClick={() => setIsAddingSection(false)}
                className="px-4 py-2 bg-slate-800 hover:bg-slate-700 text-slate-300 rounded-xl text-xs font-semibold cursor-pointer"
              >
                Cancel
              </button>
              <button
                onClick={handleAddSection}
                className="px-4 py-2 bg-lime-500 hover:bg-lime-400 text-slate-950 font-bold rounded-xl text-xs cursor-pointer shadow-md shadow-lime-500/20"
              >
                Add Module
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Modal: Add Lesson */}
      {activeSectionForLesson && (
        <div className="fixed inset-0 bg-slate-950/80 backdrop-blur-xs flex items-center justify-center p-4 z-50">
          <div className="bg-slate-900 border border-slate-800 rounded-3xl p-6 max-w-lg w-full space-y-4 shadow-2xl max-h-[90vh] overflow-y-auto">
            <h3 className="text-base font-bold text-white">
              Add Lesson to {activeSectionForLesson.title}
            </h3>

            <div className="space-y-3">
              <div>
                <label className="text-xs font-semibold text-slate-300">Lesson Title *</label>
                <input
                  type="text"
                  value={lessonForm.title}
                  onChange={(e) => setLessonForm({ ...lessonForm, title: e.target.value })}
                  placeholder="e.g. 1.1 Microservices Architecture Walkthrough"
                  className="w-full p-2.5 bg-slate-950 border border-slate-800 rounded-xl text-xs text-white focus:outline-none focus:border-lime-400 mt-1"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="text-xs font-semibold text-slate-300">Lesson Type</label>
                  <select
                    value={lessonForm.lessonType}
                    onChange={(e) => setLessonForm({ ...lessonForm, lessonType: e.target.value })}
                    className="w-full p-2.5 bg-slate-950 border border-slate-800 rounded-xl text-xs text-white focus:outline-none focus:border-lime-400 mt-1"
                  >
                    <option value="VIDEO">VIDEO</option>
                    <option value="TEXT">TEXT</option>
                    <option value="PDF">PDF / DOCUMENT</option>
                    <option value="QUIZ">QUIZ</option>
                    <option value="ASSIGNMENT">ASSIGNMENT</option>
                  </select>
                </div>

                <div>
                  <label className="text-xs font-semibold text-slate-300">Duration (e.g. 15m)</label>
                  <input
                    type="text"
                    value={lessonForm.duration}
                    onChange={(e) => setLessonForm({ ...lessonForm, duration: e.target.value })}
                    placeholder="15m"
                    className="w-full p-2.5 bg-slate-950 border border-slate-800 rounded-xl text-xs text-white focus:outline-none focus:border-lime-400 mt-1"
                  />
                </div>
              </div>

              {/* Video upload / URL */}
              <div>
                <label className="text-xs font-semibold text-slate-300">Content / Video URL *</label>
                <div className="flex gap-2 mt-1">
                  <input
                    type="text"
                    value={lessonForm.contentUrl}
                    onChange={(e) => setLessonForm({ ...lessonForm, contentUrl: e.target.value })}
                    placeholder="https://... or upload MP4/WEBM/MOV"
                    className="w-full p-2.5 bg-slate-950 border border-slate-800 rounded-xl text-xs text-white focus:outline-none focus:border-lime-400"
                  />
                  <label className="px-3 py-2 bg-slate-800 hover:bg-slate-700 text-slate-300 rounded-xl text-xs font-semibold cursor-pointer shrink-0 flex items-center gap-1">
                    <Upload className="w-3.5 h-3.5" />
                    <span>Upload</span>
                    <input
                      type="file"
                      accept="video/*,application/pdf"
                      onChange={(e) => handleFileUpload(e, 'lessonVideo')}
                      className="hidden"
                    />
                  </label>
                </div>
                {uploadProgress && (
                  <p className="text-[11px] text-lime-400 mt-1 animate-pulse">{uploadProgress}</p>
                )}
              </div>

              <div>
                <label className="text-xs font-semibold text-slate-300">Description / Summary</label>
                <textarea
                  rows={2}
                  value={lessonForm.description}
                  onChange={(e) => setLessonForm({ ...lessonForm, description: e.target.value })}
                  placeholder="Key topics covered in this lesson..."
                  className="w-full p-2.5 bg-slate-950 border border-slate-800 rounded-xl text-xs text-white focus:outline-none focus:border-lime-400 mt-1"
                />
              </div>

              <div className="flex items-center justify-between pt-2">
                <label className="flex items-center gap-2 text-xs text-slate-300 cursor-pointer">
                  <input
                    type="checkbox"
                    checked={lessonForm.freePreview}
                    onChange={(e) => setLessonForm({ ...lessonForm, freePreview: e.target.checked })}
                    className="w-4 h-4 rounded text-lime-500 bg-slate-950 border-slate-700"
                  />
                  <span>Mark as Free Preview</span>
                </label>

                <label className="flex items-center gap-2 text-xs text-slate-300 cursor-pointer">
                  <input
                    type="checkbox"
                    checked={lessonForm.required}
                    onChange={(e) => setLessonForm({ ...lessonForm, required: e.target.checked })}
                    className="w-4 h-4 rounded text-lime-500 bg-slate-950 border-slate-700"
                  />
                  <span>Required for Completion</span>
                </label>
              </div>
            </div>

            <div className="flex justify-end gap-2 pt-4 border-t border-slate-800">
              <button
                onClick={() => setActiveSectionForLesson(null)}
                className="px-4 py-2 bg-slate-800 hover:bg-slate-700 text-slate-300 rounded-xl text-xs font-semibold cursor-pointer"
              >
                Cancel
              </button>
              <button
                onClick={handleAddLesson}
                className="px-4 py-2 bg-lime-500 hover:bg-lime-400 text-slate-950 font-bold rounded-xl text-xs cursor-pointer shadow-md shadow-lime-500/20"
              >
                Save Lesson
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
