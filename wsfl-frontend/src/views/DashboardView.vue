<template>
  <div class="dashboard">
    <div class="leagues-section">
      <h2>My Leagues</h2>
      
      <div v-if="loading" class="loading">
        Loading leagues...
      </div>
      
      <div v-else-if="error" class="error">
        {{ error }}
      </div>
      
      <div v-else-if="!leagues.length" class="no-leagues">
        You are not currently in any leagues.
      </div>
      
      <div v-else class="leagues-container">
        <div v-for="league in leagues" :key="league.id" class="league-card">
          <h3>{{ league.name }}</h3>
          
          <div v-for="draft in league.drafts" :key="draft.id" class="draft-section">
            <div class="draft-header">
              <div class="draft-title">
                <h4>{{ draft.name }}</h4>
                <div class="draft-actions">
                  <button 
                    v-if="draft.status === 'Not Started' && isLeagueAdmin(league)"
                    @click="startDraft(draft.id)"
                    class="start-draft-button"
                    :disabled="startingDraft === draft.id"
                  >
                    {{ startingDraft === draft.id ? 'Starting...' : 'Start Draft' }}
                  </button>
                  <router-link 
                    :to="{ name: 'draft', params: { draftId: draft.id }}" 
                    class="draft-button"
                  >
                    Go to draft
                  </router-link>
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

            <div class="standings-section">
              <h5 class="section-title">Current Standings</h5>
              <LeagueStandings :standings="draft.standings" />
            </div>
            
            <PlayoffBracket
              v-if="showPlayoffBracket(draft)"
              :playoffMatchups="draft.playoffMatchups"
              :championshipMatchup="draft.championshipMatchup"
            />

            <div class="teams-section">
              <div class="section-header">
                <h5 class="section-title">My Teams</h5>
                <div class="section-actions">
                  <button 
                    v-if="draft.season?.status === 'Season pending' && isLeagueAdmin(league)"
                    @click="createMatchups(draft.id)"
                    class="start-season-button"
                    :disabled="creatingMatchups === draft.id"
                  >
                    {{ creatingMatchups === draft.id ? 'Starting...' : 'Start Season' }}
                  </button>
                  <button 
                    v-if="draft.season?.status === 'Playoff pending' && isLeagueAdmin(league)"
                    @click="startPlayoffs(draft.id)"
                    class="start-playoffs-button"
                    :disabled="startingPlayoffs === draft.id"
                  >
                    {{ startingPlayoffs === draft.id ? 'Starting...' : 'Start Playoffs' }}
                  </button>
                  <button 
                    v-if="draft.season?.status === 'Championship pending' && isLeagueAdmin(league)"
                    @click="startChampionship(draft.id)"
                    class="start-championship-button"
                    :disabled="startingChampionship === draft.id"
                  >
                    {{ startingChampionship === draft.id ? 'Starting...' : 'Start Championship' }}
                  </button>
                  <div v-if="draft.season?.status" 
                       class="season-status" 
                       :class="draft.season.status.toLowerCase().replace(' ', '-')">
                    {{ draft.season.status }}
                  </div>
                </div>
              </div>
              <div class="teams-container">
                <div v-for="team in draft.teams" :key="team.id" class="team-card">
                  <div class="team-header">
                    <h5>{{ team.name }}</h5>
                    <div class="team-record">
                      <span>{{ team.wins }}-{{ team.losses }}-{{ team.ties }}</span>
                      <span class="record-divider">•</span>
                      <span>{{ team.totalScore }} pts</span>
                    </div>
                  </div>
                  <div class="team-matchups">
                    <h6 class="matchups-title">Schedule & Results</h6>
                    <MatchupCarousel :matchups="team.matchups" />
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useStore } from 'vuex';
import axios from 'axios';
import LeagueStandings from '@/components/LeagueStandings.vue';
import MatchupCarousel from '@/components/MatchupCarousel.vue';
import PlayoffBracket from '@/components/PlayoffBracket.vue';

const store = useStore();
const startingDraft = ref(null);
const creatingMatchups = ref(null);
const startingPlayoffs = ref(null);
const startingChampionship = ref(null);

const leagues = computed(() => store.getters['leagues/getUserLeagues']);
const loading = computed(() => store.getters['leagues/getLeagueLoading']);
const error = computed(() => store.getters['leagues/getLeagueError']);
const currentUser = computed(() => store.getters['auth/currentUser']);

