# Quick Start Guide

This guide will help you run your first simulation in under 10 minutes.

## Prerequisites

- Java 8 or higher installed on your system
- Basic familiarity with running Java applications

## Running Your First Simulation

### Step 1: Download and Launch

1. **If you have the pre-built model archive:**
   - Extract the archive to a folder
   - Windows: Double-click `start_model.bat`
   - Mac/Linux: Double-click `start_model.command`

2. **If you have the source code:**
   - Open in Eclipse with Repast Simphony
   - Right-click the project → Run As → Java Application
   - Select `SingleFacilityBuilder` as the main class

### Step 2: Configure Basic Parameters

When the Repast GUI opens, you'll see a parameters panel. For your first run, you can use the defaults, but here are the key settings:

**Essential Parameters:**
- `longTermAcuteCareBeta`: 0.0615 (transmission rate)
- `importationRate`: 0.206 (probability patients arrive infected)
- `isolationEffectiveness`: 0.5 (50% reduction in transmission when isolated)
- `isBatchRun`: false (enables detailed output files)

### Step 3: Initialize and Run

1. Click **"Initialize"** button
2. Wait for initialization to complete (should take a few seconds)
3. Click **"Start"** button to begin simulation
4. The simulation will run automatically

### Step 4: Monitor Progress

- Watch the console output for progress messages
- The simulation runs for 15 years by default (10 year burn-in + 5 year measurement)
- Each tick represents one day
- You'll see messages like "Current tick: 3651" showing daily progress

### Step 5: Review Results

After completion, check these output files in your project directory:

**Key Files to Examine:**
- `simulation_results.txt`: Summary statistics
- `daily_population_stats.txt`: Daily counts over time
- `transmissions.txt`: Individual transmission events
- `clinicalDetection.txt`: Detection events

**Example Results Interpretation:**
```
simulation_results.txt shows:
- clinical_detections: 94526 (total cases detected)
- mean_daily_prevalence: 0.383 (38% of patients colonized on average)
- number_of_transmissions: 12598 (facility-acquired infections)
```

## Common First-Run Scenarios

### Scenario 1: No Surveillance Testing
- Set `doActiveSurveillanceAfterBurnIn`: false
- Compare transmission rates without active case finding

### Scenario 2: More Effective Isolation
- Set `isolationEffectiveness`: 0.9
- See how better isolation reduces transmission

### Scenario 3: Batch Analysis
- Set `isBatchRun`: true
- Run multiple times to get statistical confidence

## Typical Runtime

- **Development machine**: 5-15 minutes for full simulation
- **Batch runs**: 2-5 minutes per run (no detailed output)
- **Large populations**: May take 30+ minutes

## Quick Troubleshooting

**Simulation won't start:**
- Check Java is installed: `java -version` in command line
- Verify all required files are present

**Results look strange:**
- Check that burn-in completed (messages at tick 3650)
- Verify parameters are in expected ranges
- Ensure `isBatchRun` is false for detailed output

**Performance is slow:**
- Close unnecessary applications
- Increase Java heap size if needed
- Consider shorter simulation periods for testing

## What's Next?

After your first successful run:

1. **Experiment with parameters** - Try different transmission rates and intervention strategies
2. **Analyze outputs** - Use Excel, R, or Python to analyze the time series data
3. **Read the full documentation** - See `README.md` for detailed explanations
4. **Explore batch runs** - Set up parameter sweeps for systematic analysis

## Example Analysis Questions

With your first results, you can explore:
- How does surveillance frequency affect detection rates?
- What isolation effectiveness is needed to control transmission?
- How do importation rates impact facility outbreak risk?
- What are the trade-offs between different intervention strategies?

## Getting Help

- **Technical issues**: Check `TECHNICAL_DOCS.md`
- **Model questions**: Review code comments and `todo.txt`
- **Repast Simphony help**: Visit https://repast.github.io/docs/

Happy modeling!