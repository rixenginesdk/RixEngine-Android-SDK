# RixEngine Android SDK

中文 | [English](README.md)

[![Maven Central](https://img.shields.io/maven-central/v/io.github.rixenginesdk/rixengine?label=Maven%20Central&color=4C6FFF)](https://central.sonatype.com/artifact/io.github.rixenginesdk/rixengine)
[![Android](https://img.shields.io/badge/Android-4.1%2B-3DDC84?logo=android&logoColor=white)](https://developer.android.com)
[![License](https://img.shields.io/badge/License-Apache%202.0-4C6FFF)](LICENSE)

> **v4.4.0 稳定版** · 更稳定、更快速、更全面的变现引擎

**欢迎使用 RixEngine Android SDK** —— 助您充分释放应用内变现潜力的门户。

本仓库包含：

- 使用 RixEngine 的示例源代码
- 开源聚合适配器

---

## ✨ 版本亮点

v4.4.0 以「稳定」为底座、以「速度」为抓手、以「收益」为目标，带来一次全面升级。

### 🛡️ 更稳定

- **线上验证底座**：以多款应用与游戏产品长期线上验证的稳定版本为基础，安全可靠。
- **历史问题清零**：多轮内部迭代修复已知线上崩溃与广告展示异常，解决老版本遗留 Bug。
- **全设备兼容**：适配最新 Android 系统及手机、平板、折叠屏等多种设备形态。

### ⚡ 更快速

- **冷热双缓存**：内置冷 / 热启动双缓存，热启动预加载提速，冷启动广告加载更快。
- **弱网专项优化**：针对弱网环境优化请求链路，显著提升弱网地区的请求成功率。
- **老旧设备优化**：加载与展示双端优化，规避内存不足风险，并加强安全性防范。

### 🚀 更全面

- **Bidding 竞价回传**：新增成功 / 失败 / 展示回传 API，竞价链路更透明、更可控。
- **Google Play 内联安装**：缩短转化路径，提升 eCPM 与转化率。
- **全广告样式**：插屏、激励视频、横幅、原生、开屏广告一应俱全。
- **白名单测试设备**：可针对单台设备精准测试，联调更高效便捷。
- **主流聚合适配**：AdMob、GAM、MAX、Unity Ads、TopOn 等三方聚合平台新版本适配与升级。

---

## 📦 仓库结构

| 目录 / 文件 | 说明 |
| --- | --- |
| [`Demo App - Java`](Demo%20App%20-%20Java) | Java 示例工程，演示各广告样式的完整接入流程 |
| [`Demo App - Kotlin`](Demo%20App%20-%20Kotlin) | Kotlin 示例工程，功能与 Java 版保持一致 |
| [`custom_adapter`](custom_adapter) | 开源聚合适配器源码（AdMob / IronSource / MAX / TopOn / TradPlus） |
| `rixengine-sdk-demo.apk` | 可直接安装体验的 Demo 安装包 |

---

## 🚀 SDK 集成指南

在项目中引入 RixEngine Android SDK，只需确认环境要求并添加依赖即可。

### 环境要求

| 项目     | 要求                        |
|--------|---------------------------|
| JDK    | 11 及以上                    |
| minSdk | 16 及以上                    |

### 添加 SDK 依赖

**Groovy（`build.gradle`）**

```groovy
dependencies {
    // RixEngine Android SDK（必需）
    implementation 'io.github.rixenginesdk:rixengine:4.4.0'
}
```

**Kotlin DSL（`build.gradle.kts`）**

```kotlin
dependencies {
    // RixEngine Android SDK（必需）
    implementation("io.github.rixenginesdk:rixengine:4.4.0")
}
```

> 请确保工程已配置 `mavenCentral()` 仓库，否则将无法拉取依赖。

---

## 🎯 广告样式

| 广告样式 | 适用场景 | Demo 入口 |
| --- | --- | --- |
| 开屏广告 | 应用启动首屏曝光，抢占第一眼注意力 | `SplashVideoActivity` / `SplashBannerActivity` |
| 插屏广告 | 页面切换、关卡结束等自然停顿点 | `InterstitialVideoActivity` / `InterstitialBannerActivity` |
| 激励视频 | 观看视频换取奖励，兼顾体验与收益 | `RewardVideoActivity` |
| 横幅广告 | 常驻页面顶部或底部，持续曝光 | `BannerActivity` |
| 原生广告 | 自定义样式，自然融入信息流 | `NativeActivity` |

---

## 🔌 聚合平台适配

[`custom_adapter`](custom_adapter) 提供以下主流聚合平台的开源适配器，Demo 工程已集成对应版本，可按需取用。

| 聚合平台 | 适配器目录 | Demo 集成版本 |
| --- | --- | --- |
| AdMob / GAM | `custom_adapter/admob` | `play-services-ads 23.5.0` |
| AppLovin MAX | `custom_adapter/max` | `applovin-sdk 13.6.0` |
| LevelPlay（原 IronSource） | `custom_adapter/ironsource` | `mediation-sdk 9.2.0` |
| TopOn | `custom_adapter/topon` | `core-tpn 6.6.30.2` |
| TradPlus | `custom_adapter/tradplus` | `tradplus 14.5.0.1` |

### 使用步骤

1. 在项目工程App主模块中引入对应聚合平台的SDK包（如通过 implementation 引入 Admob、Max、TopOn、LevelPlay、TradPlus依赖包）。
2. 将对应聚合平台所需的适配器源码包文件复制到Android工程App主模块中参与编译。
3. 在聚合平台官网后台中添加适配器的类名，具体按照聚合平台官网说明文档进行调整。

---

## 💬 技术支持

如有任何集成与使用疑问，请联系 [rix-sdk@rixengine.com](mailto:rix-sdk@rixengine.com)。

---

## 📄 开源协议

本项目基于 [Apache License 2.0](LICENSE) 开源。

如果 RixEngine SDK 对您有帮助，欢迎 Star ⭐ 支持我们。
