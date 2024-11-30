<template>
  <div class="draft-pick-view">
    <div v-if="loading.draft || loading.runners || loading.teams" class="loading">Loading...</div>
    <div v-else-if="error.draft || error.runners || error.teams" class="error-message">
      {{ error.draft || error.runners || error.teams }}
    </div>
    <div v-else-if="!draft || !isMyTeamOnClock" class="not-your-turn">
      It's not your team's turn to pick
    </div>
    <div v-else class="draft-pick-content">
      <header class="draft-header">
        <div class="draft-info">
          <h1>{{ draft.name }}</h1>
          <div class="draft-meta">
            <span class="league-name">{{ draft.league.name }}</span>
            <span class="draft-progress">Round {{ draft.currentRound }}, Pick {{ draft.currentPick }}</span>
          </div>
        </div>
        <div class="elapsed-time">
          Time Elapsed: {{ elapsedTimeDisplay }}
        </div>
      </header>

      <div class="pick-section">
        <h2>Make Your Pick</h2>
        <div class="runners-table">
          <table>
            <thead>
              <tr>
                <th 
                  v-for="column in columns" 
                  :key="column.key"
                  @click="sortBy(column.key)"
                  :class="{ sortable: column.sortable }"
                >
                  {{ column.label }}
                  <span v-if="column.sortable" class="sort-indicator">
                    {{ sortKey === column.key ? (sortOrder === 'asc' ? '▲' : '▼') : '▲▼' }}
                  </span>
                </th>
              </tr>
            </thead>
            <tbody>
              <tr 
                v-for="runner in sortedRunners" 
                :key="runner.id"
                :class="{ selected: selectedRunner?.id === runner.id }"
                @click="selectRunner(runner)"
              >
                <td>{{ runner.name }}</td>
                <td>{{ runner.gender }}</td>
                <td class="text-right">{{ formatPlace(runner.averageOverallPlace) }}</td>
                <td class="text-right">{{ formatPlace(runner.averageGenderPlace) }}</td>
                <td class="text-right">{{ runner.totalRaces }}</td>
              </tr>
            </tbody>
          </table>
        </div>

        <div class="action-bar">
          <button 
            class="submit-pick" 
            :disabled="!selectedRunner || loading.submitPick"
            @click="submitPick"
          >
            {{ loading.submitPick ? 'Submitting...' : 'Submit Pick' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { defineComponent, ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useStore } from 'vuex'
import axios from 'axios'

export default defineComponent({
  name: 'DraftPickView',
  setup() {
    const store = useStore()
    const route = useRoute()
    const router = useRouter()

    const draft = ref(null)
    const availableRunners = ref([])
    const selectedRunner = ref(null)
    const elapsedTime = ref(0)
    const timerInterval = ref(null)
    const teams = ref([])

    const loading = ref({
      draft: false,
      runners: false,
      submitPick: false,
      teams: false
    })

    const error = ref({
      draft: null,
      runners: null,
      submitPick: null,
      teams: null
    })

    const columns = [
      { key: 'name', label: 'Name', sortable: true },
      { key: 'gender', label: 'Gender', sortable: true },
      { key: 'averageOverallPlace', label: 'Avg Place', sortable: true },
      { key: 'averageGenderPlace', label: 'Avg Gender Place', sortable: true },
      { key: 'totalRaces', label: 'Total Races', sortable: true }
    ]

    const sortKey = ref('name')
    const sortOrder = ref('asc')

    const sortBy = (key) => {
      if (sortKey.value === key) {
        sortOrder.value = sortOrder.value === 'asc' ? 'desc' : 'asc'
      } else {
        sortKey.value = key
        sortOrder.value = 'asc'
      }
    }

    const sortedRunners = computed(() => {
      return [...availableRunners.value].sort((a, b) => {
        const aVal = a[sortKey.value]
        const bVal = b[sortKey.value]
        
        if (aVal === bVal) return 0
        
        // Handle null values
        if (aVal === null) return 1
        if (bVal === null) return -1
        
        const modifier = sortOrder.value === 'asc' ? 1 : -1
        return aVal > bVal ? modifier : -modifier
      })
    })

    const formatPlace = (place) => {
      return place ? place.toFixed(1) : '-'
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

    const isMyTeamOnClock = computed(() => {
      if (!draft.value) return false
      const currentTeamId = getCurrentPickTeam(draft.value)
      return teams.value.some(team => team.id === currentTeamId)
    })

    const elapsedTimeDisplay = computed(() => {
      const days = Math.floor(elapsedTime.value / (24 * 3600))
      const hours = Math.floor((elapsedTime.value % (24 * 3600)) / 3600)
      const minutes = Math.floor((elapsedTime.value % 3600) / 60)
      const seconds = elapsedTime.value % 60

      const timeStr = `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`
      return days > 0 ? `${days} ${days === 1 ? 'Day' : 'Days'} ${timeStr}` : timeStr
    })

    const startTimer = () => {
      if (timerInterval.value) clearInterval(timerInterval.value)
      
      if (draft.value?.startTime) {
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

    const fetchTeams = async () => {
      const user = store.getters['auth/currentUser']
      if (!user) return
      
      loading.value.teams = true
      error.value.teams = null
      try {
        const response = await axios.get(`/api/users/${user.id}/teams`)
        teams.value = response.data.content || []
      } catch (err) {
        error.value.teams = 'Failed to load teams'
        console.error('Error fetching teams:', err)
      } finally {
        loading.value.teams = false
      }
    }

    const fetchDraft = async () => {
      loading.value.draft = true
      error.value.draft = null
      try {
        const response = await axios.get(`/api/drafts/${route.params.id}`)
        draft.value = response.data
        startTimer()
      } catch (err) {
        error.value.draft = 'Failed to load draft'
        console.error('Error fetching draft:', err)
      } finally {
        loading.value.draft = false
      }
    }

    const fetchAvailableRunners = async () => {
      loading.value.runners = true
      error.value.runners = null
      try {
        let allRunners = []
        let currentPage = 0
        let hasMore = true
        
        while (hasMore) {
          const response = await axios.get(`/api/drafts/${route.params.id}/runners`, {
            params: {
              status: 'available',
              page: currentPage,
              size: 50 // Fetch more runners per request to minimize API calls
            }
          })
          
          allRunners = [...allRunners, ...response.data.content]
          
          hasMore = !response.data.last
          currentPage++
        }
        
        availableRunners.value = allRunners
      } catch (err) {
        error.value.runners = 'Failed to load available runners'
        console.error('Error fetching runners:', err)
      } finally {
        loading.value.runners = false
      }
    }

    const selectRunner = (runner) => {
      selectedRunner.value = runner
    }

    const submitPick = async () => {
      if (!selectedRunner.value || loading.value.submitPick) return

      loading.value.submitPick = true
      error.value.submitPick = null
      try {
        await axios.post(`/api/drafts/${route.params.id}/picks`, {
          runnerId: selectedRunner.value.id
        })
        router.push(`/drafts/${route.params.id}`)
      } catch (err) {
        error.value.submitPick = 'Failed to submit pick'
        console.error('Error submitting pick:', err)
      } finally {
        loading.value.submitPick = false
      }
    }

    onMounted(async () => {
      if (!route.params.id) {
        router.push('/dashboard')
        return
      }

      await Promise.all([
        fetchDraft(),
        fetchTeams(),
        fetchAvailableRunners()
      ])
    })

    onUnmounted(() => {
      stopTimer()
    })

    return {
      draft,
      availableRunners,
      selectedRunner,
      loading,
      error,
      isMyTeamOnClock,
      elapsedTimeDisplay,
      selectRunner,
      submitPick,
      columns,
      sortKey,
      sortOrder,
      sortBy,
      sortedRunners,
      formatPlace
    }
  }
})
</script>

<style scoped>
.draft-pick-view {
  padding: 2rem;
}

.draft-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 2rem;
}

.draft-info h1 {
  margin: 0;
  font-size: 1.875rem;
  color: #1e293b;
  margin-bottom: 0.5rem;
}

.draft-meta {
  display: flex;
  gap: 1rem;
  color: #64748b;
}

.elapsed-time {
  font-family: monospace;
  font-size: 1.125rem;
  padding: 0.5rem 1rem;
  background-color: #f1f5f9;
  border-radius: 6px;
  color: #1e293b;
}

.pick-section {
  background: white;
  border-radius: 8px;
  padding: 2rem;
  box-shadow: 0 1px 3px 0 rgb(0 0 0 / 0.1);
}

.pick-section h2 {
  margin: 0 0 1.5rem 0;
  font-size: 1.5rem;
  color: #1e293b;
}

.runners-table {
  margin-bottom: 2rem;
  overflow-x: auto;
}

.runners-table table {
  width: 100%;
  border-collapse: collapse;
  background: white;
}

.runners-table th {
  background-color: #f8fafc;
  padding: 0.75rem 1rem;
  text-align: left;
  font-weight: 600;
  color: #1e293b;
  border-bottom: 2px solid #e2e8f0;
}

.runners-table th.sortable {
  cursor: pointer;
  user-select: none;
}

.runners-table th.sortable:hover {
  background-color: #f1f5f9;
}

.sort-indicator {
  margin-left: 0.5rem;
  color: #94a3b8;
  font-size: 0.75rem;
}

.runners-table td {
  padding: 0.75rem 1rem;
  border-bottom: 1px solid #e2e8f0;
  color: #1e293b;
}

.runners-table tr {
  cursor: pointer;
  transition: all 0.2s;
}

.runners-table tr:hover {
  background-color: #f8fafc;
}

.runners-table tr.selected {
  background-color: #eff6ff;
}

.runners-table tr.selected td {
  border-bottom-color: #bfdbfe;
}

.runners-table td.text-right {
  text-align: right;
}

.runners-table th.text-right {
  text-align: right;
}

.action-bar {
  display: flex;
  justify-content: flex-end;
  padding-top: 1rem;
  border-top: 1px solid #e2e8f0;
}

.submit-pick {
  background-color: #2563eb;
  color: white;
  font-weight: 600;
  padding: 0.75rem 2rem;
  border-radius: 6px;
  border: none;
  cursor: pointer;
  transition: background-color 0.2s;
}

.submit-pick:hover {
  background-color: #1d4ed8;
}

.submit-pick:disabled {
  background-color: #93c5fd;
  cursor: not-allowed;
}

.not-your-turn {
  text-align: center;
  padding: 2rem;
  background-color: #fee2e2;
  border-radius: 8px;
  color: #991b1b;
  font-weight: 500;
}

.loading {
  text-align: center;
  padding: 2rem;
  color: #64748b;
}

.error-message {
  text-align: center;
  padding: 2rem;
  background-color: #fee2e2;
  border-radius: 8px;
  color: #991b1b;
}
</style>
