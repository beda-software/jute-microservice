FROM eclipse-temurin:19-jre

WORKDIR /

COPY target/jute-microservice-standalone.jar jute-microservice-standalone.jar

EXPOSE 8090

CMD java -jar jute-microservice-standalone.jar