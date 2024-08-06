FROM gradle:8.5 as builder

COPY . /home/build/project
WORKDIR /home/build/project
RUN gradle build

RUN java -Djarmode=layertools -jar build/libs/translator-fintech.jar extract

FROM openjdk:21

WORKDIR /app

COPY --from=builder /home/build/project/dependencies/ ./
COPY --from=builder /home/build/project/snapshot-dependencies/ ./
COPY --from=builder /home/build/project/spring-boot-loader/ ./
COPY --from=builder /home/build/project/application/ ./

ENTRYPOINT ["java","-Duser.timezone=Europe/Moscow", "org.springframework.boot.loader.launch.JarLauncher"]