const isLeagueAdmin = (league) => {
  return league.admin?.id === currentUser.value?.id;
};

const startDraft = async (draftId) => {
  startingDraft.value = draftId;
  try {
    await axios.patch(`/api/drafts/${draftId}`, {
      started: true
    });
    await store.dispatch('leagues/fetchUserLeagues');
  } catch (err) {
    console.error('Error starting draft:', err);
  } finally {
    startingDraft.value = null;
  }
};

const createMatchups = async (draftId) => {
  creatingMatchups.value = draftId;
  try {
    await axios.post(`/api/drafts/${draftId}/matchups`);
    await store.dispatch('leagues/fetchUserLeagues');
  } catch (err) {
    console.error('Error creating matchups:', err);
  } finally {
    creatingMatchups.value = null;
  }
};

const startPlayoffs = async (draftId) => {
  startingPlayoffs.value = draftId;
  try {
    await axios.post(`/api/drafts/${draftId}/playoffs`);
    await store.dispatch('leagues/fetchUserLeagues');
  } catch (err) {
    console.error('Error starting playoffs:', err);
  } finally {
    startingPlayoffs.value = null;
  }
};

const startChampionship = async (draftId) => {
  startingChampionship.value = draftId;
  try {
    await axios.post(`/api/drafts/${draftId}/championship`);
    await store.dispatch('leagues/fetchUserLeagues');
  } catch (err) {
    console.error('Error starting championship:', err);
  } finally {
    startingChampionship.value = null;
  }
};

const showPlayoffBracket = (draft) => {
  const playoffStatuses = [
    'Playoff active',
    'Championship pending',
    'Championship active',
    'Season complete'
  ];
  return playoffStatuses.includes(draft.season?.status);
};

onMounted(() => {
  store.dispatch('leagues/fetchUserLeagues');
});
</script>

<style scoped>
.dashboard {
  width: 100%;
  max-width: 100vw;
  min-height: 100vh;
  padding: var(--spacing-md);
  background-color: var(--bg-primary);
  overflow-x: hidden;
}

.leagues-section {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 var(--spacing-sm);
}

.leagues-container {
  width: 100%;
  display: grid;
  gap: var(--spacing-lg);
}

.league-card {
  width: 100%;
  background-color: var(--bg-secondary);
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-primary);
  box-shadow: var(--shadow-md);
  overflow: hidden;
}

.league-card h3 {
  color: var(--text-primary);
  font-size: 1.25rem;
  padding: var(--spacing-md);
  margin: 0;
  font-weight: 600;
  letter-spacing: 0.5px;
  border-bottom: 1px solid var(--border-primary);
  background-color: var(--bg-tertiary);
}

.teams-section {
  width: 100%;
  max-width: 100%;
  overflow: hidden;
}

.teams-container {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
  padding: var(--spacing-sm);
}

.team-card {
  width: 100%;
  max-width: 100%;
  background-color: var(--bg-secondary);
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-primary);
  padding: var(--spacing-md);
  box-shadow: var(--shadow-md);
}

.team-record {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  font-size: 0.875rem;
  color: var(--text-primary);
  background-color: var(--bg-tertiary);
  padding: 4px 12px;
  border-radius: var(--radius-full);
  border: 1px solid var(--border-secondary);
  font-weight: 500;
}

.record-divider {
  color: var(--text-muted);
  font-size: 0.75rem;
}

.team-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-sm);
  padding: var(--spacing-sm);
  background-color: var(--bg-secondary);
  border-radius: var(--radius-md);
  border: 1px solid var(--border-primary);
}

.team-header h5 {
  color: var(--text-primary);
  font-size: 1.125rem;
  font-weight: 600;
  margin: 0;
  letter-spacing: 0.5px;
}

.team-matchups {
  width: 100%;
  max-width: 100%;
  overflow: hidden;
}

.matchups-title {
  color: var(--text-primary);
  font-size: 0.875rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  margin: 0 0 var(--spacing-md) 0;
}

.draft-section {
  padding: var(--spacing-lg);
  background-color: var(--bg-tertiary);
  border-bottom: 1px solid var(--border-primary);
}

.draft-section h4 {
  color: var(--text-primary);
  font-size: 1.125rem;
  margin: 0;
  font-weight: 600;
  letter-spacing: 0.5px;
}

.draft-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-md);
}

.draft-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--spacing-md);
}

