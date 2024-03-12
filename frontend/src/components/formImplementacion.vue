<template>
  <h2>Formulario de Implementación</h2>
  <div class="form-container" >
    <form @submit.prevent="submitForm">
      <div>
        <label for="nombre">Nombre:</label>
        <input type="text" id="nombre" v-model="nombre" required>
      </div>
      <div>
        <label for="version">Versión:</label>
        <input type="text" id="version" v-model="version" required>
      </div>
      <div>
        <label for="fecha">Fecha:</label>
        <input type="date" id="fecha" v-model="fecha" required>
      </div>
      <div>
        <label for="descripcion">Descripción:</label>
        <textarea id="descripcion" v-model="descripcion" required></textarea>
      </div>
      <div>
        <label for="alcance">Alcance:</label>
        <textarea id="alcance" v-model="alcance" required></textarea>
      </div>
      <button type="submit">Enviar</button>
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
      alcance: ''
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
            //filtrar los atributos que son listas
            this.data = response.data

            this.nombre = response.data.nombre
            this.version = response.data.version
            this.descripcion = response.data.descripcion
            this.alcance = response.data.alcance

            const date = new Date(this.data.fecha);
            const day = date.getDate().toString().padStart(2, '0');
            const month = (date.getMonth() + 1).toString().padStart(2, '0'); // Los meses empiezan desde 0
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

      // Aquí puedes realizar la lógica para enviar los datos a través de una petición HTTP
      // Por ejemplo, utilizando axios o fetch
      console.log(this.data);
    }
  }
};
</script>

<style scoped>
.form-container {
  display: flex;
  justify-content: center; /* Centra el contenido horizontalmente */
  height: 100vh; /* Establece la altura del contenedor al 100% de la ventana */
}
</style>