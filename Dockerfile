 FROM openjdk:21-jdk-slim

 WORKDIR /app

 # Copy Gradle wrapper and build files
 COPY gradlew gradlew.bat ./
 COPY gradle/ gradle/
 COPY build.gradle settings.gradle ./

 # Copy source code
 COPY src/ src/

 # Make gradlew executable and build the application
 RUN chmod +x ./gradlew && ./gradlew build -x test

  # Copy the built JAR to the final location
  RUN cp build/libs/ms-transactions-0.0.1-SNAPSHOT.jar app.jar

 EXPOSE 8081

 ENTRYPOINT ["java", "-jar", "app.jar"]
