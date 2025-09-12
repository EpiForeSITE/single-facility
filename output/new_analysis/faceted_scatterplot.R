# R script to create scatter plots for sensitivity analysis
library(ggplot2)

# Get command line arguments
args <- commandArgs(trailingOnly = TRUE)
column <- "probSurveillanceDetection" # default
if (length(args) > 0) {
  for (arg in args) {
    if (grepl("^column=", arg)) {
      column <- sub("^column=", "", arg)
    }
  }
}

# Read merged data
merged <- read.csv("merged.txt", header=TRUE)

# Helper function to plot
plot_scatter <- function(yvar, ylab, filename) {
  p <- ggplot(merged, aes_string(x=column, y=yvar)) +
    geom_point(size=3) +
    labs(title=paste(ylab, "vs", column),
         x=column,
         y=ylab)
  ggsave(filename, p)
}

plot_scatter("NumberOfTransmissions", "NumberOfTransmissions", paste0("scatter_NumberOfTransmissions_vs_", column, ".png"))
plot_scatter("MeanDailyPrevalence", "MeanDailyPrevalence", paste0("scatter_MeanDailyPrevalence_vs_", column, ".png"))
plot_scatter("ClinicalDetections", "ClinicalDetections", paste0("scatter_ClinicalDetections_vs_", column, ".png"))
plot_scatter("MeanDischargePrevalence", "MeanDischargePrevalence", paste0("scatter_MeanDischargePrevalence_vs_", column, ".png"))
plot_scatter("ImportationPrevalence", "ImportationPrevalence", paste0("scatter_ImportationPrevalence_vs_", column, ".png"))
