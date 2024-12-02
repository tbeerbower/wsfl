<template>
  <div class="dashboard-view">
    <header class="dashboard-header">
      <h1>Dashboard</h1>
    </header>

    <div class="dashboard-content">
      <div class="dashboard-section">
        <div class="section-header">
          <h2>In Progress Drafts</h2>
          <router-link to="/drafts" class="view-all-button">
            View All Drafts
          </router-link>
        </div>
        <div v-if="loading.drafts">Loading drafts...</div>
        <div v-else-if="error.drafts" class="error-message">{{ error.drafts }}</div>
        <div v-else-if="inProgressDrafts.length === 0" class="empty-state">
          No drafts in progress
        </div>
        <div v-else class="drafts-grid">
          <DraftCard
            v-for="draft in inProgressDrafts"
            :key="draft.id"
            :draft="{ ...draft, teams: draftTeams[draft.id] }"
            :is-my-team-on-clock="isMyTeamOnClock(draft)"
            @mounted="() => console.log(`DraftCard mounted for draft ${draft.id}`, {
              teams: draftTeams[draft.id],
              draft: draft
            })"
          />
        </div>
      </div>

      <div v-if="adminLeagues.length > 0" class="dashboard-section">
        <div class="section-header">
          <h2>My Leagues (Admin)</h2>
          <router-link to="/add-league" class="add-league-button">
            Add League
          </router-link>
        </div>
        <div v-if="loading.adminLeagues">Loading leagues...</div>
        <div v-else-if="error.adminLeagues" class="error-message">{{ error.adminLeagues }}</div>
        <div class="leagues-grid">
          <router-link
            v-for="league in adminLeagues"
            :key="league.id"
            :to="`/leagues/${league.id}`"
            class="league-card"
          >
            <h3>{{ league.name }}</h3>
            <div class="league-stats">
              <div class="stat-item">
                <span class="stat-label">Teams</span>
                <span class="stat-value">{{ league.teams?.length || 0 }}</span>
              </div>
            </div>
          </router-link>
        </div>
      </div>

      <div class="dashboard-section">
        <div class="section-header">
          <h2>My Teams</h2>
          <router-link to="/add-team" class="add-team-button">
            Add Team
          </router-link>
        </div>
        <div v-if="loading.teams">Loading teams...</div>
        <div v-else-if="error.teams" class="error-message">{{ error.teams }}</div>
        <div v-else-if="teams.length === 0" class="empty-state">
          You don't have any teams yet.
        </div>
        <div v-else class="teams-grid">
          <div v-for="team in teams" :key="team.id" class="team-card">
            <div class="team-header">
              <h3>{{ team.name }}</h3>
              <div class="league-badge">{{ team.league?.name || 'No League' }}</div>
            </div>
            <div class="team-stats">
              <div class="stat-item">
                <span class="stat-label">Record</span>
                <span class="stat-value">{{ team.wins }}-{{ team.losses }}-{{ team.ties }}</span>
              </div>
              <div class="stat-item">
                <span class="stat-label">Win %</span>
                <span class="stat-value">{{ calculateWinPercentage(team) }}%</span>
              </div>
              <div class="stat-item">
                <span class="stat-label">Score</span>
                <span class="stat-value">{{ team.totalScore }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, defineComponent, computed, watch } from 'vue'
import { useStore } from 'vuex'
import axios from 'axios'
import DraftCard from '@/components/DraftCard.vue'

