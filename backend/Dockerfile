FROM openjdk:21-jdk-slim

WORKDIR /app

# 复制打包后的jar文件
COPY target/smart-consume-1.0.0.jar app.jar

# 暴露端口
EXPOSE 8080

# 启动命令
ENTRYPOINT ["java", "-jar", "app.jar"]
