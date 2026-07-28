#!/bin/sh
# DunDunDunOS Run Script
# Compiles (if needed) and runs the OS simulator

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$SCRIPT_DIR"

# Find Java
JAVA=$(which java 2>/dev/null)
JAVAC=$(which javac 2>/dev/null)

if [ -z "$JAVA" ]; then
    for dir in /usr/lib/jvm/*/bin; do
        [ -x "$dir/java" ] && JAVA="$dir/java" && break
    done
fi

if [ -z "$JAVAC" ]; then
    for dir in /usr/lib/jvm/*/bin; do
        [ -x "$dir/javac" ] && JAVAC="$dir/javac" && break
    done
fi

if [ ! -d "out" ] || [ ! -f "out/dunos/Main.class" ]; then
    echo "[RUN] Build required. Running build script..."
    if [ -f "build.sh" ]; then
        sh build.sh
    else
        echo "ERROR: build.sh not found!"
        exit 1
    fi
fi

echo "[RUN] Starting DunDunDunOS..."
echo ""

$JAVA -cp out dunos.Main 2>&1

if [ $? -ne 0 ]; then
    echo ""
    echo "[RUN] DunDunDunOS exited with error."
    echo "Try rebuilding: sh build.sh"
    exit 1
fi

