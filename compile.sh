#!/bin/bash

# ================================
# CONFIGURABLE VARIABLES
# ================================
OUTPUT_DIR="out"
JAR_NAME="JCMusic.jar"
MAIN_CLASS="JCMusic.Main"
MANIFEST_VERSION="b0.0.3"

# ================================
# CLEAN PREVIOUS OUTPUT
# ================================
rm -rf "$OUTPUT_DIR" "$JAR_NAME" manifest.txt
mkdir -p "$OUTPUT_DIR"

# ================================
# COMPILE JAVA SOURCES
# ================================
echo "compiling java source files..."
find . -name "*.java" > sources.txt
javac -d "$OUTPUT_DIR" @sources.txt
rm sources.txt

# ================================
# CREATE MANIFEST FILE
# ================================
echo "creating manifest..."
cat <<EOF > manifest.txt
Manifest-Version: $MANIFEST_VERSION
Main-Class: $MAIN_CLASS

EOF

# ================================
# CREATE EXECUTABLE JAR
# ================================
echo "building JAR: $JAR_NAME..."
jar cfm "$JAR_NAME" manifest.txt -C "$OUTPUT_DIR" .

echo "build complete: $JAR_NAME"
