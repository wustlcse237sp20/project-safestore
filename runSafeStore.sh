#!/bin/sh

# how to include jar files in classpath code from https://alvinalexander.com/blog/post/java/unix-shell-script-i-use-for-compiling-java-programs/

cd src

THE_CLASSPATH=
for i in `ls ../lib/*.jar`
    do
    THE_CLASSPATH=${THE_CLASSPATH}:${i}
done

javac -classpath ".:${THE_CLASSPATH}" application/SafeStore.java
java -classpath ".:${THE_CLASSPATH}" application.SafeStore
