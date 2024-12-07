<template>
  <header class="header">
    <div class="header-content">
      <img src="@/assets/wsfl-header-logo-2.jpg" alt="WSFL Logo" class="logo">
      <div v-if="isAuthenticated" class="user-section">
        <span class="user-name">{{ currentUser?.name }}</span>
        <button @click="handleLogout" class="logout-button">Logout</button>
      </div>
    </div>
  </header>
</template>

<script>
import { computed } from 'vue';
import { useStore } from 'vuex';
import { useRouter } from 'vue-router';

export default {
  name: 'AppHeader',
  setup() {
    const store = useStore();
    const router = useRouter();

    const isAuthenticated = computed(() => store.getters['auth/isAuthenticated']);
    const currentUser = computed(() => store.getters['auth/currentUser']);

    const handleLogout = async () => {
      await store.dispatch('auth/logout');
      router.push('/login');
    };

    return {
      isAuthenticated,
      currentUser,
      handleLogout,
    };
  },
};
</script>

<style scoped>
.header {
  background-color: #ffffff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  padding: 1rem;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo {
  height: 40px;
  object-fit: contain;
}

.user-section {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.user-name {
  font-weight: 600;
  color: #333;
}

.logout-button {
  background-color: #dc3545;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  cursor: pointer;
  font-weight: 500;
  transition: background-color 0.2s;
}

.logout-button:hover {
  background-color: #c82333;
}
</style>
