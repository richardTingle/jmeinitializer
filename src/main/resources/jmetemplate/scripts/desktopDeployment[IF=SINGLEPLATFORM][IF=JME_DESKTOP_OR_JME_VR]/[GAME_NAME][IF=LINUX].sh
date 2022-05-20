#!/bin/bash
jre/bin/java -XX:MaxRAMPercentage=60 -classpath "lib/*" [GAME_PACKAGE].[GAME_NAME]
exit 0