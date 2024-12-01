import axios from 'axios'

const getUserFromStorage = () => {
  try {
    const userData = localStorage.getItem('user')
    return userData ? JSON.parse(userData) : null
  } catch (error) {
    console.error('Error parsing user data:', error)
    return null
  }
}

const state = {
  token: localStorage.getItem('token') || null,
  user: getUserFromStorage()
}

const getters = {
  isAuthenticated: state => !!state.token,
  currentUser: state => state.user
}

const actions = {
  async login({ commit }, credentials) {
    const response = await axios.post('/api/auth/login', credentials)
    const { token, user } = response.data
    
    localStorage.setItem('token', token)
    localStorage.setItem('user', JSON.stringify(user))
    
    axios.defaults.headers.common['Authorization'] = `Bearer ${token}`
    
    commit('SET_AUTH', { token, user })
    return true
  },

  async register(_, userData) {
    const response = await axios.post('/api/auth/register', userData)
    return response.data
  },

  logout({ commit }) {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    delete axios.defaults.headers.common['Authorization']
    commit('CLEAR_AUTH')
  }
}

const mutations = {
  SET_AUTH(state, { token, user }) {
    state.token = token
    state.user = user
  },
  CLEAR_AUTH(state) {
    state.token = null
    state.user = null
  }
}

export default {
  namespaced: true,
  state,
  getters,
  actions,
  mutations
}
