# Makefile to render Quarto project and measure execution time
# Designed for Git Bash on Windows

.PHONY: render clean time-render

# Default target
render: time-render

# Target that measures time for quarto render
time-render:
	@echo "Starting Quarto render at $$(date)"
	@start_time=$$(date +%s); \
	quarto render; \
	end_time=$$(date +%s); \
	duration=$$((end_time - start_time)); \
	echo "Quarto render completed at $$(date)"; \
	echo "Total render time: $$duration seconds"

# Clean output directory
clean:
	@echo "Cleaning output directory..."
	@if [ -d "_output" ]; then rm -rf _output; fi
	@echo "Output directory cleaned"

# Full clean and render with timing
full-render: clean time-render
	@echo "Full clean and render completed"
