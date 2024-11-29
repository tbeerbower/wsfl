<template>
  <div class="add-league">
    <div class="form-container">
      <h1>Add New League</h1>
      <form @submit.prevent="handleSubmit" class="add-league-form">
        <div class="form-group">
          <label for="name">League Name</label>
          <input
            type="text"
            id="name"
            v-model="league.name"
            :class="{ 'error': validation.name }"
            placeholder="Enter league name"
          >
          <span class="error-message" v-if="validation.name">{{ validation.name }}</span>
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

    const league = ref({
      name: '',
    })

    const validation = ref({
      name: '',
    })

    const loading = ref(false)
    const error = ref(null)

    const validateForm = () => {
      let isValid = true
      validation.value.name = ''

      if (!league.value.name.trim()) {
        validation.value.name = 'League name is required'
        isValid = false
      }

      return isValid
    }

    const handleSubmit = async () => {
      if (!validateForm()) return

      loading.value = true
      error.value = ''

      try {
        await axios.post('/api/leagues', {
          name: league.value.name.trim(),
          adminId: user.id
        })
        router.push('/dashboard')
      } catch (err) {
        error.value = 'Failed to create league'
        console.error('Error creating league:', err)
      } finally {
        loading.value = false
      }
    }

    return {
      league,
      loading,
      error,
      validation,
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
