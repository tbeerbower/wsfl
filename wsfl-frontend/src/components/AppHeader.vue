<template>
  <header class="app-header">
    <div class="header-container">
      <div class="header-left">
        <h1 class="app-title">Winter Series Fantasy League</h1>
      </div>
      <nav class="main-nav">
        <router-link to="/dashboard" class="nav-link">Dashboard</router-link>
        <router-link to="/drafts" class="nav-link">Drafts</router-link>
      </nav>
      <div class="header-right">
        <div v-if="isAuthenticated" class="user-section">
          <span class="username">{{ currentUser?.name }}</span>
          <button @click="handleLogout" class="logout-button">
            Logout
          </button>
        </div>
      </div>
    </div>
  </header>
</template>

<script>
import { defineComponent, computed } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'

export default defineComponent({
  name: 'AppHeader',
  setup() {
    const store = useStore()
    const router = useRouter()

    const isAuthenticated = computed(() => store.getters['auth/isAuthenticated'])
    const currentUser = computed(() => store.getters['auth/currentUser'])

    const handleLogout = async () => {
      await store.dispatch('auth/logout')
      router.push('/login')
    }

    return {
      isAuthenticated,
      currentUser,
      handleLogout
    }
  }
})
</script>

<style scoped>
.app-header {
  background-color: #1a237e;
  color: white;
  padding: 1rem 0;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
}

.header-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 1rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  flex: 1;
}

.app-title {
  margin: 0;
  font-size: 1.5rem;
  font-weight: 600;
  color: white;
}

.main-nav {
  flex: 1;
  display: flex;
  justify-content: center;
}

.nav-link {
  color: white;
  text-decoration: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  transition: background-color 0.2s;
}

.nav-link:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.nav-link.router-link-active {
  background-color: rgba(255, 255, 255, 0.2);
}

.header-right {
  flex: 1;
  display: flex;
  justify-content: flex-end;
}

.user-section {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.username {
  color: white;
  font-size: 0.9rem;
}

.logout-button {
  padding: 0.5rem 1rem;
  background-color: transparent;
  border: 1px solid rgba(255, 255, 255, 0.5);
  color: white;
  border-radius: 4px;
  font-size: 0.9rem;
  transition: all 0.2s;
}

.logout-button:hover {
  background-color: rgba(255, 255, 255, 0.1);
  border-color: white;
}
</style>
