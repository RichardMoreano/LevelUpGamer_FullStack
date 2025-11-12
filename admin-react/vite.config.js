import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import { writeFileSync } from 'fs'
import { join } from 'path'

// Plugin para crear .nojekyll para GitHub Pages
const githubPagesPlugin = () => ({
  name: 'github-pages',
  writeBundle(options, bundle) {
    const outDir = options.dir || 'dist'
    writeFileSync(join(outDir, '.nojekyll'), '')
  }
})

// https://vite.dev/config/
export default defineConfig({
  plugins: [react(), githubPagesPlugin()],
  base: process.env.NODE_ENV === 'production' ? '/LevelUpGamer_FullStack/' : '/',
  build: {
    outDir: 'dist',
    assetsDir: 'assets',
    sourcemap: false
  },
  server: {
    port: 5173,
    host: true,
    open: true,
    proxy: {
      // Proxy para desarrollo local con backend Spring Boot
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false
      }
    }
  },
  test: {
    globals: true,
    environment: 'jsdom',
    setupFiles: './src/test/setup.js',
  }
})
