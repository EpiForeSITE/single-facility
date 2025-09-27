#!/bin/bash
# Setup script for single-facility simulation Makefile

echo "Setting up single-facility simulation environment..."

# Check if config.mk exists
if [ ! -f "config.mk" ]; then
    echo "Creating config.mk from template..."
    cp config.mk.template config.mk
    echo "Please edit config.mk to set the correct REPAST_HOME path"
else
    echo "config.mk already exists"
fi

# Check if bin directory exists
if [ ! -d "bin" ]; then
    echo "Creating bin directory..."
    mkdir -p bin
fi

# Check for Java
if ! command -v java &> /dev/null; then
    echo "Warning: Java not found in PATH"
    echo "Please ensure Java is installed and accessible"
else
    echo "Java found: $(java -version 2>&1 | head -n1)"
fi

# Check if we can find a Repast installation
POSSIBLE_REPAST_DIRS=(
    "C:/Program Files/Repast*"
    "/Applications/RepastSimphony*"
    "$HOME/repast*"
    "/opt/repast*"
)

echo "Looking for Repast Simphony installation..."
for dir_pattern in "${POSSIBLE_REPAST_DIRS[@]}"; do
    if ls $dir_pattern 2>/dev/null | head -n1; then
        echo "Possible Repast installation found: $dir_pattern"
        break
    fi
done

echo ""
echo "Setup complete!"
echo "Next steps:"
echo "1. Edit config.mk to set REPAST_HOME to your Repast Simphony installation"
echo "2. Run 'make run-simulation' to start the simulation"
echo "3. Run 'make help' to see all available targets"