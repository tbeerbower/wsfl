import axios from 'axios';

const state = {
  token: localStorage.getItem('token') || '',
  user: JSON.parse(localStorage.getItem('user')) || null,
};

const getters = {
  isAuthenticated: state => !!state.token,
  currentUser: state => state.user,
};

const actions = {
  async register({ commit }, userData) {
    try {
      const response = await axios.post('/api/auth/register', userData);
      const { token, user } = response.data;
      
      // Store token in axios defaults for future requests
      axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
      
      // Store in localStorage
      localStorage.setItem('token', token);
      localStorage.setItem('user', JSON.stringify(user));
      
      commit('SET_AUTH_SUCCESS', { token, user });
      return response;
    } catch (error) {
      commit('SET_AUTH_ERROR');
      throw error;
    }
  },

  async login({ commit }, credentials) {
    try {
      const response = await axios.post('/api/auth/login', credentials);
      const { token, user } = response.data;
      
      // Store token in axios defaults for future requests
      axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
      
      // Store in localStorage
      localStorage.setItem('token', token);
      localStorage.setItem('user', JSON.stringify(user));
      
      commit('SET_AUTH_SUCCESS', { token, user });
      return response;
    } catch (error) {
      commit('SET_AUTH_ERROR');
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      throw error;
    }
  },

  logout({ commit }) {
    commit('SET_LOGOUT');
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    delete axios.defaults.headers.common['Authorization'];
  },
};

const mutations = {
  SET_AUTH_SUCCESS(state, { token, user }) {
    state.token = token;
    state.user = user;
  },
  SET_AUTH_ERROR(state) {
    state.token = '';
    state.user = null;
  },
  SET_LOGOUT(state) {
    state.token = '';
    state.user = null;
  },
};

export default {
  namespaced: true,
  state,
  getters,
  actions,
  mutations,
};
