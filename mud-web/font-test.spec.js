const { test, expect } = require('@playwright/test');

test('验证 Maple Mono 字体加载', async ({ page }) => {
  // 访问页面
  await page.goto('http://localhost:5173/');
  
  // 等待页面加载完成
  await page.waitForLoadState('networkidle');
  
  // 检查当前使用的字体
  const fontFamily = await page.evaluate(() => {
    const computedStyle = window.getComputedStyle(document.body);
    return computedStyle.fontFamily;
  });
  
  console.log('当前字体:', fontFamily);
  
  // 检查 Maple Mono 是否已加载
  const isMapleMonoLoaded = await page.evaluate(() => {
    return document.fonts.check('16px "Maple Mono"');
  });
  
  console.log('Maple Mono 已加载:', isMapleMonoLoaded);
  
  // 断言字体应该包含 Maple Mono
  expect(fontFamily).toContain('Maple Mono');
  expect(isMapleMonoLoaded).toBe(true);
  
  // 截图保存以便视觉验证
  await page.screenshot({ path: 'font-test.png', fullPage: true });
  console.log('截图已保存: font-test.png');
});