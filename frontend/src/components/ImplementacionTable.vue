<template>
  <div class="w-full" @keydown.esc="mostrarFormulario=false; showModal=false">
    <h2 class="text-2xl font-bold mb-4">{{ title }}</h2>
    <button @click="mostrarFormulario=true" class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded mb-4">Añadir Implementación</button>
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
        <tr v-for="(row, rowIndex) in sortedData" :key="rowIndex"
            class="text-gray-700 border-b hover:bg-gray-100">
          <td v-for="(value, columnIndex) in row" :key="columnIndex"
              class="px-4 py-2 border">{{ value }}</td>
          <td class="px-4 py-2 border">
            <button @click="verRequisitosFuncionales(rowIndex)"
                    class="mr-2 bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded">Requisitos
              Funcionales</button>
            <button @click="editar(rowIndex)"
                    class="mr-2 bg-yellow-500 hover:bg-yellow-700 text-white font-bold py-2 px-4 rounded">Editar</button>
            <button @click="crearPDF(rowIndex)"
                    class="mr-2 bg-green-500 hover:bg-green-700 text-white font-bold py-2 px-4 rounded">PDF</button>
            <button @click="eliminarImplementacion(rowIndex)"
                    class="bg-red-500 hover:bg-red-700 text-white font-bold py-2 px-4 rounded">Borrar</button>
          </td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>

  <!-- Modal de confirmacion para borrar -->
  <div v-if="showModal" class="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50">
    <div class="modal-content bg-white p-4 rounded shadow-lg">
        <span class="close absolute top-0 right-0 m-4 text-gray-600 cursor-pointer"
              @click="closeModal">&times;</span>
      <p class="text-lg text-gray-800 mb-4">¿Está seguro que desea borrar este elemento?</p>
      <div class="flex justify-end">
        <button @click="confirmDelete"
                class="mr-2 bg-red-500 hover:bg-red-700 text-white font-bold py-2 px-4 rounded">Confirmar</button>
        <button @click="closeModal"
                class="bg-gray-400 hover:bg-gray-600 text-gray-800 font-bold py-2 px-4 rounded">Cancelar</button>
      </div>
    </div>
  </div>

  <!-- Modal con el formulario -->
  <div v-if="mostrarFormulario" class="fixed inset-0 flex items-center justify-center bg-gray-500 bg-opacity-75">
    <div class="bg-white rounded-lg p-8 w-1/2">
      <h3 class="text-lg font-bold mb-4">Nuevo Comentario</h3>
      <form @submit.prevent="crearImplementacion">
        <div class="mb-4">
          <label for="nombre" class="block text-gray-700 text-sm font-bold mb-2">Nombre:</label>
          <input id="nombre" v-model="nuevaImplementacion.nombre" required
                    class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"  />
          <label for="version" class="block text-gray-700 text-sm font-bold mb-2">Version:</label>
          <input id="version" v-model="nuevaImplementacion.version" required
                 class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"  />
          <label for="fecha" class="block text-gray-700 text-sm font-bold mb-2">Fecha:</label>
          <input type="date" id="fecha" v-model="nuevaImplementacion.fecha" required
                 class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"/>
          <label for="descripcion" class="block text-gray-700 text-sm font-bold mb-2">Descripción:</label>
          <textarea id="descripcion" v-model="nuevaImplementacion.descripcion" required
                    class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"></textarea>
          <label for="alcance" class="block text-gray-700 text-sm font-bold mb-2">Alcance:</label>
          <textarea id="alcance" v-model="nuevaImplementacion.alcance" required
                    class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"></textarea>
        </div>
        <div class="flex justify-end">
          <button type="submit" class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline">Enviar</button>
          <button @click="mostrarFormulario=false" class="bg-gray-500 hover:bg-gray-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline">Cancelar</button>
        </div>
      </form>
    </div>
  </div>

</template>

<script>
import axios from 'axios';

export default {
  props: {
    apiUrl: {
      type: String,
      required: true
    },
    title: {
      type: String,
      required: true
    }
  },
  data() {
    return {
      headers: [],
      data: [],
      showModal: false,
      rowIndexToDelete: null,
      sortByColumn: '',
      sortDirection: 'asc',
      mostrarFormulario: false,
      nuevaImplementacion: {
        nombre: '',
        version: '',
        fecha: new Date().toISOString().substring(0, 10),
        descripcion: '',
        alcance: ''
      }
    };
  },
  mounted() {
    this.fetchData();
  },
  methods: {
    fetchData() {
      axios.get(this.apiUrl)
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
    verRequisitosFuncionales(rowIndex) {
      let id = this.data.at(rowIndex).id;
      this.$router.push({
        name: 'RF',
        params: {id}
      });
    },
    editar(rowIndex) {
      let id = this.data.at(rowIndex).id;
      this.$router.push({
        name: 'edit',
        params: {id}
      });
    },
    crearPDF(rowIndex) {
      let id = this.data.at(rowIndex).id;
      let nombre = this.data.at(rowIndex).nombre

      axios.get(`http://localhost:8080/api/implementacion/${id}/pdf/RT`, {
        responseType: 'blob' // Establece el tipo de respuesta como 'blob' para manejar archivos binarios
      })
          .then(response => {
            const url = window.URL.createObjectURL(new Blob([response.data]));
            const link = document.createElement('a');
            link.href = url;
            link.setAttribute('download', `${nombre}.pdf`);
            document.body.appendChild(link);
            link.click();
          })
          .catch(error => {
            console.error('Error al descargar el archivo PDF:', error);
          });
    },
    eliminarImplementacion(rowIndex) {
      // Mostrar el modal de confirmación y almacenar el índice de la fila a eliminar
      this.rowIndexToDelete = rowIndex;
      this.showModal = true;
    },
    confirmDelete() {
      // Obtener el ID del elemento a eliminar
      let id = this.data[this.rowIndexToDelete].id;

      // Realizar la eliminación del elemento
      axios.delete(`http://localhost:8080/api/implementacion/${id}`)
          .then(() => {
            // Eliminar la fila de la tabla localmente si la solicitud al servidor fue exitosa
            this.data = this.data.filter(item => item.id !== id);
            // Cerrar el modal después de eliminar
            this.closeModal();
          })
          .catch(error => {
            console.error('Error al eliminar el elemento:', error);
            // Cerrar el modal en caso de error
            this.closeModal();
          });
    },
    closeModal() {
      // Cerrar el modal y limpiar el índice de la fila a eliminar
      this.showModal = false;
      this.rowIndexToDelete = null;
    },
    crearImplementacion() {
      this.nuevaImplementacion.fecha = new Date(this.nuevaImplementacion.fecha)
      axios.post(`http://localhost:8080/api/implementacion`, this.nuevaImplementacion)
          .then(() => {
            this.mostrarFormulario=false
            this.fetchData(`http://localhost:8080/api/implementacion/RequisitoFuncional`)
            this.nuevaImplementacion= {
                  nombre: '',
                  version: '',
                  fecha: new Date().toISOString().substring(0, 10),
                  descripcion: '',
                  alcance: ''
            }
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
