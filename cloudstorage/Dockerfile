FROM adoptopenjdk/openjdk11:jre-11.0.6_10-alpine
EXPOSE 8080
VOLUME /cloudstorage
ADD target/cloudstorage-0.0.1.jar cloudstorage.jar
ENTRYPOINT ["java","-jar","cloudstorage.jar"]