export default defineComponent({
  name: 'DashboardView',
  components: {
    DraftCard
  },
  setup() {
    const store = useStore()
    const user = store.getters['auth/currentUser']

    const teams = ref([])
    const adminLeagues = ref([])
    const myDrafts = ref([])
    const draftTeams = ref({})
    
    const loading = ref({
      teams: false,
      adminLeagues: false,
      drafts: false
    })
    
    const error = ref({
      teams: null,
      adminLeagues: null,
      drafts: null
    })

    const getDraftStatus = (draft) => {
      if (draft.complete) return 'COMPLETE'
      if (draft.started) return 'IN_PROGRESS'
      return 'PENDING'
    }

    const debugDraftTeams = computed(() => {
      console.log('Current draftTeams state:', JSON.stringify(draftTeams.value, null, 2))
      return draftTeams.value
    })

    const getCurrentPickTeam = (draft) => {
      if (!draft?.draftOrder || !draft?.currentPick) return null
      
      const pickNumber = draft.currentPick
      const draftOrder = draft.draftOrder
      const roundNumber = draft.currentRound
      
      const isReverseOrder = draft.snakeOrder && roundNumber % 2 === 0
      const orderIndex = isReverseOrder
        ? draftOrder.length - ((pickNumber - 1) % draftOrder.length) - 1
        : (pickNumber - 1) % draftOrder.length
      
      return draftOrder[orderIndex]
    }

    const inProgressDrafts = computed(() => {
      console.log('Computing inProgressDrafts')
      console.log('All drafts:', JSON.stringify(myDrafts.value, null, 2))
      const filtered = myDrafts.value.filter(draft => {
        const status = getDraftStatus(draft)
        console.log(`Draft ${draft.id} status:`, status)
        return status === 'IN_PROGRESS'
      }).map(draft => ({
        ...draft,
        currentTeamId: getCurrentPickTeam(draft)
      }))
      console.log('Filtered in-progress drafts:', JSON.stringify(filtered, null, 2))
      return filtered
    })

    watch(draftTeams, (newVal) => {
      console.log('draftTeams changed:', JSON.stringify(newVal, null, 2))
    }, { deep: true })

    const isMyTeamOnClock = (draft) => {
      if (!draft || !draft.currentTeamId) return false
      
      // Check if the current team belongs to the user
      console.log('Checking team on clock:', {
        draftId: draft.id,
        currentTeamId: draft.currentTeamId,
        draftTeams: draftTeams.value[draft.id],
        userId: user.id
      })
      
      const currentTeam = draftTeams.value[draft.id]?.find(team => team.id === draft.currentTeamId)
      console.log('Found current team:', currentTeam)
      return currentTeam?.owner?.id === user.id
    }

    const calculateWinPercentage = (team) => {
      const totalGames = team.wins + team.losses + team.ties
      if (totalGames === 0) return '.000'
      const percentage = (team.wins + (team.ties * 0.5)) / totalGames
      return percentage.toFixed(3).substring(1) // Remove leading zero
    }

    const sortedTeams = (teams) => {
      return [...teams].sort((a, b) => {
        // Sort by win percentage first
        const aWinPct = (a.wins + 0.5 * a.ties) / (a.wins + a.losses + a.ties || 1)
        const bWinPct = (b.wins + 0.5 * b.ties) / (b.wins + b.losses + b.ties || 1)
        if (aWinPct !== bWinPct) return bWinPct - aWinPct
        // Use total score as tiebreaker
        return b.totalScore - a.totalScore
      })
    }

    const getTeamName = async (teamId) => {
      console.log(`Looking up team name for ID: ${teamId}`)
      
      // First check the cache
      if (draftTeams.value[teamId]) {
        return draftTeams.value[teamId].name
      }

      // Then check user's teams
      const userTeam = teams.value.find(t => t.id === teamId)
      if (userTeam) {
        return userTeam.name
      }

      // Then check admin leagues
      for (const league of adminLeagues.value) {
        if (league.teams) {
          const team = league.teams.find(t => t.id === teamId)
          if (team) {
            return team.name
          }
        }
      }

      // If not found anywhere, fetch the team directly
      try {
        const response = await axios.get(`/api/teams/${teamId}`)
        const team = response.data
        // Cache the result
        draftTeams.value[teamId] = team
        return team.name
      } catch (err) {
        console.error(`Error fetching team ${teamId}:`, err)
        return 'Unknown Team'
      }
    }

    const getMyTeamOnClock = (draft) => {
      const currentTeamId = getCurrentPickTeam(draft)
      return teams.value.find(team => team.id === currentTeamId)
    }

    const getElapsedTime = (draft) => {
      if (!draft.startTime) return '00:00:00'
      
      const now = Date.now()
      const start = new Date(draft.startTime)
      const totalSeconds = Math.floor((now - start) / 1000)
      
      const days = Math.floor(totalSeconds / (24 * 3600))
      const hours = Math.floor((totalSeconds % (24 * 3600)) / 3600)
      const minutes = Math.floor((totalSeconds % 3600) / 60)
      const seconds = totalSeconds % 60

      const timeStr = `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`
      return days > 0 ? `${days} ${days === 1 ? 'Day' : 'Days'} ${timeStr}` : timeStr
    }

    const fetchTeams = async () => {
      if (!user) return
      
      loading.value.teams = true
      error.value.teams = null
      try {
        const response = await axios.get(`/api/users/${user.id}/teams`)
        teams.value = response.data.content || [] // Extract teams from paginated response
      } catch (err) {
        error.value.teams = 'Failed to load teams'
        console.error(err)
      } finally {
        loading.value.teams = false
      }
    }

    const fetchAdminLeagues = async () => {
      if (!user) return
      
      loading.value.adminLeagues = true
      error.value.adminLeagues = null
      try {
        const response = await axios.get(`/api/users/${user.id}/leagues`)
        
        // Fetch teams for each admin league
        const leaguesWithTeams = await Promise.all(
          response.data.content.map(async (league) => {
            try {
              const teamsResponse = await axios.get(`/api/leagues/${league.id}/teams`)
              return {
                ...league,
                teams: teamsResponse.data.content || []
              }
            } catch (err) {
              console.error(`Error fetching teams for league ${league.id}:`, err)
              return {
                ...league,
                teams: []
              }
            }
          })
        )
        
        adminLeagues.value = leaguesWithTeams
      } catch (err) {
        error.value.adminLeagues = 'Failed to load leagues'
        console.error('Error fetching admin leagues:', err)
      } finally {
        loading.value.adminLeagues = false
      }
    }

    const fetchDrafts = async () => {
      console.log('Starting fetchDrafts')
      if (!user || !teams.value.length) {
        console.log('Skipping fetchDrafts - no user or teams:', { user, teamsLength: teams.value.length })
        return
      }
      
      loading.value.drafts = true
      error.value.drafts = null
      try {
        console.log('Fetching drafts from API...')
        const response = await axios.get('/api/drafts')
        console.log('Drafts API response:', JSON.stringify(response.data, null, 2))
        
        const userTeamIds = teams.value.map(team => team.id)
        console.log('User team IDs:', userTeamIds)
        
        // Filter drafts where user has a team OR is league admin
        myDrafts.value = response.data.content.filter(draft => {
          const hasTeam = draft.draftOrder.some(teamId => userTeamIds.includes(teamId))
          const isAdmin = adminLeagues.value.some(league => league.id === draft.league.id)
          console.log(`Draft ${draft.id} - hasTeam: ${hasTeam}, isAdmin: ${isAdmin}`)
          return hasTeam || isAdmin
        })
        
        console.log('Filtered myDrafts:', JSON.stringify(myDrafts.value, null, 2))
        
        // Initialize draftTeams for each draft
        const newDraftTeams = {}
        
        // Cache team names for each draft
        for (const draft of myDrafts.value) {
          console.log(`Processing draft ${draft.id}`)
          if (draft.draftOrder) {
            const draftTeamsList = []
            console.log(`Fetching teams for draft ${draft.id}:`, draft.draftOrder)
            for (const teamId of draft.draftOrder) {
              try {
                console.log(`Fetching team ${teamId}...`)
                const teamResponse = await axios.get(`/api/teams/${teamId}`)
                console.log(`Team ${teamId} data:`, JSON.stringify(teamResponse.data, null, 2))
                draftTeamsList.push(teamResponse.data)
              } catch (err) {
                console.error(`Error fetching team ${teamId}:`, err)
              }
            }
            console.log(`Setting teams for draft ${draft.id}:`, JSON.stringify(draftTeamsList, null, 2))
            newDraftTeams[draft.id] = draftTeamsList
          }
        }
        
        console.log('Setting final draftTeams:', JSON.stringify(newDraftTeams, null, 2))
        draftTeams.value = newDraftTeams
        
      } catch (err) {
        error.value.drafts = 'Failed to load drafts'
        console.error('Error in fetchDrafts:', err)
      } finally {
        loading.value.drafts = false
      }
    }

    onMounted(async () => {
      // First fetch teams and admin leagues
      await Promise.all([
        fetchTeams(),
        fetchAdminLeagues()
      ])
      // Then fetch drafts after we have both teams and admin leagues loaded
      await fetchDrafts()
    })

    return {
      user,
      teams,
      adminLeagues,
      loading,
      error,
      myDrafts,
      draftTeams,
      sortedTeams,
      calculateWinPercentage,
      isMyTeamOnClock,
      getTeamName,
      getMyTeamOnClock,
      getElapsedTime,
      getDraftStatus,
      inProgressDrafts,
      debugDraftTeams
    }
  }
})
</script>

