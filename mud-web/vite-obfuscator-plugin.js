import JavaScriptObfuscator from 'javascript-obfuscator';

/**
 * Vite/Rollup 代码混淆插件
 */
export function obfuscatorPlugin(options = {}) {
  const defaultOptions = {
    // 字符串数组编码
    stringArray: true,
    stringArrayThreshold: 0.75,
    // 控制流扁平化
    controlFlowFlattening: true,
    controlFlowFlatteningThreshold: 0.75,
    // 死代码注入
    deadCodeInjection: true,
    deadCodeInjectionThreshold: 0.4,
    // 调试保护
    debugProtection: false,
    disableConsoleOutput: false,
    // 标识符名称生成器
    identifierNamesGenerator: 'hexadecimal',
    // 数字转换
    numbersToExpressions: true,
    // 简化表达式
    simplify: true,
    // 拆分字符串
    splitStrings: true,
    splitStringsChunkLength: 5,
    ...options
  };

  return {
    name: 'obfuscator',
    generateBundle(opts, bundle) {
      for (const [fileName, chunk] of Object.entries(bundle)) {
        if (fileName.endsWith('.js') && chunk.type === 'chunk') {
          try {
            const result = JavaScriptObfuscator.obfuscate(chunk.code, defaultOptions);
            chunk.code = result.getObfuscatedCode();
          } catch (error) {
            console.warn(`[obfuscator] Failed to obfuscate ${fileName}:`, error.message);
          }
        }
      }
    }
  };
}
