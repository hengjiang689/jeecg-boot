#!/bin/bash
git pull
mvn clean install
killall -9 java
nohup  java -Dspring.profiles.active=dev -jar jeecg-boot-module-system/target/jeecg-boot-module-system-2.1.1.jar &
tail -900f nohup.out
