#!/bin/bash

# Clean build artifacts
# Remove compiled classes
if [ -d "build" ]; then
    rm -rf build
    echo "✓ Removed build directory"
fi
echo "Clean complete!"
