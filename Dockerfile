FROM eclipse-temurin:25-jre-alpine
RUN adduser -S -u 10001 appuser
WORKDIR /app
COPY target/jute-microservice-standalone.jar jute-microservice-standalone.jar
USER appuser
EXPOSE 8090
CMD ["java", "-jar", "jute-microservice-standalone.jar"]
