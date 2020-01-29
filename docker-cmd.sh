#!/bin/sh
set -e

APP_VERSION=${APP_VERSION:="1970-01-01-00-00-00-unknown"}
XMS=${XMS:="256m"}
XMX=${XMX:="512m"}
COUNTRY=${COUNTRY:="US"}
LANGUAGE=${LANGUAGE:="en"}
TIMEZONE=${TIMEZONE:="UTC"}
JAVA_TOOL_OPTIONS=${JAVA_TOOL_OPTIONS:="-XX:+UseContainerSupport"}

JAVA_TOOL_OPTIONS="${JAVA_TOOL_OPTIONS} \
                   -Duser.country=${COUNTRY} \
                   -Duser.language=${LANGUAGE} \
                   -Duser.timezone=${TIMEZONE}"

JAVA_OPTIONS=${JAVA_OPTIONS:="-server -Xms${XMS} -Xmx${XMX}"}

JAVA_TOOL_OPTIONS=$(echo "${JAVA_TOOL_OPTIONS}" | xargs)
echo "Set up JAVA_TOOL_OPTIONS: ${JAVA_TOOL_OPTIONS}"

JAVA_OPTIONS=$(echo "${JAVA_OPTIONS}" | xargs)
echo "Set up JAVA_OPTIONS: ${JAVA_OPTIONS}"

exec java ${JAVA_OPTIONS} -jar app.jar
