FROM gradle AS build
WORKDIR app
COPY src/ src/
COPY build.gradle .
COPY settings.gradle .

RUN gradle bootJar

FROM openjdk:17.0.2-jdk
WORKDIR app
COPY --from=build /home/gradle/app/build/libs .
COPY config config
CMD ["java", "-jar","bft-smart-0.0.1-SNAPSHOT.jar", "com.csd.bftsmart.BftSmartApplication"]