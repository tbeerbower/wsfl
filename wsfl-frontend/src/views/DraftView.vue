<template>
  <div class="draft-view">
    <nav class="nav-header">
      <router-link to="/dashboard" class="nav-link">
        <span class="nav-icon">←</span>
        Back to Dashboard
      </router-link>
    </nav>

    <div v-if="draft" class="draft-header">
      <div class="draft-info">
        <h2>{{ draft.name }}</h2>
        <div class="draft-meta">
          <span class="league-name">{{ draft.league.name }}</span>
          <span class="divider">•</span>
          <span class="season-name">{{ draft.season.name }}</span>
          <span class="divider">•</span>
          <span class="draft-details">
            {{ draft.numberOfRounds }} Rounds
            <span v-if="draft.snakeOrder">(Snake Order)</span>
          </span>
        </div>
        <div class="draft-time">
          Started: {{ new Date(draft.startTime).toLocaleString() }}
        </div>
      </div>
      <div class="draft-status" :class="{
        'not-started': draft.status === 'Not Started',
        'in-progress': draft.status === 'In Progress',
        'complete': draft.status === 'Complete'
      }">
        {{ draft.status }}
      </div>
    </div>

    <div v-if="draft?.status === 'In Progress'" class="current-pick-section">
      <div class="current-pick-header">
        <h3>Current Pick</h3>
        <div class="pick-details">
          Round {{ draft.currentRound }} • Pick {{ draft.currentPick }} 
          {{ isSnakeRound(draft.currentRound) ? '(Snake Round)' : '' }}
        </div>
      </div>
      <div class="team-on-clock">
        <div class="clock-indicator"></div>
        <div class="team-info">
          <div class="on-clock-label">On the Clock</div>
          <div class="team-name">{{ getCurrentTeam()?.name || 'Loading...' }}</div>
        </div>
        <button 
          v-if="isCurrentUserTeam(getCurrentTeam())" 
          class="make-pick-button"
          @click="$router.push({ name: 'draft-pick', params: { draftId: draft.id }})"
        >
          Make Pick
        </button>
      </div>
    </div>

    <div v-if="draft?.status === 'In Progress' && currentTeam" class="on-clock">
      <h3>On the Clock</h3>
      <div class="team-card">
        <h4>{{ currentTeam.name }}</h4>
        <p>Pick #{{ nextPickNumber }}</p>
      </div>
    </div>

    <div v-if="draft && ['Complete', 'In Progress'].includes(draft.status)" class="draft-picks">
      <h3>Draft Picks</h3>
      <div v-for="round in draftRounds" :key="round.roundNumber" class="round-section">
        <div class="round-header">
          <h4>Round {{ round.roundNumber }} 
            <span class="round-direction">
              {{ isSnakeRound(round.roundNumber) ? '(Snake Round)' : '(Standard Round)' }}
            </span>
          </h4>
          <div class="round-picks">Picks {{ getRoundPickRange(round.roundNumber) }}</div>
        </div>
        <div class="round-table">
          <table>
            <thead>
              <tr>
                <th>Pick</th>
                <th>Team</th>
                <th>Runner</th>
                <th>Gender</th>
                <th>Avg Place</th>
                <th>Time</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="pick in round.picks" :key="pick.id">
                <td class="pick-number">#{{ pick.pickNumber }}</td>
                <td class="team-name">{{ pick.team?.name }}</td>
                <td class="runner-name">{{ pick.runner?.name }}</td>
                <td class="runner-gender">{{ pick.runner?.gender }}</td>
                <td class="runner-stats">
                  {{ pick.runner?.averageOverallPlace.toFixed(1) }}
                  <span class="stats-detail">
                    ({{ pick.runner?.averageGenderPlace.toFixed(1) }} {{ pick.runner?.gender }})
                  </span>
                </td>
                <td class="pick-time">{{ formatTime(pick.pickTime) }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <div v-else-if="loading" class="loading-state">
      Loading draft information...
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute } from 'vue-router';
import { useStore } from 'vuex';
import axios from 'axios';

const route = useRoute();
const store = useStore();
const draft = ref(null);
const draftPicks = ref([]);
const currentTeam = ref(null);
const nextPickNumber = ref(null);
const teams = ref({});
const loading = ref(true);

const formatTime = (timeString) => {
  return new Date(timeString).toLocaleTimeString([], { 
    hour: '2-digit', 
    minute: '2-digit'
  });
};

const draftRounds = computed(() => {
  if (!draft.value || !draftPicks.value.length) return [];
  
  const rounds = [];
  const picksPerRound = draft.value.draftOrder.length;
  
  for (let i = 0; i < draft.value.numberOfRounds; i++) {
    const roundNumber = i + 1;
    const startIdx = i * picksPerRound;
    const endIdx = startIdx + picksPerRound;
    
    rounds.push({
      roundNumber,
      picks: draftPicks.value.slice(startIdx, endIdx)
    });
  }
  
  return rounds;
});

