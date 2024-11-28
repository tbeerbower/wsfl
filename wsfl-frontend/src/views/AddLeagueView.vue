<template>
  <div class="add-league">
    <div class="form-container">
      <h1>Add New League</h1>
      <form @submit.prevent="handleSubmit" class="add-league-form">
        <div class="form-group">
          <label for="leagueName">League Name</label>
          <input
            id="leagueName"
            v-model="leagueName"
            type="text"
            required
            placeholder="Enter league name"
            :class="{ 'error': errors.leagueName }"
          >
          <span v-if="errors.leagueName" class="error-message">{{ errors.leagueName }}</span>
        </div>

        <div class="form-group">
          <label for="season">Season Year</label>
          <input
            id="season"
            v-model.number="season"
            type="number"
            required
            min="2024"
            max="2100"
            :class="{ 'error': errors.season }"
          >
          <span v-if="errors.season" class="error-message">{{ errors.season }}</span>
        </div>

        <div class="form-actions">
          <button type="button" class="secondary" @click="$router.push('/dashboard')">Cancel</button>
          <button type="submit" :disabled="loading">
            {{ loading ? 'Creating...' : 'Create League' }}
          </button>
        </div>

        <div v-if="error" class="error-message">{{ error }}</div>
      </form>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import axios from 'axios'

export default {
  name: 'AddLeagueView',
  setup() {
    const router = useRouter()
    const store = useStore()
    const user = store.getters['auth/currentUser']

    const leagueName = ref('')
    const season = ref(new Date().getFullYear())
    const loading = ref(false)
    const error = ref(null)
    const errors = ref({
      leagueName: '',
      season: ''
    })

    const validateForm = () => {
      errors.value = {
        leagueName: '',
        season: ''
      }

      if (!leagueName.value.trim()) {
        errors.value.leagueName = 'League name is required'
      }
      
      const seasonNum = Number(season.value)
      if (!season.value || seasonNum < 2024 || seasonNum > 2100) {
        errors.value.season = 'Please enter a valid year (2024-2100)'
      }

      return !errors.value.leagueName && !errors.value.season
    }

    const handleSubmit = async () => {
      if (!validateForm()) return

      loading.value = true
      error.value = null

      try {
        await axios.post('/api/leagues', {
          name: leagueName.value.trim(),
          season: Number(season.value),
          adminId: user.id
        })

        router.push('/dashboard')
      } catch (err) {
        error.value = err.response?.data?.message || 'Failed to create league'
        console.error('Error creating league:', err)
      } finally {
        loading.value = false
      }
    }

    return {
      leagueName,
      season,
      loading,
      error,
      errors,
      handleSubmit
    }
  }
}
</script>

<style scoped>
.add-league {
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

input {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #e2e8f0;
  border-radius: 4px;
  font-size: 1rem;
  transition: border-color 0.2s;
}

input:focus {
  outline: none;
  border-color: #3182ce;
}

input.error {
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
