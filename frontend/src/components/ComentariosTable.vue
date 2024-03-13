<template>
  <div>
    <h2 class="text-2xl font-bold mb-4">{{ title }}</h2>
    <div class="overflow-x-auto">
      <table class="table-auto border-collapse w-full">
        <thead>
        <tr>
          <th v-for="(header, index) in headers" :key="index"
              class="px-4 py-2 bg-gray-200 text-gray-700 uppercase font-bold text-sm border-b">
            {{ header }}</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="(row, rowIndex) in data" :key="rowIndex" class="text-gray-700 border-b hover:bg-gray-100">
          <td v-for="(value, columnIndex) in row" :key="columnIndex" class="px-4 py-2 border">
            {{value}}
          </td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  props: {
    title: {
      type: String,
      required: true
    }
  },
  data() {
    return {
      headers: [],
      data: []
    };
  },
  mounted() {
    const id = this.$route.params.id;
    this.fetchData(`http://localhost:8080/api/implementacion/RequisitoFuncional/${id}/Comentario`);
  },
  methods: {
    fetchData(url) {
      axios.get(url)
          .then(response => {
            //filtrar los atributos que son listas
            this.data = response.data.map(row => {
              const filteredRow = {};
              Object.entries(row).forEach(([key, value]) => {
                if (!Array.isArray(value)) {
                  filteredRow[key] = value;
                }
              });
              return filteredRow;
            });
            this.headers = Object.keys(this.data[0]);
          })
          .catch(error => {
            console.error('Error fetching data:', error);
          });
    }
  }
};
</script>

<style scoped>
</style>