const isSnakeRound = (roundNumber) => {
  return roundNumber % 2 === 0;
};

const getRoundPickRange = (roundNumber) => {
  const picksPerRound = draft.value.draftOrder.length;
  const startPick = (roundNumber - 1) * picksPerRound + 1;
  const endPick = startPick + picksPerRound - 1;
  
  return `${startPick} - ${endPick}`;
};

const getCurrentTeam = () => {
  if (!draft.value?.draftOrder || !draft.value?.currentRound || !draft.value?.currentPick) return null;
  if (!Array.isArray(teams.value?.content)) return null;
  
  const teamsCount = draft.value.draftOrder.length;
  const totalPick = ((draft.value.currentRound - 1) * teamsCount) + draft.value.currentPick;
  const pickIndex = totalPick - 1;
  
  const currentOrder = isSnakeRound(draft.value.currentRound) 
    ? [...draft.value.draftOrder].reverse() 
    : draft.value.draftOrder;
    
  const teamId = currentOrder[pickIndex % teamsCount];
  return teams.value.content.find(team => team.id === teamId);
};

const isCurrentUserTeam = (team) => {
  const currentUser = store.getters['auth/currentUser'];
  return team?.owner?.id === currentUser?.id;
};

const fetchDraft = async () => {
  try {
    loading.value = true;
    const [draftResponse, teamsResponse] = await Promise.all([
      axios.get(`/api/drafts/${route.params.draftId}`),
      axios.get('/api/teams')
    ]);
    
    draft.value = draftResponse.data;
    teams.value = teamsResponse.data; // API returns { content: [...teams] }
    
    if (draft.value && ['Complete', 'In Progress'].includes(draft.value.status)) {
      const picksResponse = await axios.get(`/api/drafts/${route.params.draftId}/picks?size=1000`);
      draftPicks.value = picksResponse.data.content || [];
      
      if (draft.value.status === 'In Progress') {
        const maxPickNumber = draftPicks.value.length > 0 
          ? Math.max(...draftPicks.value.map(p => p.pickNumber), 0)
          : 0;
        nextPickNumber.value = maxPickNumber + 1;
        
        if (draft.value.draftOrder && draft.value.draftOrder.length > 0) {
          const nextPick = draft.value.draftOrder.find(pick => pick.pickNumber === nextPickNumber.value);
          currentTeam.value = nextPick?.team || null;
        }
      }
    }
  } catch (error) {
    console.error('Error fetching draft data:', error);
  } finally {
    loading.value = false;
  }
};

onMounted(fetchDraft);
</script>

<style scoped>
.draft-view {
  width: 100%;
  max-width: 100vw;
  min-height: 100vh;
  padding: var(--spacing-md);
  background-color: var(--bg-primary);
  overflow-x: hidden;
}

.nav-header {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  margin-bottom: var(--spacing-lg);
}

.nav-link {
  display: inline-flex;
  align-items: center;
  gap: var(--spacing-xs);
  color: var(--text-secondary);
  text-decoration: none;
  padding: var(--spacing-sm) var(--spacing-md);
  border-radius: var(--radius-md);
  transition: all 0.2s ease;
  background-color: var(--bg-secondary);
  border: 1px solid var(--border-primary);
}

.nav-link:hover {
  background-color: var(--bg-tertiary);
  color: var(--text-primary);
  border-color: var(--border-secondary);
}

.draft-header {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  background-color: var(--bg-secondary);
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-primary);
  box-shadow: var(--shadow-md);
  padding: var(--spacing-lg);
  margin-bottom: var(--spacing-xl);
}

.draft-header h2 {
  color: #e4e4e7;
  font-size: 1.5rem;
  font-weight: 600;
  margin: 0 0 var(--spacing-sm) 0;
  letter-spacing: 0.5px;
}

.draft-meta {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  margin-top: var(--spacing-xs);
  color: #a1a1aa;
  font-size: 0.875rem;
}

.draft-time {
  color: #a1a1aa;
  font-size: 0.875rem;
  margin-top: var(--spacing-xs);
}

.divider {
  color: #71717a;
  font-size: 0.75rem;
}

.league-name,
.season-name,
.draft-details {
  color: #d4d4d8;
}

.draft-status {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 4px 12px;
  border-radius: var(--radius-full);
  font-size: 0.875rem;
  font-weight: 600;
  letter-spacing: 0.5px;
}

.draft-status.not-started {
  background-color: #71717a;
  color: #f4f4f5;
  border: 1px solid #52525b;
}

.draft-status.in-progress {
  background-color: #facc15;
  color: #1c1917;
  border: 1px solid #eab308;
}

.draft-status.complete {
  background-color: #22c55e;
  color: #f4f4f5;
  border: 1px solid #16a34a;
}

.current-pick-section {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  background-color: var(--bg-secondary);
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-primary);
  box-shadow: var(--shadow-md);
  padding: var(--spacing-lg);
  margin-bottom: var(--spacing-xl);
}

