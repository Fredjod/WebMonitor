# Base Alpine Linux based image with OpenJDK JRE only
FROM openjdk:11.0.3-jre-slim-stretch

ENV TZ 'Europe/Paris'
RUN mkdir -p /usr/webmon
RUN mkdir -p /usr/webmon/conf

# copy application WAR (with libraries inside)
COPY web-monitor-*-jar-with-dependencies.jar /usr/webmon/web-monitor.jar

VOLUME /usr/webmon/conf

# specify default command
WORKDIR /usr/webmon
CMD ["/usr/bin/java", "-jar", "web-monitor.jar"]