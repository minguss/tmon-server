#####################
# author  minguss
# version 1.0
# tmon-server (based on vert.x framework)docker using a Java verticle packaged as a fatjar
# To build:
#  docker build -t sample/vertx-java-fat .
# To run:
#   docker run -t -i -p 8080:8080 sample/vertx-java-fat
#####################

FROM java:8-jre 

ENV VERTICLE_FILE tmon-server-1.0.0-fat.jar

# Set the location of the verticles
ENV VERTICLE_HOME /usr/verticles

EXPOSE 8080

# Copy your fat jar to the container
COPY build/libs/tmon-server-1.0.0-fat.jar $VERTICLE_HOME/

# Launch the verticle
WORKDIR $VERTICLE_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["exec java -jar $VERTICLE_FILE 2019451152"]

