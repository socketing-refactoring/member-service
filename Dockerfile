FROM bellsoft/liberica-openjdk-alpine:21
WORKDIR /app
COPY ./build/libs/member-service.jar /app/member-service.jar
ENTRYPOINT ["java", "-jar", "member-service.jar"]
