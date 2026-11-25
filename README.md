# RixEngine Android SDK

English | [中文](README_ZH.md)

[![Maven Central](https://img.shields.io/maven-central/v/io.github.rixenginesdk/rixengine?label=Maven%20Central&color=4C6FFF)](https://central.sonatype.com/artifact/io.github.rixenginesdk/rixengine)
[![Android](https://img.shields.io/badge/Android-4.1%2B-3DDC84?logo=android&logoColor=white)](https://developer.android.com)
[![License](https://img.shields.io/badge/License-Apache%202.0-4C6FFF)](LICENSE)

> **v4.4.0 Stable** · A more stable, faster, and more capable monetization engine

**Welcome to the RixEngine Android SDK** — your gateway to unlocking the full potential of in-app monetization.

This `RixEngine-Android-SDK` repository contains:

- Example source code for using RixEngine
- Open-source mediation adapters

---

## ✨ What's New in v4.4.0

v4.4.0 is a comprehensive upgrade built on stability, driven by speed, and aimed at revenue.

### 🛡️ More Stable

- **Proven in production**: Built on a stable release validated over the long term across multiple apps and games — safe and reliable.
- **Legacy issues resolved**: Multiple internal iterations fix known production crashes and ad display anomalies, clearing out long-standing bugs.
- **Broad device compatibility**: Adapted to the latest Android versions and a wide range of device form factors, including phones, tablets, and foldables.

### ⚡ Faster

- **Cold / warm start caching**: Built-in dual caching for cold and warm starts — faster preloading on warm start, quicker ad loading on cold start.
- **Weak-network optimization**: The request pipeline is specifically optimized for weak-network environments, significantly improving request success rates in those regions.
- **Low-end device optimization**: Loading and rendering optimizations reduce the risk of out-of-memory issues and add safety safeguards on older devices.

### 🚀 More Capabilities

- **Bidding event callbacks**: New success / failure / impression callback APIs make the bidding pipeline more transparent and controllable.
- **Google Play inline installation**: Shortens the conversion path to lift eCPM and conversion rates.
- **Full ad format coverage**: Interstitial, rewarded video, banner, native, and splash ads.
- **Whitelisted test devices**: Test a single device precisely for more efficient integration and debugging.
- **Mainstream mediation support**: Updated adapters for the latest AdMob, GAM, MAX, Unity Ads, TopOn, and other major mediation platforms.

---

## 📦 Repository Structure

| Directory / File | Description |
| --- | --- |
| [`Demo App - Java`](Demo%20App%20-%20Java) | Java sample project demonstrating the full integration flow for each ad format |
| [`Demo App - Kotlin`](Demo%20App%20-%20Kotlin) | Kotlin sample project with the same features as the Java version |
| [`custom_adapter`](custom_adapter) | Open-source mediation adapters (AdMob / IronSource / MAX / TopOn / TradPlus) |
| `rixengine-sdk-demo.apk` | Ready-to-install demo APK |

---

## 🚀 Quick Start

### Requirements

| Item | Requirement       |
| --- |-------------------|
| JDK | 11 or higher      |
| minSdk | 16 or higher      |

### 1. Clone the repository

```bash
git clone https://github.com/rixenginesdk/RixEngine-Android-SDK.git
```

> GitHub mirror: <https://github.com/rixenginesdk/RixEngine-Android-SDK>

### 2. Open a sample project

In Android Studio, choose **Open** and select `Demo App - Java` or `Demo App - Kotlin`, then wait for Gradle Sync to finish.

### 3. Add the SDK dependency

**Groovy (`build.gradle`)**

```groovy
dependencies {
    // RixEngine Android SDK (required)
    implementation 'io.github.rixenginesdk:rixengine:4.4.0'
}
```

**Kotlin DSL (`build.gradle.kts`)**

```kotlin
dependencies {
    // RixEngine Android SDK (required)
    implementation("io.github.rixenginesdk:rixengine:4.4.0")
}
```

> Make sure the `mavenCentral()` repository is configured; otherwise the dependency cannot be resolved.

### 4. Set your application ID

In `app/build.gradle`, replace `applicationId` with your own unique identifier — ideally matching the app name you created in the RixEngine dashboard.

### 5. Configure ad units

In `AdConfig` (Java / Kotlin), enter the app ID, SID, token, and the ad unit IDs for each ad format that you created in the RixEngine dashboard.

### 6. Run the demo

Connect a physical device or start an emulator, then click **Run ▶** to try the full ad flow.

---

## 🎯 Ad Formats

| Ad Format | Best For | Demo Entry |
| --- | --- | --- |
| Splash | First-screen exposure at app launch | `SplashVideoActivity` / `SplashBannerActivity` |
| Interstitial | Natural pauses such as page transitions or level completion | `InterstitialVideoActivity` / `InterstitialBannerActivity` |
| Rewarded Video | Rewarding users for watching, balancing experience and revenue | `RewardVideoActivity` |
| Banner | Persistent top or bottom exposure | `BannerActivity` |
| Native | Custom layouts that blend into feeds | `NativeActivity` |

---

## 🔌 Mediation Platforms

[`custom_adapter`](custom_adapter) provides open-source adapters for the following mainstream mediation platforms. The demo projects already integrate the matching versions — use them as needed.

| Platform | Adapter Directory | Version Used in Demo |
| --- | --- | --- |
| AdMob / GAM | `custom_adapter/admob` | `play-services-ads 23.5.0` |
| AppLovin MAX | `custom_adapter/max` | `applovin-sdk 13.6.0` |
| LevelPlay (formerly IronSource) | `custom_adapter/ironsource` | `mediation-sdk 9.2.0` |
| TopOn | `custom_adapter/topon` | `core-tpn 6.6.30.2` |
| TradPlus | `custom_adapter/tradplus` | `tradplus 14.5.0.1` |

---

## 💬 Support

For technical support or inquiries, please contact [rix-sdk@rixengine.com](mailto:rix-sdk@rixengine.com).

---

## 📄 License

This project is licensed under the [Apache License 2.0](LICENSE).

If RixEngine SDK helps you, a Star ⭐ would be much appreciated.
