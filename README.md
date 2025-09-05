# Single Facility Disease Transmission Model

A Java-based agent-based model (ABM) built with Repast Simphony for simulating disease transmission in healthcare facilities, with a focus on Carbapenem-resistant Enterobacteriaceae (CRE) and other healthcare-associated infections.

## Overview

This simulation model represents a single healthcare facility where patients are admitted, may become colonized with a disease, can transmit the disease to other patients, and may be clinically detected and isolated. The model is designed to evaluate the effectiveness of different surveillance and intervention strategies in controlling healthcare-associated infections.

### Key Features

- **Patient Flow**: Realistic patient admission and discharge patterns with exponential length of stay distributions
- **Disease Dynamics**: Models disease importation, transmission, colonization, clinical detection, and decolonization
- **Surveillance Strategies**: Configurable active surveillance testing with adjustable intervals and adherence rates
- **Intervention Measures**: Patient isolation upon detection with configurable effectiveness
- **Multiple Metrics**: Tracks prevalence, transmission rates, clinical detections, and discharge prevalence
- **Burn-in Period**: Allows the system to reach steady state before measurement begins

## How It Works

### Conceptual Model

The simulation operates on several key components:

1. **Agents (Patients)**: Individual patients with disease status, admission/discharge times, and isolation status
2. **Facility**: The healthcare facility container that manages patient populations and outbreak dynamics
3. **Disease Model**: Represents the specific pathogen (e.g., CRE) with transmission parameters and clinical characteristics
4. **Processes**: Handle patient admissions, discharges, and disease-related events

### Disease Transmission Process

1. **Importation**: New patients may arrive already colonized based on the importation rate
2. **Transmission**: Colonized patients can transmit the disease to susceptible patients based on contact rates and transmission probability
3. **Clinical Detection**: Colonized patients may be clinically detected after a time delay, triggering isolation
4. **Surveillance Testing**: Active surveillance tests can detect colonized patients before clinical symptoms
5. **Decolonization**: Patients naturally clear the infection over time
6. **Isolation**: Detected patients are isolated, reducing their transmission potential

### Simulation Timeline

- **Burn-in Period** (default: 10 years): Allows the system to reach epidemiological equilibrium
- **Measurement Period** (default: 5 years): Data collection period for analysis
- **Daily Events**: Population tallies, surveillance testing, and metric calculations

## Installation and Setup

### Prerequisites

- **Java Runtime Environment (JRE)**: Java 8 or higher
- **Repast Simphony**: Included in the project dependencies

### Installation Options

#### Option 1: Using Pre-built Model Archive (Recommended)

1. Download the model archive from the releases section
2. Extract to your desired location
3. Run the appropriate script:
   - **Windows**: Double-click `start_model.bat`
   - **Linux/macOS**: Double-click `start_model.command` or run from terminal

#### Option 2: Development Setup with Eclipse

1. Clone this repository:
   ```bash
   git clone https://github.com/EpiForeSITE/single-facility-repast.git
   ```

2. Open Eclipse with Repast Simphony plugin installed

3. Import the project:
   - File → Import → General → Existing Projects into Workspace
   - Select the cloned directory

4. Build and run the model through Eclipse

### Verification

After launching, you should see the Repast Simphony GUI with:
- Parameter settings panel
- Run controls (Initialize, Start, Pause, Stop)
- Real-time displays and charts (if configured)

## Configuration

### Key Parameters

The simulation behavior is controlled through parameters defined in `single-facility.rs/parameters.xml`:

#### Disease Parameters
- **`longTermAcuteCareBeta`** (default: 0.0615): Base transmission rate coefficient
- **`avgDecolonizationTime`** (default: 387.0 days): Average time for natural clearance
- **`importationRate`** (default: 0.206): Probability of arriving colonized

#### Detection Parameters  
- **`acuteCareMeanDetectionTime`** (default: 122.0 days): Average time to clinical detection
- **`probSurveillanceDetection`** (default: 0.8): Probability of detection by surveillance testing

