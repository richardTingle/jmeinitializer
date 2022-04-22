#!/bin/bash
if [ "$RUNTIME" = "" ];
then
    export RUNTIME="`which podman`"
    if [ "$RUNTIME" = "" ];
    then
        export RUNTIME="`which docker`"
    fi
fi
$RUNTIME rmi jmeInitialiser
$RUNTIME build -t jmeInitialiser .