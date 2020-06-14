@echo off
title 12345

@echo on

set JAVA_HOME={your java_home}

%JAVA_HOME%\jre\bin\java -Xmx4096m -Xms1024m -XX:+UseG1GC -jar -Dspring.config.location=application.properties cedis-0.0.1-SNAPSHOT.jar >> cedis.log
