# Technical Documentation

## Model Architecture

### Class Hierarchy and Design Patterns

The single-facility disease transmission model follows object-oriented design principles with clear separation of concerns:

#### Core Components

1. **SingleFacilityBuilder** (`builders/SingleFacilityBuilder.java`)
   - Main simulation controller implementing `ContextBuilder<Object>`
   - Manages simulation lifecycle (initialization, burn-in, data collection, termination)
   - Handles parameter loading and output file generation
   - Schedules recurring events using Repast's scheduling system

2. **Agent Classes** (`agents/`)
   - `Person`: Individual patient agents with disease states and movement
   - `Agent`: Base class for all agent types

3. **Agent Containers** (`agentcontainers/`)
   - `Facility`: Represents the healthcare facility with patient populations
   - `Region`: Higher-level container managing facilities and regional statistics
   - `AgentContainer`: Base class for spatial containers

4. **Disease Model** (`disease/`)
   - `Disease`: Pathogen-specific parameters and behaviors
   - `PersonDisease`: Individual infection instances with state transitions
   - `FacilityOutbreak`: Manages transmission dynamics within a facility

5. **Processes** (`processes/`)
   - `Admission`: Handles patient admission logic and importation
   - `Process`: Base class for discrete events

### Key Design Patterns

- **Builder Pattern**: SingleFacilityBuilder constructs and configures the simulation context
- **Observer Pattern**: Repast's scheduling system allows event-driven programming
- **Strategy Pattern**: Different facility types use varying parameter sets
- **State Pattern**: PersonDisease objects maintain colonization/detection states

## Disease Transmission Algorithm

### Mathematical Model

The transmission model uses a modified SIS (Susceptible-Infected-Susceptible) framework:

```
dS/dt = μN - λS - μS + γI
dI/dt = λS - γI - μI
```

Where:
- S = susceptible patients
- I = colonized patients  
- λ = force of infection = β × (I_effective / N_effective)
- β = transmission rate parameter
- γ = decolonization rate
- μ = admission/discharge rate

### Transmission Rate Calculation

The effective transmission rate is computed as:

```java
transmissionRate = baseline_beta × (numContagiousEffective / numSusceptibleEffective)
```

Where effective populations account for isolation status:
- `numContagiousEffective` = colonized_non_isolated + (colonized_isolated × isolation_effectiveness)
- `numSusceptibleEffective` = susceptible_non_isolated + (susceptible_isolated × isolation_effectiveness)

### Event Scheduling

Disease events use exponential distributions for realistic timing:

1. **Transmission Events**: Scheduled with rate proportional to transmission probability
2. **Decolonization**: Exponential distribution with mean `avgDecolonizationTime`
3. **Clinical Detection**: Exponential distribution with facility-specific mean times
4. **Surveillance Testing**: Regular intervals with configurable adherence

## Parameter Configuration

### Parameter Inheritance

Parameters flow through the system hierarchy:
1. XML configuration files define default values
2. Repast GUI allows runtime modification
3. Disease class retrieves parameters via `RunEnvironment.getInstance().getParameters()`
4. Local caching improves performance for frequently accessed values

### Facility Type Parameters

Different facility types use scaled transmission parameters:

| Facility Type | Beta Multiplier | Detection Time Factor |
|---------------|-----------------|----------------------|
| Acute Care | 1.0 | 1.0 |
| Long-term Acute Care | `longTermAcuteCareBeta` | 0.87 |
| Nursing Home | 0.1 | 8.0 |

### Parameter Sensitivity

Critical parameters for model behavior:
- **High Sensitivity**: `longTermAcuteCareBeta`, `importationRate`, `isolationEffectiveness`
- **Medium Sensitivity**: `avgDecolonizationTime`, `daysBetweenTests`
- **Low Sensitivity**: `probSurveillanceDetection`, detection time variations

## Output Data Structures

### File Formats