#### Intervention Parameters
- **`isolationEffectiveness`** (default: 0.5): Reduction in transmission when isolated
- **`doActiveSurveillanceAfterBurnIn`** (default: true): Enable surveillance testing
- **`daysBetweenTests`** (default: 14.0): Interval between surveillance tests

#### Simulation Control
- **`isBatchRun`** (default: false): Enable batch mode (disables individual output files)
- **`allowImportationsDuringBurnIn`** (default: false): Allow new infections during burn-in

### Parameter Modification

1. **Through GUI**: Modify parameters in the Repast interface before clicking "Initialize"
2. **Batch Runs**: Configure parameter sweeps for systematic exploration
3. **Configuration Files**: Edit `parameters.xml` for default values

## Output Files and Interpretation

The model generates several output files for analysis:

### Individual Run Outputs (when `isBatchRun = false`)

- **`simulation_results.txt`**: Summary statistics for the entire run
- **`daily_population_stats.txt`**: Daily counts of colonized, detected, and isolated patients
- **`admissions.txt`**: Log of all patient admissions with colonization status
- **`transmissions.txt`**: Record of all transmission events
- **`clinicalDetection.txt`**: Log of clinical detection events
- **`decolonization.txt`**: Record of decolonization events
- **`surveillance.txt`**: Results of surveillance testing

### Key Metrics

- **Mean Daily Prevalence**: Average proportion of patients colonized per day
- **Discharge Prevalence**: Proportion of patients colonized at discharge
- **Importation Prevalence**: Proportion of admissions that are colonized
- **Clinical Detections per 10,000 Patient-Days**: Rate of clinical detection
- **Total Transmissions**: Number of facility-acquired infections

### Data Analysis

The output files can be analyzed using statistical software (R, Python, etc.) to:
- Calculate confidence intervals for key metrics
- Compare intervention scenarios
- Analyze temporal trends
- Estimate outbreak probabilities

## Model Validation and Assumptions

### Key Assumptions

- **Well-mixed Population**: All patients have equal contact probability
- **Exponential Distributions**: Length of stay and event times follow exponential distributions
- **Single Disease**: Models one pathogen at a time
- **Perfect Detection**: Surveillance tests detect all colonized patients (with specified probability)

### Validation Considerations

- Compare model outputs to published literature on CRE transmission
- Validate parameter estimates against facility-specific data
- Conduct sensitivity analyses on key parameters

## Customization and Extension

### Adding New Disease Types

1. Modify the `Disease` class to add new pathogen-specific parameters
2. Update `parameters.xml` with disease-specific values
3. Adjust transmission and detection logic as needed

### Facility Types

The model supports different facility types with varying transmission rates:
- Acute care hospitals
- Long-term acute care hospitals  
- Nursing homes

### Advanced Features

- **Multiple Diseases**: Extend to model co-circulation of multiple pathogens
- **Staff Transmission**: Add healthcare worker agents as transmission vectors
- **Spatial Structure**: Implement ward-based transmission patterns

## Troubleshooting

### Common Issues

1. **Model Won't Start**: Verify Java installation and Repast Simphony setup
2. **Parameter Errors**: Check that all required parameters are specified
3. **Memory Issues**: Increase JVM heap size for large population simulations
4. **Output Files Missing**: Ensure `isBatchRun` is set to `false` for individual files

### Support

For technical issues or questions about the model:
- Check the Repast Simphony documentation: https://repast.github.io/docs/
- Review the code comments and TODO items in `todo.txt`
- Contact the development team for assistance

## Contributing

This model is under active development. Contributions are welcome in the form of:
- Bug reports and fixes
- Parameter validation studies
- Feature enhancements
- Documentation improvements

## License

This project is licensed under the terms specified in `license.txt`. Please contact the developers for details regarding usage and distribution.

## Citation

When using this model in research, please cite:
[Add appropriate citation information once published]

## Development Team

[Add developer contact information]

---

*This documentation provides a comprehensive overview of the single-facility disease transmission model. For technical details about the implementation, refer to the source code and inline comments.*