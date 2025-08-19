#!/bin/bash

# ================================
# CONFIGURABLE VARIABLES
# ================================
OUTPUT_DIR="out"
JAR_NAME="JCMusic.jar"
MAIN_CLASS="JCMusic.Main"
MANIFEST_VERSION="b0.0.4"
LIB_DIR="lib" # Directory where all external JARs are placed

# ================================
# CLEAN PREVIOUS OUTPUT
# ================================
echo "Cleaning previous build..."
rm -rf "$OUTPUT_DIR" "$JAR_NAME" manifest.txt
mkdir -p "$OUTPUT_DIR/lib"

# ================================
# PROCESS LIBRARIES (copy all jars to output/lib, no extraction)
# ================================
echo "Processing libraries..."
for jar in "$LIB_DIR"/*.jar; do
    echo "Copying jar to output lib: $jar"
    cp "$jar" "$OUTPUT_DIR/lib/"
done

# ================================
# BUILD CLASSPATH FROM LIBS
# ================================
CLASSPATH=""
for jar in "$OUTPUT_DIR/lib"/*.jar; do
    CLASSPATH="$CLASSPATH:$jar"
done
CLASSPATH=${CLASSPATH#:}

# ================================
# COMPILE JAVA SOURCES
# ================================
echo "Compiling Java source files..."
find . -type f -name "*.java" > sources.txt
javac --module-path "$OUTPUT_DIR/lib" \
      --add-modules javafx.controls,javafx.media,javafx.swing,jfx.incubator.richtext \
      -classpath "$CLASSPATH" \
      -d "$OUTPUT_DIR" @sources.txt
rm sources.txt

# ================================
# CREATE MANIFEST FILE
# ================================
echo "Creating manifest..."
cat <<EOF > manifest.txt
Manifest-Version: $MANIFEST_VERSION
Main-Class: $MAIN_CLASS

EOF

# ================================
# CREATE EXECUTABLE FAT JAR
# ================================
echo "Building fat JAR: $JAR_NAME..."
cd "$OUTPUT_DIR"
jar cfm "../$JAR_NAME" ../manifest.txt .
cd ..

echo "Build complete: $JAR_NAME"

echo "Run your jar with:"
echo  "java --module-path out/lib --add-modules javafx.controls,javafx.media,javafx.swing,jfx.incubator.richtext -jar JCMusic.jar"