All output files use comma-separated values (CSV) or space-separated formats for easy analysis:

1. **simulation_results.txt**: Single-row summary with key metrics
2. **daily_population_stats.txt**: Time series data with colonization counts
3. **Event logs**: Timestamped records of admissions, transmissions, detections, etc.

### Batch Run Aggregation

When `isBatchRun = true`:
- Individual event files are disabled for performance
- Only summary statistics are generated
- Multiple runs can be aggregated for statistical analysis

## Performance Considerations

### Scalability

- **Population Size**: Model tested up to 1000+ concurrent patients
- **Simulation Length**: Efficient for 15+ year simulations (burn-in + measurement)
- **Memory Usage**: Approximately 1-2 GB for typical parameter ranges

### Optimization Strategies

1. **Event Caching**: Transmission events use cached probability calculations
2. **Conditional Output**: File I/O disabled during batch runs
3. **Efficient Collections**: LinkedLists for dynamic patient populations
4. **Lazy Evaluation**: Disease states computed on-demand

## Validation and Verification

### Model Validation

1. **Parameter Estimation**: Based on published literature for CRE transmission
2. **Equilibrium Testing**: Burn-in period ensures steady-state conditions
3. **Sensitivity Analysis**: Key outputs tested across parameter ranges
4. **Face Validity**: Clinical experts review model assumptions and outputs

### Verification Methods

1. **Unit Testing**: Individual components tested in isolation
2. **Integration Testing**: Full simulation runs with known outcomes
3. **Extreme Value Testing**: Boundary conditions (β=0, isolation=1.0, etc.)
4. **Conservation Laws**: Patient counts remain consistent

### Known Limitations

1. **Well-Mixed Assumption**: No spatial structure within facilities
2. **Single Pathogen**: No co-infection or competition effects
3. **Perfect Information**: Detection/isolation has no delays or errors
4. **Homogeneous Population**: No patient risk stratification

## Extension Guidelines

### Adding New Features

1. **New Disease Types**: 
   - Extend Disease class with pathogen-specific parameters
   - Modify PersonDisease for different natural histories
   - Update parameter files with new configuration options

2. **Staff Transmission**:
   - Create HealthcareWorker agent class
   - Implement staff-patient contact patterns
   - Add hand hygiene and contact precaution behaviors

3. **Multi-Facility Networks**:
   - Extend Region to manage facility transfers
   - Implement patient movement between facilities
   - Track importation sources and transmission chains

### Code Modification Best Practices

1. **Parameter Changes**: Always update both XML defaults and documentation
2. **New Events**: Use Repast's scheduling framework for consistency
3. **Output Files**: Follow existing naming conventions and formats
4. **Performance**: Profile new features with large population sizes

## Debugging and Troubleshooting

### Common Issues

1. **NullPointerException**: Usually indicates missing parameter initialization
2. **Infinite Loops**: Check event scheduling logic and termination conditions
3. **Memory Leaks**: Verify that event cleanup occurs properly
4. **Incorrect Output**: Review parameter units and calculation order

### Debugging Tools

1. **Console Output**: Debug statements show simulation progress
2. **Event Logging**: Individual event files help trace model behavior
3. **Parameter Verification**: Print statements confirm loaded values
4. **Population Checks**: Assert statements verify conservation laws

### Testing Framework

The model includes several verification methods:
- Daily population tallies for conservation checks
- Event counters to verify model dynamics
- Parameter bounds checking
- Output format validation

## Future Development

### Planned Enhancements

1. **Graphical Interface**: Real-time visualization of facility status
2. **Parameter Estimation**: Automated fitting to facility data
3. **Uncertainty Quantification**: Monte Carlo parameter sampling
4. **Policy Optimization**: Automated search for optimal intervention strategies

### Research Applications

The model supports research in:
- Healthcare-associated infection control
- Antimicrobial resistance spread
- Surveillance strategy optimization
- Economic evaluation of interventions
- Outbreak investigation and response