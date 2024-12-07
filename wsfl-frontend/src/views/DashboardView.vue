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
              <h4>{{ draft.name }}</h4>
              <span class="draft-status" :class="draft.status.toLowerCase()">
                {{ draft.status }}
              </span>
            </div>

            <div class="standings-section">
              <h5 class="section-title">Current Standings</h5>
              <LeagueStandings :standings="draft.standings" />
            </div>
            
            <div class="teams-section">
              <h5 class="section-title">My Teams</h5>
              <div class="teams-container">
                <div v-for="team in draft.teams" :key="team.id" class="team-card">
                  <div class="team-header">
                    <h5>{{ team.name }}</h5>
                    <div class="team-record">
                      {{ team.wins }}-{{ team.losses }}-{{ team.ties }}
                    </div>
                  </div>
                  <div class="team-stats">
                    <div class="stat">
                      <span class="stat-label">Total Score</span>
                      <span class="stat-value">{{ team.totalScore }}</span>
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

<script>
import { computed, onMounted } from 'vue';
import { useStore } from 'vuex';
import LeagueStandings from '@/components/LeagueStandings.vue';
import MatchupCarousel from '@/components/MatchupCarousel.vue';

export default {
  name: 'DashboardView',
  
  components: {
    LeagueStandings,
    MatchupCarousel
  },

  setup() {
    const store = useStore();
    
    const leagues = computed(() => store.getters['leagues/getUserLeagues']);
    const loading = computed(() => store.getters['leagues/getLeagueLoading']);
    const error = computed(() => store.getters['leagues/getLeagueError']);
    
    onMounted(() => {
      store.dispatch('leagues/fetchUserLeagues');
    });
    
    return {
      leagues,
      loading,
      error
    };
  }
};
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

.team-stats {
  width: 100%;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: var(--spacing-sm);
  padding: var(--spacing-sm) 0;
}

.team-header h5 {
  color: var(--text-primary);
  font-size: 1.25rem;
  font-weight: 600;
  margin: 0;
  letter-spacing: 0.5px;
}

.team-record {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  font-size: 0.875rem;
  color: var(--text-primary);
  background-color: var(--bg-tertiary);
  padding: var(--spacing-xs) var(--spacing-md);
  border-radius: var(--radius-full);
  border: 1px solid var(--border-secondary);
  font-weight: 500;
}

.stat {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-md);
  background-color: var(--bg-tertiary);
  border-radius: var(--radius-md);
  border: 1px solid var(--border-primary);
}

.stat-label {
  color: var(--text-secondary);
  font-size: 0.875rem;
  font-weight: 500;
}

.stat-value {
  color: var(--text-primary);
  font-size: 1rem;
  font-weight: 600;
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
  margin: 0 0 var(--spacing-md) 0;
  font-weight: 600;
  letter-spacing: 0.5px;
}

.draft-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-md);
}

h2 {
  color: var(--text-primary);
  font-size: 1.875rem;
  font-weight: 600;
  margin-bottom: var(--spacing-lg);
  letter-spacing: 0.5px;
}

.draft-status.complete {
  background-color: var(--accent-success);
  color: var(--bg-primary);
  font-weight: 600;
}

.draft-status.in-progress {
  background-color: var(--accent-warning);
  color: var(--bg-primary);
  font-weight: 600;
}

.draft-status.pending {
  background-color: var(--bg-tertiary);
  color: var(--text-primary);
  font-weight: 500;
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
  
  .team-stats {
    grid-template-columns: 1fr;
  }
  
  .stat {
    padding: var(--spacing-xs);
  }
}

@media (max-width: 480px) {
  .dashboard {
    padding: var(--spacing-xs);
  }
  
  .team-card {
    padding: var(--spacing-xs);
  }
  
  .team-stats {
    gap: var(--spacing-xs);
  }
}
</style>