.draft-actions {
  display: flex;
  gap: var(--spacing-sm);
  align-items: center;
}

.start-draft-button {
  background-color: #22c55e;
  color: white;
  border: none;
  padding: var(--spacing-xs) var(--spacing-sm);
  border-radius: var(--radius-md);
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.start-draft-button:hover:not(:disabled) {
  background-color: #16a34a;
}

.start-draft-button:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.start-season-button {
  background-color: #22c55e;
  color: white;
  border: none;
  padding: 4px 12px;
  height: 24px;
  border-radius: 9999px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  display: inline-flex;
  align-items: center;
  font-size: 0.75rem;
}

.start-season-button:hover:not(:disabled) {
  background-color: #16a34a;
}

.start-season-button:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.start-playoffs-button {
  background-color: #f97316;
  color: white;
  border: none;
  padding: 4px 12px;
  height: 24px;
  border-radius: 9999px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  display: inline-flex;
  align-items: center;
  font-size: 0.75rem;
}

.start-playoffs-button:hover:not(:disabled) {
  background-color: #ea580c;
}

.start-playoffs-button:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.start-championship-button {
  background-color: #a855f7;
  color: white;
  border: none;
  padding: 4px 12px;
  height: 24px;
  border-radius: 9999px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  display: inline-flex;
  align-items: center;
  font-size: 0.75rem;
}

.start-championship-button:hover:not(:disabled) {
  background-color: #9333ea;
}

.start-championship-button:disabled {
  opacity: 0.7;
  cursor: not-allowed;
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
  transition: all 0.2s ease;
}

.draft-status.not-started {
  background-color: var(--bg-tertiary);
  color: var(--text-muted);
  border: 1px solid var(--border-secondary);
}

.draft-status.in-progress {
  background-color: var(--accent-warning);
  color: var(--bg-primary);
  border: 1px solid var(--accent-warning);
}

.draft-status.complete {
  background-color: var(--accent-success);
  color: var(--bg-primary);
  border: 1px solid var(--accent-success);
}

.draft-button {
  display: inline-flex;
  align-items: center;
  padding: 4px 12px;
  border-radius: var(--radius-full);
  font-size: 0.875rem;
  font-weight: 500;
  background-color: var(--accent-primary);
  color: var(--bg-primary);
  text-decoration: none;
  transition: all 0.2s ease;
  border: 1px solid var(--accent-primary);
}

.draft-button:hover {
  background-color: var(--bg-primary);
  color: var(--accent-primary);
}

h2 {
  color: var(--text-primary);
  font-size: 1.875rem;
  font-weight: 600;
  margin-bottom: var(--spacing-lg);
  letter-spacing: 0.5px;
}

h5.section-title {
  margin-top: .75em;
  font-size: 1em;
  color: lightslategray;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--spacing-sm);
  margin-bottom: var(--spacing-md);
}

.section-actions {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.season-status {
  font-size: 0.75rem;
  padding: 4px 12px;
  border-radius: 9999px;
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  display: inline-flex;
  align-items: center;
  height: 24px;
}

.season-status.season-pending {
  background-color: var(--bg-secondary);
  color: var(--text-secondary);
}

.season-status.season-active {
  background-color: rgba(34, 197, 94, 0.2);
  color: #22c55e;
}

.season-status.playoff-pending {
  background-color: rgba(249, 115, 22, 0.2);
  color: #f97316;
}

.season-status.playoff-active {
  background-color: rgba(249, 115, 22, 0.2);
  color: #f97316;
}

.season-status.championship-pending {
  background-color: rgba(168, 85, 247, 0.2);
  color: #a855f7;
}

.season-status.championship-active {
  background-color: rgba(168, 85, 247, 0.2);
  color: #a855f7;
}

.season-status.season-complete {
  background-color: var(--bg-secondary);
  color: var(--text-secondary);
}

.section-title {
  color: var(--text-primary);
  font-size: 1rem;
  font-weight: 600;
  margin: 0;
}

@media (max-width: 768px) {
  .dashboard {
    padding: var(--spacing-sm);
  }
  
  .leagues-section {
    padding: 0;
  }
  
  .team-card {
    padding: var(--spacing-sm);
  }
}

@media (max-width: 480px) {
  .dashboard {
    padding: var(--spacing-xs);
  }
  
  .team-card {
    padding: var(--spacing-xs);
  }
}
</style>
