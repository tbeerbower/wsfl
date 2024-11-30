<template>
  <div class="dashboard-view">
    <header class="dashboard-header">
      <h1>Dashboard</h1>
    </header>

    <div class="dashboard-content">
      <div class="dashboard-section">
        <h2>My Drafts</h2>
        <div v-if="loading.drafts">Loading drafts...</div>
        <div v-else-if="error.drafts" class="error-message">{{ error.drafts }}</div>
        <div v-else-if="myDrafts.length === 0" class="empty-state">
          No active drafts found
        </div>
        <div v-else class="drafts-grid">
          <router-link
            v-for="draft in myDrafts"
            :key="draft.id"
            :to="isMyTeamOnClock(draft) ? `/drafts/${draft.id}/pick` : `/drafts/${draft.id}`"
            class="draft-card"
            :class="{ 'my-team-up': isMyTeamOnClock(draft) }"
          >
            <div class="draft-header">
              <h3>{{ draft.name }}</h3>
              <div class="draft-league">{{ draft.league.name }}</div>
            </div>
            <div class="draft-status">
              <div class="status-item">
                <span class="status-label">Round</span>
                <span class="status-value">{{ draft.currentRound }}/{{ draft.numberOfRounds }}</span>
              </div>
              <div class="status-item">
                <span class="status-label">Pick</span>
                <span class="status-value">{{ draft.currentPick }}</span>
              </div>
            </div>
            <div class="on-clock-info" :class="{ 'my-turn': isMyTeamOnClock(draft) }">
              <template v-if="isMyTeamOnClock(draft)">
                <div class="your-turn-banner">Your Turn to Pick!</div>
                <div class="team-name">{{ getMyTeamOnClock(draft).name }}</div>
                <div class="elapsed-time">
                  Time Elapsed: {{ getElapsedTime(draft) }}
                </div>
              </template>
              <template v-else>
                <span class="on-clock-label">On The Clock</span>
                <span class="on-clock-team">{{ getTeamName(getCurrentPickTeam(draft)) }}</span>
              </template>
            </div>
          </router-link>
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
          <div v-for="league in adminLeagues" :key="league.id" class="league-card" @click="$router.push(`/leagues/${league.id}`)">
            <div class="league-header">
              <h3>{{ league.name }}</h3>
            </div>
            <div class="league-stats">
              <div class="stat-item">
                <span class="stat-label">Teams</span>
                <span class="stat-value">{{ league.teams.length }}</span>
              </div>
            </div>
            <div class="teams-list">
              <h4>Teams</h4>
              <div class="team-standings">
                <div v-for="team in sortedTeams(league.teams)" :key="team.id" class="team-row">
                  <span class="team-name">{{ team.name }}</span>
                  <span class="team-record">{{ team.wins }}-{{ team.losses }}-{{ team.ties }}</span>
                  <span class="team-score">{{ team.totalScore }}</span>
                </div>
              </div>
            </div>
          </div>
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

      <div class="dashboard-section">
        <h2>Current Matchups</h2>
        <div v-if="loading.matchups">Loading matchups...</div>
        <div v-else-if="error.matchups" class="error-message">{{ error.matchups }}</div>
        <div v-else-if="matchups.length === 0" class="empty-state">
          No current matchups.
        </div>
        <div v-else class="matchups-list">
          <div v-for="matchup in matchups" :key="matchup.id" class="matchup-card">
            <div class="matchup-header">
              <span class="race-label">Race #{{ matchup.raceId }}</span>
            </div>
            <div class="matchup-teams">
              <div class="team-column" :class="{ 'winner': matchup.team1Score < matchup.team2Score }">
                <h4>{{ matchup.team1?.name }}</h4>
                <div class="team-record">
                  {{ matchup.team1?.wins || 0 }}W - {{ matchup.team1?.losses || 0 }}L - {{ matchup.team1?.ties || 0 }}T
                </div>
                <div class="team-score" v-if="matchup.team1Score !== null">
                  Score: {{ matchup.team1Score }}
                </div>
              </div>
              <div class="vs-badge">VS</div>
              <div class="team-column" :class="{ 'winner': matchup.team2Score < matchup.team1Score }">
                <h4>{{ matchup.team2?.name }}</h4>
                <div class="team-record">
                  {{ matchup.team2?.wins || 0 }}W - {{ matchup.team2?.losses || 0 }}L - {{ matchup.team2?.ties || 0 }}T
                </div>
                <div class="team-score" v-if="matchup.team2Score !== null">
                  Score: {{ matchup.team2Score }}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="dashboard-section">
        <h2>League Standings</h2>
        <div v-if="loading.standings">Loading standings...</div>
        <div v-else-if="error.standings" class="error-message">{{ error.standings }}</div>
        <div v-else-if="Object.keys(leagueStandings).length === 0" class="empty-state">
          No standings available.
        </div>
        <div v-else class="leagues-container">
          <div v-for="(league, leagueId) in leagueStandings" :key="leagueId" class="league-section">
            <h3>{{ league.name }}</h3>
            <div class="standings-table">
              <table>
                <thead>
                  <tr>
                    <th class="text-left">Team</th>
                    <th class="text-center">W</th>
                    <th class="text-center">L</th>
                    <th class="text-center">T</th>
                    <th class="text-center">PCT</th>
                    <th class="text-right">Score</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="team in league.teams" :key="team.id" :class="{ 'user-team': isUserTeam(team.id) }">
                    <td class="text-left">{{ team.name }}</td>
                    <td class="text-center">{{ team.wins }}</td>
                    <td class="text-center">{{ team.losses }}</td>
                    <td class="text-center">{{ team.ties }}</td>
                    <td class="text-center">{{ calculateWinPercentage(team) }}</td>
                    <td class="text-right">{{ team.totalScore }}</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, defineComponent } from 'vue'
