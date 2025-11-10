#!/bin/bash

if ! command -v javac &> /dev/null; then
    echo "Error: Java compiler (javac) is not installed or not in PATH"
    exit 1
fi

mkdir -p build/classes
mkdir -p lib

# Download dependencies if they don't exist
MYSQL_JAR="lib/mysql-connector-j-8.2.0.jar"
if [ ! -f "$MYSQL_JAR" ]; then
    curl -L -o "$MYSQL_JAR" "https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.2.0/mysql-connector-j-8.2.0.jar"
fi

# BCrypt
BCRYPT_JAR="lib/jbcrypt-0.4.jar"
if [ ! -f "$BCRYPT_JAR" ]; then
    curl -L -o "$BCRYPT_JAR" "https://repo1.maven.org/maven2/org/mindrot/jbcrypt/0.4/jbcrypt-0.4.jar"
fi

# Compile Java files
javac -d build/classes \
      -cp "lib/*" \
      -sourcepath src/main/java \
      src/main/java/com/onlineshopping/*.java \
      src/main/java/com/onlineshopping/model/*.java \
      src/main/java/com/onlineshopping/database/*.java \
      src/main/java/com/onlineshopping/db/*.java \
      src/main/java/com/onlineshopping/service/*.java \
      src/main/java/com/onlineshopping/ui/*.java \
      src/main/java/com/onlineshopping/util/*.java

if [ $? -ne 0 ]; then
    echo "Compilation failed!"
    exit 1
fi

echo "✓ Build successful!"