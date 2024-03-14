<template>
  <div class="w-full" tabindex="0" @keydown.esc="cancelarComentario">
    <h2 class="text-2xl font-bold mb-4">{{ title }}</h2>
    <button @click="mostrarFormulario = true"
            class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded mb-4">Añadir Comentario
    </button>
    <div v-if="mostrarFormulario" class="fixed inset-0 flex items-center justify-center bg-gray-500 bg-opacity-75">
      <div class="bg-white rounded-lg p-8 w-1/2">
        <h3 class="text-lg font-bold mb-4">Nuevo Comentario</h3>
        <form @submit.prevent="crearComentario">
          <div class="mb-4">
            <label for="comentario" class="block text-gray-700 text-sm font-bold mb-2">Contenido:</label>
            <textarea id="comentario" v-model="nuevoComentario.comentario" required
                      class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"></textarea>
            <label for="fecha" class="block text-gray-700 text-sm font-bold mb-2">Fecha:</label>
            <input type="date" id="fecha" v-model="nuevoComentario.fecha" required
                   class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline">
          </div>
          <div class="flex justify-end">
            <button type="submit" class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline">Enviar</button>
            <button @click="cancelarComentario" class="bg-gray-500 hover:bg-gray-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline">Cancelar</button>
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
        <tr v-for="(row, rowIndex) in data" :key="rowIndex" class="text-gray-700 border-b hover:bg-gray-100">
          <td v-for="(value, columnIndex) in row" :key="columnIndex" class="px-4 py-2 border">
            {{value}}
          </td>
          <td class="px-4 py-2 border">
            <button @click="eliminarComentario(rowIndex)"
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
      nuevoComentario: {
        comentario: '',
        fecha: new Date().toISOString().substring(0, 10)
      }
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
    },
    crearComentario(){
      this.nuevoComentario.fecha = new Date(this.nuevoComentario.fecha)
      console.log(this.nuevoComentario.fecha)
      axios.post(`http://localhost:8080/api/implementacion/RequisitoFuncional/${this.$route.params.id}/Comentario`, this.nuevoComentario)
          .then(() => {
            this.mostrarFormulario=false
            const id = this.$route.params.id;
            this.fetchData(`http://localhost:8080/api/implementacion/RequisitoFuncional/${id}/Comentario`)
          })
          .catch(error => {
            console.error('Error al enviar el comentario:', error);
          });
    },
    cancelarComentario(){
      this.mostrarFormulario=false
    },
    eliminarComentario(rowIndex){
      axios.delete(`http://localhost:8080/api/implementacion/RequisitoFuncional/Comentario/${this.data.at(rowIndex).id}`)
          .then(() => {
            const id = this.$route.params.id;
            this.fetchData(`http://localhost:8080/api/implementacion/RequisitoFuncional/${id}/Comentario`)
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
