import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as Icons from '@element-plus/icons-vue'
import App from './App.vue'
import router from './router'
import './assets/styles/index.css'
import { useUserStore } from './stores/userStore'

const app = createApp(App)
Object.entries(Icons).forEach(([name, component]) => app.component(name, component))

const pinia = createPinia()
app.use(pinia)
app.use(router)
app.use(ElementPlus)


const userStore = useUserStore()
if (userStore.token) {
    userStore.fetchUser().catch(() => userStore.logout())
}

app.mount('#app')