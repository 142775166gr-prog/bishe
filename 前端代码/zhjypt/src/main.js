import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './state'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'


let app=createApp(App)
app.use(router).use(store).use(ElementPlus)
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
  }

app.mount('#app')
