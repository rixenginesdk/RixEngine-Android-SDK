# RixEngine Android SDK

中文 | [English](README.md)

---

**欢迎使用 RixEngine Android SDK**, 助您充分释放应用内变现潜力的门户。

此 `RixEngine-Android-SDK` 仓库包含：

1. 使用 RixEngine 的示例源代码
2. 开源聚合适配器

# 示例Demo APP
Java Demo App 是示例项目，如何使用 RixEngine SDK 广告。要开始使用示例应用，请按照以下说明操作：

1. 在 Android Studio 中打开您的项目：RixEngine 演示应用 - Java。

2. 添加依赖项

```
    dependencies {
    
        //RixEngine Android SDK (Necessary)
    
        implementation 'io.github.rixenginesdk:rixengine:4.3.1'
    
    }
```

包含在你的 build.gradle（模块：app）中。

3. 在 build.gradle（模块：app）中使用您自己的唯一标识符来更改包名。  
您的唯一标识符应基于您要创建的应用程序名称，或已在 RixEngine 仪表板中创建的应用程序名称。

4. 在每个广告格式的活动代码中更新唯一的 RixEngine 广告单元 ID 值。  
每个广告格式对应您在 RixEngine 仪表板中为之前使用的包创建的一个唯一 RixEngine 广告单元 ID。


# 技术支持

如有任何集成与使用疑问，请联系 [rix-sdk@rixengine.com](mailto:rix-sdk@rixengine.com)。


