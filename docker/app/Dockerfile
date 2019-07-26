FROM openjdk:8-jre-stretch
RUN mkdir /usr/src/myapp
WORKDIR /usr/src/myapp
COPY ./demo-0.0.1-SNAPSHOT.jar /usr/src/myapp
CMD java -jar demo-0.0.1-SNAPSHOT.jar
EXPOSE 8080