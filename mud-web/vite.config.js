import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import compression from 'vite-plugin-compression';
import { obfuscatorPlugin } from './vite-obfuscator-plugin.js';

export default defineConfig({
  plugins: [
    vue(),
    // Gzip 压缩
    compression({
      algorithm: 'gzip',
      ext: '.gz',
      threshold: 10240, // 只压缩大于 10KB 的文件
      deleteOriginFile: false // 保留原始文件
    }),
    // Brotli 压缩 (更好的压缩率)
    compression({
      algorithm: 'brotliCompress',
      ext: '.br',
      threshold: 10240,
      deleteOriginFile: false
    })
  ],
  resolve: {
    alias: {
      vue: 'vue/dist/vue.esm-bundler.js'
    }
  },
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:3000',
        changeOrigin: true
      }
    }
  },
  build: {
    outDir: 'dist',
    emptyOutDir: true,
    // 启用 minify 压缩
    minify: 'terser',
    terserOptions: {
      compress: {
        // 移除 console.log (生产环境)
        drop_console: true,
        // 移除 debugger
        drop_debugger: true,
        // 纯函数调用优化
        pure_funcs: ['console.log']
      },
      format: {
        // 移除注释
        comments: false
      }
    },
    // 代码分割优化
    rollupOptions: {
      output: {
        // 手动分包策略
        manualChunks: {
          vue: ['vue'],
          vendor: ['ws', 'iconv-lite']
        },
        // 资源文件名格式
        chunkFileNames: 'js/[name]-[hash].js',
        entryFileNames: 'js/[name]-[hash].js',
        assetFileNames: 'assets/[name]-[hash].[ext]'
      },
      // 添加代码混淆插件
      plugins: [
        obfuscatorPlugin()
      ]
    },
    // 资源内联限制 (小于 4kb 的图片转 base64)
    assetsInlineLimit: 4096,
    // 启用 sourcemap (生产环境建议关闭以减小体积)
    sourcemap: false
  }
});