<style scoped>
.dashboard-view {
  padding: 2rem;
  max-width: 1400px;
  margin: 0 auto;
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 40px;
}

.dashboard-content {
  display: grid;
  gap: 30px;
}

.dashboard-section {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.teams-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.team-card {
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
  background-color: white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.team-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 1rem;
  gap: 1rem;
}

.team-header h3 {
  margin: 0;
  font-size: 1.2rem;
  color: #2c3e50;
  flex: 1;
}

.league-badge {
  padding: 0.25rem 0.75rem;
  background-color: #e3f2fd;
  color: #1976d2;
  border-radius: 1rem;
  font-size: 0.875rem;
  white-space: nowrap;
}

.team-stats {
  display: flex;
  gap: 1.5rem;
  margin-bottom: 1rem;
}

.stat-item {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.stat-label {
  font-size: 0.875rem;
  color: #666;
}

.stat-value {
  font-size: 1.125rem;
  font-weight: 600;
  color: #2c3e50;
}

.leagues-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1.5rem;
  margin-top: 1rem;
}

.league-card {
  background-color: white;
  border-radius: 8px;
  padding: 1.5rem;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  position: relative;
  overflow: hidden;
  min-height: 280px;
  transition: all 0.2s ease;
  border: 1px solid #E2E8F0;
}

.league-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -2px rgba(0, 0, 0, 0.06);
  border-color: #CBD5E1;
}

