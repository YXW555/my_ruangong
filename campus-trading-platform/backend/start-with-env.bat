@echo off
REM Set Aliyun environment variables and start Spring Boot application
REM Make sure you run this script from the backend directory (where pom.xml is located)

set ALIYUN_ACCESS_KEY_ID=LTAI5tCTqqaDCkJUua23F7bb
set ALIYUN_ACCESS_KEY_SECRET=vAAPIsx4ekObIaVWZeL5Bb8LSz9JJi

echo Environment variables set successfully
echo ALIYUN_ACCESS_KEY_ID=%ALIYUN_ACCESS_KEY_ID%
echo ALIYUN_ACCESS_KEY_SECRET=***HIDDEN***

echo.
echo Starting Spring Boot application with Aliyun NLP API...
call mvn spring-boot:run

