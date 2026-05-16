import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  server: {
    proxy: {
      '/auth':   { target: 'http://localhost:8081', changeOrigin: true },
      '/order':  { target: 'http://localhost:8081', changeOrigin: true },
      '/orders': { target: 'http://localhost:8081', changeOrigin: true },
      '/api':    { target: 'http://localhost:8081', changeOrigin: true },
    },
  }
})
