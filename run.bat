@echo off
java -Xms512m -Xmx1024m -cp bin;lib/jython.jar;lib/slf4j-api-1.5.8.jar;lib/mina-core-2.0.0-M6.jar;lib/slf4j-jdk14-1.5.8.jar;lib/commons-compress-1.0.jar;lib/junit-4.6.jar org.hyperion.Server
pause