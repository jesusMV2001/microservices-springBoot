import { createRouter, createWebHistory } from 'vue-router'
import ApiTable from '@/components/ImplementacionTable.vue'
import RFTable from '@/components/RFTable.vue'
import formImplementacion from "@/components/EditarImplementacion.vue";
import RTTable from "@/components/RTTable.vue";
import ComentariosTable from "@/components/ComentariosTable.vue";
import NotFound from "@/components/NotFound.vue";

const routes = [
    {
        path: '/',
        component: ApiTable,
        props: {
            apiUrl: 'http://localhost:8080/api/implementacion',
            title: 'Implementaciones'
        }
    },
    {
        path: '/RF/:id',
        name: 'RF',
        component: RFTable,
        props: {
            title: 'Requisitos Funcionales'
        }
    },
    {
        path: '/implementacion/:id',
        name: 'edit',
        component: formImplementacion
    },
    {
        path: '/RT/:id',
        name: 'RT',
        component: RTTable,
        props: {
            title: 'Requisitos Técnicos'
        }
    },
    {
        path: '/Comentario/:id',
        name: 'Comentarios',
        component: ComentariosTable,
        props: {
            title: 'Comentarios'
        }
    },
    {
        path: '/:pathMatch(.*)*',
        name: 'NotFound',
        component: NotFound
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router
