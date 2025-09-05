# R script to create scatter plots for sensitivity analysis
library(ggplot2)

# Read merged data
merged <- read.csv("merged.txt", header=TRUE)

# NumberOfTransmissions vs probSurveillanceDetection
p1 <- ggplot(merged, aes(x=probSurveillanceDetection, y=NumberOfTransmissions)) +
  geom_point(size=3) +
  labs(title="NumberOfTransmissions vs probSurveillanceDetection",
       x="probSurveillanceDetection",
       y="NumberOfTransmissions")
ggsave("scatter_NumberOfTransmissions_vs_probSurveillanceDetection.png", p1)

# MeanDailyPrevalence vs probSurveillanceDetection
p2 <- ggplot(merged, aes(x=probSurveillanceDetection, y=MeanDailyPrevalence)) +
  geom_point(size=3) +
  labs(title="MeanDailyPrevalence vs probSurveillanceDetection",
       x="probSurveillanceDetection",
       y="MeanDailyPrevalence")
ggsave("scatter_MeanDailyPrevalence_vs_probSurveillanceDetection.png", p2)

# ClinicalDetections vs probSurveillanceDetection
p3 <- ggplot(merged, aes(x=probSurveillanceDetection, y=ClinicalDetections)) +
  geom_point(size=3) +
  labs(title="ClinicalDetections vs probSurveillanceDetection",
       x="probSurveillanceDetection",
       y="ClinicalDetections")
ggsave("scatter_ClinicalDetections_vs_probSurveillanceDetection.png", p3)

# MeanDischargePrevalence vs probSurveillanceDetection
p4 <- ggplot(merged, aes(x=probSurveillanceDetection, y=MeanDischargePrevalence)) +
  geom_point(size=3) +
  labs(title="MeanDischargePrevalence vs probSurveillanceDetection",
       x="probSurveillanceDetection",
       y="MeanDischargePrevalence")
ggsave("scatter_MeanDischargePrevalence_vs_probSurveillanceDetection.png", p4)

# ImportationPrevalence vs probSurveillanceDetection
p5 <- ggplot(merged, aes(x=probSurveillanceDetection, y=ImportationPrevalence)) +
  geom_point(size=3) +
  labs(title="ImportationPrevalence vs probSurveillanceDetection",
       x="probSurveillanceDetection",
       y="ImportationPrevalence")
ggsave("scatter_ImportationPrevalence_vs_probSurveillanceDetection.png", p5)
