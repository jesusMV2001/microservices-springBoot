<template>
  <h2 class="text-2xl font-bold mb-4">Formulario de Implementación</h2>
  <div class="max-w-md mx-auto">
    <form @submit.prevent="submitForm" class="bg-white shadow-md rounded px-8 pt-6 pb-8">
      <div class="mb-4">
        <label for="nombre" class="block text-gray-700 text-sm font-bold mb-2">Nombre:</label>
        <input type="text" id="nombre" v-model="nombre" required
               class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline">
      </div>
      <div class="mb-4">
        <label for="version" class="block text-gray-700 text-sm font-bold mb-2">Versión:</label>
        <input type="text" id="version" v-model="version" required
               class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline">
      </div>
      <div class="mb-4">
        <label for="fecha" class="block text-gray-700 text-sm font-bold mb-2">Fecha:</label>
        <input type="date" id="fecha" v-model="fecha" required
               class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline">
      </div>
      <div class="mb-4">
        <label for="descripcion" class="block text-gray-700 text-sm font-bold mb-2">Descripción:</label>
        <textarea id="descripcion" v-model="descripcion" required
                  class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"></textarea>
      </div>
      <div class="mb-6">
        <label for="alcance" class="block text-gray-700 text-sm font-bold mb-2">Alcance:</label>
        <textarea id="alcance" v-model="alcance" required
                  class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"></textarea>
      </div>

      <!-- Lista de requisitos funcionales -->
      <div class="mb-4">
        <label class="block text-gray-700 text-sm font-bold mb-2">Requisitos Funcionales:</label>
        <ul class="list-disc pl-4">
          <li v-for="(rf, index) in requisitosFuncionales" :key="index">
            {{ rf.id }}(id): {{rf.titulo}}
            <button @click.prevent="eliminarRequisitoFuncional(rf.id)" class="text-red-500 ml-2">Eliminar</button>
          </li>
        </ul>
      </div>

      <div class="flex items-center justify-between">
        <button type="submit"
                class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline">
          Enviar
        </button>
      </div>
    </form>
  </div>

</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      data: Object,
      nombre: '',
      version: '',
      fecha: '',
      descripcion: '',
      alcance: '',
      requisitosFuncionales: []
    };
  },
  mounted() {
    const id = this.$route.params.id;
    this.fetchData('http://localhost:8080/api/implementacion/'+id);
},
  methods: {
    fetchData(url) {
      axios.get(url)
          .then(response => {
            this.data = response.data

            this.nombre = response.data.nombre
            this.version = response.data.version
            this.descripcion = response.data.descripcion
            this.alcance = response.data.alcance
            this.requisitosFuncionales = response.data.requisitoFuncional

            const date = new Date(this.data.fecha);
            const day = date.getDate().toString().padStart(2, '0');
            const month = (date.getMonth() + 1).toString().padStart(2, '0');
            const year = date.getFullYear().toString();
            this.fecha=`${year}-${month}-${day}`
          })
          .catch(error => {
            console.error('Error fetching data:', error);
          });
    },
    submitForm() {
      this.data.nombre = this.nombre
      this.data.version = this.version
      this.data.fecha = this.fecha
      this.data.descripcion = this.descripcion
      this.data.alcance = this.alcance

      //TODO hacer peticion a la api
      console.log("aa");
    },
    eliminarRequisitoFuncional(id) {
      console.log(id)
    }
  }
};
</script>

<style scoped>
</style>