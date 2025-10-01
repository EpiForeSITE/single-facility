# Single-Facility Instructions for Copilot

## Purpose

These instructions are designed to help GitHub Copilot generate code that adheres to the specific coding standards and practices of our project. By following these guidelines, we aim to maintain code quality, consistency, and readability across our codebase.

## Project Overview
The Single-Facility project is a simulation model built using the Repast Simphony framework. It focuses on simulating the operations and dynamics a single hospital, including patient flow, and disease dynamics and transmission.  

In single-iteration mode we run a simulation and collect large quantities of data on the events that occur.  These include admission, discharge, clinical detection, detection by surveillance, decolonization and isolation, and transmission events.  These files are output in the base folder and have either .txt or .csv extensions.  These data need to be analyzed in the new_analysis folder using R scripts, specifically quarto reports.  

### Specifically:
- admissions.txt
- clinicalDetection.txt
- daily_population_stats.txt
- decolonization.txt
- discharged_patients.csv
- surveillance.txt
- transmissions.txt

## General principles:
-  Write clear, concise, and well-documented code.
-  Follow the coding conventions and style guides relevant to the programming languages used in this project (Java for Repast, R for analysis).
-  Ensure that the code is modular and reusable where possible.
-  wherever possible limit the use of external libraries to thhose already in use in the project.
- baby steps:  I want to iterate over small features and build up reports over several requests.  I want to be able to review and edit code as it is generated.
-  Test the generated code to ensure it works as intended and does not introduce bugs or performance issues.
-  Reports should be in pdf format and use quarto.

### Resources:
-  [Repast Documentation](https://repast.github.io/docs/api/repast_simphony/index.html)
-  [Repast User Guide](https://repast.github.io/docs/RepastReference/RepastReference.html)
-  [Java style guide](https://google.github.io/styleguide/javaguide.html)
-  [R style guide](https://style.tidyverse.org/)
