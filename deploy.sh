#!/usr/bin/env bash

REPOSITORY=/home/ec2-user/tabling
cd $REPOSITORY

APP_NAME=tabling
JAR_NAME=$(ls $REPOSITORY/build/libs/ | grep 'SNAPSHOT.jar' | tail -n 1)
JAR_PATH=$REPOSITORY/build/libs/$JAR_NAME

CURRENT_PID=$(pgrep -f $APP_NAME)

if [ -z $CURRENT_PID ]
then
  echo "> 종료할 애플리케이션이 없습니다."
else
  echo "> kill -9 $CURRENT_PID"
  kill -15 $CURRENT_PID
  sleep 5
fi

cp /home/ec2-user/tabling/build/libs/tabling_batch-0.0.1-SNAPSHOT.jar /var/lib/jenkins/workspace/usergrade-batch/tabling_batch-0.0.1-SNAPSHOT.jar
echo "> Deploy - $JAR_PATH "
nohup java -jar $JAR_PATH > /home/ec2-user/nohub.out 2> /home/ec2-user/nohub.out < /home/ec2-user/nohub.out &
