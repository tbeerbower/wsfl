import axios from 'axios';

const state = {
  leagues: [],
  loading: false,
  error: null
};

const getters = {
  getUserLeagues: (state) => state.leagues,
  getLeagueById: (state) => (id) => state.leagues.find(league => league.id === id),
  isLoading: (state) => state.loading,
  getError: (state) => state.error
};

const actions = {
  async fetchUserLeagues({ commit, rootGetters }) {
    commit('setLoading', true);
    commit('setError', null);
    try {
      const userId = rootGetters['auth/currentUser'].id;
      const response = await axios.get(`/api/users/${userId}/leagues`);
      commit('setLeagues', response.data.content);
    } catch (error) {
      commit('setError', error.response?.data?.message || 'Failed to fetch leagues');
    } finally {
      commit('setLoading', false);
    }
  }
};

const mutations = {
  setLeagues(state, leagues) {
    state.leagues = leagues;
  },
  setLoading(state, loading) {
    state.loading = loading;
  },
  setError(state, error) {
    state.error = error;
  }
};

export default {
  namespaced: true,
  state,
  getters,
  actions,
  mutations
};
