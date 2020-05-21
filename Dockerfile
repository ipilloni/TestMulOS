FROM openjdk:8
ADD target/demo-multi.jar demo-multi.jar
EXPOSE 8089
ENTRYPOINT ["java", "-jar", "demo-nica.jar"]