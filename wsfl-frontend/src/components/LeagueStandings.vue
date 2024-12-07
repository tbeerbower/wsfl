<template>
  <div class="standings-table">
    <table>
      <thead>
        <tr>
          <th>Team</th>
          <th class="numeric">W</th>
          <th class="numeric">L</th>
          <th class="numeric">T</th>
          <th class="numeric">PTS</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="standing in standings" :key="standing.team.id">
          <td>{{ standing.team.name }}</td>
          <td class="numeric">{{ standing.wins }}</td>
          <td class="numeric">{{ standing.losses }}</td>
          <td class="numeric">{{ standing.ties }}</td>
          <td class="numeric">{{ standing.totalScore }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
export default {
  name: 'LeagueStandings',
  props: {
    standings: {
      type: Array,
      required: true
    }
  }
};
</script>

<style scoped>
.standings-table {
  width: 100%;
  overflow-x: auto;
  background: var(--color-surface);
  border-radius: 8px;
  margin-top: 1rem;
}

table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0;
  font-size: 0.875rem;
}

th, td {
  padding: 0.75rem 1rem;
  text-align: left;
  border-bottom: 1px solid var(--color-border);
}

th {
  background-color: var(--color-surface);
  font-weight: 600;
  color: var(--color-text);
  position: sticky;
  top: 0;
}

td {
  color: var(--color-text-secondary);
}

.numeric {
  text-align: right;
}

tbody tr:hover {
  background-color: var(--color-surface-hover);
}

tbody tr:last-child td {
  border-bottom: none;
}

@media (max-width: 768px) {
  th, td {
    padding: 0.5rem;
  }
}
</style>
