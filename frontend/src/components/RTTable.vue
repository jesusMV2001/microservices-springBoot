<template>
  <div class="w-full" tabindex="0" @keydown.esc="cancelarRT">
    <h2 class="text-lg font-bold mb-4">{{ title }}</h2>
    <button @click="mostrarFormulario = true"
            class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded mb-4">Añadir Requisito
    </button>

    <div v-if="mostrarFormulario"
         class="fixed inset-0 flex items-center justify-center bg-gray-500 bg-opacity-75">
      <div class="bg-white rounded-lg p-8 w-1/2">
        <h3 class="text-lg font-bold mb-4">Nuevo Requisito Técnico</h3>
        <form @submit.prevent="crearRT" >
          <div class="mb-4">
            <label for="titulo" class="block text-gray-700 text-sm font-bold mb-2">titulo:</label>
            <input id="titulo" v-model="nuevoRT.titulo" required @keydown.esc="cancelarRT"
                      class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"/>
            <label for="descripcion" class="block text-gray-700 text-sm font-bold mb-2">descripcion:</label>
            <textarea id="descripcion" v-model="nuevoRT.descripcion" required
                      class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"></textarea>
          </div>
          <div class="flex justify-end">
            <button type="submit" class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline">Enviar</button>
            <button  @click="cancelarRT" class="bg-gray-500 hover:bg-gray-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline">Cancelar</button>
          </div>
        </form>
      </div>
    </div>

    <div class="overflow-x-auto">
      <table class="table-auto border-collapse w-full">
        <thead>
        <tr>
          <th v-for="(header, index) in headers" :key="index"
              class="px-4 py-2 bg-gray-200 text-gray-700 uppercase font-bold text-sm border-b">
            {{ header }}</th>
          <th class="px-4 py-2 bg-gray-200 text-gray-700 uppercase font-bold text-sm border-b">Acciones</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="(row, rowIndex) in data" :key="rowIndex"
            class="text-gray-700 border-b hover:bg-gray-100">
          <td v-for="(value, columnIndex) in row" :key="columnIndex"
              class="px-4 py-2 border">{{ value }}</td>
          <td class="px-4 py-2 border">
            <button @click="eliminarRT(rowIndex)"
                    class="bg-red-500 hover:bg-red-700 text-white font-bold py-2 px-4 rounded">Borrar</button>
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
      data: [],
      mostrarFormulario: false,
      nuevoRT: {
        titulo: '',
        descripcion: ''
      }
    };
  },
  mounted() {
    const id = this.$route.params.id;
    this.fetchData(`http://localhost:8080/api/implementacion/RequisitoFuncional/${id}/RequisitoTecnico`);
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
    },
    crearRT(){
      axios.post(`http://localhost:8080/api/implementacion/RequisitoFuncional/${this.$route.params.id}/RequisitoTecnico`, this.nuevoRT)
          .then(() => {
            this.mostrarFormulario=false
            const id = this.$route.params.id;
            this.fetchData(`http://localhost:8080/api/implementacion/RequisitoFuncional/${id}/RequisitoTecnico`)
          })
          .catch(error => {
            console.error('Error al enviar el comentario:', error);
          });
    },
    cancelarRT(){
      this.mostrarFormulario=false
    },
    eliminarRT(rowIndex){
      const id = this.$route.params.id;
      axios.delete(`http://localhost:8080/api/implementacion/RequisitoFuncional/${id}/RequisitoTecnico/${this.data.at(rowIndex).id}`)
          .then(() => {
            this.fetchData(`http://localhost:8080/api/implementacion/RequisitoFuncional/${id}/RequisitoTecnico`)
          })
          .catch(error => {
            console.error('Error al enviar el comentario:', error);
          });
    }
  }
};
</script>

<style scoped>
</style>
