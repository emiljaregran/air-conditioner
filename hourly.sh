#!/bin/bash

DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )

java -jar $DIR/sensor.jar A
printf "\n"
java -jar $DIR/sensor.jar B
printf "\n"
java -jar $DIR/sensor.jar C
printf "\n"
java -jar $DIR/price.jar
