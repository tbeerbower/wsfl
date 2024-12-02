<template>
  <div class="drafts-view">
    <header class="drafts-header">
      <h1>My Drafts</h1>
      <router-link to="/add-draft" class="add-draft-button">
        Add Draft
      </router-link>
    </header>

    <div class="drafts-content">
      <!-- In Progress Drafts -->
      <div class="drafts-section">
        <h2>In Progress</h2>
        <div v-if="loading">Loading drafts...</div>
        <div v-else-if="error" class="error-message">{{ error }}</div>
        <div v-else-if="inProgressDrafts.length === 0" class="empty-state">
          No drafts in progress
        </div>
        <div v-else class="drafts-grid">
          <DraftCard
            v-for="draft in inProgressDrafts"
            :key="draft.id"
            :draft="draft"
            :is-my-team-on-clock="isMyTeamOnClock(draft)"
          />
        </div>
      </div>

      <!-- Pending Drafts -->
      <div class="drafts-section">
        <h2>Pending</h2>
        <div v-if="loading">Loading drafts...</div>
        <div v-else-if="error" class="error-message">{{ error }}</div>
        <div v-else-if="pendingDrafts.length === 0" class="empty-state">
          No pending drafts
        </div>
        <div v-else class="drafts-grid">
          <DraftCard
            v-for="draft in pendingDrafts"
            :key="draft.id"
            :draft="draft"
            :is-my-team-on-clock="isMyTeamOnClock(draft)"
          />
        </div>
      </div>

      <!-- Completed Drafts -->
      <div class="drafts-section">
        <h2>Completed</h2>
        <div v-if="loading">Loading drafts...</div>
        <div v-else-if="error" class="error-message">{{ error }}</div>
        <div v-else-if="completedDrafts.length === 0" class="empty-state">
          No completed drafts
        </div>
        <div v-else class="drafts-grid">
          <DraftCard
            v-for="draft in completedDrafts"
            :key="draft.id"
            :draft="draft"
            :is-my-team-on-clock="isMyTeamOnClock(draft)"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { defineComponent, ref, computed, onMounted } from 'vue'
import { useStore } from 'vuex'
import axios from 'axios'
import DraftCard from '@/components/DraftCard.vue'

export default defineComponent({
  name: 'DraftsView',
  components: {
    DraftCard
  },
  setup() {
    const store = useStore()
    const user = store.getters['auth/currentUser']
    
    const drafts = ref([])
    const loading = ref(false)
    const error = ref(null)
    const draftTeams = ref({})

    const inProgressDrafts = computed(() => 
      drafts.value.filter(draft => getDraftStatus(draft) === 'IN_PROGRESS')
    )

    const pendingDrafts = computed(() => 
      drafts.value.filter(draft => getDraftStatus(draft) === 'PENDING')
    )

    const completedDrafts = computed(() => 
      drafts.value.filter(draft => getDraftStatus(draft) === 'COMPLETE')
    )

    const getDraftStatus = (draft) => {
      if (draft.completedAt) return 'COMPLETE'
      if (draft.startedAt) return 'IN_PROGRESS'
      return 'PENDING'
    }

    const isMyTeamOnClock = (draft) => {
      if (!draft || !draft.currentTeamId) return false
      const myTeam = draftTeams.value[draft.id]?.find(team => 
        team.ownerId === user.id
      )
      return myTeam?.id === draft.currentTeamId
    }

    const fetchDrafts = async () => {
      if (!user) return
      
      loading.value = true
      error.value = null
      try {
        const response = await axios.get('/api/drafts', {
          params: { userId: user.id }
        })
        drafts.value = response.data.content || []
        
        // Fetch teams for each draft
        await Promise.all(
          drafts.value.map(async (draft) => {
            try {
              const teamsResponse = await axios.get(`/api/drafts/${draft.id}/teams`)
              draftTeams.value[draft.id] = teamsResponse.data.content || []
            } catch (err) {
              console.error(`Failed to load teams for draft ${draft.id}:`, err)
            }
          })
        )
      } catch (err) {
        error.value = 'Failed to load drafts'
        console.error(err)
      } finally {
        loading.value = false
      }
    }

    onMounted(fetchDrafts)

    return {
      drafts,
      loading,
      error,
      inProgressDrafts,
      pendingDrafts,
      completedDrafts,
      isMyTeamOnClock,
      getDraftStatus
    }
  }
})
</script>

<style scoped>
.drafts-view {
  padding: 2rem;
}

.drafts-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.drafts-content {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.drafts-section {
  background: white;
  padding: 1.5rem;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.drafts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1.5rem;
  margin-top: 1rem;
}

.add-draft-button {
  display: inline-flex;
  align-items: center;
  padding: 0.5rem 1rem;
  background-color: #3182ce;
  color: white;
  border: none;
  border-radius: 4px;
  text-decoration: none;
  font-weight: 500;
  transition: background-color 0.2s;
}

.add-draft-button:hover {
  background-color: #2c5282;
}

.error-message {
  color: #f44336;
  margin-top: 10px;
}

.empty-state {
  color: #666;
  margin-top: 10px;
  font-style: italic;
}

h1 {
  margin: 0;
  font-size: 24px;
}

h2 {
  margin: 0 0 1rem 0;
  font-size: 20px;
  color: #2c3e50;
}
</style>
