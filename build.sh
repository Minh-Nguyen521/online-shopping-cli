#!/bin/bash

if [ -d "build" ]; then
    rm -rf build
    echo "✓ Removed build directory"
fi
echo "Clean complete!"

if ! command -v javac &> /dev/null; then
    echo "Error: Java compiler (javac) is not installed or not in PATH"
    exit 1
fi

mkdir -p build/classes
mkdir -p lib

# download dependencies 
MYSQL_JAR="lib/mysql-connector-j-8.2.0.jar"
if [ ! -f "$MYSQL_JAR" ]; then
    curl -L -o "$MYSQL_JAR" "https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.2.0/mysql-connector-j-8.2.0.jar"
fi

# compile
javac -d build/classes \
      -cp "lib/*" \
      -sourcepath src/main/java \
      src/main/java/com/onlineshopping/*.java \
      src/main/java/com/onlineshopping/model/*.java \
      src/main/java/com/onlineshopping/database/*.java \
      src/main/java/com/onlineshopping/dbquery/*.java \
      src/main/java/com/onlineshopping/service/*.java \
      src/main/java/com/onlineshopping/ui/*.java \
      src/main/java/com/onlineshopping/util/*.java

if [ $? -ne 0 ]; then
    echo "Compilation failed!"
    exit 1
fi

echo "✓ Build successful!"