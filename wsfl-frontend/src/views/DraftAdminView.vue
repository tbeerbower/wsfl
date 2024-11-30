<template>
  <div class="draft-admin-view">
    <div v-if="loading.draft" class="loading">Loading draft details...</div>
    <div v-else-if="error.draft" class="error-message">{{ error.draft }}</div>
    <div v-else-if="draft" class="draft-content">
      <header class="draft-header">
        <h1>{{ draft.name }}</h1>
        <div class="draft-actions">
          <button 
            v-if="!draft.started && !draft.complete"
            @click="startDraft"
            class="start-button"
            :disabled="loading.startDraft"
          >
            {{ loading.startDraft ? 'Starting...' : 'Start Draft' }}
          </button>
          <div class="draft-status" :class="getDraftStatusClass(draft)">
            {{ getDraftStatus(draft) }}
          </div>
        </div>
      </header>

      <section class="draft-details">
        <h2>Draft Details</h2>
        <div class="details-grid">
          <div class="detail-item">
            <span class="detail-label">Season</span>
            <span class="detail-value">{{ draft.season }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">League</span>
            <span class="detail-value">{{ draft.league.name }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">Draft Type</span>
            <span class="detail-value">{{ draft.snakeOrder ? 'Snake' : 'Standard' }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">Number of Rounds</span>
            <span class="detail-value">{{ draft.numberOfRounds }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">Start Time</span>
            <span class="detail-value">{{ new Date(draft.startTime).toLocaleString() }}</span>
          </div>
          <div v-if="!draft.complete && draft.draftOrder?.length > 0" class="detail-item">
            <span class="detail-label">Current Round</span>
            <span class="detail-value">{{ draft.currentRound }}/{{ draft.numberOfRounds }}</span>
          </div>
          <div v-if="!draft.complete && draft.draftOrder?.length > 0" class="detail-item">
            <span class="detail-label">Current Pick</span>
            <span class="detail-value">{{ draft.currentPick }}</span>
          </div>
        </div>
      </section>

      <section v-if="draft.draftOrder?.length > 0" class="draft-order-section">
        <h2>Draft Order</h2>
        <div v-if="loading.teams" class="loading">Loading teams...</div>
        <div v-else-if="error.teams" class="error-message">{{ error.teams }}</div>
        <div v-else>
          <table class="draft-order-table">
            <thead>
              <tr>
                <th>Position</th>
                <th>Team</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(teamId, index) in draft.draftOrder" :key="index" class="draft-order-row">
                <td class="order-number">{{ index + 1 }}</td>
                <td class="team-name">{{ getTeamName(teamId) }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>

      <section v-if="draftPicks.length > 0" class="draft-picks-section">
        <h2>Draft Picks</h2>
        <div v-if="loading.picks" class="loading">Loading draft picks...</div>
        <div v-else-if="error.picks" class="error-message">{{ error.picks }}</div>
        <div v-else>
          <div v-for="round in groupedPicks" :key="round.number" class="round-section">
            <h3 class="round-header">Round {{ round.number }}</h3>
            <table class="draft-picks-table">
              <thead>
                <tr>
                  <th>Pick</th>
                  <th>Team</th>
                  <th>Runner</th>
                  <th>Gender</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="pick in round.picks" :key="pick.id" class="draft-pick-row">
                  <td class="pick-number">{{ pick.pickNumber }}</td>
                  <td class="team-name">{{ pick.team.name }}</td>
                  <td class="runner-name">{{ pick.runner.name }}</td>
                  <td>
                    <span :class="['gender-indicator', pick.runner.gender === 'M' ? 'male' : 'female']">
                      {{ pick.runner.gender === 'M' ? '♂' : '♀' }}
                    </span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          <div v-if="loading.morePicks" class="loading">Loading more picks...</div>
          <button 
            v-if="hasMorePicks && !loading.morePicks" 
            @click="loadMorePicks" 
            class="load-more-button"
          >
            Load More Picks
          </button>
        </div>
      </section>

      <div v-if="draft.started && !draft.complete" class="draft-progress">
        <div class="on-clock">
          <h3>On The Clock</h3>
          <div class="team-info">
            <span class="team-name">{{ currentTeamOnClock }}</span>
          </div>
          <div class="current-status">
            <div class="pick-info">
              <div class="info-item">
                <span class="info-label">Round</span>
                <span class="info-value">{{ draft.currentRound }}/{{ draft.numberOfRounds }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">Pick</span>
                <span class="info-value">{{ draft.currentPick }}</span>
              </div>
            </div>
            <div class="elapsed-time">
              <span class="info-label">Time Elapsed</span>
              <span class="timer">{{ elapsedTimeDisplay }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { defineComponent, ref, onMounted, computed, onUnmounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'

export default defineComponent({
  name: 'DraftAdminView',

  setup() {
    const route = useRoute()
    const router = useRouter()
    const draft = ref(null)
    const draftPicks = ref([])
    const teams = ref([])
    const currentPage = ref(0)
    const totalPages = ref(1)
    const loading = ref({
      draft: false,
      picks: false,
      morePicks: false,
      teams: false,
      startDraft: false
    })
    const error = ref({
      draft: null,
      picks: null,
      teams: null,
      startDraft: null
    })

    const hasMorePicks = computed(() => currentPage.value < totalPages.value - 1)

    const getTeamName = (teamId) => {
      const team = teams.value.find(t => t.id === teamId)
      return team ? team.name : `Team ${teamId}`
    }

    const calculateRound = (pickNumber) => {
      return Math.floor((pickNumber - 1) / (draft.value?.draftOrder?.length || 4)) + 1
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

    const groupedPicks = computed(() => {
      const rounds = new Map()
      
      draftPicks.value.forEach(pick => {
        const roundNumber = calculateRound(pick.pickNumber)
        if (!rounds.has(roundNumber)) {
          rounds.set(roundNumber, {
            number: roundNumber,
            picks: []
          })
        }
        rounds.get(roundNumber).picks.push(pick)
      })

      return Array.from(rounds.values()).sort((a, b) => a.number - b.number)
    })

    const elapsedTime = ref(0)
    const timerInterval = ref(null)

    const currentTeamOnClock = computed(() => {
      if (!draft.value?.draftOrder || !draft.value?.currentPick) return ''
      
      const pickNumber = draft.value.currentPick
      const draftOrder = draft.value.draftOrder
      const roundNumber = draft.value.currentRound
      
      const isReverseOrder = draft.value.snakeOrder && roundNumber % 2 === 0
      const orderIndex = isReverseOrder
        ? draftOrder.length - ((pickNumber - 1) % draftOrder.length) - 1
        : (pickNumber - 1) % draftOrder.length
      
      const teamId = draftOrder[orderIndex]
      return getTeamName(teamId)
    })

    const elapsedTimeDisplay = computed(() => {
      const totalSeconds = elapsedTime.value
      const days = Math.floor(totalSeconds / (24 * 3600))
      const hours = Math.floor((totalSeconds % (24 * 3600)) / 3600)
      const minutes = Math.floor((totalSeconds % 3600) / 60)
      const seconds = totalSeconds % 60

      const timeStr = `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`
      return days > 0 ? `${days} ${days === 1 ? 'Day' : 'Days'} ${timeStr}` : timeStr
    })

    const startTimer = () => {
      if (timerInterval.value) clearInterval(timerInterval.value)
      
      if (draft.value?.currentPick === 1) {
        elapsedTime.value = Math.floor((Date.now() - new Date(draft.value.startTime)) / 1000)
      }
      
      timerInterval.value = setInterval(() => {
        elapsedTime.value++
      }, 1000)
    }

    const stopTimer = () => {
      if (timerInterval.value) {
        clearInterval(timerInterval.value)
        timerInterval.value = null
      }
    }

    const fetchDraft = async () => {
      loading.value.draft = true
      error.value.draft = null
      try {
        const response = await axios.get(`/api/drafts/${route.params.id}`)
        draft.value = response.data
        if (draft.value.league?.id) {
          fetchTeams(draft.value.league.id)
        }
      } catch (err) {
        error.value.draft = 'Failed to load draft details'
        console.error('Error fetching draft:', err)
      } finally {
        loading.value.draft = false
      }
    }

    const fetchTeams = async (leagueId) => {
      loading.value.teams = true
      error.value.teams = null
      try {
        const response = await axios.get(`/api/leagues/${leagueId}/teams`)
        teams.value = response.data.content || []
      } catch (err) {
        error.value.teams = 'Failed to load teams'
        console.error('Error fetching teams:', err)
      } finally {
        loading.value.teams = false
      }
    }

    const fetchDraftPicks = async (page = 0) => {
      const isInitialLoad = page === 0
      loading.value[isInitialLoad ? 'picks' : 'morePicks'] = true
      error.value.picks = null
      
      try {
        const response = await axios.get(`/api/drafts/${route.params.id}/picks?page=${page}`)
        if (isInitialLoad) {
          draftPicks.value = response.data.content
        } else {
          draftPicks.value = [...draftPicks.value, ...response.data.content]
        }
        currentPage.value = response.data.number
        totalPages.value = response.data.totalPages
      } catch (err) {
        error.value.picks = 'Failed to load draft picks'
        console.error('Error fetching draft picks:', err)
      } finally {
        loading.value[isInitialLoad ? 'picks' : 'morePicks'] = false
      }
    }

    const loadMorePicks = () => {
      if (hasMorePicks.value && !loading.value.morePicks) {
        fetchDraftPicks(currentPage.value + 1)
      }
    }

    const startDraft = async () => {
      loading.value.startDraft = true
      error.value.startDraft = null
      try {
        await axios.patch(`/api/drafts/${route.params.id}`, {
          started: true
        })
        await fetchDraft()
      } catch (err) {
        error.value.startDraft = 'Failed to start draft'
        console.error('Error starting draft:', err)
      } finally {
        loading.value.startDraft = false
      }
    }

    onMounted(() => {
      if (!route.params.id) {
        router.push('/dashboard')
        return
      }
      
      fetchDraft()
      fetchDraftPicks()
    })

    onUnmounted(() => {
      stopTimer()
    })

    watch(() => draft.value?.started, (isStarted) => {
      if (isStarted && !draft.value?.complete) {
        startTimer()
      } else {
        stopTimer()
      }
    })

    return {
      draft,
      draftPicks,
      loading,
      error,
      hasMorePicks,
      getDraftStatus,
      getDraftStatusClass,
      calculateRound,
      loadMorePicks,
      getTeamName,
      groupedPicks,
      currentTeamOnClock,
      elapsedTimeDisplay,
      startDraft
    }
  }
})
</script>

<style scoped>
.draft-admin-view {
  padding: 2rem;
  max-width: 1200px;
  margin: 0 auto;
}

.draft-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 2rem;
}

.draft-actions {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.start-button {
  background-color: #16a34a;
  color: white;
  font-weight: 600;
  padding: 0.5rem 1.5rem;
  border-radius: 6px;
  border: none;
  cursor: pointer;
  transition: background-color 0.2s;
}

.start-button:hover {
  background-color: #15803d;
}

.start-button:disabled {
  background-color: #86efac;
  cursor: not-allowed;
}

.draft-status {
  display: inline-flex;
  align-items: center;
  padding: 0.375rem 0.75rem;
  border-radius: 9999px;
  font-weight: 500;
  font-size: 0.875rem;
}

.draft-status.complete {
  background-color: #dcfce7;
  color: #166534;
}

.draft-status.in-progress {
  background-color: #dbeafe;
  color: #1e40af;
}

.draft-status.not-started {
  background-color: #f1f5f9;
  color: #475569;
}

section {
  background-color: white;
  border-radius: 8px;
  padding: 1.5rem;
  margin-bottom: 2rem;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

section h2 {
  margin: 0 0 1.5rem 0;
  font-size: 1.25rem;
  color: #1e293b;
}

.details-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 1.5rem;
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.detail-label {
  font-size: 0.875rem;
  color: #64748b;
}

.detail-value {
  font-size: 1rem;
  font-weight: 500;
  color: #1e293b;
}

.draft-order-table {
  width: 100%;
  border-collapse: collapse;
  background-color: white;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 2rem;
}

.draft-order-table th {
  text-align: left;
  padding: 0.75rem 1rem;
  background-color: #f8fafc;
  color: #475569;
  font-weight: 600;
  border-bottom: 2px solid #e2e8f0;
}

.draft-order-table td {
  padding: 0.75rem 1rem;
  border-bottom: 1px solid #e2e8f0;
}

.draft-order-table tr:last-child td {
  border-bottom: none;
}

.draft-order-row:hover {
  background-color: #f8fafc;
}

.order-number {
  font-weight: 500;
  color: #64748b;
}

.team-name {
  font-weight: 500;
  color: #1e293b;
}

.round-section {
  margin-bottom: 2rem;
}

.round-header {
  font-size: 1.25rem;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 1rem;
  padding-bottom: 0.5rem;
  border-bottom: 2px solid #e2e8f0;
}

.draft-picks-table {
  width: 100%;
  border-collapse: collapse;
  background-color: white;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 1rem;
}

.draft-picks-table th {
  text-align: left;
  padding: 0.75rem 1rem;
  background-color: #f8fafc;
  color: #475569;
  font-weight: 600;
  border-bottom: 2px solid #e2e8f0;
}

.draft-picks-table td {
  padding: 0.75rem 1rem;
  border-bottom: 1px solid #e2e8f0;
}

.draft-picks-table tr:last-child td {
  border-bottom: none;
}

.draft-pick-row:hover {
  background-color: #f8fafc;
}

.pick-number {
  color: #64748b;
  font-weight: 500;
}

.team-name {
  font-weight: 500;
  color: #1e293b;
}

.runner-name {
  color: #1e293b;
}

.gender-indicator {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 1.75rem;
  height: 1.75rem;
  border-radius: 9999px;
  font-weight: bold;
}

.gender-indicator.male {
  background-color: #dbeafe;
  color: #1e40af;
}

.gender-indicator.female {
  background-color: #fce7f3;
  color: #9d174d;
}

.load-more-button {
  display: block;
  width: 100%;
  padding: 0.75rem;
  background-color: #3182ce;
  color: white;
  border: none;
  border-radius: 6px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s;
}

.load-more-button:hover {
  background-color: #2563eb;
}

.load-more-button:disabled {
  background-color: #94a3b8;
  cursor: not-allowed;
}

.loading {
  text-align: center;
  padding: 2rem;
  color: #64748b;
}

.error-message {
  padding: 1rem;
  background-color: #fee2e2;
  border: 1px solid #fecaca;
  border-radius: 6px;
  color: #dc2626;
  margin-bottom: 1rem;
}

.draft-progress {
  background-color: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  padding: 1.5rem;
  margin-bottom: 2rem;
}

.on-clock {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.on-clock h3 {
  font-size: 1.25rem;
  font-weight: 600;
  color: #1e293b;
  margin: 0;
}

.current-status {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: white;
  padding: 1rem;
  border-radius: 6px;
  border: 1px solid #e2e8f0;
}

.pick-info {
  display: flex;
  gap: 2rem;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.info-label {
  font-size: 0.875rem;
  color: #64748b;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.info-value {
  font-size: 1.25rem;
  font-weight: 600;
  color: #1e293b;
}

.elapsed-time {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 0.25rem;
}

.timer {
  font-family: monospace;
  font-size: 1.125rem;
  font-weight: 600;
  color: #1e293b;
  padding: 0.25rem 0.75rem;
  background-color: #f1f5f9;
  border-radius: 4px;
  white-space: nowrap;
}

.team-info {
  display: flex;
  align-items: center;
  background-color: white;
  padding: 1.5rem;
  border-radius: 6px;
  border: 1px solid #e2e8f0;
  margin-bottom: 0.5rem;
}

.team-name {
  font-size: 1.75rem;
  font-weight: 600;
  color: #1e40af;
}
</style>
