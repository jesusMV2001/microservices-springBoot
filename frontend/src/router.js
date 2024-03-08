import { createRouter, createWebHistory } from 'vue-router'
import ApiTable from '@/components/ApiTable.vue'
import RFTable from '@/components/RFTable.vue'

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
            title: 'Requisitos Técnicos'
        }
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router
