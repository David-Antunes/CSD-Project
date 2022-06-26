FROM gradle AS build
WORKDIR app
COPY server/ server
COPY settings.gradle .

RUN gradle bootJar

FROM openjdk:17.0.2-jdk
WORKDIR app
COPY --from=build /home/gradle/app/server/client/build/libs .
COPY config config
CMD ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-Djavax.net.ssl.trustStore=/app/config/users.pkcs12", "-Djavax.net.ssl.trustStorePassword=users", "-Dhttps.protocols=TLSv1.3","-Djavax.net.ssl.keyStoreType=pkcs12","-jar", "client-0.0.1-SNAPSHOT.jar", "com.csd.blockneat.Client"]