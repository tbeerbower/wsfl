<template>
  <div 
    class="draft-card"
    :class="{
      'draft-complete': status === 'COMPLETE',
      'draft-pending': status === 'PENDING',
      'my-team-turn': isMyTeamOnClock
    }"
  >
    <div class="card-header">
      <h3>{{ draft.name }}</h3>
      <div class="league-name">{{ draft.league.name }}</div>
    </div>
    <div class="card-content">
      <div class="status-item">
        <span class="label">Status:</span>
        <span class="value">{{ status }}</span>
      </div>
      <div class="status-item">
        <span class="label">Time:</span>
        <span class="value time">{{ formattedTime }}</span>
      </div>
      <div class="status-item">
        <span class="label">On Clock:</span>
        <span class="value on-clock-team">{{ currentTeamName }}</span>
      </div>
      <div class="status-item">
        <span class="label">Round:</span>
        <span class="value">{{ draft.currentRound || '-' }}</span>
      </div>
      <div class="status-item">
        <span class="label">Pick:</span>
        <span class="value">{{ draft.currentPick || '-' }}</span>
      </div>
    </div>
  </div>
</template>

<script>
import { defineComponent, computed, onMounted, ref, onBeforeUnmount } from 'vue'

export default defineComponent({
  name: 'DraftCard',
  props: {
    draft: {
      type: Object,
      required: true
    },
    isMyTeamOnClock: {
      type: Boolean,
      required: true
    }
  },
  setup(props) {
    console.log('DraftCard setup for draft:', props.draft.id, {
      draft: props.draft,
      isMyTeamOnClock: props.isMyTeamOnClock
    })

    const timeInterval = ref(null)
    const currentTime = ref(new Date())

    const updateTime = () => {
      currentTime.value = new Date()
    }

    onMounted(() => {
      console.log('DraftCard mounted for draft:', props.draft.id, {
        draft: props.draft,
        isMyTeamOnClock: props.isMyTeamOnClock
      })
      timeInterval.value = setInterval(updateTime, 1000)
    })

    onBeforeUnmount(() => {
      if (timeInterval.value) {
        clearInterval(timeInterval.value)
      }
    })

    const status = computed(() => {
      if (props.draft.complete) return 'COMPLETE'
      if (props.draft.started) return 'IN_PROGRESS'
      return 'PENDING'
    })

    const formattedTime = computed(() => {
      const startTime = new Date(props.draft.startTime)
      const now = currentTime.value
      const diff = startTime - now

      if (diff <= 0) {
        if (props.draft.complete) {
          return 'Complete'
        }
        // Show elapsed time since start
        const elapsedDiff = now - startTime
        const days = Math.floor(elapsedDiff / (1000 * 60 * 60 * 24))
        const hours = Math.floor((elapsedDiff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60))
        const minutes = Math.floor((elapsedDiff % (1000 * 60 * 60)) / (1000 * 60))
        const seconds = Math.floor((elapsedDiff % (1000 * 60)) / 1000)

        let timeStr = ''
        if (days > 0) timeStr += `${days}d `
        if (hours > 0 || days > 0) timeStr += `${hours}h `
        if (minutes > 0 || hours > 0 || days > 0) timeStr += `${minutes}m `
        timeStr += `${seconds}s`
        
        return `Started ${timeStr} ago`
      } else {
        // Show time until start
        const days = Math.floor(diff / (1000 * 60 * 60 * 24))
        const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60))
        const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60))
        const seconds = Math.floor((diff % (1000 * 60)) / 1000)

        let timeStr = ''
        if (days > 0) timeStr += `${days}d `
        if (hours > 0 || days > 0) timeStr += `${hours}h `
        if (minutes > 0 || hours > 0 || days > 0) timeStr += `${minutes}m `
        timeStr += `${seconds}s`
        
        return `Starts in ${timeStr}`
      }
    })

    const currentTeamName = computed(() => {
      console.log('Computing currentTeamName for draft:', props.draft.id, {
        currentTeamId: props.draft.currentTeamId,
        teams: props.draft.teams,
        draft: props.draft
      })

      if (!props.draft.currentTeamId) {
        console.log('No currentTeamId')
        return 'No team on clock'
      }
      if (!props.draft.teams) {
        console.log('No teams data available for draft:', props.draft.id)
        return `Team ${props.draft.currentTeamId}`
      }
      const currentTeam = props.draft.teams.find(team => team.id === props.draft.currentTeamId)
      if (!currentTeam) {
        console.log('Current team not found:', {
          currentTeamId: props.draft.currentTeamId,
          availableTeams: props.draft.teams.map(t => ({ id: t.id, name: t.name }))
        })
        return `Team ${props.draft.currentTeamId}`
      }
      console.log('Found current team:', currentTeam)
      return currentTeam.name
    })

    return {
      status,
      currentTeamName,
      formattedTime
    }
  }
})
</script>

<style scoped>
.draft-card {
  background-color: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  border: 1px solid #E2E8F0;
}

.draft-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}

.card-header {
  margin-bottom: 20px;
}

.card-header h3 {
  margin: 0;
  font-size: 1.25rem;
  font-weight: 600;
  color: #1A202C;
  line-height: 1.4;
}

.league-name {
  font-size: 0.875rem;
  color: #2D3748;
  margin-top: 4px;
  font-weight: 500;
}

.card-content {
  display: grid;
  gap: 16px;
}

.status-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #EDF2F7;
}

.status-item:last-child {
  border-bottom: none;
}

.status-item .label {
  color: #2D3748;
  font-size: 0.875rem;
  font-weight: 500;
}

.status-item .value {
  font-weight: 600;
  font-size: 0.875rem;
  color: #1A202C;
}

/* Draft status colors */
.draft-card.draft-complete {
  background-color: #F0FFF4;
  border-color: #9AE6B4;
}

.draft-card.draft-complete .status-item .value {
  color: #2F855A;
}

.draft-card.draft-pending {
  background-color: #FFFAF0;
  border-color: #FBD38D;
}

.draft-card.draft-pending .status-item .value {
  color: #C05621;
}

.draft-card.my-team-turn {
  background-color: #EBF8FF;
  border-color: #4299E1;
}

.draft-card.my-team-turn .status-item .value {
  color: #2B6CB0;
}

/* Team on clock styling */
.status-item .on-clock-team {
  font-weight: 700;
  color: #2C5282;
}

.draft-card.my-team-turn .on-clock-team {
  color: #2B6CB0;
  font-weight: 700;
  background-color: #BEE3F8;
  padding: 4px 8px;
  border-radius: 4px;
}

/* Time display */
.status-item .value.time {
  font-family: monospace;
  font-size: 0.9rem;
  letter-spacing: 0.5px;
}
</style>
