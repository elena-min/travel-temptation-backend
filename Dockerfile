FROM gradle:7.5.0-jdk17
WORKDIR /opt/app
COPY ./build/libs/IndividualProject-1.0-SNAPSHOT.jar ./

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar IndividualProject-1.0-SNAPSHOT.jar"]