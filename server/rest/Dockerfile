FROM gradle AS build
WORKDIR app
COPY bft/ src/
COPY build.gradle .
COPY ../../settings.gradle .

RUN gradle bootJar

FROM openjdk:17.0.2-jdk
WORKDIR app
COPY --from=build /home/gradle/app/build/libs .
COPY ../../config config
CMD ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-jar", "bft-smart-0.0.1-SNAPSHOT.jar", "com.csd.blockneat.BftSmartApplication"]