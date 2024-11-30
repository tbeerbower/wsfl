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
          No drafts found
        </div>
        <div class="drafts-grid">
          <router-link
            v-for="draft in myDrafts"
            :key="draft.id"
            :to="isMyTeamOnClock(draft) ? `/drafts/${draft.id}/pick` : `/drafts/${draft.id}`"
            class="draft-card"
            :class="{
              'my-team-turn': isMyTeamOnClock(draft),
              'draft-complete': getDraftStatus(draft) === 'COMPLETE',
              'draft-pending': getDraftStatus(draft) === 'PENDING'
            }"
          >
            <div class="draft-info">
              <h3>{{ draft.name }}</h3>
              <div class="draft-league">{{ draft.league.name }}</div>
              <div class="draft-status-badge" :class="getDraftStatus(draft).toLowerCase()">
                {{ getDraftStatus(draft) }}
              </div>
            </div>
            <div class="draft-status">
              <div class="status-item">
                <span class="label">Round</span>
                <span class="value">{{ draft.currentRound || '-' }}</span>
              </div>
              <div class="status-item">
                <span class="label">Pick</span>
                <span class="value">{{ draft.currentPick || '-' }}</span>
              </div>
              <div class="status-item">
                <span class="label">Time</span>
                <span class="value">{{ getDraftStatus(draft) === 'COMPLETE' ? 'Complete' : getElapsedTime(draft) }}</span>
              </div>
            </div>
            <div v-if="getDraftStatus(draft) === 'IN_PROGRESS'" class="on-clock-status" :class="{ 'my-team': isMyTeamOnClock(draft) }">
              <span class="on-clock-label">On The Clock</span>
              <span class="on-clock-team">
                {{ draftTeams[getCurrentPickTeam(draft)]?.name || 'Loading...' }}
                <span v-if="isMyTeamOnClock(draft)" class="my-team-badge">Your Turn!</span>
              </span>
            </div>
            <div v-if="isMyTeamOnClock(draft) && getDraftStatus(draft) === 'IN_PROGRESS'" class="make-pick-prompt">
              Click to Make Your Pick
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
    const draftTeams = ref({}) // Cache for teams from drafts
    
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

    const getDraftStatus = (draft) => {
      if (draft.complete) return 'COMPLETE'
      if (draft.started) return 'IN_PROGRESS'
      return 'PENDING'
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
        console.log('Admin leagues response:', response.data)
        
        // Fetch teams for each admin league
        const leaguesWithTeams = await Promise.all(
          response.data.content.map(async (league) => {
            try {
              const teamsResponse = await axios.get(`/api/leagues/${league.id}/teams`)
              console.log(`Teams for league ${league.id}:`, teamsResponse.data)
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
      if (!user || !teams.value.length) return
      
      loading.value.drafts = true
      error.value.drafts = null
      try {
        const response = await axios.get('/api/drafts')
        const userTeamIds = teams.value.map(team => team.id)
        
        // Filter drafts where user has a team OR is league admin
        myDrafts.value = response.data.content.filter(draft => 
          draft.draftOrder.some(teamId => userTeamIds.includes(teamId)) || 
          adminLeagues.value.some(league => league.id === draft.league.id)
        )
        
        // Cache team names for each draft
        for (const draft of myDrafts.value) {
          if (draft.draftOrder) {
            for (const teamId of draft.draftOrder) {
              if (!draftTeams.value[teamId]) {
                try {
                  const teamResponse = await axios.get(`/api/teams/${teamId}`)
                  draftTeams.value[teamId] = teamResponse.data
                } catch (err) {
                  console.error(`Error fetching team ${teamId}:`, err)
                }
              }
            }
          }
        }
      } catch (err) {
        error.value.drafts = 'Failed to load drafts'
        console.error(err)
      } finally {
        loading.value.drafts = false
      }
    }

    onMounted(async () => {
      // First fetch teams and admin leagues
      await Promise.all([
        fetchTeams(),
        fetchMatchups(),
        fetchAdminLeagues(),
        fetchStandings()
      ])
      // Then fetch drafts after we have both teams and admin leagues loaded
      await fetchDrafts()
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
      draftTeams,
      sortedTeams,
      calculateWinPercentage,
      isUserTeam,
      getCurrentPickTeam,
      isMyTeamOnClock,
      getTeamName,
      getMyTeamOnClock,
      getElapsedTime,
      getDraftStatus
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
</style>
