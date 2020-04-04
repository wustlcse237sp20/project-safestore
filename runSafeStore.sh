#!/bin/sh

THE_CLASSPATH=
PROGRAM_NAME=application/SafeStore.java
cd src
for i in `ls ../lib/*.jar`
    do
    THE_CLASSPATH=${THE_CLASSPATH}:${i}
done

javac -classpath ".:${THE_CLASSPATH}" $PROGRAM_NAME

if [ $? -eq 0 ]
then
    echo "compile worked!"
fi
java -classpath ".:${THE_CLASSPATH}" application.SafeStore
