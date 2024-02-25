# 基础镜像 
FROM openjdk:8-jdk-alpine

# 作者信息
MAINTAINER "sunzheng 14547557@qq.com"

# 添加一个存储空间
VOLUME /tmp

# 暴露容器的8080端口，启动时自定义映射地址到容器的8080端口 此处根据不同的功能开放不同的端口
EXPOSE 8080

# 添加变量，如果使用dockerfile-maven-plugin，则会自动替换这里的变量内容,此处注意打包jar后的jar包名称
ARG JAR_FILE=target/user-center-backend-0.0.1-SNAPSHOT.jar

# 往容器中添加jar包 target.jar是别名，可以任意修改
ADD ${JAR_FILE} target.jar

# 启动镜像自动运行程序
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/urandom","-jar","/target.jar"]

#构建 docker build -t usercenter:v1 .
#启动 docker run -p 8080:8080 -d usercenter:v1