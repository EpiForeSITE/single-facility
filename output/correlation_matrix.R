# Correlation matrix for sim_modeloutputs.2025.Jul.18.13_07_39.txt
# Load necessary libraries

# install.packages("ggplot2") # Uncomment if 'ggplot2' is not installed
library(readr)
library(ggplot2)

# Read the data (adjust path if needed)
data <- read_csv("sim_modeloutputs.2025.Jul.18.13_07_39.txt")

# View the first few rows (optional)

# Select only numeric columns for correlation
numeric_data <- data[sapply(data, is.numeric)]

# Compute the correlation matrix
cor_matrix <- cor(numeric_data, use = "complete.obs")

# Print the correlation matrix
print(cor_matrix)

# Plot the correlation matrix using ggplot2 (no reshape2 needed)
cor_melt <- as.data.frame(as.table(cor_matrix))
p <- ggplot(cor_melt, aes(Var1, Var2, fill = Freq)) +
  geom_tile() +
  scale_fill_gradient2(low = "blue", high = "red", mid = "white", 
                       midpoint = 0, limit = c(-1,1), space = "Lab", 
                       name = "Correlation") +
  theme_minimal() +
  theme(axis.text.x = element_text(angle = 45, vjust = 1, hjust = 1)) +
  labs(title = "Correlation Matrix", x = NULL, y = NULL) +
  geom_text(aes(label = round(Freq, 2)), color = "black", size = 4)
print(p)

# Save the plot to a PNG file in the current folder
ggsave("correlation_matrix_plot.png", plot = p, width = 6, height = 5, dpi = 300)

# Faceted scatter plots: each numeric column (except IsolationEffectiveness) vs IsolationEffectiveness
library(tidyr) # for pivot_longer


 # Gather all numeric columns except IsolationEffectiveness, tick, and DoActiveSurveillance
cols_to_remove <- c("DoActiveSurveillance", "tick")
plot_data <- data[, !(names(data) %in% cols_to_remove)]
plot_data <- tidyr::pivot_longer(
  plot_data,
  cols = -IsolationEffectiveness,
  names_to = "Variable",
  values_to = "Value"
)

# Remove IsolationEffectiveness as a variable to plot
plot_data <- plot_data[plot_data$Variable != "IsolationEffectiveness", ]

# Faceted scatter plot
facet_plot <- ggplot(plot_data, aes(x = IsolationEffectiveness, y = Value)) +
  geom_point(alpha = 0.6) +
  facet_wrap(~ Variable, scales = "free_y") +
  theme_minimal() +
  labs(title = "Scatterplots vs IsolationEffectiveness",
       x = "IsolationEffectiveness",
       y = NULL)

print(facet_plot)

# Save the faceted plot with a white background
ggsave("faceted_scatterplots_vs_isolationEffectiveness.png", plot = facet_plot, width = 8, height = 6, dpi = 300, bg = "white")
