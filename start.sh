#!/bin/bash
if [ "$1" = "--init-data" ]; then
    echo "Initializing sample data..."
    java -cp "build/classes:lib/*" com.onlineshopping.Main --init-sample-data
else
    echo "Starting Online Shopping CLI..."
    echo "======================================================="
    java -cp "build/classes:lib/*" com.onlineshopping.Main "$@"
fi
