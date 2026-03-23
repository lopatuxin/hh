import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'
import path from 'node:path'

export default defineConfig({
  plugins: [react(), tailwindcss()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
    },
  },
  server: {
    host: '0.0.0.0',
    proxy: {
      '/api': {
        target: process.env.VITE_API_TARGET || 'http://localhost:8080',
        changeOrigin: true,
      },
      '/vnc': {
        target: process.env.VITE_VNC_TARGET || 'http://localhost:6080',
        changeOrigin: true,
        rewrite: (path: string) => path.replace(/^\/vnc/, ''),
        ws: true,
      },
    },
  },
})
