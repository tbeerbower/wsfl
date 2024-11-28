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
            <p>League: {{ team.league?.name || 'No League' }}</p>
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
            <div class="matchup-teams">
              {{ matchup.team1?.name || 'Team 1' }} vs {{ matchup.team2?.name || 'Team 2' }}
            </div>
            <div class="matchup-score" v-if="matchup.score1 !== null && matchup.score2 !== null">
              {{ matchup.score1 }} - {{ matchup.score2 }}
            </div>
          </div>
        </div>
      </div>

      <div class="dashboard-section">
        <h2>League Standings</h2>
        <div v-if="loading.standings">Loading standings...</div>
        <div v-else-if="error.standings" class="error-message">{{ error.standings }}</div>
        <div v-else-if="standings.length === 0" class="empty-state">
          No standings available.
        </div>
        <div v-else class="standings-table">
          <table>
            <thead>
              <tr>
                <th>Team</th>
                <th>W</th>
                <th>L</th>
                <th>T</th>
                <th>Score</th>
                <th>League</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="team in standings" :key="team.id">
                <td>{{ team.name || 'Unknown Team' }}</td>
                <td>{{ team.wins || 0 }}</td>
                <td>{{ team.losses || 0 }}</td>
                <td>{{ team.ties || 0 }}</td>
                <td>{{ team.totalScore || 0 }}</td>
                <td>{{ team.leagueName || 'Unknown League' }}</td>
              </tr>
            </tbody>
          </table>
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
    const standings = ref([])
    
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

    const fetchTeams = async () => {
      if (!user.value?.id) return
      
      loading.value.teams = true
      error.value.teams = null
      try {
        const response = await axios.get(`/api/users/${user.value.id}/teams`)
        teams.value = response.data || []
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
        matchups.value = response.data || []
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
        // Get all unique league IDs from user's teams
        const leagueIds = [...new Set(teams.value
          .filter(team => team.league?.id)
          .map(team => team.league.id))]

        // Fetch standings for each league
        const allStandings = await Promise.all(
          leagueIds.map(async (leagueId) => {
            const response = await axios.get(`/api/leagues/${leagueId}/teams`)
            const leagueTeams = response.data || []
            // Add league info to each team
            const league = teams.value.find(t => t.league?.id === leagueId)?.league
            return leagueTeams.map(team => ({
              ...team,
              leagueName: league?.name || 'Unknown League'
            }))
          })
        )

        // Flatten and sort all standings
        standings.value = allStandings
          .flat()
          .sort((a, b) => {
            // Sort by win percentage first
            const aWinPct = (a.wins || 0) / ((a.wins || 0) + (a.losses || 0) + (a.ties || 0) || 1)
            const bWinPct = (b.wins || 0) / ((b.wins || 0) + (b.losses || 0) + (b.ties || 0) || 1)
            if (aWinPct !== bWinPct) return bWinPct - aWinPct
            // Use total score as tiebreaker
            return (a.totalScore || 0) - (b.totalScore || 0)
          })
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
      standings,
      loading,
      error,
      handleLogout
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
  padding: 15px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.matchups-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
  margin-top: 20px;
}

.matchup-card {
  padding: 15px;
  border: 1px solid #ddd;
  border-radius: 4px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.standings-table {
  margin-top: 20px;
  overflow-x: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th, td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #ddd;
}

th {
  background-color: #f5f5f5;
  font-weight: bold;
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
  margin: 0 0 10px 0;
  font-size: 18px;
}
</style>
