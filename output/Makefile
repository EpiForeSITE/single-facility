# Target to copy the two latest sim_modeloutputs*.txt files into new_analysis
copy_latest_outputs:
	mkdir -p new_analysis
	ls -t sim_modeloutputs*.txt | head -2 | xargs -I{} cp {} new_analysis/
# Makefile for generating plots from correlation_matrix.R

plots:
	Rscript correlation_matrix.R

# Target to join the latest sim_modeloutputs.*.batch_param_map.txt and sim_modeloutputs.*.txt files on their "run" columns
join_outputs:
	@output_file=$(if $(name),$(name),merged.txt); \
	mkdir -p new_analysis; \
	map_file=$$(ls -t sim_modeloutputs.*.batch_param_map.txt | head -1); \
	data_file=$$(ls -t sim_modeloutputs.*.txt | grep -v batch_param_map | head -1); \
	awk 'NR==FNR && FNR>1 {a[$$1]=$$0; next} FNR>1 && $$1 in a {print a[$$1] "," substr($$0, index($$0,$$2))}' $$map_file $$data_file > new_analysis/$$output_file
