import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  // server:{
  //   port:8888,
  //   proxy:{
  //     // 代理规则
  //     '/api':{
  //       target:'http://localhost:9999',
  //       changeOrigin:true,
  //       rewrite:(path)=>{
  //         return path.replace(/\api/,'')
  //       }
  //     }
  //   }
  // }
})

