ARG JDK_VERSION=17
FROM amazoncorretto:17-alpine3.17-jdk AS corretto-jdk17

FROM corretto-jdk${JDK_VERSION} AS base
ARG JDK_VERSION
WORKDIR /workspace/app
COPY ./gradle gradle
COPY ./*.gradle ./
COPY ./gradlew ./
COPY ./src src
RUN ./gradlew build --parallel -x test

FROM corretto-jdk${JDK_VERSION} AS corretto-jdk
ARG JDK_VERSION
ARG JAR_FILE=/workspace/app/build/libs/*.jar
COPY --from=base ${JAR_FILE} /app/app.jar
RUN mkdir /app/unpacked && \
    cd /app/unpacked && \
    unzip ../app.jar && \
    cd .. && \
    $JAVA_HOME/bin/jdeps \
    --ignore-missing-deps \
    --print-module-deps \
    -q \
    --recursive \
    --multi-release ${JDK_VERSION} \
    --class-path="./unpacked/BOOT-INF/lib/*" \
    --module-path="./unpacked/BOOT-INF/lib/*" \
    ./app.jar > /deps.info
RUN apk add --no-cache binutils

# Build small JRE image
RUN $JAVA_HOME/bin/jlink \
    --verbose \
    --add-modules $(cat /deps.info) \
    --strip-debug \
    --no-man-pages \
    --no-header-files \
    --compress=2 \
    --output /customjre

# main app image
FROM alpine:3.18.2
ENV JAVA_HOME=/jre
ENV PATH="${JAVA_HOME}/bin:${PATH}"
ARG APPLICATION_USER=appuser
ARG JAR_FILE=/workspace/app/build/libs/*.jar
COPY --from=corretto-jdk /customjre $JAVA_HOME
RUN adduser --no-create-home -u 1000 -D $APPLICATION_USER
RUN mkdir /app && chown -R $APPLICATION_USER /app
USER 1000
COPY --chown=1000:1000 --from=base ${JAR_FILE} /app/app.jar
WORKDIR /app
EXPOSE 8080

ENTRYPOINT [ "/jre/bin/java", "-jar", "/app/app.jar" ]