# Makefile for single-facility simulation

# Include configuration if available
-include config.mk

# Variables
PROJECT_ROOT = .
SIMULATION_CONFIG = $(PROJECT_ROOT)/single-facility.rs
BIN_DIR = $(PROJECT_ROOT)/bin
LIB_DIR = $(PROJECT_ROOT)/lib
MAIN_CLASS = repast.simphony.runtime.RepastMain
JVM_MEMORY ?= -Xmx1g -Xms512m

# Use Java 11 which is required for Repast Simphony 2.11.0
JAVA_HOME_11 = C:/Users/willy/OpenJDK11U-jdk_x86-32_windows_hotspot_11.0.24_8/jdk-11.0.24+8
JAVA_BIN = $(JAVA_HOME_11)/bin/java
JAVAC_BIN = $(JAVA_HOME_11)/bin/javac

# VM arguments from the launch configuration - Windows-compatible format
VM_ARGS = -XX:+IgnoreUnrecognizedVMOptions \
	"--add-opens=java.base/java.lang.reflect=ALL-UNNAMED" \
	"--add-modules=ALL-SYSTEM" \
	"--add-exports=java.base/jdk.internal.ref=ALL-UNNAMED" \
	"--add-exports=java.base/java.lang=ALL-UNNAMED" \
	"--add-exports=java.xml/com.sun.org.apache.xpath.internal=ALL-UNNAMED" \
	"--add-exports=java.xml/com.sun.org.apache.xpath.internal.objects=ALL-UNNAMED" \
	"--add-exports=java.desktop/sun.awt=ALL-UNNAMED" \
	"--add-exports=java.desktop/sun.java2d=ALL-UNNAMED" \
	"--add-opens=java.base/java.lang=ALL-UNNAMED" \
	"--add-opens=java.base/java.util=ALL-UNNAMED"

# Repast Simphony installation directory
REPAST_HOME ?= C:/Users/willy/repastSimphony-new-2.11.0
REPAST_PLUGINS_DIR = $(REPAST_HOME)/eclipse/plugins

