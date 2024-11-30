<template>
  <div class="league-admin">
    <div class="league-admin-container">
      <header class="league-header">
        <div class="header-content">
          <h1>{{ league?.name || 'Loading...' }}</h1>
        </div>
      </header>

      <div v-if="loading.league" class="loading">Loading league details...</div>
      <div v-else-if="error.league" class="error-message">{{ error.league }}</div>
      <div v-else class="league-content">
        <section class="teams-section">
          <h2>League Teams</h2>
          <div class="teams-grid">
            <div v-for="team in league?.teams" :key="team.id" class="team-card">
              <h3>{{ team.name }}</h3>
              <div class="team-stats">
                <div class="stat-item">
                  <span class="stat-label">Record</span>
                  <span class="stat-value">{{ team.wins }}-{{ team.losses }}-{{ team.ties }}</span>
                </div>
                <div class="stat-item">
                  <span class="stat-label">Score</span>
                  <span class="stat-value">{{ team.totalScore }}</span>
                </div>
              </div>
            </div>
          </div>
        </section>

        <section class="drafts-section">
          <div class="section-header">
            <h2>Drafts</h2>
            <router-link 
              :to="`/leagues/${route.params.id}/add-draft`" 
              class="add-button"
            >
              Add Draft
            </router-link>
          </div>
          <div v-if="loading.drafts" class="loading">Loading drafts...</div>
          <div v-else-if="error.drafts" class="error-message">{{ error.drafts }}</div>
          <div v-else-if="drafts.length === 0" class="no-content">No drafts found for this league.</div>
          <div v-else class="drafts-grid">
            <div v-for="draft in drafts" :key="draft.id" class="draft-card" @click="router.push(`/drafts/${draft.id}`)">
              <div class="draft-header">
                <h3>{{ draft.name }}</h3>
                <span :class="['draft-status', getDraftStatusClass(draft)]">
                  {{ getDraftStatus(draft) }}
                </span>
              </div>
              <div class="draft-details">
                <div class="detail-item">
                  <span class="detail-label">Season:</span>
                  <span class="detail-value">{{ draft.season }}</span>
                </div>
                <div class="detail-item">
                  <span class="detail-label">Rounds:</span>
                  <span class="detail-value">{{ draft.numberOfRounds }}</span>
                </div>
                <div class="detail-item">
                  <span class="detail-label">Draft Type:</span>
                  <span class="detail-value">{{ draft.snakeOrder ? 'Snake' : 'Standard' }}</span>
                </div>
                <div class="detail-item">
                  <span class="detail-label">Start Time:</span>
                  <span class="detail-value">{{ new Date(draft.startTime).toLocaleString() }}</span>
                </div>
                <div v-if="!draft.complete" class="detail-item">
                  <span class="detail-label">Current Round:</span>
                  <span class="detail-value">{{ draft.currentRound }}/{{ draft.numberOfRounds }}</span>
                </div>
              </div>
            </div>
          </div>
        </section>

        <section class="add-team-section">
          <h2>Add Team to League</h2>
          <div v-if="loading.availableTeams" class="loading">Loading available teams...</div>
          <div v-else-if="error.availableTeams" class="error-message">{{ error.availableTeams }}</div>
          <div v-else-if="availableTeams.length === 0" class="empty-state">
            No teams available to add to this league.
          </div>
          <div v-else class="available-teams-list">
            <div v-for="team in availableTeams" :key="team.id" class="available-team-row">
              <div class="team-info">
                <h3>{{ team.name }}</h3>
                <div class="team-details">
                  <div v-if="team.league" class="current-league">
                    Currently in: {{ team.league.name }}
                  </div>
                  <div class="team-owner">
                    Owner: {{ team.owner.name }}
                  </div>
                </div>
              </div>
              <button 
                @click="addTeamToLeague(team)"
                :disabled="loading.addTeam === team.id"
                class="add-button"
              >
                {{ loading.addTeam === team.id ? 'Adding...' : 'Move to League' }}
              </button>
            </div>
          </div>
        </section>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, defineComponent } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'

