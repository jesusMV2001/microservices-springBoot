<template>
  <h2 class="text-2xl font-bold mb-4">Formulario de Implementación</h2>
  <div class="max-w-md mx-auto">
    <!-- Mostrar mensaje si se ha guardado exitosamente -->
    <div v-if="mostrarMensaje" class="bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded relative" role="alert">
      <strong class="font-bold">Éxito:</strong>
      <span class="block sm:inline">La implementación se ha guardado exitosamente.</span>
      <span class="absolute top-0 bottom-0 -right-3.5 px-4 py-3">
        <svg @click="mostrarMensaje = false" class="fill-current h-6 w-6 text-green-500" role="button" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20"><title>Close</title><path d="M14.348 14.849a.5.5 0 01-.708 0L10 10.707l-3.646 3.646a.5.5 0 01-.708-.708L9.293 10 5.646 6.354a.5.5 0 01.708-.708L10 9.293l3.646-3.647a.5.5 0 01.708.708L10.707 10l3.647 3.646a.5.5 0 010 .708z"/></svg>
      </span>
    </div>

    <!-- Mostrar mensaje de error si se produce un error -->
    <div v-if="mostrarError" class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded relative" role="alert">
      <strong class="font-bold">Error:</strong>
      <span class="block sm:inline">Se produjo un error al guardar la implementación.</span>
      <span class="absolute top-0 bottom-0 -right-3.5 px-4 py-3">
        <svg @click="mostrarError = false" class="fill-current h-6 w-6 text-red-500" role="button" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20"><title>Close</title><path d="M14.348 14.849a.5.5 0 01-.708 0L10 10.707l-3.646 3.646a.5.5 0 01-.708-.708L9.293 10 5.646 6.354a.5.5 0 01.708-.708L10 9.293l3.646-3.647a.5.5 0 01.708.708L10.707 10l3.647 3.646a.5.5 0 010 .708z"/></svg>
      </span>
    </div>

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
          <li v-for="(rf, index) in requisitosFuncionales" :key="index"  >
            <span :class="{ 'line-through': rfEliminados[index] }">{{ rf.id }}(id): {{ rf.titulo }}</span>
            <button @click.prevent="eliminarRequisitoFuncional(rf.id)" v-if="!rfEliminados.at(index)" class="text-red-500 ml-2">Eliminar</button>
            <button @click.prevent="cancelarEliminacion(rf.id)" v-else class="text-gray-500 ml-2">Cancelar</button>
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
      requisitosFuncionales: [],
      rfEliminados: [],
      mostrarMensaje: false,
      mostrarError:false
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
            this.requisitosFuncionales.forEach(() => this.rfEliminados.push(false))

            const date = new Date(this.data.fecha);
            const day = date.getDate().toString().padStart(2, '0');
            const month = (date.getMonth() + 1).toString().padStart(2, '0');
            const year = date.getFullYear().toString();
            this.fecha=`${year}-${month}-${day}`
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
    submitForm() {
      this.data.nombre = this.nombre
      this.data.version = this.version
      this.data.fecha = new Date(this.fecha)
      this.data.descripcion = this.descripcion
      this.data.alcance = this.alcance

      //obtiene los indices de los rf a eliminar
      let indicesBorrar = this.rfEliminados.map((value, index) => value ? index : -1).filter(index => index !== -1);
      this.data.requisitoFuncional = this.requisitosFuncionales.filter((_, index) => !indicesBorrar.includes(index))

      axios.put(`http://localhost:8080/api/implementacion`, this.data)
          .then(() =>{
            this.mostrarMensaje=true
            const id = this.$route.params.id;
            this.fetchData('http://localhost:8080/api/implementacion/'+id);
          })
          .catch(error => {
            this.mostrarError=true
            console.error('Error updating data:', error);
          })
    },
    eliminarRequisitoFuncional(id) {
      let index = this.requisitosFuncionales.findIndex(value => value.id === id);
      this.rfEliminados[index]=true
    },
    cancelarEliminacion(id){
      let index = this.requisitosFuncionales.findIndex(value => value.id === id);
      this.rfEliminados[index]=false
    }
  }
};
</script>

<style scoped>
</style>