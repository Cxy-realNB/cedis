#!/bin/bash

export JAVA_HOME=/opt/jdk/jdk1.8.0_221
export JRE_HOME=${JAVA_HOME}/jre

${JRE_HOME}/bin/java -Xmx512m -Xms256m -XX:+UseG1GC -jar -Djava.security.egd=file:/dev/./urandom -Dspring.config.location=/tmp/cedis/application.properties /tmp/cedis/cedis-0.0.1-SNAPSHOT.jar >> /tmp/cedis/cedis.log