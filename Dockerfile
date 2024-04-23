FROM amazoncorretto:21

WORKDIR /
COPY ./build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "-Duser.timezone=Asia/Seoul", "-Dspring.profiles.active=server", "/app.jar"]