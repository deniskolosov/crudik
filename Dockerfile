FROM java:8-alpine
RUN mkdir -p /app
WORKDIR /app
COPY target/uberjar/*-standalone.jar .
CMD java -jar crudik-0.1.0-SNAPSHOT-standalone.jar
EXPOSE 3000