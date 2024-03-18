<template>
  <div class="w-full" tabindex="0" @keydown.esc="mostrarFormulario=false">
    <h2 class="text-2xl font-bold mb-4">{{ title }}</h2>
    <button @click="mostrarFormulario = true"
            class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded mb-4">Añadir Comentario
    </button>
    <!-- Modal con el formulario -->
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
            <button @click="mostrarFormulario=false" class="bg-gray-500 hover:bg-gray-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline">Cancelar</button>
          </div>
        </form>
      </div>
    </div>

    <!-- Tabla con los datos -->
    <div class="overflow-x-auto">
      <table class="table-auto border-collapse w-full">
        <thead>
        <tr>
          <th v-for="(header, index) in headers" :key="index" @click="sortBy(header)" :class="{ 'by-gray-200': header === sortByColumn }"
              class="px-4 py-2 bg-gray-200 text-gray-700 uppercase font-bold text-sm border-b">
            {{ header }}
            <span v-if="header === sortByColumn" :class="sortDirection === 'asc' ? 'inline' : 'hidden'" >▲</span>
            <span v-if="header === sortByColumn" :class="sortDirection === 'desc' ? 'inline' : 'hidden'" >▼</span>
          </th>
          <th class="px-4 py-2 bg-gray-200 text-gray-700 uppercase font-bold text-sm border-b">Acciones</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="(row, rowIndex) in sortedData" :key="rowIndex" class="text-gray-700 border-b hover:bg-gray-100">
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
      },
      sortByColumn: '',
      sortDirection: 'asc'
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
                  if(typeof value === "string" && value.match('T\?\?:'))
                    value = value.split('T')[0]
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
      axios.post(`http://localhost:8080/api/implementacion/RequisitoFuncional/${this.$route.params.id}/Comentario`, this.nuevoComentario)
          .then(() => {
            this.mostrarFormulario=false
            const id = this.$route.params.id;
            this.fetchData(`http://localhost:8080/api/implementacion/RequisitoFuncional/${id}/Comentario`)
            this.nuevoComentario= {
              comentario: '',
              fecha: new Date().toISOString().substring(0, 10)
            }
          })
          .catch(error => {
            console.error('Error al enviar el comentario:', error);
          });
    },
    eliminarComentario(rowIndex){
      let id = this.$route.params.id
      let comentarioId = this.data.at(rowIndex).id

      axios.delete(`http://localhost:8080/api/implementacion/RequisitoFuncional/${id}/Comentario/${comentarioId}`)
          .then(() => {
            this.fetchData(`http://localhost:8080/api/implementacion/RequisitoFuncional/${id}/Comentario`)
          })
          .catch(error => {
            console.error('Error al enviar el comentario:', error);
          });
    },
    sortBy(column){
      if(column === this.sortByColumn)
        this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc'
      else {
        this.sortByColumn = column
        this.sortDirection = 'asc'
      }
    }
  },
  computed: {
    sortedData(){
      return this.data.slice().sort((a,b) =>{
        const aValue = a[this.sortByColumn]
        const bValue = b[this.sortByColumn]
        if(aValue < bValue) return this.sortDirection === 'asc' ? -1 : 1
        if(aValue > bValue) return this.sortDirection === 'asc' ? 1 : -1
        return 0
      })
    }
  }
};
</script>

<style scoped>
</style>
