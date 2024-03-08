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
          <!-- Agregar los cuatro botones en la columna de acciones -->
          <button @click="verRequisitosFuncionales(rowIndex)">Requisitos Funcionales</button>
          <button @click="handleAction2(row)">Botón 2</button>
          <button @click="handleAction3(row)">Botón 3</button>
          <button class="delete-button" @click="eliminarImplementacion(rowIndex)">Borrar</button>
        </td>
      </tr>
      </tbody>
    </table>
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
      completeData: []
    };
  },
  mounted() {
    this.fetchData();
  },
  methods: {
    fetchData() {
      axios.get(this.apiUrl)
          .then(response => {
            this.completeData = response.data;
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
      let id = this.completeData.at(rowIndex).Id;
      this.$router.push({
        name: 'RF',
        params: {id}
      });
    },
    handleAction2(row) {
      // Manejar la acción del botón 2 para la fila dada
      console.log('Botón 2 clickeado para la fila:', row);
    },
    handleAction3(row) {
      // Manejar la acción del botón 3 para la fila dada
      console.log('Botón 3 clickeado para la fila:', row);
    },
    eliminarImplementacion(rowIndex) {
      let id = this.completeData.at(rowIndex).Id;
      console.log(id)
      axios.delete(`http://localhost:8080/api/implementacion/${id}`)
          .then(() => {
            // Eliminar la fila de la tabla localmente si la solicitud al servidor fue exitosa
            this.data = this.data.filter(item => item.id !== id);
          })
          .catch(error => {
            console.error('Error al eliminar el elemento:', error);
          });
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
</style>
