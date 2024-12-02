import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'
import RegisterView from '../views/RegisterView.vue'
import DashboardView from '../views/DashboardView.vue'
import AddTeamView from '../views/AddTeamView.vue'
import AddLeagueView from '../views/AddLeagueView.vue'
import LeagueAdminView from '../views/LeagueAdminView.vue'
import AddDraftView from '../views/AddDraftView.vue'
import DraftAdminView from '../views/DraftAdminView.vue'
import DraftPickView from '../views/DraftPickView.vue'
import DraftsView from '../views/DraftsView.vue'

const routes = [
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
    path: '/login',
    name: 'login',
    component: LoginView
  },
  {
    path: '/register',
    name: 'register',
    component: RegisterView
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
  },
  {
    path: '/leagues/:id',
    name: 'league-admin',
    component: LeagueAdminView,
    meta: { requiresAuth: true }
  },
  {
    path: '/leagues/:leagueId/add-draft',
    name: 'add-draft',
    component: AddDraftView,
    meta: { requiresAuth: true }
  },
  {
    path: '/drafts',
    name: 'drafts',
    component: DraftsView,
    meta: { requiresAuth: true }
  },
  {
    path: '/drafts/:id',
    name: 'draft-admin',
    component: DraftAdminView,
    meta: { requiresAuth: true }
  },
  {
    path: '/drafts/:id/pick',
    name: 'draft-pick',
    component: DraftPickView,
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

// Navigation guard
router.beforeEach((to, from, next) => {
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)
  const isAuthenticated = localStorage.getItem('token')

  if (requiresAuth && !isAuthenticated) {
    next('/login')
  } else {
    next()
  }
})

export default router
