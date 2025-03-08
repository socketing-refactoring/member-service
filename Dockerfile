FROM bellsoft/liberica-openjdk-alpine:21

WORKDIR /app

# Copy the jar file into the container
COPY ./build/libs/member-service.jar /app/member-service.jar

# Optionally list the contents to verify the file is copied correctly
RUN ls -l /app

# Run the jar file with java
ENTRYPOINT ["java", "-jar", "member-service.jar"]
