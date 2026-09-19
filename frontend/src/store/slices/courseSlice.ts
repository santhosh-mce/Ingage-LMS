import { createSlice, createAsyncThunk, PayloadAction } from '@reduxjs/toolkit';
import { CourseDto, getCourses } from '../../api/courseApi';
import { CourseContentDetail, getCourseContent, getMyEnrollments, UserEnrollmentRecord } from '../../api/paymentApi';

export interface CourseState {
  courses: CourseDto[];
  selectedCourseContent: CourseContentDetail | null;
  loading: boolean;
  contentLoading: boolean;
  error: string | null;
  enrollments: UserEnrollmentRecord[];
  enrollmentsLoading: boolean;
  enrollmentsError: string | null;
}

const initialState: CourseState = {
  courses: [],
  selectedCourseContent: null,
  loading: false,
  contentLoading: false,
  error: null,
  enrollments: [],
  enrollmentsLoading: false,
  enrollmentsError: null,
};

export const fetchCourses = createAsyncThunk<CourseDto[], string | void, { rejectValue: string }>(
  'course/fetchCourses',
  async (search, { rejectWithValue }) => {
    try {
      const response = await getCourses(search || undefined);
      return response;
    } catch (err: any) {
      return rejectWithValue(err?.response?.data?.message || err?.message || 'Failed to load courses');
    }
  }
);

export const fetchCourseContent = createAsyncThunk<CourseContentDetail, number | string, { rejectValue: string }>(
  'course/fetchCourseContent',
  async (courseId, { rejectWithValue }) => {
    try {
      const response = await getCourseContent(courseId);
      return response;
    } catch (err: any) {
      return rejectWithValue(err?.response?.data?.error || err?.message || 'Failed to load course details');
    }
  }
);

export const fetchMyEnrollments = createAsyncThunk<UserEnrollmentRecord[], void, { rejectValue: string }>(
  'course/fetchMyEnrollments',
  async (_, { rejectWithValue }) => {
    try {
      const response = await getMyEnrollments();
      return response;
    } catch (err: any) {
      return rejectWithValue(err?.response?.data?.message || err?.message || 'Failed to load enrolled courses');
    }
  }
);

export const courseSlice = createSlice({
  name: 'course',
  initialState,
  reducers: {
    setSelectedCourseContent: (state, action: PayloadAction<CourseContentDetail | null>) => {
      state.selectedCourseContent = action.payload;
    },
    clearSelectedCourseContent: (state) => {
      state.selectedCourseContent = null;
      state.error = null;
    },
    markCourseAsEnrolled: (state, action: PayloadAction<number>) => {
      if (state.selectedCourseContent && state.selectedCourseContent.id === action.payload) {
        state.selectedCourseContent.isEnrolled = true;
      }
    },
  },
  extraReducers: (builder) => {
    // Fetch Courses List
    builder
      .addCase(fetchCourses.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchCourses.fulfilled, (state, action) => {
        state.courses = action.payload;
        state.loading = false;
        state.error = null;
      })
      .addCase(fetchCourses.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || 'Failed to load courses';
      });

    // Fetch Course Content
    builder
      .addCase(fetchCourseContent.pending, (state) => {
        state.contentLoading = true;
        state.error = null;
      })
      .addCase(fetchCourseContent.fulfilled, (state, action) => {
        state.selectedCourseContent = action.payload;
        state.contentLoading = false;
        state.error = null;
      })
      .addCase(fetchCourseContent.rejected, (state, action) => {
        state.contentLoading = false;
        state.error = action.payload || 'Failed to load course content';
      });

    // Fetch User Enrollments
    builder
      .addCase(fetchMyEnrollments.pending, (state) => {
        state.enrollmentsLoading = true;
        state.enrollmentsError = null;
      })
      .addCase(fetchMyEnrollments.fulfilled, (state, action) => {
        state.enrollments = action.payload;
        state.enrollmentsLoading = false;
        state.enrollmentsError = null;
      })
      .addCase(fetchMyEnrollments.rejected, (state, action) => {
        state.enrollmentsLoading = false;
        state.enrollmentsError = action.payload || 'Failed to load enrolled courses';
      });
  },
});

export const { setSelectedCourseContent, clearSelectedCourseContent, markCourseAsEnrolled } =
  courseSlice.actions;

export default courseSlice.reducer;
