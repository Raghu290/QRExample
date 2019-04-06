#!/bin/bash

cd `dirname "$0"`;

if [[ -z $JAVA_HOME ]]; then
    JAVA_HOME=`java -XshowSettings:properties -version 2>&1 | grep -E "java\.home" | awk '{print $3}'`;
fi

echo "Path to \"JAVA_HOME\": $JAVA_HOME"

JAVA_EXEC="$JAVA_HOME/bin/java"

DEBUG_CR_ARGS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005"

JAVA_ARGS="$JAVA_ARGS
-XX:+HeapDumpOnOutOfMemoryError
-Duser.timezone=UTC
-Dfile.encoding=UTF-8
-Djava.net.preferIPv4Stack=true
-Dlogback.configurationFile=logback.xml"


# Minimal version to run CodeRunner
MINIMAL_VERSION=1.8.0

# Check if Java is present and the minimal version requirement
_java=`type $JAVA_EXEC | awk '{ print $ NF }'`
CURRENT_VERSION=`"$_java" -version 2>&1 | awk -F'"' '/version/ {print $2}'`
minimal_version=`echo $MINIMAL_VERSION | awk -F'.' '{ print $2 }'`
current_version=`echo $CURRENT_VERSION | awk -F'.' '{ print $2 }'`
if [ $current_version ]; then
  if [ $current_version -lt $minimal_version ]; then
    echo "Error: Java version is too low to run CodeRunner. At least Java >= $MINIMAL_VERSION needed.";
    exit 1;
  fi
    else
      echo "Not able to find Java executable or version. Please check your Java installation.";
      exit 1;
fi

CODERUNNER_RUN_CMD="env JAVA_HOME=$JAVA_HOME $JAVA_EXEC $JAVA_ARGS $DEBUG_CR_ARGS -cp \"$JAVA_HOME/lib/tools.jar:*:../libs/*\" com.backendless.coderunner.CodeRunnerLoader"

if [[ "$1" == "nohup" ]];
then
  shift
  CODERUNNER_RUN_CMD="nohup ${CODERUNNER_RUN_CMD} $@ &"
else
  CODERUNNER_RUN_CMD="${CODERUNNER_RUN_CMD} $@"
fi


separator="----------------------------------------"
echo -e "${separator}\nRunning CodeRunner ..."
echo $CODERUNNER_RUN_CMD
echo -e "${separator}\n"
eval $CODERUNNER_RUN_CMD
