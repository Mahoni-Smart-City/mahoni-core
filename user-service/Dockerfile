FROM openjdk:17-alpine
RUN apk upgrade --no-cache && \
apk add --no-cache libstdc++

CMD ["java", "-jar", "/usr/share/mahoni/mahoni.jar"]

# Add the service itself
ARG JAR_FILE
COPY target/${JAR_FILE} /usr/share/mahoni/mahoni.jar