import { createSlice, createAsyncThunk, PayloadAction } from '@reduxjs/toolkit';
import {
  OpportunityItem,
  ApplicationItem,
  OpportunityType,
  WorkMode,
  getOpportunitiesApi,
  applyToOpportunityApi,
  getSavedOpportunityIds,
  saveOpportunityId,
  removeSavedOpportunityId,
  getStoredApplications,
} from '../../api/opportunityApi';

export interface OpportunityState {
  opportunities: OpportunityItem[];
  applications: ApplicationItem[];
  savedIds: string[];
  activeTab: 'all' | 'my-applications' | 'saved';
  selectedType: 'all' | OpportunityType;
  workModeFilter: 'all' | WorkMode;
  searchQuery: string;
  selectedOpportunity: OpportunityItem | null;
  loading: boolean;
  error: string | null;
  applyingId: string | null;
}

const initialState: OpportunityState = {
  opportunities: [],
  applications: typeof window !== 'undefined' ? getStoredApplications() : [],
  savedIds: typeof window !== 'undefined' ? getSavedOpportunityIds() : [],
  activeTab: 'all',
  selectedType: 'all',
  workModeFilter: 'all',
  searchQuery: '',
  selectedOpportunity: null,
  loading: false,
  error: null,
  applyingId: null,
};

export const fetchOpportunitiesThunk = createAsyncThunk(
  'opportunity/fetchOpportunities',
  async (_, { rejectWithValue }) => {
    try {
      const data = await getOpportunitiesApi();
      return data;
    } catch (err: any) {
      return rejectWithValue(err.message || 'Failed to fetch opportunities');
    }
  }
);

export const applyToOpportunityThunk = createAsyncThunk<
  ApplicationItem,
  { opportunity: OpportunityItem; notes?: string },
  { rejectValue: string }
>('opportunity/applyToOpportunity', async ({ opportunity, notes }, { rejectWithValue }) => {
  try {
    const application = await applyToOpportunityApi(opportunity, notes);
    return application;
  } catch (err: any) {
    return rejectWithValue(err.message || 'Failed to submit application');
  }
});

export const opportunitySlice = createSlice({
  name: 'opportunity',
  initialState,
  reducers: {
    setActiveTab: (state, action: PayloadAction<'all' | 'my-applications' | 'saved'>) => {
      state.activeTab = action.payload;
    },
    setSelectedType: (state, action: PayloadAction<'all' | OpportunityType>) => {
      state.selectedType = action.payload;
    },
    setWorkModeFilter: (state, action: PayloadAction<'all' | WorkMode>) => {
      state.workModeFilter = action.payload;
    },
    setSearchQuery: (state, action: PayloadAction<string>) => {
      state.searchQuery = action.payload;
    },
    setSelectedOpportunity: (state, action: PayloadAction<OpportunityItem | null>) => {
      state.selectedOpportunity = action.payload;
    },
    toggleSaveOpportunity: (state, action: PayloadAction<string>) => {
      const id = action.payload;
      if (state.savedIds.includes(id)) {
        state.savedIds = removeSavedOpportunityId(id);
      } else {
        state.savedIds = saveOpportunityId(id);
      }
    },
    clearFilters: (state) => {
      state.selectedType = 'all';
      state.workModeFilter = 'all';
      state.searchQuery = '';
    }
  },
  extraReducers: (builder) => {
    builder
      // Fetch Opportunities
      .addCase(fetchOpportunitiesThunk.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchOpportunitiesThunk.fulfilled, (state, action: PayloadAction<OpportunityItem[]>) => {
        state.loading = false;
        state.opportunities = action.payload;
      })
      .addCase(fetchOpportunitiesThunk.rejected, (state, action) => {
        state.loading = false;
        state.error = (action.payload as string) || 'Could not load opportunities. Please try again.';
      })
      // Apply to Opportunity
      .addCase(applyToOpportunityThunk.pending, (state, action) => {
        state.applyingId = action.meta.arg.opportunity.id;
      })
      .addCase(applyToOpportunityThunk.fulfilled, (state, action: PayloadAction<ApplicationItem>) => {
        state.applyingId = null;
        // Check if already in applications list
        const exists = state.applications.some((app) => app.opportunityId === action.payload.opportunityId);
        if (!exists) {
          state.applications.unshift(action.payload);
        }
      })
      .addCase(applyToOpportunityThunk.rejected, (state) => {
        state.applyingId = null;
      });
  },
});

export const {
  setActiveTab,
  setSelectedType,
  setWorkModeFilter,
  setSearchQuery,
  setSelectedOpportunity,
  toggleSaveOpportunity,
  clearFilters,
} = opportunitySlice.actions;

export default opportunitySlice.reducer;
