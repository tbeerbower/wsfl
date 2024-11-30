<template>
  <div class="add-draft-view">
    <header class="view-header">
      <h1>Add New Draft</h1>
    </header>

    <div class="form-container">
      <div v-if="error" class="error-message">{{ error }}</div>
      
      <form @submit.prevent="handleSubmit" class="draft-form">
        <div class="form-group">
          <label for="name">Draft Name</label>
          <input 
            id="name"
            v-model="formData.name"
            type="text"
            required
            class="form-input"
            :class="{ 'error': validationErrors.name }"
          >
          <span v-if="validationErrors.name" class="validation-error">
            {{ validationErrors.name }}
          </span>
        </div>

        <div class="form-group">
          <label for="season">Season</label>
          <select 
            id="season"
            v-model="formData.seasonId"
            required
            class="form-input"
            :class="{ 'error': validationErrors.seasonId }"
          >
            <option value="">Select a Season</option>
            <option v-for="season in seasons" :key="season.id" :value="season.id">
              {{ season.name }}
            </option>
          </select>
          <span v-if="validationErrors.seasonId" class="validation-error">
            {{ validationErrors.seasonId }}
          </span>
        </div>

        <div class="form-group">
          <label for="numberOfRounds">Number of Rounds</label>
          <input 
            id="numberOfRounds"
            v-model.number="formData.numberOfRounds"
            type="number"
            required
            min="1"
            class="form-input"
            :class="{ 'error': validationErrors.numberOfRounds }"
          >
          <span v-if="validationErrors.numberOfRounds" class="validation-error">
            {{ validationErrors.numberOfRounds }}
          </span>
        </div>

        <div class="form-group">
          <label for="startTime">Start Time</label>
          <input 
            id="startTime"
            v-model="formData.startTime"
            type="datetime-local"
            required
            class="form-input"
            :class="{ 'error': validationErrors.startTime }"
          >
          <span v-if="validationErrors.startTime" class="validation-error">
            {{ validationErrors.startTime }}
          </span>
        </div>

        <div class="form-group checkbox-group">
          <label class="checkbox-label">
            <input 
              type="checkbox"
              v-model="formData.snakeOrder"
            >
            Snake Draft Order
          </label>
        </div>

        <div class="form-actions">
          <button 
            type="button" 
            @click="$router.go(-1)" 
            class="cancel-button"
          >
            Cancel
          </button>
          <button 
            type="submit" 
            class="submit-button"
            :disabled="loading"
          >
            {{ loading ? 'Creating...' : 'Create Draft' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
import { defineComponent, ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'

export default defineComponent({
  name: 'AddDraftView',
  
  setup() {
    const route = useRoute()
    const router = useRouter()
    const leagueId = computed(() => route.params.leagueId)

    const formData = ref({
      name: '',
      seasonId: '',
      numberOfRounds: 6,
      startTime: '',
      snakeOrder: true
    })

    const loading = ref(false)
    const error = ref(null)
    const validationErrors = ref({})
    const seasons = ref([])

    const fetchSeasons = async () => {
      try {
        const response = await axios.get('/api/seasons')
        seasons.value = response.data.content || []
        
        // If there's only one season, auto-select it
        if (seasons.value.length === 1) {
          formData.value.seasonId = seasons.value[0].id
        }
      } catch (err) {
        error.value = 'Failed to load seasons'
        console.error('Error fetching seasons:', err)
      }
    }

    const validateForm = () => {
      const errors = {}
      
      if (!formData.value.name.trim()) {
        errors.name = 'Draft name is required'
      }
      
      if (!formData.value.seasonId) {
        errors.seasonId = 'Please select a season'
      }
      
      if (!formData.value.numberOfRounds || formData.value.numberOfRounds < 1) {
        errors.numberOfRounds = 'Number of rounds must be at least 1'
      }
      
      if (!formData.value.startTime) {
        errors.startTime = 'Start time is required'
      } else {
        const startDate = new Date(formData.value.startTime)
        if (startDate < new Date()) {
          errors.startTime = 'Start time must be in the future'
        }
      }

      validationErrors.value = errors
      return Object.keys(errors).length === 0
    }

    const handleSubmit = async () => {
      if (!validateForm()) return

      loading.value = true
      error.value = null

      try {
        const payload = {
          ...formData.value,
          leagueId: leagueId.value
        }

        await axios.post('/api/drafts', payload)
        router.push(`/leagues/${leagueId.value}`)
      } catch (err) {
        error.value = err.response?.data?.message || 'Failed to create draft'
        console.error('Error creating draft:', err)
      } finally {
        loading.value = false
      }
    }

    onMounted(fetchSeasons)

    return {
      formData,
      loading,
      error,
      validationErrors,
      handleSubmit,
      seasons
    }
  }
})
</script>

<style scoped>
.add-draft-view {
  max-width: 600px;
  margin: 0 auto;
  padding: 2rem;
}

.view-header {
  margin-bottom: 2rem;
}

.view-header h1 {
  font-size: 1.5rem;
  color: #1e293b;
  margin: 0;
}

.form-container {
  background-color: white;
  border-radius: 8px;
  padding: 2rem;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.draft-form {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.form-group label {
  font-size: 0.875rem;
  font-weight: 500;
  color: #475569;
}

.form-input {
  padding: 0.5rem;
  border: 1px solid #cbd5e1;
  border-radius: 4px;
  font-size: 1rem;
  transition: border-color 0.2s;
}

.form-input:focus {
  outline: none;
  border-color: #3182ce;
}

.form-input.error {
  border-color: #dc2626;
}

.checkbox-group {
  flex-direction: row;
  align-items: center;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  cursor: pointer;
}

.validation-error {
  font-size: 0.75rem;
  color: #dc2626;
}

.error-message {
  padding: 1rem;
  background-color: #fee2e2;
  border: 1px solid #fecaca;
  border-radius: 4px;
  color: #dc2626;
  margin-bottom: 1rem;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  margin-top: 1rem;
}

.submit-button,
.cancel-button {
  padding: 0.5rem 1rem;
  border-radius: 4px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.submit-button {
  background-color: #3182ce;
  color: white;
  border: none;
}

.submit-button:hover:not(:disabled) {
  background-color: #2c5282;
}

.submit-button:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.cancel-button {
  background-color: #e2e8f0;
  color: #475569;
  border: none;
}

.cancel-button:hover {
  background-color: #cbd5e1;
}
</style>
