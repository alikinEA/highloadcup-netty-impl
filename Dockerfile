FROM shipilev/openjdk-shenandoah
ADD /target/highloadcupNetty-jar-with-dependencies.jar highloadcupNetty.jar
ENV JAVA_OPTS="-Xmx4g -Xms4g -server"
ENTRYPOINT exec java $JAVA_OPTS -jar /highloadcupNetty.jar