.league-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 1rem;
  gap: 1rem;
}

.league-header h3 {
  margin: 0;
  font-size: 1.2rem;
  color: #2c3e50;
  flex: 1;
}

.season-badge {
  padding: 0.25rem 0.75rem;
  background-color: #e3f2fd;
  color: #1976d2;
  border-radius: 1rem;
  font-size: 0.875rem;
  white-space: nowrap;
}

.league-stats {
  display: flex;
  gap: 1.5rem;
  margin-bottom: 1rem;
}

.teams-list {
  border-top: 1px solid #eee;
  padding-top: 1rem;
}

.teams-list h4 {
  margin: 0 0 0.5rem 0;
  font-size: 1rem;
  color: #2c3e50;
}

.team-standings {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.team-row {
  display: grid;
  grid-template-columns: 1fr auto auto;
  gap: 1rem;
  align-items: center;
  padding: 0.5rem;
  border-radius: 4px;
  transition: background-color 0.2s;
}

.team-row:hover {
  background-color: #f8f9fa;
}

.team-name {
  font-weight: 500;
  color: #2c3e50;
}

.team-record {
  color: #666;
  font-size: 0.875rem;
}

.team-score {
  color: #1976d2;
  font-weight: 600;
  font-size: 0.875rem;
}

.add-league-button {
  display: inline-flex;
  align-items: center;
  padding: 0.5rem 1rem;
  background-color: #3182ce;
  color: white;
  border: none;
  border-radius: 4px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s;
}

.add-league-button:hover {
  background-color: #2c5282;
}

.drafts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1.5rem;
  margin-top: 1rem;
}

.draft-card {
  background-color: white;
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
  text-decoration: none;
  color: inherit;
  position: relative;
  overflow: hidden;
  min-height: 280px;
  transition: all 0.2s ease;
  border: 1px solid #E2E8F0;
}

.draft-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -2px rgba(0, 0, 0, 0.06);
  border-color: #CBD5E1;
}

.draft-card:not(.draft-complete):not(.draft-pending):not(.my-team-turn) {
  background-color: white;
  border-color: #3B82F6;  /* Blue 500 */
  box-shadow: 0 1px 3px rgba(59, 130, 246, 0.1);
}

.draft-card:not(.draft-complete):not(.draft-pending):not(.my-team-turn):hover {
  box-shadow: 0 4px 6px -1px rgba(59, 130, 246, 0.1), 0 2px 4px -2px rgba(59, 130, 246, 0.06);
  border-color: #2563EB;  /* Blue 600 */
}

.draft-card:not(.draft-complete):not(.draft-pending):not(.my-team-turn) .draft-info h3 {
  color: #1E40AF;  /* Blue 800 */
}

