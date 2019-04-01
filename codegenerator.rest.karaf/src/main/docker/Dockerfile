FROM openjdk:8-jdk
LABEL maintainer="amedrano@lst.tfo.upm.es"
#ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64

RUN mkdir /karaf
COPY assembly/ /karaf/
RUN chmod +x /karaf/bin/*

VOLUME ["/karaf/data"]

EXPOSE 8181 
#EXPOSE 1099 8101 44444 # Karaf ports

#RUN sed -i "21s/out/stdout/" /uAAL/etc/org.ops4j.pax.logging.cfg;

CMD ["/karaf/bin/karaf"]
