<template>
  <div class="w-full" @keydown.esc="mostrarFormulario=false; mostrarConfirmacion=false">
    <h2 class="text-lg font-bold mb-4">{{ title }}</h2>
    <button @click="mostrarFormulario = true; modoFormulario='Crear'"
            class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded mb-4">Añadir Requisito
    </button>
    <!-- Tabla con los datos -->
    <div class="overflow-x-auto">
      <table class="table-auto border-collapse w-full">
        <thead>
        <tr>
          <th v-for="(header, index) in headers" :key="index" @click="sortBy(header)" :class="{ 'bg-gray-200': header === sortByColumn }"
              class="px-4 py-2 bg-gray-200 text-gray-700 uppercase font-bold text-sm border-b">
            {{ header }}
            <span v-if="header === sortByColumn" :class="sortDirection === 'asc' ? 'inline' : 'hidden'">▲</span>
            <span v-if="header === sortByColumn" :class="sortDirection === 'desc' ? 'inline' : 'hidden'">▼</span>
          </th>
          <th class="px-4 py-2 bg-gray-200 text-gray-700 uppercase font-bold text-sm border-b">Acciones</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="(row, rowIndex) in sortedData" :key="rowIndex" class="text-gray-700 border-b hover:bg-gray-100">
          <td v-for="(value, columnIndex) in row" :key="columnIndex"  class="px-4 py-2 border">
            {{ value }}
          </td>
          <td class="px-4 py-2 border">
            <button @click="verRequisitosTecnicos(rowIndex)"
                    class="mr-2 bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded">Requisitos Tecnicos</button>
            <button @click="verComentarios(rowIndex)"
                    class="mr-2 bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded">Comentarios</button>
            <button @click="eliminarRF(rowIndex)"
                    class="mr-2 bg-red-500 hover:bg-red-700 text-white font-bold py-2 px-4 rounded">Borrar</button>
            <button @click="editarRF(rowIndex)"
                    class="mr-2 bg-yellow-500 hover:bg-yellow-700 text-white font-bold py-2 px-4 rounded">Editar</button>
          </td>
        </tr>
        </tbody>
      </table>
    </div>

    <!-- Modal con el formulario -->
    <div v-if="mostrarFormulario" class="fixed inset-0 flex items-center justify-center bg-gray-500 bg-opacity-75">
      <div class="bg-white rounded-lg p-8 w-1/2">
        <h3 class="text-lg font-bold mb-4">{{modoFormulario}} Comentario</h3>
        <form @submit.prevent="onSubmit">
          <div class="mb-4">
            <label for="titulo" class="block text-gray-700 text-sm font-bold mb-2">Título:</label>
            <input id="titulo" v-model="nuevoRF.titulo" required
                      class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"/>
            <label for="descripcion" class="block text-gray-700 text-sm font-bold mb-2" >Descripción:</label>
            <textarea id="descripcion" v-model="nuevoRF.descripcion"
                      class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"/>
            <label for="reglas" class="block text-gray-700 text-sm font-bold mb-2">Reglas:</label>
            <textarea id="reglas" v-model="nuevoRF.reglas" required
                      class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow" />
            <label for="estado" class="block text-gray-700 text-sm font-bold mb-2" >Estado:</label>
            <select id="estado" v-model="nuevoRF.estado" required
                    class="block appearance-none w-full bg-gray-200 border border-gray-200 text-gray-700 py-2 px-4 pr-8 rounded leading-tight focus:outline-none focus:bg-white focus:border-gray-500">>
              <option value="creado">Creado</option>
              <option value="pendiente de validar">Pendiente de validar</option>
              <option value="validado">Validado</option>
            </select>
            <label for="fecha" class="block text-gray-700 text-sm font-bold mb-2">Fecha:</label>
            <input type="date" id="fecha" v-model="nuevoRF.fechaCreacion" required
                   class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline">
          </div>
          <div class="flex justify-end">
            <button type="submit" class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline">Enviar</button>
            <button @click="mostrarFormulario=false" class="bg-gray-500 hover:bg-gray-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline">Cancelar</button>
          </div>
        </form>
      </div>
    </div>
  </div>

  <!-- Modal de confirmacion para borrar -->
  <div v-if="mostrarConfirmacion" class="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50">
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
      modoFormulario: '',
      mostrarConfirmacion: false,
      rowIndexToDelete: null,
      sortByColumn: '', // Columna actualmente ordenada
      sortDirection: 'asc', // Dirección de ordenamiento
      mostrarFormulario: false,
      tiposReglas: {},
      nuevoRF: {
        titulo: '',
        descripcion: '',
        reglas: '',
        estado: '',
        fechaCreacion: new Date().toISOString().substring(0, 10)
      }
    };
  },
  mounted() {
    const id = this.$route.params.id;
    this.fetchData('http://localhost:8080/api/implementacion/'+id+'/RequisitoFuncional');
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
            if (error.response.status === 404)
                // Redirigir a la página NotFound si no hay datos en la respuesta
              this.$router.push({
                name: 'NotFound'
              });
            console.error('Error fetching data:', error);
          });
    },
    sortBy(column) {
      if (column === this.sortByColumn) {
        this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
      } else {
        this.sortByColumn = column;
        this.sortDirection = 'asc';
      }
    },
    verRequisitosTecnicos(rowIndex){
      let id = this.data.at(rowIndex).id;
      this.$router.push({
        name: 'RT',
        params: {id}
      });
    },
    verComentarios(rowIndex){
      let id = this.data.at(rowIndex).id;
      this.$router.push({
        name: 'Comentarios',
        params: {id}
      });
    },
    crearRF(){
      this.nuevoRF.fechaCreacion = new Date(this.nuevoRF.fechaCreacion)
      const id = this.$route.params.id;
      axios.post(`http://localhost:8080/api/implementacion/${id}/RequisitoFuncional`, this.nuevoRF)
          .then(() => {
            this.mostrarFormulario=false
            this.fetchData('http://localhost:8080/api/implementacion/'+id+'/RequisitoFuncional')
            this.nuevoRF = {
              titulo: '',
                  descripcion: '',
                  reglas: '',
                  estado: '',
                  fechaCreacion: new Date().toISOString().substring(0, 10)
            }
          })
          .catch(error => {
            console.error('Error al enviar el comentario:', error);
          });
    },
    eliminarRF(rowIndex) {
      // Mostrar el modal de confirmación y almacenar el índice de la fila a eliminar
      this.rowIndexToDelete = rowIndex;
      this.mostrarConfirmacion = true;
    },
    confirmDelete() {
      let idRF = this.data[this.rowIndexToDelete].id;
      let id = this.$route.params.id

      // Realizar la eliminación del elemento
      axios.delete(`http://localhost:8080/api/implementacion/${id}/RequisitoFuncional/${idRF}`)
          .then(() => {
            // Eliminar la fila de la tabla localmente si la solicitud al servidor fue exitosa
            this.data = this.data.filter(item => item.id !== idRF);
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
      this.mostrarConfirmacion = false;
      this.rowIndexToDelete = null;
    },
    editarRF(rowIndex) {
      const rfSeleccionado = this.data[rowIndex];
      this.nuevoRF = { ...rfSeleccionado };
      this.modoFormulario = 'Editar';
      this.mostrarFormulario = true;
    },
    actualizarRF() {
      const id = this.$route.params.id; // ID del implementación
      this.nuevoRF.fechaCreacion = new Date(this.nuevoRF.fechaCreacion)
      axios.put(`http://localhost:8080/api/implementacion/${id}/RequisitoFuncional`, this.nuevoRF)
          .then(() => {
            this.mostrarFormulario = false;
            this.fetchData(`http://localhost:8080/api/implementacion/${id}/RequisitoFuncional`); // Recargar datos
            this.nuevoRF = {
              titulo: '',
              descripcion: '',
              reglas: '',
              estado: '',
              fechaCreacion: new Date().toISOString().substring(0, 10)
            }
          })
          .catch(error => {
            console.error('Error al actualizar el RF:', error);
          });
    },
    onSubmit() {
      if (this.modoFormulario === 'Crear') {
        this.crearRF();
      } else if (this.modoFormulario === 'Editar') {
        this.actualizarRF();
      }
    }

  },
  computed: {
    sortedData() {
      return this.data.slice().sort((a, b) => {
        const aValue = a[this.sortByColumn];
        const bValue = b[this.sortByColumn];
        if (aValue < bValue) return this.sortDirection === 'asc' ? -1 : 1;
        if (aValue > bValue) return this.sortDirection === 'asc' ? 1 : -1;
        return 0;
      });
    }
  }
};
</script>

<style scoped>
</style>
