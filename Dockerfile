FROM eclipse-temurin:25-jre
RUN useradd --system --uid 10001 appuser
WORKDIR /app
COPY target/jute-microservice-standalone.jar jute-microservice-standalone.jar
USER appuser
EXPOSE 8090
CMD ["java", "-jar", "jute-microservice-standalone.jar"]
