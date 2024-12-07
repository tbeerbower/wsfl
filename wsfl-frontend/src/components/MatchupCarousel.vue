<template>
  <div class="matchup-carousel">
    <button 
      class="nav-button prev" 
      @click="scrollLeft"
      :disabled="scrollPosition <= 0"
    >
      ‹
    </button>

    <div class="carousel-container" ref="container">
      <div 
        v-for="matchup in sortedMatchups" 
        :key="matchup.id" 
        class="matchup-card"
        :class="{ 'playoff-game': matchup.race.isPlayoff }"
      >
        <div class="race-info">
          <span class="race-name">{{ matchup.race.name }}</span>
          <span class="race-date">{{ formatDate(matchup.race.date) }}</span>
          <span v-if="matchup.race.isPlayoff" class="playoff-badge">Playoff</span>
        </div>

        <div class="teams-info">
          <div class="team-row" :class="{ winner: matchup.team1Score < matchup.team2Score }">
            <span class="team-name">{{ matchup.team1.name }}</span>
            <span class="team-score">{{ matchup.team1Score }}</span>
          </div>
          
          <div class="team-row" :class="{ winner: matchup.team2Score < matchup.team1Score }">
            <span class="team-name">{{ matchup.team2.name }}</span>
            <span class="team-score">{{ matchup.team2Score }}</span>
          </div>
        </div>
      </div>
    </div>

    <button 
      class="nav-button next" 
      @click="scrollRight"
      :disabled="scrollPosition >= maxScroll"
    >
      ›
    </button>
  </div>
</template>

<script>
import { ref, onMounted, computed } from 'vue';

export default {
  name: 'MatchupCarousel',
  
  props: {
    matchups: {
      type: Array,
      required: true
    }
  },

  setup(props) {
    const container = ref(null);
    const scrollPosition = ref(0);
    const maxScroll = ref(0);

    const sortedMatchups = computed(() => {
      return [...props.matchups].sort((a, b) => 
        new Date(a.race.date) - new Date(b.race.date)
      );
    });

    const formatDate = (dateString) => {
      return new Date(dateString).toLocaleDateString('en-US', {
        month: 'short',
        day: 'numeric',
        year: 'numeric'
      });
    };

    const updateMaxScroll = () => {
      if (container.value) {
        maxScroll.value = container.value.scrollWidth - container.value.clientWidth;
      }
    };

    const scrollLeft = () => {
      if (container.value) {
        const newPosition = Math.max(0, scrollPosition.value - 300);
        container.value.scrollTo({ left: newPosition, behavior: 'smooth' });
        scrollPosition.value = newPosition;
      }
    };

    const scrollRight = () => {
      if (container.value) {
        const newPosition = Math.min(maxScroll.value, scrollPosition.value + 300);
        container.value.scrollTo({ left: newPosition, behavior: 'smooth' });
        scrollPosition.value = newPosition;
      }
    };

    const handleScroll = () => {
      if (container.value) {
        scrollPosition.value = container.value.scrollLeft;
      }
    };

    onMounted(() => {
      updateMaxScroll();
      window.addEventListener('resize', updateMaxScroll);
      if (container.value) {
        container.value.addEventListener('scroll', handleScroll);
      }
    });

    return {
      container,
      scrollPosition,
      maxScroll,
      sortedMatchups,
      formatDate,
      scrollLeft,
      scrollRight
    };
  }
};
</script>

<style scoped>
.matchup-carousel {
  width: 100%;
  max-width: 100%;
  position: relative;
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-xs);
  overflow: hidden;
}

.carousel-container {
  width: 100%;
  max-width: calc(100% - 90px);
  display: flex;
  gap: var(--spacing-sm);
  overflow-x: auto;
  scroll-behavior: smooth;
  -webkit-overflow-scrolling: touch;
  scrollbar-width: none;
  -ms-overflow-style: none;
}

.carousel-container::-webkit-scrollbar {
  display: none;
}

.matchup-card {
  flex: 0 0 280px;
  max-width: 280px;
  min-width: 200px;
  background-color: var(--bg-tertiary);
  border-radius: var(--radius-md);
  padding: var(--spacing-sm);
  border: 1px solid var(--border-primary);
}

.race-info {
  margin-bottom: var(--spacing-xs);
}

.race-name {
  color: var(--text-primary);
  font-weight: 600;
  font-size: 0.875rem;
  letter-spacing: 0.5px;
}

.race-date {
  color: var(--text-secondary);
  font-size: 0.75rem;
  font-weight: 500;
}

.playoff-badge {
  background-color: var(--accent-secondary);
  color: var(--bg-primary);
  font-size: 0.75rem;
  font-weight: 600;
  padding: 2px 8px;
  border-radius: var(--radius-full);
}

.teams-info {
  margin-top: var(--spacing-xs);
}

.team-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-xs) var(--spacing-sm);
  border-radius: var(--radius-sm);
  background-color: var(--bg-secondary);
  transition: all 0.2s ease;
  border: 1px solid transparent;
}

.team-row.winner {
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

.nav-button {
  flex: 0 0 40px;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  border: 1px solid var(--border-secondary);
  background-color: var(--bg-secondary);
  color: var(--text-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
  z-index: 1;
}

.nav-button:hover:not(:disabled) {
  background-color: var(--bg-tertiary);
  border-color: var(--accent-primary);
  color: var(--accent-primary);
}

.nav-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  border-color: var(--border-primary);
}

@media (max-width: 768px) {
  .matchup-carousel {
    gap: var(--spacing-xs);
  }

  .carousel-container {
    max-width: calc(100% - 70px);
  }

  .matchup-card {
    flex: 0 0 240px;
    max-width: 240px;
    min-width: 180px;
  }

  .nav-button {
    flex: 0 0 30px;
    width: 30px;
    height: 30px;
  }
}

@media (max-width: 480px) {
  .carousel-container {
    max-width: calc(100% - 60px);
  }

  .matchup-card {
    flex: 0 0 200px;
    max-width: 200px;
    min-width: 160px;
    padding: var(--spacing-xs);
  }

  .nav-button {
    flex: 0 0 25px;
    width: 25px;
    height: 25px;
  }
}
</style>
