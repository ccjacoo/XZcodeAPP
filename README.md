# XZcodeAPP

使用说明

我用的是花生壳映射到公网，需要花钱，如果只是本地使用的话需要将
\Front-End\app\src\main\java\cn\edu\heuet\login\constant 下的NetConstant.java文件里修改：baseService = "localhost:端口";   即可

如何部署文件？
用 IntelliJ Idea 打开 Back-End 文件
用 Android Studio 打开 Front-End 文件
用 Navicat 导入 login_dbt.sql 文件


如何快速打开Android文件？
修改build.gradle文件里的gradle:4.1.1版本号为你自己的版本
修改\gradle\wrapper的gradle-wrapper.properties文件中：distributionUrl=https\://services.gradle.org/distributions/gradle-6.5-bin.zip ，改为你自己的zip文件。
