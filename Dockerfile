FROM java:8-alpine
RUN apk add --update --no-cache firefox-esr ca-certificates curl \
 && curl -L https://github.com/mozilla/geckodriver/releases/download/v0.26.0/geckodriver-v0.26.0-linux64.tar.gz | tar xz -C /usr/local/bin \
 && apk del firefox-esr ca-certificates curl

RUN mkdir -p /app
WORKDIR /app
COPY target/uberjar/*-standalone.jar .
CMD java -jar crudik-0.1.0-SNAPSHOT-standalone.jar
EXPOSE 3000