# 配置说明

在此配置 Web App 的相关配置即可

```json5
{
  "landscape": false, // 是否支持横屏
  "hardwareAccelerated": false, // 是否开启硬件加速
  "pcMode": false, // 是否开启电脑模式（UA）
  "openAppEnable": false, // 是否允许 webview 打开其他应用
  "supportMultipleWindows": true, // 是否支持多窗口
  "enableProgressBar": false,  // 是否开启进度条
  "launchUrl": "https://www.baidu.com", // 首页 URL（启动即加载）
  "theme": {
    "colorPrimary": "#79BD9A" // 主题色
  },
  "appbar": {
    "title": "测试百度", // 顶栏标题（不展示网页标题则展示这个）
    "showWebTitle": true, // 是否展示网页菜单
    "menuEnable": false, // 是否开启菜单
    "enable": false // 是否开启顶栏
  }
}
```