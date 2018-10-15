#!/bin/bash

THEMES="dark light green"

for THEME in $THEMES
do
    export THEME
    yarn node-sass --functions=functions.js -r -o ../api/src/main/resources/stylesheet/$THEME src/
done
