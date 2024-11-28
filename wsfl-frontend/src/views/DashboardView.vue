<template>
  <div class="dashboard-view">
    <header class="dashboard-header">
      <h1>Welcome, {{ user?.name || 'User' }}</h1>
      <button @click="handleLogout" class="logout-button">Logout</button>
    </header>

    <div class="dashboard-content">
      <div class="dashboard-section">
        <h2>My Teams</h2>
        <div v-if="loading.teams">Loading teams...</div>
        <div v-else-if="error.teams" class="error-message">{{ error.teams }}</div>
        <div v-else-if="teams.length === 0" class="empty-state">
          You haven't created any teams yet.
        </div>
        <div v-else class="teams-grid">
          <div v-for="team in teams" :key="team.id" class="team-card">
            <h3>{{ team.name }}</h3>
            <div class="team-stats">
              <div class="stat-row">
                <span class="stat-label">Record:</span>
                <span class="stat-value">{{ team.wins || 0 }}W - {{ team.losses || 0 }}L - {{ team.ties || 0 }}T</span>
              </div>
              <div class="stat-row">
                <span class="stat-label">Total Score:</span>
                <span class="stat-value">{{ team.totalScore || 0 }}</span>
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
import { ref, onMounted, computed } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
import axios from 'axios'

export default {
  name: 'DashboardView',
  setup() {
    const store = useStore()
    const router = useRouter()
    
    const teams = ref([])
    const matchups = ref([])
    const leagueStandings = ref({})
    
    const loading = ref({
      teams: false,
      matchups: false,
      standings: false
    })
    
    const error = ref({
      teams: null,
      matchups: null,
      standings: null
    })

    const user = computed(() => store.getters['auth/currentUser'])

    const isUserTeam = (teamId) => {
      return teams.value.some(team => team.id === teamId)
    }

    const calculateWinPercentage = (team) => {
      const totalGames = team.wins + team.losses + team.ties
      if (totalGames === 0) return '.000'
      const percentage = (team.wins + (team.ties * 0.5)) / totalGames
      return percentage.toFixed(3).substring(1) // Remove leading zero
    }

    const fetchTeams = async () => {
      if (!user.value?.id) return
      
      loading.value.teams = true
      error.value.teams = null
      try {
        const response = await axios.get(`/api/users/${user.value.id}/teams`)
        teams.value = response.data.content || [] // Extract teams from paginated response
      } catch (err) {
        error.value.teams = 'Failed to load teams'
        console.error(err)
      } finally {
        loading.value.teams = false
      }
    }

    const fetchMatchups = async () => {
      if (!user.value?.id) return
      
      loading.value.matchups = true
      error.value.matchups = null
      try {
        const response = await axios.get('/api/matchups', {
          params: { userId: user.value.id }
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
      if (!user.value?.id) return
      
      loading.value.standings = true
      error.value.standings = null
      try {
        // Get user's teams to find league IDs
        const userTeamsResponse = await axios.get(`/api/users/${user.value.id}/teams`)
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

    const handleLogout = () => {
      store.dispatch('auth/logout')
      router.push('/login')
    }

    onMounted(() => {
      if (user.value?.id) {
        fetchTeams().then(() => {
          fetchMatchups()
          fetchStandings()
        })
      }
    })

    return {
      user,
      teams,
      matchups,
      leagueStandings,
      loading,
      error,
      handleLogout,
      isUserTeam,
      calculateWinPercentage
    }
  }
}
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

.logout-button {
  padding: 8px 16px;
  background-color: #f44336;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
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

.team-stats {
  margin-top: 15px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.stat-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 5px 0;
  border-bottom: 1px solid #eee;
}

.stat-row:last-child {
  border-bottom: none;
}

.stat-label {
  color: #666;
  font-weight: 500;
}

.stat-value {
  font-weight: 600;
  color: #333;
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
</style>
