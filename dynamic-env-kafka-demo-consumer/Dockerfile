FROM maven:3.6.3-jdk-8 AS build

WORKDIR /app

COPY pom.xml .

COPY ci_settings.xml .

RUN mvn dependency:go-offline -B -s ci_settings.xml

COPY src ./src

RUN mvn package -DskipTests -s ci_settings.xml

FROM openjdk:8-jdk-alpine3.9

WORKDIR /app

RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.aliyun.com/g' /etc/apk/repositories && \
    apk add curl

COPY --from=build /app/target/*.jar ./app.jar

EXPOSE 8012

CMD ["java", "-jar", "app.jar"]
