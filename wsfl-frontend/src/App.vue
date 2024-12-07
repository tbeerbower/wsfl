<template>
  <div class="app">
    <AppHeader />
    <main class="main-content">
      <router-view></router-view>
    </main>
  </div>
</template>

<script>
import AppHeader from '@/components/AppHeader.vue'

export default {
  name: 'App',
  components: {
    AppHeader
  }
}
</script>

<style>
@import './assets/styles/theme.css';

* {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}

:root {
  --color-background: #f3f4f6;
  --color-surface: #ffffff;
  --color-surface-hover: #f8f9fa;
  --color-border: #e5e7eb;
  --color-text: #1f2937;
  --color-text-secondary: #6b7280;
  --color-primary: #4CAF50;
  --color-primary-dark: #45a049;
  --color-error: #dc3545;
  --shadow-sm: 0 1px 2px rgba(0, 0, 0, 0.05);
  --shadow-md: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
}

html, body {
  margin: 0;
  padding: 0;
  background-color: var(--color-background);
  color: var(--color-text);
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  width: 100%;
  overflow-x: hidden;
}

#app {
  min-height: 100vh;
  width: 100%;
  max-width: 100vw;
  overflow-x: hidden;
  background-color: var(--color-background);
}

.container {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 var(--spacing-md);
}

/* Modern Button Styles */
.btn {
  padding: var(--spacing-xs) var(--spacing-md);
  border-radius: var(--radius-md);
  border: 1px solid var(--color-border);
  background-color: var(--color-surface);
  color: var(--color-text);
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn:hover {
  background-color: var(--color-surface-hover);
  border-color: var(--color-border);
}

/* Card Styles */
.card {
  background-color: var(--color-surface);
  border-radius: var(--radius-lg);
  border: 1px solid var(--color-border);
  box-shadow: var(--shadow-md);
}

.app {
  min-height: 100vh;
}

.main-content {
  padding-top: 72px; /* Header height + some spacing */
  min-height: calc(100vh - 72px);
}
</style>