# Build simplified classpath based on Eclipse launcher approach
ifeq ($(OS),Windows_NT)
    CLASSPATH_SEP = ;
    # Essential Repast Simphony JARs - include the main bin_and_src jar first
    REPAST_BIN_JAR = $(REPAST_PLUGINS_DIR)/repast.simphony.bin_and_src_2.11.0/repast.simphony.bin_and_src.jar
    REPAST_CORE_JARS = $(REPAST_PLUGINS_DIR)/repast.simphony.runtime_2.11.0/lib/*$(CLASSPATH_SEP)$(REPAST_PLUGINS_DIR)/repast.simphony.core_2.11.0/lib/*$(CLASSPATH_SEP)$(REPAST_PLUGINS_DIR)/repast.simphony.batch_2.11.0/lib/*$(CLASSPATH_SEP)$(REPAST_PLUGINS_DIR)/repast.simphony.data_2.11.0/lib/*$(CLASSPATH_SEP)$(REPAST_PLUGINS_DIR)/repast.simphony.essentials_2.11.0/lib/*$(CLASSPATH_SEP)$(REPAST_PLUGINS_DIR)/libs.ext_2.11.0/lib/*
    CLASSPATH = $(BIN_DIR)$(CLASSPATH_SEP)$(REPAST_BIN_JAR)$(CLASSPATH_SEP)$(REPAST_CORE_JARS)
else
    CLASSPATH_SEP = :
    # Essential Repast Simphony JARs for Unix-like systems
    REPAST_BIN_JAR = $(REPAST_PLUGINS_DIR)/repast.simphony.bin_and_src_2.11.0/repast.simphony.bin_and_src.jar
    REPAST_CORE_JARS = $(REPAST_PLUGINS_DIR)/repast.simphony.runtime_2.11.0/lib/*$(CLASSPATH_SEP)$(REPAST_PLUGINS_DIR)/repast.simphony.core_2.11.0/lib/*$(CLASSPATH_SEP)$(REPAST_PLUGINS_DIR)/repast.simphony.batch_2.11.0/lib/*$(CLASSPATH_SEP)$(REPAST_PLUGINS_DIR)/repast.simphony.data_2.11.0/lib/*$(CLASSPATH_SEP)$(REPAST_PLUGINS_DIR)/repast.simphony.essentials_2.11.0/lib/*$(CLASSPATH_SEP)$(REPAST_PLUGINS_DIR)/libs.ext_2.11.0/lib/*
    CLASSPATH = $(BIN_DIR)$(CLASSPATH_SEP)$(REPAST_BIN_JAR)$(CLASSPATH_SEP)$(REPAST_CORE_JARS)
endif

# Program arguments - point to the .rs scenario directory
PROGRAM_ARGS = $(PROJECT_ROOT)/single-facility.rs

# Default target
.PHONY: help
help:
	@echo "Available targets:"
	@echo "  validate           - Compile and validate the simulation code"
	@echo "  run-eclipse-headless - Information about running via Eclipse"
	@echo "  create-run-script  - Create a helper batch script"
	@echo "  compile           - Compile Java source files"  
	@echo "  clean             - Clean compiled files"
	@echo "  debug-classpath   - Show classpath information for debugging"
	@echo "  help              - Show this help message"
	@echo ""
	@echo "Note: This simulation is designed to run through Eclipse with Repast Simphony plugin."
	@echo "For command-line execution, the Eclipse launcher configuration should be used."

# Debug target to show classpath and configuration
.PHONY: debug-classpath
debug-classpath:
	@echo "=== Debugging Information ==="
	@echo "PROJECT_ROOT: $(PROJECT_ROOT)"
	@echo "REPAST_HOME: $(REPAST_HOME)"
	@echo "REPAST_PLUGINS_DIR: $(REPAST_PLUGINS_DIR)"
	@echo "BIN_DIR: $(BIN_DIR)"
	@echo "MAIN_CLASS: $(MAIN_CLASS)"
	@echo "PROGRAM_ARGS: $(PROGRAM_ARGS)"
	@echo ""
	@echo "=== VM Arguments ==="
	@echo "$(VM_ARGS)"
	@echo ""
	@echo "=== Classpath ==="
	@echo "$(CLASSPATH)"
	@echo ""
	@echo "=== Checking if key directories exist ==="
	@if [ -d "$(REPAST_PLUGINS_DIR)" ]; then echo "✓ Repast plugins directory exists"; else echo "✗ Repast plugins directory NOT found: $(REPAST_PLUGINS_DIR)"; fi
	@if [ -d "$(BIN_DIR)" ]; then echo "✓ Bin directory exists"; else echo "✗ Bin directory NOT found: $(BIN_DIR)"; fi
	@if [ -d "$(PROGRAM_ARGS)" ]; then echo "✓ Scenario directory exists"; else echo "✗ Scenario directory NOT found: $(PROGRAM_ARGS)"; fi

# Simplified approach - use Eclipse's capabilities
.PHONY: run-eclipse-headless  
run-eclipse-headless:
	@echo "Running simulation via Eclipse in headless mode..."
	@echo "This requires Eclipse to be properly set up with Repast Simphony plugin"
	@echo "Note: This approach leverages Eclipse's REPAST_SIMPHONY_LAUNCHER container"
	# This would require Eclipse headless mode - for now, use Eclipse GUI
	@echo "Please run the simulation through Eclipse for now."
	@echo "For automated runs, consider using the batch functionality within Eclipse."

# Alternative: Create a wrapper script that calls Eclipse
.PHONY: create-run-script
create-run-script:
	@echo "Creating batch script to run simulation..."
	@echo "@echo off" > run_simulation.bat
	@echo "cd /d $(PROJECT_ROOT)" >> run_simulation.bat  
	@echo "echo Running single-facility simulation..." >> run_simulation.bat
	@echo "echo Use Eclipse to run the simulation with proper plugin support" >> run_simulation.bat
	@echo "pause" >> run_simulation.bat
	@echo "Batch script created: run_simulation.bat"

# For development: compile and validate
.PHONY: validate
validate: compile
	@echo "Simulation code compiled successfully!"
	@echo "Classes available:"
	@find $(BIN_DIR) -name "*.class" | head -10
	@echo "To run: Use Eclipse with the launcher configuration"
	@echo "  File: launchers/single-facility Model.launch"

# Target for batch runs - optimized for command line  
.PHONY: run-batch
run-batch:
	@echo "Running batch simulation..."
	@echo "Working directory: $(REPAST_PLUGINS_DIR)/repast.simphony.runtime_2.11.0"
	@echo "Batch configuration: $(PROJECT_ROOT)/batch/batch_params.xml"
	cd "$(REPAST_PLUGINS_DIR)/repast.simphony.runtime_2.11.0" && \
	"$(JAVA_BIN)" $(JVM_MEMORY) $(VM_ARGS) \
		-Djava.awt.headless=true \
		-Dplugin.dir="../" \
		-Dboot.config="boot.properties" \
		-cp "$(PROJECT_ROOT)/bin$(CLASSPATH_SEP)../repast.simphony.bin_and_src_2.11.0/repast.simphony.bin_and_src.jar$(CLASSPATH_SEP)lib/*$(CLASSPATH_SEP)../repast.simphony.core_2.11.0/lib/*$(CLASSPATH_SEP)../repast.simphony.batch_2.11.0/lib/*$(CLASSPATH_SEP)../repast.simphony.data_2.11.0/lib/*$(CLASSPATH_SEP)../repast.simphony.essentials_2.11.0/lib/*$(CLASSPATH_SEP)../libs.ext_2.11.0/lib/*" \
		$(MAIN_CLASS) -batch "$(PROJECT_ROOT)/single-facility.rs" "$(PROJECT_ROOT)/batch/batch_params.xml"

# Target to compile Java sources (if needed)
.PHONY: compile
compile:
	@echo "Compiling Java sources..."
	@mkdir -p $(BIN_DIR)
	find src -name "*.java" -exec "$(JAVAC_BIN)" -cp "$(CLASSPATH)" -d $(BIN_DIR) {} +

# Clean compiled files
.PHONY: clean
clean:
	@echo "Cleaning compiled files..."
	rm -rf $(BIN_DIR)/*

# Development target - compile and run
.PHONY: dev
dev: compile run-simulation

# Include output analysis targets from the existing Makefile
.PHONY: copy-latest-outputs
copy-latest-outputs:
	mkdir -p new_analysis
	ls -t sim_modeloutputs*.txt | head -2 | xargs -I{} cp {} new_analysis/

.PHONY: plots
plots:
	Rscript correlation_matrix.R

.PHONY: join-outputs
join-outputs:
	@output_file=$(if $(name),$(name),merged.txt); \
	mkdir -p new_analysis; \
	map_file=$$(ls -t sim_modeloutputs.*.batch_param_map.txt | head -1); \
	data_file=$$(ls -t sim_modeloutputs.*.txt | grep -v batch_param_map | head -1); \
	awk 'NR==FNR && FNR>1 {a[$$1]=$$0; next} FNR>1 && $$1 in a {print a[$$1] "," substr($$0, index($$0,$$2))}' $$map_file $$data_file > new_analysis/$$output_file