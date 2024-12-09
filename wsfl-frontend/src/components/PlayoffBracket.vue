<template>
  <div class="playoff-bracket">
    <div class="bracket-header">
      <h4>Playoffs</h4>
    </div>
    <div class="bracket-container">
      <!-- Playoff Round -->
      <div class="bracket-round playoff-round">
        <div v-for="matchup in playoffMatchups" :key="matchup.id" class="matchup-card">
          <div class="matchup-date">{{ formatDate(matchup.race.date) }}</div>
          <div class="matchup-teams">
            <div class="team" :class="{ winner: matchup.team1Score < matchup.team2Score }">
              <span class="team-name">{{ matchup.team1.name }}</span>
              <span class="team-score">{{ matchup.team1Score }}</span>
            </div>
            <div class="team" :class="{ winner: matchup.team2Score < matchup.team1Score }">
              <span class="team-name">{{ matchup.team2.name }}</span>
              <span class="team-score">{{ matchup.team2Score }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Championship Round -->
      <div class="bracket-round championship-round" v-if="championshipMatchup">
        <div class="matchup-card championship">
          <div class="matchup-date">{{ formatDate(championshipMatchup.race.date) }}</div>
          <div class="trophy-icon">🏆</div>
          <div class="matchup-teams">
            <div class="team" :class="{ winner: championshipMatchup.team1Score < championshipMatchup.team2Score }">
              <span class="team-name">{{ championshipMatchup.team1.name }}</span>
              <span class="team-score">{{ championshipMatchup.team1Score }}</span>
            </div>
            <div class="team" :class="{ winner: championshipMatchup.team2Score < championshipMatchup.team1Score }">
              <span class="team-name">{{ championshipMatchup.team2.name }}</span>
              <span class="team-score">{{ championshipMatchup.team2Score }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineProps } from 'vue';

defineProps({
  playoffMatchups: {
    type: Array,
    required: true
  },
  championshipMatchup: {
    type: Object,
    default: null
  }
});

const formatDate = (dateString) => {
  return new Date(dateString).toLocaleDateString('en-US', {
    month: 'short',
    day: 'numeric'
  });
};
</script>

<style scoped>
.playoff-bracket {
  width: 100%;
  padding: var(--spacing-md);
  background-color: var(--bg-secondary);
  border-radius: var(--radius-lg);
  margin: var(--spacing-md) 0;
}

.bracket-header {
  margin-bottom: var(--spacing-md);
}

.bracket-header h4 {
  color: var(--text-primary);
  font-size: 1.25rem;
  font-weight: 600;
  margin: 0;
}

.bracket-container {
  display: flex;
  gap: var(--spacing-xl);
  justify-content: center;
  align-items: flex-start;
}

.bracket-round {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
  flex: 1;
}

.playoff-round {
  max-width: 320px;
}

.championship-round {
  max-width: 320px;
}

.matchup-card {
  background-color: var(--bg-tertiary);
  border-radius: var(--radius-md);
  padding: var(--spacing-sm);
  border: 1px solid var(--border-primary);
}

.matchup-card.championship {
  border: 2px solid var(--accent-primary);
  background-color: var(--bg-tertiary);
}

.matchup-date {
  color: var(--text-secondary);
  font-size: 0.75rem;
  font-weight: 500;
  margin-bottom: var(--spacing-xs);
}

.trophy-icon {
  font-size: 1.5rem;
  text-align: center;
  margin: var(--spacing-xs) 0;
}

.matchup-teams {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xs);
}

.team {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-xs) var(--spacing-sm);
  border-radius: var(--radius-sm);
  background-color: var(--bg-secondary);
  transition: all 0.2s ease;
  border: 1px solid transparent;
}

.team.winner {
  background-color: var(--accent-success);
  color: var(--bg-primary);
  font-weight: 600;
  border-color: var(--accent-success);
  transform: translateY(-1px);
  box-shadow: var(--shadow-sm);
}

.team-name {
  color: var(--text-primary);
  font-weight: 500;
}

.team-score {
  color: var(--text-primary);
  font-weight: 600;
  min-width: 2.5rem;
  text-align: right;
}

.winner .team-name,
.winner .team-score {
  color: var(--bg-primary);
}

@media (max-width: 768px) {
  .bracket-container {
    flex-direction: column;
    align-items: center;
  }

  .bracket-round {
    width: 100%;
    max-width: 320px;
  }

  .playoff-round,
  .championship-round {
    max-width: 100%;
  }
}

@media (max-width: 480px) {
  .playoff-bracket {
    padding: var(--spacing-sm);
  }

  .matchup-card {
    padding: var(--spacing-xs);
  }
}
</style>
