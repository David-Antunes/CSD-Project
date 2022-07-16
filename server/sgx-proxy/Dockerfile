FROM gradle AS build

WORKDIR app
COPY server/ server
COPY settings.gradle .
RUN gradle bootJar

FROM registry.scontain.com:5050/sconecuratedimages/apps:openjdk-18-alpine
WORKDIR app
COPY --from=build /home/gradle/app/server/sgx-proxy/build/libs .
COPY config config
#CMD ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-jar", "sgx-proxy-0.0.1-SNAPSHOT.jar", "com.csd.blockneat.ProxyBFT"]
CMD ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005","-Xms2G", "-Xmx2G", "-Djavax.net.ssl.trustStore=/app/config/keystore4.p12", "-Djavax.net.ssl.trustStorePassword=password", "-Dhttps.protocols=TLSv1.3","-jar", "sgx-proxy-0.0.1-SNAPSHOT.jar", "com.csd.blockneat.ProxyBFT"]