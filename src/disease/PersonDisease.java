package disease;

import agents.Person;
import builders.SingleFacilityBuilder;

import java.io.IOException;
import java.io.PrintWriter;

import org.apache.commons.math3.distribution.ExponentialDistribution;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.ISchedulableAction;
import utils.TimeUtils;

public class PersonDisease {

	private Disease disease;
	private Person person;
	private boolean colonized = false;
	private boolean detected = false;
	private boolean detectedBySurveillance = false; // NEW: track source of detection
	private double transmissionRateContribution = 1.0;
	private boolean clinicallyDetectedDuringCurrentStay = false;
	private boolean initialInfection = false;
	private int detectionCount = 0;

	private ISchedule schedule;
	private ExponentialDistribution decolonizationDistribution;
	private ExponentialDistribution clinicalDetectionDistribution;

	private ISchedulableAction clinicalDetectionAction;

	private static PrintWriter decolWriter;
	private static PrintWriter clinicalWriter;
	private static PrintWriter verificationWriter; // NEW: verification log
	public static int clinicalOutputNum;
	public static int surveillanceOutputNum; // NEW: count surveillance detections

	static {
		try {
			if (!SingleFacilityBuilder.isBatchRun) {
				decolWriter = new PrintWriter("decolonization.txt");
				clinicalWriter = new PrintWriter("clinicalDetection.txt");
				verificationWriter = new PrintWriter("detection_verification.txt");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public PersonDisease(Disease disease, Person person, ISchedule schedule) {
		this.disease = disease;
		this.person = person;
		this.schedule = repast.simphony.engine.environment.RunEnvironment.getInstance().getCurrentSchedule();

		initializeEventDistributions();

		if (person.getCurrentFacility() == null) {
			System.err.println("Current facility is null for person: " + person);
			return;
		}
	}

	public void doDecolonization() {
		double currentTime = schedule.getTickCount();

		if (colonized) {
			colonized = false;
			if (!SingleFacilityBuilder.isBatchRun && decolWriter != null) {
				decolWriter.printf("Time: %.2f, Decolonized Patient: %d%n", currentTime, person.hashCode());
				decolWriter.flush();
			}
			if (clinicalDetectionAction != null) {
				schedule.removeAction(clinicalDetectionAction);
				clinicalDetectionAction = null;
			}
			person.updateAllTransmissionRateContributions();
		}
	}

	public void startDecolonizationTimer() {
		if (decolonizationDistribution == null) {
			System.err.println("Decolonization distribution is not initialized.");
			return;
		}
		double timeToDecolonization = decolonizationDistribution.sample();
		ScheduleParameters params = ScheduleParameters.createOneTime(schedule.getTickCount() + timeToDecolonization);
		schedule.schedule(params, this, "doDecolonization");
	}

	public void doClinicalDetection() {
		if (detected || clinicallyDetectedDuringCurrentStay) {
			return;
		}

		double currentTime = schedule.getTickCount();
		detected = true;
		detectedBySurveillance = false; // mark as clinical
		clinicallyDetectedDuringCurrentStay = true;

		incrementDetectionCount();

		if (!SingleFacilityBuilder.isBatchRun && clinicalWriter != null) {
			clinicalWriter.printf("Time: %.2f, Detected Patient: %d, DetectionCount: %d%n", currentTime,
					person.hashCode(), getDetectionCount());
			clinicalWriter.flush();
		}
		clinicalOutputNum++;

		// Verification log for detection source
		if (!SingleFacilityBuilder.isBatchRun && verificationWriter != null) {
			verificationWriter.printf("Time: %.2f, Patient: %d, Source: CLINICAL, Colonized: %b, DetectionCount: %d%n",
					currentTime, person.hashCode(), colonized, getDetectionCount());
			verificationWriter.flush();
		}

		if (!person.isIsolated() && disease.isolatePatientWhenDetected()) {
			person.isolate();
			person.updateAllTransmissionRateContributions();
		}
		if (clinicalDetectionAction != null) {
			schedule.removeAction(clinicalDetectionAction);
			clinicalDetectionAction = null;
		}
	}

	/**
	 * Mark this PersonDisease as detected by surveillance testing. Writes to
	 * verification log and performs isolation if configured.
	 */
	public void setDetectedBySurveillance() {
		if (detected)
			return; // already detected by some source
		detected = true;
		detectedBySurveillance = true;
		surveillanceOutputNum++;
		double currentTime = schedule.getTickCount();
		// Verification log
		if (!SingleFacilityBuilder.isBatchRun && verificationWriter != null) {
			verificationWriter.printf(
					"Time: %.2f, Patient: %d, Source: SURVEILLANCE, Colonized: %b, DetectionCount: %d%n", currentTime,
					person.hashCode(), colonized, getDetectionCount());
			verificationWriter.flush();
		}
		if (!person.isIsolated() && disease.isolatePatientWhenDetected()) {
			person.isolate();
			person.updateAllTransmissionRateContributions();
		}
		if (clinicalDetectionAction != null) {
			schedule.removeAction(clinicalDetectionAction);
			clinicalDetectionAction = null;
		}
	}

	public void startClinicalDetectionTimer() {
		if (detected || clinicallyDetectedDuringCurrentStay) {
			return;
		}
		if (clinicalDetectionAction != null) {
			schedule.removeAction(clinicalDetectionAction);
			clinicalDetectionAction = null;
		}

		double meanTimeToClinicalDetection = disease
				.getMeanTimeToClinicalDetection(person.getCurrentFacility().getType());
		double timeToDetection = clinicalDetectionDistribution.sample();

		ScheduleParameters params = ScheduleParameters.createOneTime(schedule.getTickCount() + timeToDetection);
		clinicalDetectionAction = schedule.schedule(params, this, "doClinicalDetection");
	}

	public void resetClinicalDetectionEvent() {
		detected = false;
		clinicallyDetectedDuringCurrentStay = false;

		// Clear any pending detection first
		if (clinicalDetectionAction != null) {
			schedule.removeAction(clinicalDetectionAction);
			clinicalDetectionAction = null;
		}

		if (clinicalDetectionDistribution != null) {
			double timeToDetection = clinicalDetectionDistribution.sample();
			ScheduleParameters params = ScheduleParameters.createOneTime(schedule.getTickCount() + timeToDetection);
			clinicalDetectionAction = schedule.schedule(params, this, "doClinicalDetection");
		}
	}

	public void colonize() {
		colonized = true;
		startDecolonizationTimer();
		person.updateAllTransmissionRateContributions();
	}

	public void addAcquisition() {
		startClinicalDetectionTimer();
		person.updateAllTransmissionRateContributions();
	}

	public void incrementDetectionCount() {
		this.detectionCount++;
	}

	public int getDetectionCount() {
		return this.detectionCount;
	}

	public void updateTransmissionRateContribution() {
		double score = 1.0;
		if (person.isIsolated())
			score *= person.getCurrentFacility().getBetaIsolationReduction();
		transmissionRateContribution = score;
	}

	public void initializeEventDistributions() {
		if (disease != null && person != null && person.getCurrentFacility() != null) {
			double meanTimeToClinicalDetection = disease
					.getMeanTimeToClinicalDetection(person.getCurrentFacility().getType());

			decolonizationDistribution = new ExponentialDistribution(disease.getAvgDecolonizationTime());
			clinicalDetectionDistribution = new ExponentialDistribution(meanTimeToClinicalDetection);
		} else {
			System.err.println("Cannot initialize distributions: disease, person, or current facility is null.");
		}
	}

	public boolean isColonized() {
		return colonized;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public double getTransmissionRateContribution() {
		return transmissionRateContribution;
	}

	public void setInitialInfection(boolean initialInfection) {
		this.initialInfection = initialInfection;
	}

	public Disease getDisease() {
		return disease;
	}

	public boolean isDetected() {
		return detected;
	}

	public void setDetected(boolean detected) {
		this.detected = detected;
	}

	public boolean isDetectedBySurveillance() {
		return detectedBySurveillance;
	}

	public boolean isClinicallyDetectedDuringCurrentStay() {
		return clinicallyDetectedDuringCurrentStay;
	}

	public boolean isInitialInfection() {
		return initialInfection;
	}
}
