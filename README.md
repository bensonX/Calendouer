# Calendouer
豆瓣日历
Calendouer-豆瓣日历(农历, 天气, 电影推荐)

## 功能
- 日历（阳历/阴历，节气，节日）
- 天气预报
- 备忘提醒*
- 电影推荐**
- 图书推荐*
- 桌面小部件

\* 开发中  
\** 功能增强中

## IOS
[Desgard/Calendouer-iOS](https://github.com/Desgard/Calendouer-iOS)

## 注意
- 请替换 `AndroidManifest.xml` 中高德地图的apikey `com.amap.api.v2.apikey`

```
<application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:supportsRtl="true"
        android:theme="@style/AppTheme">
        <meta-data
            android:name="com.amap.api.v2.apikey"
            android:value="your apikey" />
```

- 暂时没有对 `apikey` 进行验证，为避免后期验证策略修改最好替换
- 若使用 `Firebase` 请替换 `app` 目录下的 `google-services.json` 文件，否则会导致错误。
- 若不使用 `Firebase` 请删除 `app` 目录下的 `google-services.json` 文件。并删除 `build.gradle(Project)` 文件中关于 Google Services 的依赖，以及 `build.gradle(Module)` 中对 Firebase 的依赖，否则会导致错误。

```
//build.gradle(Project)
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:2.3.0'
        // 删除下面这行
        classpath 'com.google.gms:google-services:3.0.0'
    }
}

//build.gradle(Module)
dependencies {
    compile fileTree(include: ['*.jar'], dir: 'libs')
    androidTestCompile('com.android.support.test.espresso:espresso-core:2.2.2', {
        exclude group: 'com.android.support', module: 'support-annotations'
    })

    ...
    // 删除下面这行
    compile 'com.google.firebase:firebase-crash:9.6.1'
    ...
```

## 下载：
- [Google Play](https://play.google.com/store/apps/details?id=cn.sealiu.calendouer)
- 百度手机助手——由于更新后网址会变化，请直接搜索 `豆瓣日历` (注意版本号)

## 反馈
- email
- [去豆瓣小组畅聊](https://www.douban.com/group/calendouer)
