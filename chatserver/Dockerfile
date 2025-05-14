FROM eclipse-temurin:17-jdk-jammy AS builder

WORKDIR /workspace/app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# 최적화 .. COPY src src를 복사하기전에 이 두개의 명령어를 실행하면 build.gradle 파일이 변경되지 않는 한 캐에 저장되어 재사용됨 .
RUN chmod +x ./gradlew #

RUN ./gradlew dependencies

COPY src src

RUN ./gradlew build -x test

FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

COPY --from=builder /workspace/app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
