import { createStore } from 'vuex';
import auth from './modules/auth';
import leagues from './modules/leagues';

export default createStore({
  modules: {
    auth,
    leagues
  }
});
