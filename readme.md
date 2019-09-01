This is a library of android. you can import it to to your project as library. 
But it is a chinese language currently, and i don't want to translate it to english interface.

Open your android studio, and file -> new -> import module,and to select this library path to import.
The library dependies the flow :
dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])
    testImplementation 'junit:junit:4.12'
    implementation 'com.google.code.gson:gson:2.8.5'
    implementation 'com.squareup.okhttp3:okhttp:4.1.0'
    implementation 'androidx.core:core:1.0.2'
}

这是一个应用内安装apk的案例。以库的方式导入到你的安卓项目中。
为了兼容性，使用了androidX支持库。如果你的项目中使用了老的支持库，则可能在编译上有些麻烦。
androidx有关的问题，可以看我的博客 https://www.cnblogs.com/htsky/p/11404946.html

博客连接里没有说明如何使用这个库。
大致步骤是这样的：启动库中的activity，然后点击按钮依此的动作为 ———— 查找新版本 —— 下载文件（显示下载进度） —— 启动安装（打开系统的打包工具）。
启动activity前，需要在 intent中传入 更新版本的地址，这个地址应该返回一个json结果。
intent.putExtra("update","http://xxx.com/update.json");
json应包含版本号、描述、下载地址、MD5校验码。具体看 pojo/VersionInfo
{
version: 1.1,
md5: "sdfsdfsdfsdfsdfsdfsfdsswerssdfserwcgkpgk",
url: "http://xxx.com/download/tobacoo.apk",
desc: "1、应用内更新app。 2、修复发现的缺陷。 3、显示二维码更新地址。"
}

启动activity后，将显示当前的版本。新老版本号都得到了。但该例子没有做对比和处理。
兼容android P，所以当前的app的版本号为long的格式。
在研究时，一开始，启动安装，总是提示几种打开的方式。现在已经解决了，直接打开打包工具。

另外就是，我在这里没有做md5下载后的校验和下载文件已经下载过的检查，以及中断下载过程。如果有要求应自行完善。
