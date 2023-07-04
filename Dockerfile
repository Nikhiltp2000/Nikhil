FROM openjdk:17-alpine

WORKDIR /app

# JAR file into the container
COPY Controller/target/Controller-0.0.1-SNAPSHOT.war /app/spring-boot.war

# port
EXPOSE 8080

# command to run the application
CMD ["java", "-jar", "spring-boot.war"]
