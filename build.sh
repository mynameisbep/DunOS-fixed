#!/bin/sh
# DunDunDunOS Build Script
# Compiles and runs the DunOS desktop operating system simulator

echo "========================================"
echo "  DunDunDunOS - Build Script"
echo "========================================"
echo ""

# Find Java
JAVA=$(which java 2>/dev/null)
JAVAC=$(which javac 2>/dev/null)

if [ -z "$JAVAC" ]; then
    # Try common paths
    for dir in /usr/lib/jvm/*/bin; do
        if [ -x "$dir/javac" ]; then
            JAVAC="$dir/javac"
            JAVA="$dir/java"
            break
        fi
    done
fi

if [ -z "$JAVAC" ]; then
    echo "ERROR: Java compiler (javac) not found."
    echo "Please install JDK 21+ to build DunDunDunOS."
    exit 1
fi

echo "  Java: $JAVA"
echo "  Javac: $JAVAC"
echo ""

# Clean and create output directory
echo "[BUILD] Cleaning previous build..."
rm -rf out/
mkdir -p out
echo "[BUILD] Output directory created."

# Find all Java source files
echo "[BUILD] Finding source files..."
SOURCES=$(find src -name "*.java" | sort)
echo "[BUILD] Found $(echo "$SOURCES" | wc -l) source files."

# Compile
echo "[BUILD] Compiling..."
echo ""

$JAVAC -d out -sourcepath src $SOURCES 2>&1

if [ $? -ne 0 ]; then
    echo ""
    echo "========================================"
    echo "  BUILD FAILED"
    echo "========================================"
    exit 1
fi

echo ""
echo "[BUILD] Compilation successful!"

# Copy resources
echo "[BUILD] Copying resources..."
if [ -d "Images" ]; then
    mkdir -p out/Images
    cp -r Images/* out/Images/ 2>/dev/null
    echo "[BUILD] Images copied."
fi

# Create output directories
for dir in Desktop Documents Downloads Music Pictures Videos RecycleBin Fonts Config Temp Logs Users; do
    mkdir -p "out/$dir"
done
echo "[BUILD] Resource directories created."

echo ""
echo "========================================"
echo "  BUILD COMPLETE"
echo "========================================"
echo ""
echo "To run:"
echo "  $JAVA -cp out dunos.Main"
echo ""
echo "Or simply:"
echo "  ./run.sh"
echo ""

