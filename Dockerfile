FROM adoptopenjdk/openjdk11:alpine

RUN apk add --update --no-cache dumb-init wget

WORKDIR /app

COPY docker-cmd.sh docker-cmd.sh
COPY build/libs/auth-backend-0.0.1-SNAPSHOT.jar app.jar

ARG APP_VERSION="1970-01-01-00-00-00-unknown"
ENV APP_VERSION=${APP_VERSION}

ENTRYPOINT ["/usr/bin/dumb-init", "--"]

EXPOSE 8080

RUN ["chmod", "+x", "./docker-cmd.sh"]
CMD ["./docker-cmd.sh"]
