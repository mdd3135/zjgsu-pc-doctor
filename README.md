# zjgsu-pc-doctor

 这是浙工商电脑医院预约程序的后端项目，采用springboot + mysql + mirai

## 文件解释：

1. src/main/java/com/example目录下为java代码

2. src/main/resources/application.properties文件是应用配置，包括mysql数据库登录方式，最大上传文件大小，上传文件存储路径等重要配置信息

3. python目录下是一些python代码，调用qq机器人时，通过java调用python再调用mirai机器人。

4. sql目录是数据库建表语句，数据库名为demo，登录用户名为mdd，这要跟上面说的应用配置对应。

## 使用方法

此项目仅在linux平台做测试，服务器操作系统建议ubuntu20.04/22.04，本地开发或调试可以使用任意主流linux发行版，我用的是arch linux。

### 一、克隆项目
终端执行
```
git clone https://github.com/mdd3135/zjgsu-pc-doctor.git
cd zjgsu-pc-doctor/
```

### 二、建立目录

/var/demo/   *存储文件路径*

### 三、安装并配置mysql数据库

1. 安装mysql数据库. *不同操作系统安装过程略有不同，请自行搜索教程*
2. 建立新的用户mdd: ```create user mdd@localhost identified by '313521996';``` *这里的mdd用户名与application.properties配置文件里的用户名一致*
3. 让渡所有权限给用户mdd: ```grant all on *.* to mdd@localhost;```
4. 建立demo数据库: ```create database demo;``` *这里的数据库名与application.properties配置文件里的数据库名一致*
5. 在demo数据库中，分别创建appointment_table, category_table, message_table, user_table，document_table，notice_table数据表，即，分别执行项目目录sql/下的appointment_table.sql, category_table.sql, message_table.sql, user_table.sql，document_table.sql，notice_table.sql，文件中的sql语句
5. 导入测试数据，即，分别执行目录sql/下的appointment_table_export_xxxx.sql, category_table_export_xxxx.sql, user_table_export_xxx.sql文件中的sql语句

### 四、部署mirai机器人以及YiriMirai
1. 安装相应环境，详情请见<https://yiri-mirai.wybxc.cc/docs/quickstart>

### 五、部署springboot项目
1. 在服务器上安装java17及以上版本
2. 前往<https://github.com/mdd3135/zjgsu-pc-doctor/releases>下载最新的demo-0.0.1-SNAPSHOT.jar文件
3. 在终端执行```java -jar demo-0.0.1-SNAPSHOT.jar```，运行项目。注意，一旦关闭当前终端，项目也会随之终止。想要在后台运行项目，则在终端执行```nohup java -jar demo-0.0.1-SNAPSHOT.jar &```