.draft-card:not(.draft-complete):not(.draft-pending):not(.my-team-turn) .draft-status,
.draft-card:not(.draft-complete):not(.draft-pending):not(.my-team-turn) .on-clock-status {
  background-color: #EFF6FF;  /* Blue 50 */
  border-color: #BFDBFE;  /* Blue 200 */
}

.draft-card:not(.draft-complete):not(.draft-pending):not(.my-team-turn) .status-item .value {
  color: #1E40AF;  /* Blue 800 */
}

.draft-card:not(.draft-complete):not(.draft-pending):not(.my-team-turn) .on-clock-team {
  color: #1E40AF;  /* Blue 800 */
}

.draft-complete {
  background-color: #F8FAFC;
  border: 1px solid #E2E8F0;
}

.draft-complete .draft-status,
.draft-complete .on-clock-status {
  background-color: #F1F5F9;
  border-color: #E2E8F0;
}

.draft-complete .status-item .value {
  color: #64748B;
}

.draft-pending {
  background-color: white;
  border: 1px dashed #E2E8F0;
}

.draft-pending .draft-status,
.draft-pending .on-clock-status {
  background-color: #F8FAFC;
  border: 1px dashed #E2E8F0;
}

.draft-card.my-team-turn {
  background-color: #4F46E5;
  color: white;
  padding-bottom: 3.5rem;
  border: none;
}

.draft-card.my-team-turn:hover {
  background-color: #4338CA;
  transform: translateY(-2px);
  box-shadow: 0 4px 6px -1px rgba(79, 70, 229, 0.2);
}

.draft-card.my-team-turn h3,
.draft-card.my-team-turn .draft-league,
.draft-card.my-team-turn .status-item .label,
.draft-card.my-team-turn .status-item .value,
.draft-card.my-team-turn .on-clock-team {
  color: white;
}

.draft-card.my-team-turn .draft-status,
.draft-card.my-team-turn .on-clock-status {
  background-color: rgba(255, 255, 255, 0.1);
  border-color: rgba(255, 255, 255, 0.1);
}

.make-pick-prompt {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background-color: #22C55E;
  color: white;
  text-align: center;
  padding: 0.875rem;
  font-weight: 500;
  font-size: 0.875rem;
  transition: background-color 0.2s;
}

.make-pick-prompt:hover {
  background-color: #16A34A;
}

.my-team-badge {
  display: inline-block;
  font-size: 0.75rem;
  font-weight: 500;
  background-color: #22C55E;
  color: white;
  padding: 0.25rem 0.75rem;
  border-radius: 4px;
  margin-left: 0.5rem;
}

.draft-status-badge {
  display: inline-flex;
  align-items: center;
  padding: 0.375rem 0.75rem;
  border-radius: 9999px;
  font-size: 0.75rem;
  font-weight: 500;
  text-transform: capitalize;
  margin-top: 0.5rem;
  width: fit-content;
}

.draft-status-badge.complete {
  background-color: #F0FDF4;
  color: #16A34A;
  border: 1px solid #BBF7D0;
}

.draft-status-badge.in_progress {
  background-color: #EFF6FF;
  color: #2563EB;
  border: 1px solid #BFDBFE;
}

.draft-status-badge.pending {
  background-color: #FEF3C7;
  color: #D97706;
  border: 1px solid #FDE68A;
}

.draft-card.my-team-turn .draft-status-badge {
  background-color: rgba(255, 255, 255, 0.1);
  color: white;
  border-color: rgba(255, 255, 255, 0.2);
}

.draft-league {
  font-size: 0.875rem;
  color: #4B5563;
  margin-bottom: 0.25rem;
}

.draft-season {
  font-size: 0.875rem;
  color: #6B7280;
  margin-bottom: 0.5rem;
}

.add-team-button {
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

.add-team-button:hover {
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
  margin: 0 0 20px 0;
  font-size: 20px;
}

h3 {
  margin: 0;
  font-size: 18px;
  color: #2196F3;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.view-all-button {
  display: inline-flex;
  align-items: center;
  padding: 0.5rem 1rem;
  background-color: #E5E7EB;
  color: #374151;
  border: none;
  border-radius: 4px;
  text-decoration: none;
  font-weight: 500;
  transition: background-color 0.2s;
}

.view-all-button:hover {
  background-color: #D1D5DB;
}
</style>
