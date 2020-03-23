FROM gradle:jdk8-alpine AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon 

FROM openjdk:8-jre-alpine
EXPOSE 9096
COPY --from=build /home/gradle/src/build/libs/*.jar /app/web-scraper-service.jar

ENTRYPOINT ["java", "-jar", "/app/web-scraper-service.jar", "-Dspring.profiles.active=production"]