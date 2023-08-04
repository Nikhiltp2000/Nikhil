FROM openjdk:17-alpine

WORKDIR /app


ENV SPRING_PROFILES_ACTIVE=prod

ENV ACCESS_KEY=AKIA46CVBZVVPE4REYWZ
ENV SECRET_KEY=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

# JAR file into the container
COPY Controller/target/Controller-0.0.1-SNAPSHOT.war /app/spring-boot.war

# port
EXPOSE 8080

# command to run the application
CMD ["java", "-jar", "spring-boot.war"]