.current-pick-header {
  margin-bottom: var(--spacing-md);
}

.current-pick-header h3 {
  color: var(--text-primary);
  font-size: 1.25rem;
  margin: 0 0 var(--spacing-xs) 0;
  font-weight: 600;
  letter-spacing: 0.5px;
}

.pick-details {
  color: var(--text-secondary);
  font-size: 0.875rem;
  background-color: var(--bg-tertiary);
  padding: 4px 12px;
  border-radius: var(--radius-full);
  border: 1px solid var(--border-secondary);
  display: inline-block;
}

.team-on-clock {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  background-color: var(--bg-tertiary);
  padding: var(--spacing-md);
  border-radius: var(--radius-md);
  border: 1px solid var(--border-primary);
  justify-content: space-between;
}

.clock-indicator {
  width: 12px;
  height: 12px;
  background-color: var(--accent-primary);
  border-radius: 50%;
  animation: pulse 2s infinite;
}

.team-info {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xs);
}

.on-clock-label {
  color: var(--text-secondary);
  font-size: 0.75rem;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.team-name {
  color: var(--text-primary);
  font-size: 1.125rem;
  font-weight: 500;
}

.make-pick-button {
  background-color: #dc2626;
  color: #ffffff;
  border: none;
  padding: var(--spacing-sm) var(--spacing-lg);
  border-radius: var(--radius-md);
  font-weight: 600;
  font-size: 0.875rem;
  cursor: pointer;
  transition: all 0.2s ease;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.make-pick-button:hover {
  background-color: #b91c1c;
}

.make-pick-button:active {
  transform: translateY(1px);
}

.clock-indicator {
  flex-shrink: 0;
}

.team-info {
  flex-grow: 1;
}

.draft-picks {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
}

.draft-picks h3 {
  color: var(--text-primary);
  font-size: 1.25rem;
  margin: 0 0 var(--spacing-lg) 0;
  font-weight: 600;
  letter-spacing: 0.5px;
}

.round-section {
  margin-bottom: var(--spacing-xl);
  background-color: var(--bg-secondary);
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-primary);
  box-shadow: var(--shadow-md);
  overflow: hidden;
}

.round-header {
  background-color: var(--bg-tertiary);
  padding: var(--spacing-md);
  border-bottom: 1px solid var(--border-primary);
}

.round-header h4 {
  color: var(--text-primary);
  font-size: 1.25rem;
  margin: 0;
  font-weight: 600;
  letter-spacing: 0.5px;
}

.round-direction {
  color: var(--text-secondary);
  font-size: 0.875rem;
  font-weight: normal;
}

.round-picks {
  color: var(--text-secondary);
  font-size: 0.875rem;
  margin-top: var(--spacing-xs);
}

.round-table {
  overflow-x: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th {
  background-color: var(--bg-tertiary);
  color: var(--text-secondary);
  font-weight: 500;
  text-align: left;
  padding: var(--spacing-sm) var(--spacing-md);
  font-size: 0.875rem;
  border-bottom: 1px solid var(--border-primary);
}

td {
  padding: var(--spacing-sm) var(--spacing-md);
  border-bottom: 1px solid var(--border-primary);
  color: var(--text-primary);
  font-size: 0.875rem;
  background-color: var(--bg-secondary);
}

tr:last-child td {
  border-bottom: none;
}

.pick-number {
  color: var(--text-secondary);
  font-weight: 500;
}

.team-name {
  font-weight: 500;
}

.runner-name {
  color: var(--text-primary);
}

.runner-gender {
  color: var(--text-secondary);
}

.runner-stats {
  font-family: monospace;
  color: var(--text-primary);
}

.stats-detail {
  color: var(--text-secondary);
  font-size: 0.75rem;
}

.pick-time {
  color: var(--text-secondary);
  font-size: 0.75rem;
}

tr:hover td {
  background-color: var(--bg-tertiary);
}

@keyframes pulse {
  0% {
    transform: scale(0.95);
    box-shadow: 0 0 0 0 rgba(var(--accent-primary-rgb), 0.7);
  }
  
  70% {
    transform: scale(1);
    box-shadow: 0 0 0 6px rgba(var(--accent-primary-rgb), 0);
  }
  
  100% {
    transform: scale(0.95);
    box-shadow: 0 0 0 0 rgba(var(--accent-primary-rgb), 0);
  }
}

@media (max-width: 768px) {
  .draft-view {
    padding: var(--spacing-sm);
  }
  
  .draft-header,
  .current-pick-section,
  .draft-picks {
    padding: var(--spacing-md);
  }
  
  .round-table {
    margin: 0 calc(var(--spacing-md) * -1);
    padding: 0 var(--spacing-md);
  }
  
  td, th {
    padding: var(--spacing-xs) var(--spacing-sm);
  }
}
</style>