export default defineComponent({
  name: 'LeagueAdminView',
  setup() {
    const route = useRoute()
    const router = useRouter()
    const leagueId = route.params.id

    const league = ref(null)
    const availableTeams = ref([])
    const drafts = ref([])
    const loading = ref({
      league: false,
      availableTeams: false,
      addTeam: null,
      drafts: false
    })
    const error = ref({
      league: null,
      availableTeams: null,
      drafts: null
    })

    const fetchLeague = async () => {
      loading.value.league = true
      error.value.league = null
      try {
        const response = await axios.get(`/api/leagues/${leagueId}`)
        league.value = response.data
      } catch (err) {
        error.value.league = 'Failed to load league details'
      } finally {
        loading.value.league = false
      }
    }

    const fetchAvailableTeams = async () => {
      loading.value.availableTeams = true
      error.value.availableTeams = null
      try {
        const response = await axios.get('/api/teams')
        // Filter out teams that are already in this league
        availableTeams.value = (response.data.content || [])
          .filter(team => !team.league || team.league.id !== Number(leagueId))
      } catch (err) {
        error.value.availableTeams = 'Failed to load available teams'
      } finally {
        loading.value.availableTeams = false
      }
    }

    const fetchDrafts = async () => {
      loading.value.drafts = true
      error.value.drafts = null
      try {
        const response = await axios.get(`/api/drafts?leagueId=${leagueId}`)
        drafts.value = response.data.content || []
      } catch (err) {
        error.value.drafts = 'Failed to load drafts'
      } finally {
        loading.value.drafts = false
      }
    }

    const addTeamToLeague = async (team) => {
      loading.value.addTeam = team.id
      try {
        await axios.put(`/api/teams/${team.id}`, {
          name: team.name,
          leagueId: Number(leagueId),
          city: team.city,
          state: team.state
        })
        
        // Refresh both lists
        await Promise.all([
          fetchLeague(),
          fetchAvailableTeams()
        ])
      } catch (err) {
        // Show error in UI
        error.value.availableTeams = 'Failed to add team to league'
      } finally {
        loading.value.addTeam = null
      }
    }

    const getDraftStatus = (draft) => {
      if (draft.complete === true) return 'Complete'
      if (draft.started === true) return 'In Progress'
      return 'Not Started'
    }

    const getDraftStatusClass = (draft) => {
      if (draft.complete === true) return 'complete'
      if (draft.started === true) return 'in-progress'
      return 'not-started'
    }

    onMounted(() => {
      if (!leagueId) {
        router.push('/dashboard')
        return
      }
      
      fetchLeague()
      fetchAvailableTeams()
      fetchDrafts()
    })

    return {
      league,
      availableTeams,
      drafts,
      loading,
      error,
      addTeamToLeague,
      getDraftStatus,
      getDraftStatusClass,
      route,
      router
    }
  }
})
</script>

<style scoped>
.league-admin {
  padding: 2rem;
}

.league-admin-container {
  max-width: 1200px;
  margin: 0 auto;
}

.league-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.header-content {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.league-header h1 {
  margin: 0;
  font-size: 1.8rem;
  color: #2c3e50;
}

.season-badge {
  padding: 0.25rem 0.75rem;
  background-color: #e3f2fd;
  color: #1976d2;
  border-radius: 1rem;
  font-size: 0.875rem;
}

.league-content {
  display: grid;
  gap: 2rem;
}

section {
  background-color: white;
  border-radius: 8px;
  padding: 1.5rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

section h2 {
  margin: 0 0 1.5rem 0;
  font-size: 1.2rem;
  color: #2c3e50;
}

.teams-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 1rem;
}

.team-card {
  padding: 1rem;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  background-color: #f8fafc;
}

.team-card h3 {
  margin: 0 0 1rem 0;
  font-size: 1rem;
  color: #2c3e50;
}

.team-stats {
  display: flex;
  gap: 1rem;
}

.stat-item {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.stat-label {
  font-size: 0.75rem;
  color: #64748b;
}

.stat-value {
  font-size: 0.875rem;
  font-weight: 600;
  color: #1e293b;
}

.drafts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 1rem;
}

.draft-card {
  padding: 1rem;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  background-color: #f8fafc;
  cursor: pointer;
  transition: all 0.2s;
}

.draft-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
}

.draft-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.draft-header h3 {
  margin: 0;
  font-size: 1rem;
  color: #2c3e50;
}

.draft-status {
  font-size: 0.875rem;
  font-weight: 500;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
}

.draft-status.complete {
  background-color: #34c759;
  color: white;
}

.draft-status.in-progress {
  background-color: #f7dc6f;
  color: #1e293b;
}

.draft-status.not-started {
  background-color: #94a3b8;
  color: white;
}

.draft-details {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.detail-item {
  display: flex;
  gap: 0.5rem;
}

.detail-label {
  font-size: 0.75rem;
  color: #64748b;
}

.detail-value {
  font-size: 0.875rem;
  font-weight: 600;
  color: #1e293b;
}

.available-teams-list {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.available-team-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem;
  background-color: #f8fafc;
  border-radius: 6px;
  transition: background-color 0.2s;
}

.available-team-row:hover {
  background-color: #f1f5f9;
}

.team-info h3 {
  margin: 0;
  font-size: 1rem;
  color: #2c3e50;
}

.team-details {
  font-size: 0.875rem;
  color: #64748b;
  margin-top: 0.5rem;
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.current-league {
  color: #9333ea;
  font-weight: 500;
}

.team-owner {
  color: #475569;
}

.add-button {
  padding: 0.5rem 1rem;
  background-color: #3182ce;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s;
}

.add-button:hover:not(:disabled) {
  background-color: #2c5282;
}

.add-button:disabled {
  background-color: #a0aec0;
  cursor: not-allowed;
}

.loading {
  color: #4a5568;
  font-size: 0.875rem;
}

.error-message {
  color: #e53e3e;
  font-size: 0.875rem;
}

.empty-state {
  color: #4a5568;
  font-size: 0.875rem;
  font-style: italic;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}
</style>
