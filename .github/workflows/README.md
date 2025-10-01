# GitHub Actions Workflows

## Compile Simulation

The `compile.yml` workflow compiles the Repast Simphony simulation automatically on push or pull request.

### What it does

1. Sets up Java 11 (required by Repast Simphony 2.11.0)
2. Downloads and caches Repast Simphony 2.11.0 from SourceForge
3. Configures the classpath with all necessary Repast libraries
4. Compiles all Java source files in the `src/` directory
5. Verifies that compilation succeeded by counting generated `.class` files

### Triggers

The workflow runs on:
- Push to `main`, `master`, or `develop` branches
- Pull requests targeting `main`, `master`, or `develop` branches

### First-time Approval

For security reasons, workflows on pull requests from bots or first-time contributors require approval from a repository maintainer. The first time this workflow runs on a PR, you'll need to:

1. Go to the Actions tab in GitHub
2. Find the workflow run
3. Click "Approve and run" to allow the workflow to execute

### Caching

The workflow caches the Repast Simphony installation to speed up subsequent runs. The cache key is `repast-simphony-2.11.0`.

### Troubleshooting

If the workflow fails:

1. **Download fails**: SourceForge might be temporarily unavailable. The workflow tries two different download URLs.
2. **Compilation fails**: Check that all Java files compile correctly locally first.
3. **Missing JARs**: The workflow expects specific Repast Simphony plugin versions (2.11.0).

### Local Testing

You can test compilation locally using the Makefile:

```bash
# Compile the simulation
make compile

# Verify compilation
find bin -name "*.class" | wc -l
```

Note: Local compilation requires Repast Simphony to be installed and `REPAST_HOME` configured in `config.mk`.
