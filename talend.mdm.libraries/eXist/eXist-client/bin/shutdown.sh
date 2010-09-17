#!/bin/bash
# -----------------------------------------------------------------------------
# shutdown.sh - Stop Jetty + eXist
#
# $Id: shutdown.sh 8815 2009-04-08 18:00:43Z dizzzz $
# -----------------------------------------------------------------------------

# will be set by the installer
if [ -z "$EXIST_HOME" ]; then
	EXIST_HOME="/home/achen/eXist"
fi

if [ -z "$JAVA_HOME" ]; then
    JAVA_HOME="/home/achen/jdk1.6.0_20"
fi

if [ ! -d "$JAVA_HOME" ]; then
    JAVA_HOME="/usr/lib/jvm/java-6-openjdk/jre"
fi

case "$0" in
	/*)
		SCRIPTPATH=$(dirname "$0")
		;;
	*)
		SCRIPTPATH=$(dirname "$PWD/$0")
		;;
esac

# source common functions and settings
source "${SCRIPTPATH}"/functions.d/eXist-settings.sh
source "${SCRIPTPATH}"/functions.d/jmx-settings.sh
source "${SCRIPTPATH}"/functions.d/getopt-settings.sh

check_exist_home "$0";

set_exist_options;

# set java options
set_java_options;

"${JAVA_HOME}"/bin/java ${JAVA_OPTIONS} ${OPTIONS} -jar "$EXIST_HOME/start.jar" \
	shutdown $*
