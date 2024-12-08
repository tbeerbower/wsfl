<template>
  <div class="draft-pick-view">
    <div class="draft-pick-container">
      <div class="header">
        <router-link :to="{ name: 'draft', params: { draftId } }" class="back-link">
          ← Back to Draft
        </router-link>
        <h1>Make Your Pick</h1>
      </div>

      <div class="runners-list">
        <div v-if="loading" class="loading">Loading available runners...</div>
        <div v-else-if="error" class="error">{{ error }}</div>
        <template v-else>
          <table class="runners-table">
            <thead>
              <tr>
                <th @click="sort('name')" :class="{ active: sortField === 'name' }">
                  Name
                  <span class="sort-indicator">{{ getSortIndicator('name') }}</span>
                </th>
                <th @click="sort('gender')" :class="{ active: sortField === 'gender' }">
                  Gender
                  <span class="sort-indicator">{{ getSortIndicator('gender') }}</span>
                </th>
                <th @click="sort('averageOverallPlace')" :class="{ active: sortField === 'averageOverallPlace' }">
                  Avg Overall
                  <span class="sort-indicator">{{ getSortIndicator('averageOverallPlace') }}</span>
                </th>
                <th @click="sort('averageGenderPlace')" :class="{ active: sortField === 'averageGenderPlace' }">
                  Avg Gender
                  <span class="sort-indicator">{{ getSortIndicator('averageGenderPlace') }}</span>
                </th>
                <th @click="sort('totalRaces')" :class="{ active: sortField === 'totalRaces' }">
                  Races
                  <span class="sort-indicator">{{ getSortIndicator('totalRaces') }}</span>
                </th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="runner in sortedRunners" :key="runner.id">
                <td>{{ runner.name }}</td>
                <td>{{ runner.gender }}</td>
                <td>{{ Math.round(runner.averageOverallPlace) }}</td>
                <td>{{ Math.round(runner.averageGenderPlace) }}</td>
                <td>{{ runner.totalRaces }}</td>
                <td>
                  <button @click="selectRunner(runner)" class="pick-button">
                    Pick
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </template>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';

const route = useRoute();
const router = useRouter();
const draftId = route.params.draftId;

const runners = ref([]);
const loading = ref(true);
const error = ref('');
const sortField = ref('averageOverallPlace');
const sortDirection = ref('asc');

const fetchRunners = async () => {
  loading.value = true;
  error.value = '';
  try {
    const response = await axios.get(`/api/drafts/${draftId}/runners`, {
      params: {
        status: 'available',
        size: 9999
      }
    });
    runners.value = response.data.content;
  } catch (err) {
    error.value = 'Failed to load available runners. Please try again.';
    console.error('Error fetching runners:', err);
  } finally {
    loading.value = false;
  }
};

const sort = (field) => {
  if (sortField.value === field) {
    sortDirection.value = sortDirection.value === 'asc' ? 'desc' : 'asc';
  } else {
    sortField.value = field;
    sortDirection.value = 'asc';
  }
};

const getSortIndicator = (field) => {
  if (sortField.value !== field) return '↕';
  return sortDirection.value === 'asc' ? '↑' : '↓';
};

const sortedRunners = computed(() => {
  return [...runners.value].sort((a, b) => {
    let aValue = a[sortField.value];
    let bValue = b[sortField.value];
    
    // Handle numeric fields
    if (['averageOverallPlace', 'averageGenderPlace', 'totalRaces'].includes(sortField.value)) {
      aValue = Number(aValue);
      bValue = Number(bValue);
    }
    
    if (sortDirection.value === 'asc') {
      return aValue > bValue ? 1 : -1;
    } else {
      return aValue < bValue ? 1 : -1;
    }
  });
});

const selectRunner = async (runner) => {
  loading.value = true;
  try {
    await axios.post(`/api/drafts/${draftId}/picks`, {
      runnerId: runner.id
    });
    router.push({ name: 'draft', params: { draftId } });
  } catch (err) {
    error.value = 'Failed to make pick. Please try again.';
    console.error('Error making pick:', err);
    loading.value = false;
  }
};

onMounted(() => {
  fetchRunners();
});
</script>

<style scoped>
.draft-pick-view {
  min-height: 100vh;
  padding: var(--spacing-lg);
  background-color: var(--bg-primary);
}

.draft-pick-container {
  max-width: 1200px;
  margin: 0 auto;
}

.header {
  margin-bottom: var(--spacing-xl);
}

.back-link {
  display: inline-block;
  color: var(--primary);
  text-decoration: none;
  margin-bottom: var(--spacing-md);
  font-weight: 500;
}

.back-link:hover {
  text-decoration: underline;
}

h1 {
  color: var(--text-primary);
  font-size: 1.875rem;
  font-weight: 700;
  margin: 0;
}

.runners-table {
  width: 100%;
  border-collapse: collapse;
  background-color: var(--bg-secondary);
  border-radius: var(--radius-md);
  overflow: hidden;
}

.runners-table th,
.runners-table td {
  padding: var(--spacing-md);
  text-align: left;
  border-bottom: 1px solid var(--border-primary);
  color: #e4e4e7;
}

.runners-table th {
  background-color: var(--bg-tertiary);
  font-weight: 600;
  color: var(--text-primary);
  cursor: pointer;
  user-select: none;
  white-space: nowrap;
}

.runners-table th.active {
  color: var(--primary);
}

.runners-table th:hover {
  background-color: var(--border-primary);
}

.sort-indicator {
  display: inline-block;
  margin-left: var(--spacing-xs);
  color: var(--text-secondary);
}

.runners-table tbody tr:hover {
  background-color: var(--bg-tertiary);
}

.runners-table tbody tr:hover td {
  color: #f4f4f5;
}

.runners-table td:nth-child(2) {
  color: #a1a1aa;  /* slightly muted color for gender */
}

.runners-table td:nth-child(3),
.runners-table td:nth-child(4),
.runners-table td:nth-child(5) {
  color: #d4d4d8;  /* brighter color for numeric values */
}

.pick-button {
  background-color: var(--primary);
  color: white;
  border: none;
  padding: var(--spacing-xs) var(--spacing-sm);
  border-radius: var(--radius-sm);
  cursor: pointer;
  font-weight: 500;
  transition: all 0.2s ease;
}

.pick-button:hover {
  background-color: var(--primary-dark);
}

.loading,
.error {
  text-align: center;
  padding: var(--spacing-xl);
  color: var(--text-secondary);
}

.error {
  color: var(--error);
}
</style>
