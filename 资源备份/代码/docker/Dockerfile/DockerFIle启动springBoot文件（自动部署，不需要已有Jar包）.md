# Docker 镜像构建 自动安装maven:3.5和 jdk8  
FROM maven:3.5-jdk-8-alpine as builder

# 作者信息
MAINTAINER "sunzheng 14547557@qq.com"

# Copy local code to the container image. 指定工作目录路径，将要启动的jar包放进这个目录
WORKDIR /app
COPY pom.xml .
COPY src ./src

# Build a release artifact. 对项目再进行一次打包操作，速度较慢但是全自动化生成，会重新生成一个实际项目对应的jar包名称
RUN mvn package -DskipTests

# Run the web service on container startup.  将下面的jar替换为实际的项目jar(实际项目名-0.0.1-SNAPSHOT.jar), 后面为启动时的追加命令
CMD ["java","-jar","/app/target/user-center-backend-0.0.1-SNAPSHOT.jar","--spring.profiles.active=prod"]



#构建 docker build -t usercenter:v1 .
#启动 docker run -p 8080:8080 -d usercenter:v1