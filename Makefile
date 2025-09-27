# Makefile for single-facility simulation

# Include configuration if available
-include config.mk

# Variables
PROJECT_ROOT = .
SIMULATION_CONFIG = $(PROJECT_ROOT)/single-facility.rs
BIN_DIR = $(PROJECT_ROOT)/bin
LIB_DIR = $(PROJECT_ROOT)/lib
MAIN_CLASS = repast.simphony.runtime.RepastMain
JVM_MEMORY ?= -Xmx2g -Xms1g

# VM arguments from the launch configuration
VM_ARGS = -XX:+IgnoreUnrecognizedVMOptions \
	--add-opens java.base/java.lang.reflect=ALL-UNNAMED \
	--add-modules=ALL-SYSTEM \
	--add-exports=java.base/jdk.internal.ref=ALL-UNNAMED \
	--add-exports=java.base/java.lang=ALL-UNNAMED \
	--add-exports java.xml/com.sun.org.apache.xpath.internal=ALL-UNNAMED \
	--add-exports java.xml/com.sun.org.apache.xpath.internal.objects=ALL-UNNAMED \
	--add-exports=java.desktop/sun.awt=ALL-UNNAMED \
	--add-exports=java.desktop/sun.java2d=ALL-UNNAMED \
	--add-opens java.base/java.lang=ALL-UNNAMED \
	--add-opens java.base/java.util=ALL-UNNAMED

# Build classpath - you'll need to adjust REPAST_HOME to point to your Repast installation
REPAST_HOME ?= /path/to/repast/simphony
EXTRA_LIBS ?= 
ifeq ($(OS),Windows_NT)
    CLASSPATH_SEP = ;
    CLASSPATH = $(BIN_DIR)$(CLASSPATH_SEP)$(LIB_DIR)/*$(CLASSPATH_SEP)$(REPAST_HOME)/lib/*$(CLASSPATH_SEP)$(EXTRA_LIBS)
else
    CLASSPATH_SEP = :
    CLASSPATH = $(BIN_DIR)$(CLASSPATH_SEP)$(LIB_DIR)/*$(CLASSPATH_SEP)$(REPAST_HOME)/lib/*$(CLASSPATH_SEP)$(EXTRA_LIBS)
endif

# Default target
.PHONY: help
help:
	@echo "Available targets:"
	@echo "  run-simulation  - Run the single-facility simulation"
	@echo "  compile        - Compile Java source files"
	@echo "  clean          - Clean compiled files"
	@echo "  help           - Show this help message"

# Target to run the simulation (equivalent to the launch configuration)
.PHONY: run-simulation
run-simulation:
	@echo "Running single-facility simulation..."
	@echo "Classpath: $(CLASSPATH)"
	@echo "Simulation config: $(SIMULATION_CONFIG)"
	java $(JVM_MEMORY) $(VM_ARGS) -cp "$(CLASSPATH)" $(MAIN_CLASS) "$(SIMULATION_CONFIG)"

# Target to compile Java sources (if needed)
.PHONY: compile
compile:
	@echo "Compiling Java sources..."
	@mkdir -p $(BIN_DIR)
	find src -name "*.java" -exec javac -cp "$(CLASSPATH)" -d $(BIN_DIR) {} +

# Clean compiled files
.PHONY: clean
clean:
	@echo "Cleaning compiled files..."
	rm -rf $(BIN_DIR)/*

# Target for batch runs (if you have batch configuration)
.PHONY: run-batch
run-batch:
	@echo "Running batch simulation..."
	java $(VM_ARGS) -cp "$(CLASSPATH)" $(MAIN_CLASS) "$(SIMULATION_CONFIG)" -batch

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