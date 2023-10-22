FROM openjdk:17-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} library-app.jar
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /library-app.jar"]