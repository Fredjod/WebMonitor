# Base Alpine Linux based image with OpenJDK JRE only
FROM openjdk:11.0.3-jre-slim-stretch

ENV TZ 'Europe/Paris'
RUN mkdir -p /usr/webmon
RUN mkdir -p /usr/webmon/conf

# copy application WAR (with libraries inside)
COPY web-monitor-*-jar-with-dependencies.jar /usr/webmon/web-monitor.jar
COPY mvnw /usr/webmon/mvnw
ADD .mvn /usr/webmon/.mvn

VOLUME /usr/webmon/conf
VOLUME /usr/webmon/logs
WORKDIR /usr/webmon

# Launch Spring Boot service
RUN /usr/webmon/mvnw spring-boot:run

# specify default command used by crontab
CMD ["/usr/bin/java", "-jar", "web-monitor.jar"]