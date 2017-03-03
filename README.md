# Calendouer
豆瓣日历
Calendouer-豆瓣日历(农历, 天气, 电影推荐)

##功能
- 日历（阳历/阴历，节气，节日）
- 天气预报
- 备忘提醒*
- 电影推荐**
- 图书推荐*
- 桌面小部件

\* 开发中  
\** 功能增强中

##IOS
**希望IOS开发使用相同的配色和图标以达到一致性体验**
- colorPrimary: \#4CAF50
- colorPrimaryDark: \#388E3C
- android default font family: Roboto and Noto sans CJK**
- mipmap: Calendouer\app\src\main\res\mipmap-xxxhdpi
- drawable: Calendouer\app\src\main\res\drawable

\** 字体不做要求

##注意
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

- 暂时没有对 `apikey` 进行验证，为避免后期验证策略修改最好需要替换

##下载：
- [Google Play](https://www.douban.com/link2/?url=https%3A%2F%2Fplay.google.com%2Fstore%2Fapps%2Fdetails%3Fid%3Dcn.sealiu.calendouer)
- 百度手机助手——搜索 `豆瓣日历` (注意版本号)

##反馈
- email
- [去豆瓣小组畅聊](https://www.douban.com/group/calendouer)
