FROM azul/zulu-openjdk-alpine:21-latest AS builder
WORKDIR /app

# Gradle 캐시 활용
COPY build.gradle.kts settings.gradle.kts gradlew ./
COPY gradle ./gradle
RUN chmod +x ./gradlew && ./gradlew dependencies --no-daemon

# 소스 복사 및 빌드
COPY src ./src
RUN ./gradlew clean build -x test --no-daemon

FROM azul/zulu-openjdk-alpine:21-latest
WORKDIR /app

COPY --from=builder /app/build/libs/*-SNAPSHOT.jar app.jar

RUN apk add --no-cache curl

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
