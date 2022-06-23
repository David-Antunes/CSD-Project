FROM gradle AS build
WORKDIR app
COPY server/ server
COPY settings.gradle .

RUN gradle bootJar

FROM openjdk:17.0.2-jdk
WORKDIR app
COPY --from=build /home/gradle/app/server/rest/build/libs .
COPY config config
CMD ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-jar", "rest-0.0.1-SNAPSHOT.jar", "com.csd.blockneat.BlockneatApplication"]