import { useStore } from 'vuex'
import axios from 'axios'

export default defineComponent({
  name: 'DashboardView',
  setup() {
    const store = useStore()
    const user = store.getters['auth/currentUser']

    const teams = ref([])
    const matchups = ref([])
    const adminLeagues = ref([])
    const leagueStandings = ref({})
    const myDrafts = ref([])
    
    const loading = ref({
      teams: false,
      matchups: false,
      standings: false,
      adminLeagues: false,
      drafts: false
    })
    
    const error = ref({
      teams: null,
      matchups: null,
      standings: null,
      adminLeagues: null,
      drafts: null
    })

    const isUserTeam = (teamId) => {
      return teams.value.some(team => team.id === teamId)
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

    const isMyTeamOnClock = (draft) => {
      const currentTeamId = getCurrentPickTeam(draft)
      return teams.value.some(team => team.id === currentTeamId)
    }

    const getTeamName = (teamId) => {
      // First check the user's teams
      const userTeam = teams.value.find(t => t.id === teamId)
      if (userTeam) return userTeam.name

      // Then check teams in admin leagues
      for (const league of adminLeagues.value) {
        const team = league.teams.find(t => t.id === teamId)
        if (team) return team.name
      }
      
      return 'Unknown Team'
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

    const fetchMatchups = async () => {
      if (!user) return
      
      loading.value.matchups = true
      error.value.matchups = null
      try {
        const response = await axios.get('/api/matchups', {
          params: { userId: user.id }
        })
        matchups.value = response.data.content || []
      } catch (err) {
        error.value.matchups = 'Failed to load matchups'
        console.error(err)
      } finally {
        loading.value.matchups = false
      }
    }

    const fetchStandings = async () => {
      if (!user) return
      
      loading.value.standings = true
      error.value.standings = null
      try {
        // Get user's teams to find league IDs
        const userTeamsResponse = await axios.get(`/api/users/${user.id}/teams`)
        const userTeams = userTeamsResponse.data.content || []
        
        // Get unique leagues with their names
        const leagues = userTeams.reduce((acc, team) => {
          if (team.league && !acc[team.league.id]) {
            acc[team.league.id] = {
              id: team.league.id,
              name: team.league.name,
              teams: []
            }
          }
          return acc
        }, {})

        // Fetch standings for each league
        await Promise.all(
          Object.values(leagues).map(async (league) => {
            const response = await axios.get(`/api/leagues/${league.id}/teams`, {
              params: { standings: true }
            })
            league.teams = response.data.content || []
          })
        )

        leagueStandings.value = leagues
      } catch (err) {
        error.value.standings = 'Failed to load standings'
        console.error(err)
      } finally {
        loading.value.standings = false
      }
    }

    const fetchAdminLeagues = async () => {
      if (!user) return
      
      loading.value.adminLeagues = true
      error.value.adminLeagues = null
      try {
        const response = await axios.get(`/api/users/${user.id}/leagues`)
        adminLeagues.value = response.data.content || []
      } catch (err) {
        error.value.adminLeagues = 'Failed to load leagues'
        console.error('Error fetching admin leagues:', err)
      } finally {
        loading.value.adminLeagues = false
      }
    }

    const fetchMyDrafts = async () => {
      loading.value.drafts = true
      error.value.drafts = null
      try {
        const response = await axios.get('/api/drafts')
        // Filter for drafts that are in progress and include user's teams
        myDrafts.value = response.data.content
          .filter(draft => 
            draft.started && !draft.complete && 
            draft.draftOrder.some(teamId => teams.value.some(team => team.id === teamId))
          )
      } catch (err) {
        error.value.drafts = 'Failed to load drafts'
      } finally {
        loading.value.drafts = false
      }
    }

    onMounted(async () => {
      await Promise.all([
        fetchTeams(),
        fetchMatchups(),
        fetchAdminLeagues(),
        fetchStandings(),
        fetchMyDrafts()
      ])
    })

    return {
      user,
      teams,
      matchups,
      adminLeagues,
      leagueStandings,
      loading,
      error,
      myDrafts,
      sortedTeams,
      calculateWinPercentage,
      isUserTeam,
      getCurrentPickTeam,
      isMyTeamOnClock,
      getTeamName,
      getMyTeamOnClock,
      getElapsedTime
    }
  }
})
</script>

<style scoped>
.dashboard-view {
  padding: 20px;
  max-width: 1200px;
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

.matchup-card {
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
  background-color: white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.matchup-header {
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.race-label {
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

.matchup-teams {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
}

.team-column {
  flex: 1;
  text-align: center;
  padding: 15px;
  border-radius: 6px;
  background-color: #f8f9fa;
  transition: background-color 0.2s;
}

.team-column.winner {
  background-color: #e3f2fd;
  border: 1px solid #90caf9;
}

.vs-badge {
  font-weight: bold;
  color: #666;
  padding: 8px;
  background-color: #f5f5f5;
  border-radius: 50%;
  min-width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
}

h4 {
  margin: 0 0 10px 0;
  font-size: 16px;
  color: #2196F3;
}

.team-record {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

.team-score {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.matchups-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.leagues-container {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.league-section {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  padding: 1.5rem;
}

.league-section h3 {
  margin: 0 0 1rem 0;
  color: #2c3e50;
  font-size: 1.2rem;
}

.standings-table {
  margin-top: 1rem;
  overflow-x: auto;
}

.standings-table table {
  width: 100%;
  border-collapse: collapse;
}

.standings-table th, .standings-table td {
  padding: 12px 16px;
  border-bottom: 1px solid #eee;
}

.standings-table th {
  background-color: #f8f9fa;
  font-weight: 600;
  color: #666;
  white-space: nowrap;
}

.standings-table .text-left {
  text-align: left;
}

.standings-table .text-center {
  text-align: center;
}

.standings-table .text-right {
  text-align: right;
}

.standings-table tbody tr {
  transition: background-color 0.2s;
}

.standings-table tbody tr:hover {
  background-color: #f8f9fa;
}

.standings-table .user-team {
  background-color: #e3f2fd;
}

.standings-table .user-team:hover {
  background-color: #bbdefb;
}

.standings-table tbody tr:last-child td {
  border-bottom: none;
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

.leagues-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1.5rem;
}

.league-card {
  background-color: white;
  border-radius: 8px;
  padding: 1.5rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.league-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
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
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  padding: 1.5rem;
  text-decoration: none;
  color: inherit;
  transition: all 0.2s;
}

.draft-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 6px -1px rgb(0 0 0 / 0.1);
}

.draft-card.my-team-up {
  border: 2px solid #2563eb;
  background-color: #eff6ff;
}

.draft-header {
  margin-bottom: 1rem;
}

.draft-header h3 {
  margin: 0;
  font-size: 1.25rem;
  color: #1e293b;
  margin-bottom: 0.5rem;
}

.draft-league {
  font-size: 0.875rem;
  color: #64748b;
}

.draft-status {
  display: flex;
  gap: 2rem;
  margin-bottom: 1rem;
  padding: 1rem;
  background-color: #f8fafc;
  border-radius: 6px;
}

.status-item {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.status-label {
  font-size: 0.75rem;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  color: #64748b;
}

.status-value {
  font-size: 1.125rem;
  font-weight: 600;
  color: #1e293b;
}

.on-clock-info {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.on-clock-info.my-turn {
  background-color: #eff6ff;
  padding: 1rem;
  border-radius: 6px;
  border: 1px solid #2563eb;
}

.your-turn-banner {
  background-color: #2563eb;
  color: white;
  padding: 0.5rem;
  border-radius: 4px;
  font-weight: 600;
  text-align: center;
  margin-bottom: 0.5rem;
}

.on-clock-info.my-turn .team-name {
  font-size: 1.125rem;
  font-weight: 600;
  color: #1e40af;
  text-align: center;
  margin-bottom: 0.5rem;
}

.elapsed-time {
  font-family: monospace;
  font-size: 0.875rem;
  color: #64748b;
  text-align: center;
}
</style>
