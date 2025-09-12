# R script to join sim_modeloutputs.txt and batch_param_map.txt on 'run' column
sim_file <- "sim_modeloutputs.txt"
map_file <- "batch_param_map.txt"
output_file <- "merged.txt"

# Read the files
sim_data <- read.csv(sim_file, header=TRUE)
map_data <- read.csv(map_file, header=TRUE)

# Merge on 'run' column
merged_data <- merge(map_data, sim_data, by="run")

# Write the result
write.csv(merged_data, output_file, row.names=FALSE)
