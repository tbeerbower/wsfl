<template>
  <div class="app">
    <app-header v-if="showHeader" />
    <main class="main-content">
      <router-view />
    </main>
  </div>
</template>

<script>
import { defineComponent, computed } from 'vue'
import { useRoute } from 'vue-router'
import AppHeader from './components/AppHeader.vue'

export default defineComponent({
  name: 'App',
  components: {
    AppHeader
  },
  setup() {
    const route = useRoute()
    
    // Don't show header on login and register pages
    const showHeader = computed(() => {
      return !['login', 'register'].includes(route.name)
    })

    return {
      showHeader
    }
  }
})
</script>

<style>
* {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen,
    Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
  background-color: #f8fafc;
}

.app {
  min-height: 100vh;
}

.main-content {
  padding-top: 4rem; /* Space for fixed header */
  min-height: calc(100vh - 4rem);
}

button {
  cursor: pointer;
}

button:disabled {
  cursor: not-allowed;
  opacity: 0.7;
}

.error-message {
  color: #dc2626;
  font-size: 0.875rem;
  margin-top: 0.25rem;
}
</style>
