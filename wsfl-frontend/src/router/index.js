import { createRouter, createWebHistory } from 'vue-router'
import store from '../store'
import DashboardView from '../views/DashboardView.vue'
import LoginView from '../views/LoginView.vue'
import RegisterView from '../views/RegisterView.vue'
import AddTeamView from '../views/AddTeamView.vue'
import AddLeagueView from '../views/AddLeagueView.vue'

const routes = [
  {
    path: '/login',
    name: 'login',
    component: LoginView,
    meta: { requiresGuest: true }
  },
  {
    path: '/register',
    name: 'register',
    component: RegisterView,
    meta: { requiresGuest: true }
  },
  {
    path: '/',
    redirect: '/dashboard'
  },
  {
    path: '/dashboard',
    name: 'dashboard',
    component: DashboardView,
    meta: { requiresAuth: true }
  },
  {
    path: '/add-team',
    name: 'add-team',
    component: AddTeamView,
    meta: { requiresAuth: true }
  },
  {
    path: '/add-league',
    name: 'add-league',
    component: AddLeagueView,
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

// Navigation guard
router.beforeEach((to, from, next) => {
  const isAuthenticated = store.getters['auth/isAuthenticated']

  if (to.meta.requiresAuth && !isAuthenticated) {
    next('/login')
  } else if (to.meta.requiresGuest && isAuthenticated) {
    next('/dashboard')
  } else {
    next()
  }
})

export default router
