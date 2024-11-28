<template>
  <div class="add-team">
    <div class="form-container">
      <h1>Add New Team</h1>
      <form @submit.prevent="handleSubmit" class="add-team-form">
        <div class="form-group">
          <label for="teamName">Team Name</label>
          <input
            id="teamName"
            v-model="teamName"
            type="text"
            required
            placeholder="Enter team name"
            :class="{ 'error': errors.teamName }"
          >
          <span v-if="errors.teamName" class="error-message">{{ errors.teamName }}</span>
        </div>

        <div class="form-group">
          <label for="league">League</label>
          <select
            id="league"
            v-model="selectedLeagueId"
            required
            :class="{ 'error': errors.league }"
          >
            <option value="">Select a league</option>
            <option v-for="league in leagues" :key="league.id" :value="league.id">
              {{ league.name }}
            </option>
          </select>
          <span v-if="errors.league" class="error-message">{{ errors.league }}</span>
        </div>

        <div class="form-actions">
          <button type="button" class="secondary" @click="$router.push('/dashboard')">Cancel</button>
          <button type="submit" :disabled="loading">
            {{ loading ? 'Creating...' : 'Create Team' }}
          </button>
        </div>

        <div v-if="error" class="error-message">{{ error }}</div>
      </form>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import axios from 'axios'

export default {
  name: 'AddTeamView',
  setup() {
    const router = useRouter()
    const store = useStore()
    const user = store.getters['auth/currentUser']

    const teamName = ref('')
    const selectedLeagueId = ref('')
    const leagues = ref([])
    const loading = ref(false)
    const error = ref(null)
    const errors = ref({
      teamName: '',
      league: ''
    })

    const fetchLeagues = async () => {
      try {
        const response = await axios.get('/api/leagues')
        leagues.value = response.data.content || []
      } catch (err) {
        error.value = 'Failed to load leagues'
        console.error('Error fetching leagues:', err)
      }
    }

    const validateForm = () => {
      errors.value = {
        teamName: '',
        league: ''
      }

      if (!teamName.value.trim()) {
        errors.value.teamName = 'Team name is required'
      }
      if (!selectedLeagueId.value) {
        errors.value.league = 'Please select a league'
      }

      return !errors.value.teamName && !errors.value.league
    }

    const handleSubmit = async () => {
      if (!validateForm()) return

      loading.value = true
      error.value = null

      try {
        await axios.post('/api/teams', {
          name: teamName.value.trim(),
          leagueId: selectedLeagueId.value,
          ownerId: user.id
        })

        router.push('/dashboard')
      } catch (err) {
        error.value = err.response?.data?.message || 'Failed to create team'
        console.error('Error creating team:', err)
      } finally {
        loading.value = false
      }
    }

    onMounted(fetchLeagues)

    return {
      teamName,
      selectedLeagueId,
      leagues,
      loading,
      error,
      errors,
      handleSubmit
    }
  }
}
</script>

<style scoped>
.add-team {
  padding: 2rem;
  display: flex;
  justify-content: center;
}

.form-container {
  max-width: 500px;
  width: 100%;
  background-color: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

h1 {
  margin: 0 0 2rem 0;
  color: #2c3e50;
  font-size: 1.5rem;
}

.form-group {
  margin-bottom: 1.5rem;
}

label {
  display: block;
  margin-bottom: 0.5rem;
  color: #4a5568;
  font-weight: 500;
}

input, select {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #e2e8f0;
  border-radius: 4px;
  font-size: 1rem;
  transition: border-color 0.2s;
}

input:focus, select:focus {
  outline: none;
  border-color: #3182ce;
}

input.error, select.error {
  border-color: #e53e3e;
}

.error-message {
  color: #e53e3e;
  font-size: 0.875rem;
  margin-top: 0.5rem;
}

.form-actions {
  display: flex;
  gap: 1rem;
  margin-top: 2rem;
}

button {
  flex: 1;
  padding: 0.75rem;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s;
}

button[type="submit"] {
  background-color: #3182ce;
  color: white;
}

button[type="submit"]:hover {
  background-color: #2c5282;
}

button[type="submit"]:disabled {
  background-color: #a0aec0;
  cursor: not-allowed;
}

button.secondary {
  background-color: #e2e8f0;
  color: #4a5568;
}

button.secondary:hover {
  background-color: #cbd5e0;
}
</style>
