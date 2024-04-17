FROM gradle:7.5.0-jdk17
WORKDIR /app
COPY ./build/libs/InvidualProject-1.0-SNAPSHOT.jar ./

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar InvidualProject-1.0-SNAPSHOT.jar"]