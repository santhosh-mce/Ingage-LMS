import React, { useState, useEffect } from 'react';
import {
  getAdminCareers,
  createAdminCareer,
  updateAdminCareer,
  deleteAdminCareer,
  assignCourseToCareer,
  removeCourseFromCareer,
  getAdminCourses,
  toggleCareerPublish,
  toggleCareerStatus,
} from '../../api/adminApi';
import {
  Briefcase,
  Plus,
  BookOpen,
  Edit2,
  Trash2,
  CheckCircle2,
  XCircle,
  RefreshCw,
  Search,
  Layers,
  Sparkles,
} from 'lucide-react';

export const AdminCareersPage: React.FC<{ onNavigate: (path: string) => void }> = ({ onNavigate }) => {
  const [careers, setCareers] = useState<any[]>([]);
  const [courses, setCourses] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);

  // Modal: Assign Course
  const [assignModalCareer, setAssignModalCareer] = useState<any>(null);
  const [selectedCourseId, setSelectedCourseId] = useState<string>('');

  // Modal: Create/Edit Career
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [editingCareerId, setEditingCareerId] = useState<number | null>(null);
  const [careerForm, setCareerForm] = useState({
    title: '',
    slug: '',
    category: 'Data & AI',
    level: 'Beginner to Advanced',
    duration: '6 Months',
    salaryMin: 600000,
    salaryMax: 1200000,
    jobOpenings: '15,000+',
    description: '',
    featured: false,
    popular: false,
    active: true,
    published: true,
  });

  const fetchData = async () => {
    setLoading(true);
    try {
      const [car, crs] = await Promise.all([
        getAdminCareers(),
        getAdminCourses(),
      ]);
      setCareers(car);
      setCourses(crs);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleSaveCareer = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!careerForm.title.trim()) return;

    try {
      if (editingCareerId) {
        await updateAdminCareer(editingCareerId, careerForm);
      } else {
        await createAdminCareer(careerForm);
      }
      setIsEditModalOpen(false);
      setEditingCareerId(null);
      fetchData();
    } catch (err) {
      alert('Failed to save career.');
    }
  };

  const handleTogglePublish = async (careerId: number, published: boolean) => {
    try {
      await toggleCareerPublish(careerId, published);
      fetchData();
    } catch {
      alert('Failed to update published status.');
    }
  };

  const handleToggleActive = async (careerId: number, active: boolean) => {
    try {
      await toggleCareerStatus(careerId, active);
      fetchData();
    } catch {
      alert('Failed to update active status.');
    }
  };

  const handleAssignCourse = async () => {
    if (!assignModalCareer || !selectedCourseId) return;
    try {
      await assignCourseToCareer(assignModalCareer.id, selectedCourseId);
      setAssignModalCareer(null);
      setSelectedCourseId('');
      fetchData();
    } catch {
      alert('Failed to assign course.');
    }
  };

  const handleRemoveCourse = async (careerId: number, courseId: number) => {
    if (!window.confirm('Remove course from this career roadmap?')) return;
    try {
      await removeCourseFromCareer(careerId, courseId);
      fetchData();
    } catch {
      alert('Failed to remove course.');
    }
  };

  const handleDeleteCareer = async (id: number) => {
    if (!window.confirm('Are you sure you want to remove this career role from database?')) return;
    try {
      await deleteAdminCareer(id);
      fetchData();
    } catch {
      alert('Failed to delete career.');
    }
  };

  return (
    <div className="space-y-6 animate-fade-in">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-white tracking-tight flex items-center gap-2">
            <Briefcase className="w-6 h-6 text-lime-400" />
            Career Paths & Role Specializations
          </h1>
          <p className="text-xs text-slate-400">
            Manage dynamic career paths in PostgreSQL and link curriculum courses to career roadmaps.
          </p>
        </div>

        <div className="flex items-center gap-3">
          <button
            onClick={fetchData}
            className="p-2.5 bg-slate-800 hover:bg-slate-700 text-slate-300 rounded-xl border border-slate-700 transition-colors cursor-pointer"
          >
            <RefreshCw className={`w-4 h-4 ${loading ? 'animate-spin text-lime-400' : ''}`} />
          </button>

          <button
            onClick={() => {
              setEditingCareerId(null);
              setCareerForm({
                title: '',
                slug: '',
                category: 'Data & AI',
                level: 'Beginner to Advanced',
                duration: '6 Months',
                salaryMin: 600000,
                salaryMax: 1200000,
                jobOpenings: '15,000+',
                description: '',
                featured: false,
                popular: false,
                active: true,
              });
              setIsEditModalOpen(true);
            }}
            className="flex items-center gap-2 px-4 py-2.5 bg-lime-500 hover:bg-lime-400 text-slate-950 font-bold text-xs rounded-xl transition-all shadow-lg shadow-lime-500/20 cursor-pointer"
          >
            <Plus className="w-4 h-4" />
            <span>Add Career Path</span>
          </button>
        </div>
      </div>

      {/* Careers Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {careers.length === 0 && !loading ? (
          <div className="col-span-2 py-12 text-center text-slate-400">
            No careers found.
          </div>
        ) : (
          careers.map((c) => (
            <div
              key={c.id}
              className="bg-slate-900 border border-slate-800 rounded-3xl p-5 sm:p-6 shadow-xl space-y-4 relative overflow-hidden"
            >
              <div className="flex items-start justify-between gap-3">
                <div>
                  <div className="flex items-center gap-2">
                    <span className="text-[10px] font-bold px-2 py-0.5 rounded-full bg-slate-800 text-slate-400 uppercase tracking-wider">
                      {c.category}
                    </span>
                    {c.featured && (
                      <span className="text-[10px] font-bold px-2 py-0.5 rounded-full bg-amber-500/10 text-amber-400 border border-amber-500/20">
                        Featured
                      </span>
                    )}

                    {/* Published Toggle Button */}
                    <button
                      onClick={() => handleTogglePublish(c.id, !c.published)}
                      className={`text-[10px] font-bold px-2.5 py-0.5 rounded-full border transition-all cursor-pointer ${
                        c.published !== false
                          ? 'bg-emerald-500/10 text-emerald-400 border-emerald-500/30 hover:bg-emerald-500/20'
                          : 'bg-amber-500/10 text-amber-400 border-amber-500/30 hover:bg-amber-500/20'
                      }`}
                      title="Click to toggle Published status for public Explore & catalog"
                    >
                      {c.published !== false ? '● Published' : '○ Unpublished'}
                    </button>

                    {/* Active Toggle Button */}
                    <button
                      onClick={() => handleToggleActive(c.id, !c.active)}
                      className={`text-[10px] font-bold px-2.5 py-0.5 rounded-full border transition-all cursor-pointer ${
                        c.active !== false
                          ? 'bg-blue-500/10 text-blue-400 border-blue-500/30 hover:bg-blue-500/20'
                          : 'bg-rose-500/10 text-rose-400 border-rose-500/30 hover:bg-rose-500/20'
                      }`}
                      title="Click to toggle Active status"
                    >
                      {c.active !== false ? 'Active' : 'Inactive'}
                    </button>
                  </div>
                  <h3 className="text-base font-bold text-white tracking-tight pt-1">
                    {c.title}
                  </h3>
                  <span className="text-[11px] font-mono text-slate-400">/roles/{c.slug}</span>
                </div>

                <div className="flex items-center gap-1.5">
                  <button
                    onClick={() => {
                      setEditingCareerId(c.id);
                      setCareerForm({
                        title: c.title,
                        slug: c.slug,
                        category: c.category,
                        level: c.level,
                        duration: c.duration,
                        salaryMin: c.salaryMin || 600000,
                        salaryMax: c.salaryMax || 1200000,
                        jobOpenings: c.jobOpenings || '10,000+',
                        description: c.description || '',
                        featured: c.featured || false,
                        popular: c.popular || false,
                        active: c.active !== false,
                        published: c.published !== false,
                      });
                      setIsEditModalOpen(true);
                    }}
                    className="p-1.5 rounded-lg bg-slate-800 hover:bg-slate-700 text-slate-300 hover:text-white transition-colors cursor-pointer"
                  >
                    <Edit2 className="w-3.5 h-3.5" />
                  </button>

                  <button
                    onClick={() => handleDeleteCareer(c.id)}
                    className="p-1.5 rounded-lg bg-slate-800 hover:bg-rose-500/20 text-slate-400 hover:text-rose-400 transition-colors cursor-pointer"
                  >
                    <Trash2 className="w-3.5 h-3.5" />
                  </button>
                </div>
              </div>

              <p className="text-xs text-slate-400 line-clamp-2">{c.description}</p>

              {/* Career Meta info */}
              <div className="grid grid-cols-3 gap-2 py-2 bg-slate-950/60 p-3 rounded-2xl border border-slate-800/80 text-[11px]">
                <div>
                  <span className="text-slate-400 block text-[10px]">Avg Salary</span>
                  <span className="font-bold text-lime-400">{c.avgSalary}</span>
                </div>
                <div>
                  <span className="text-slate-400 block text-[10px]">Openings</span>
                  <span className="font-bold text-white">{c.jobOpenings}</span>
                </div>
                <div>
                  <span className="text-slate-400 block text-[10px]">Duration</span>
                  <span className="font-bold text-white">{c.duration}</span>
                </div>
              </div>

              {/* Assigned Courses Section */}
              <div className="space-y-2 border-t border-slate-800 pt-3">
                <div className="flex items-center justify-between text-xs">
                  <span className="font-semibold text-slate-300 flex items-center gap-1.5">
                    <BookOpen className="w-3.5 h-3.5 text-lime-400" />
                    Assigned Courses ({c.assignedCoursesCount || 0})
                  </span>

                  <button
                    onClick={() => {
                      setAssignModalCareer(c);
                      setSelectedCourseId(courses[0]?.id ? String(courses[0].id) : '');
                    }}
                    className="text-[11px] font-bold text-lime-400 hover:text-lime-300 cursor-pointer"
                  >
                    + Assign Course
                  </button>
                </div>

                <div className="flex flex-wrap gap-1.5">
                  {c.assignedCourses?.length === 0 ? (
                    <span className="text-[11px] text-slate-400 italic">No courses linked yet.</span>
                  ) : (
                    c.assignedCourses?.map((crs: any) => (
                      <span
                        key={crs.id}
                        className="inline-flex items-center gap-1 px-2.5 py-1 rounded-lg bg-slate-950 border border-slate-800 text-[11px] font-medium text-slate-200"
                      >
                        <span className="truncate max-w-[150px]">{crs.title}</span>
                        <button
                          onClick={() => handleRemoveCourse(c.id, crs.id)}
                          className="text-slate-400 hover:text-rose-400 ml-1 cursor-pointer"
                        >
                          ✕
                        </button>
                      </span>
                    ))
                  )}
                </div>
              </div>
            </div>
          ))
        )}
      </div>

      {/* Modal: Assign Course */}
      {assignModalCareer && (
        <div className="fixed inset-0 bg-slate-950/80 backdrop-blur-xs flex items-center justify-center p-4 z-50">
          <div className="bg-slate-900 border border-slate-800 rounded-3xl p-6 max-w-md w-full space-y-4 shadow-2xl">
            <h3 className="text-base font-bold text-white">
              Assign Course to: {assignModalCareer.title}
            </h3>
            <p className="text-xs text-slate-400">
              Select an existing course from PostgreSQL to add to this career track.
            </p>

            <select
              value={selectedCourseId}
              onChange={(e) => setSelectedCourseId(e.target.value)}
              className="w-full p-2.5 bg-slate-950 border border-slate-800 rounded-xl text-xs text-white focus:outline-none focus:border-lime-400"
            >
              {courses.map((crs) => (
                <option key={crs.id} value={crs.id}>
                  {crs.title} (₹{crs.price})
                </option>
              ))}
            </select>

            <div className="flex justify-end gap-2 pt-2">
              <button
                onClick={() => setAssignModalCareer(null)}
                className="px-4 py-2 bg-slate-800 text-slate-300 rounded-xl text-xs font-semibold"
              >
                Cancel
              </button>
              <button
                onClick={handleAssignCourse}
                className="px-4 py-2 bg-lime-500 hover:bg-lime-400 text-slate-950 font-bold rounded-xl text-xs shadow-md shadow-lime-500/20"
              >
                Assign Course
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Modal: Create / Edit Career */}
      {isEditModalOpen && (
        <div className="fixed inset-0 bg-slate-950/80 backdrop-blur-xs flex items-center justify-center p-4 z-50">
          <form
            onSubmit={handleSaveCareer}
            className="bg-slate-900 border border-slate-800 rounded-3xl p-6 sm:p-8 max-w-lg w-full space-y-4 shadow-2xl max-h-[90vh] overflow-y-auto"
          >
            <h3 className="text-base font-bold text-white">
              {editingCareerId ? 'Edit Career Role' : 'Create New Career Role'}
            </h3>

            <div className="space-y-3">
              <div>
                <label className="text-xs font-semibold text-slate-300">Career Title *</label>
                <input
                  type="text"
                  required
                  value={careerForm.title}
                  onChange={(e) => setCareerForm({ ...careerForm, title: e.target.value })}
                  placeholder="e.g. AI Research Scientist"
                  className="w-full p-2.5 bg-slate-950 border border-slate-800 rounded-xl text-xs text-white focus:outline-none focus:border-lime-400 mt-1"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="text-xs font-semibold text-slate-300">Category</label>
                  <input
                    type="text"
                    value={careerForm.category}
                    onChange={(e) => setCareerForm({ ...careerForm, category: e.target.value })}
                    className="w-full p-2.5 bg-slate-950 border border-slate-800 rounded-xl text-xs text-white focus:outline-none focus:border-lime-400 mt-1"
                  />
                </div>

                <div>
                  <label className="text-xs font-semibold text-slate-300">Level</label>
                  <input
                    type="text"
                    value={careerForm.level}
                    onChange={(e) => setCareerForm({ ...careerForm, level: e.target.value })}
                    className="w-full p-2.5 bg-slate-950 border border-slate-800 rounded-xl text-xs text-white focus:outline-none focus:border-lime-400 mt-1"
                  />
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="text-xs font-semibold text-slate-300">Min Salary (₹)</label>
                  <input
                    type="number"
                    value={careerForm.salaryMin}
                    onChange={(e) => setCareerForm({ ...careerForm, salaryMin: Number(e.target.value) })}
                    className="w-full p-2.5 bg-slate-950 border border-slate-800 rounded-xl text-xs text-white focus:outline-none focus:border-lime-400 mt-1"
                  />
                </div>
                <div>
                  <label className="text-xs font-semibold text-slate-300">Max Salary (₹)</label>
                  <input
                    type="number"
                    value={careerForm.salaryMax}
                    onChange={(e) => setCareerForm({ ...careerForm, salaryMax: Number(e.target.value) })}
                    className="w-full p-2.5 bg-slate-950 border border-slate-800 rounded-xl text-xs text-white focus:outline-none focus:border-lime-400 mt-1"
                  />
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="text-xs font-semibold text-slate-300">Duration</label>
                  <input
                    type="text"
                    value={careerForm.duration}
                    onChange={(e) => setCareerForm({ ...careerForm, duration: e.target.value })}
                    className="w-full p-2.5 bg-slate-950 border border-slate-800 rounded-xl text-xs text-white focus:outline-none focus:border-lime-400 mt-1"
                  />
                </div>
                <div>
                  <label className="text-xs font-semibold text-slate-300">Job Openings Text</label>
                  <input
                    type="text"
                    value={careerForm.jobOpenings}
                    onChange={(e) => setCareerForm({ ...careerForm, jobOpenings: e.target.value })}
                    className="w-full p-2.5 bg-slate-950 border border-slate-800 rounded-xl text-xs text-white focus:outline-none focus:border-lime-400 mt-1"
                  />
                </div>
              </div>

              <div>
                <label className="text-xs font-semibold text-slate-300">Description</label>
                <textarea
                  rows={3}
                  value={careerForm.description}
                  onChange={(e) => setCareerForm({ ...careerForm, description: e.target.value })}
                  className="w-full p-2.5 bg-slate-950 border border-slate-800 rounded-xl text-xs text-white focus:outline-none focus:border-lime-400 mt-1"
                />
              </div>

              <div className="flex flex-wrap items-center gap-4 pt-1">
                <label className="flex items-center gap-2 text-xs text-slate-300 cursor-pointer">
                  <input
                    type="checkbox"
                    checked={careerForm.published}
                    onChange={(e) => setCareerForm({ ...careerForm, published: e.target.checked })}
                    className="w-4 h-4 rounded text-lime-500 bg-slate-950 border-slate-700"
                  />
                  <span className="font-semibold text-emerald-400">Published (Visible in Explore & Catalog)</span>
                </label>

                <label className="flex items-center gap-2 text-xs text-slate-300 cursor-pointer">
                  <input
                    type="checkbox"
                    checked={careerForm.active}
                    onChange={(e) => setCareerForm({ ...careerForm, active: e.target.checked })}
                    className="w-4 h-4 rounded text-lime-500 bg-slate-950 border-slate-700"
                  />
                  <span className="font-semibold text-blue-400">Active (Accepting Enrollments)</span>
                </label>

                <label className="flex items-center gap-2 text-xs text-slate-300 cursor-pointer">
                  <input
                    type="checkbox"
                    checked={careerForm.featured}
                    onChange={(e) => setCareerForm({ ...careerForm, featured: e.target.checked })}
                    className="w-4 h-4 rounded text-lime-500 bg-slate-950 border-slate-700"
                  />
                  <span>Featured</span>
                </label>

                <label className="flex items-center gap-2 text-xs text-slate-300 cursor-pointer">
                  <input
                    type="checkbox"
                    checked={careerForm.popular}
                    onChange={(e) => setCareerForm({ ...careerForm, popular: e.target.checked })}
                    className="w-4 h-4 rounded text-lime-500 bg-slate-950 border-slate-700"
                  />
                  <span>Popular</span>
                </label>
              </div>
            </div>

            <div className="flex justify-end gap-2 pt-4 border-t border-slate-800">
              <button
                type="button"
                onClick={() => setIsEditModalOpen(false)}
                className="px-4 py-2 bg-slate-800 text-slate-300 rounded-xl text-xs font-semibold"
              >
                Cancel
              </button>
              <button
                type="submit"
                className="px-5 py-2 bg-lime-500 hover:bg-lime-400 text-slate-950 font-bold rounded-xl text-xs shadow-md shadow-lime-500/20"
              >
                Save Career
              </button>
            </div>
          </form>
        </div>
      )}
    </div>
  );
};
