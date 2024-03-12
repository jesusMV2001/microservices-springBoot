<template>
  <div>
    <h2 class="table-title">{{ title }}</h2>
    <table>
      <thead>
      <tr>
        <th v-for="(header, index) in headers" :key="index">{{ header }}</th>
        <th>Acciones</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="(row, rowIndex) in data" :key="rowIndex">
        <td v-for="(value, columnIndex) in row" :key="columnIndex">
          {{value}}
        </td>
        <td>
          <button @click="verRequisitosFuncionales(rowIndex)">Requisitos Funcionales</button>
          <button @click="editar(rowIndex)">Editar</button>
          <button @click="crearPDF(rowIndex)">PDF</button>
          <button class="delete-button" @click="eliminarImplementacion(rowIndex)">Borrar</button>
        </td>
      </tr>
      </tbody>
    </table>

    <div v-if="showModal" class="modal">
      <div class="modal-content">
        <span class="close" @click="closeModal">&times;</span>
        <p>¿Está seguro que desea borrar este elemento?</p>
        <button @click="confirmDelete">Confirmar</button>
        <button @click="closeModal">Cancelar</button>
      </div>
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
      rowIndexToDelete: null
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
      let id = this.data.at(rowIndex).Id;
      this.$router.push({
        name: 'RF',
        params: {id}
      });
    },
    editar(rowIndex) {
      let id = this.data.at(rowIndex).Id;
      this.$router.push({
        name: 'edit',
        params: {id}
      });
    },
    crearPDF(rowIndex) {
      let id = this.data.at(rowIndex).Id;
      let nombre = this.data.at(rowIndex).nombre

      axios.get(`http://localhost:8080/api/implementacion/${id}/pdf/RT`, {
        responseType: 'blob' // Establece el tipo de respuesta como 'blob' para manejar archivos binarios
      })
          .then(response => {
            const url = window.URL.createObjectURL(new Blob([response.data]));
            const link = document.createElement('a');
            link.href = url;
            link.setAttribute('download', `${nombre}.pdf`); // Establece el nombre del archivo que se descargará
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
      let id = this.data[this.rowIndexToDelete].Id;

      // Realizar la eliminación del elemento
      axios.delete(`http://localhost:8080/api/implementacion/${id}`)
          .then(() => {
            // Eliminar la fila de la tabla localmente si la solicitud al servidor fue exitosa
            this.data = this.data.filter(item => item.Id !== id);
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
    }
  }
};
</script>

<style scoped>
/* Estilos opcionales para tu tabla */
table {
  width: 100%;
  border-collapse: collapse;
}

th, td {
  border: 1px solid #ddd;
  padding: 8px;
  text-align: left;
}

th {
  background-color: #f2f2f2;
}

.table-title {
  margin-top: 20px; /* Agregar espacio entre el título y la tabla */
}

.delete-button {
  background-color: #f44336; /* Color rojo */
  border: none;
  color: white;
  padding: 8px 16px;
  text-align: center;
  text-decoration: none;
  display: inline-block;
  font-size: 16px;
  margin: 4px 2px;
  cursor: pointer;
  border-radius: 4px;
}

.delete-button:hover {
  background-color: #d32f2f; /* Color rojo más oscuro al pasar el mouse */
}

.modal {
  position: fixed;
  z-index: 1;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
  overflow: auto;
  background-color: rgba(0,0,0,0.4);
}

.modal-content {
  background-color: #fefefe;
  margin: 15% auto;
  padding: 20px;
  border: 1px solid #888;
  width: 30%;
}

.close {
  color: #aaa;
  float: right;
  font-size: 28px;
  font-weight: bold;
}

.close:hover,
.close:focus {
  color: black;
  text-decoration: none;
  cursor: pointer;
}
</style>
