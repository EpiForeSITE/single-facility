//this is a data class to hold infomration about discharged patients.
//we need all relevant information from the discharged person class, as well as 
//the information from the person's PersonDisease object.  Assume only one disease per person for now.

package agents;

public class DischargedPatient {
	
	// Patient identification and timing information
	private int id;
	private double admissionTime;
	private double dischargeTime;
	private double los;
	
	// Patient status information
	private boolean isIsolated;
	private boolean isColonized;
	private boolean isDetected;
	private boolean detectedBySurveillance;
	private boolean clinicallyDetectedDuringCurrentStay;
	private boolean initialInfection;
	private int detectionCount;
	
	// Disease-related information
	private String diseaseName;
	private double transmissionRateContribution;
	
	// Constructors
	public DischargedPatient() {
		// Default constructor
	}
	
	public DischargedPatient(Person person) {
		this.id = person.hashCode(); // Using hashCode as ID
		this.admissionTime = person.getAdmissionTime();
		this.dischargeTime = person.getDischargeTime();
		this.los = this.dischargeTime - this.admissionTime;
		this.isIsolated = person.isIsolated();
		
		// Extract disease information (assuming one disease per person)
		if (!person.getDiseases().isEmpty()) {
			var personDisease = person.getDiseases().get(0);
			this.isColonized = personDisease.isColonized();
			this.isDetected = personDisease.isDetected();
			this.detectedBySurveillance = personDisease.isDetectedBySurveillance();
			this.clinicallyDetectedDuringCurrentStay = personDisease.isClinicallyDetectedDuringCurrentStay();
			this.initialInfection = personDisease.isInitialInfection();
			this.detectionCount = personDisease.getDetectionCount();
			this.diseaseName = personDisease.getDisease().getDiseaseName();
			this.transmissionRateContribution = personDisease.getTransmissionRateContribution();
		}
	}
	
	// Getters and Setters
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public double getAdmissionTime() {
		return admissionTime;
	}
	
	public void setAdmissionTime(double admissionTime) {
		this.admissionTime = admissionTime;
	}
	
	public double getDischargeTime() {
		return dischargeTime;
	}
	
	public void setDischargeTime(double dischargeTime) {
		this.dischargeTime = dischargeTime;
	}
	
	public double getLos() {
		return los;
	}
	
	public void setLos(double los) {
		this.los = los;
	}
	
	public boolean isIsolated() {
		return isIsolated;
	}
	
	public void setIsolated(boolean isIsolated) {
		this.isIsolated = isIsolated;
	}
	
	public boolean isColonized() {
		return isColonized;
	}
	
	public void setColonized(boolean isColonized) {
		this.isColonized = isColonized;
	}
	
	public boolean isDetected() {
		return isDetected;
	}
	
	public void setDetected(boolean isDetected) {
		this.isDetected = isDetected;
	}
	
	public boolean isDetectedBySurveillance() {
		return detectedBySurveillance;
	}
	
	public void setDetectedBySurveillance(boolean detectedBySurveillance) {
		this.detectedBySurveillance = detectedBySurveillance;
	}
	
	public boolean isClinicallyDetectedDuringCurrentStay() {
		return clinicallyDetectedDuringCurrentStay;
	}
	
	public void setClinicallyDetectedDuringCurrentStay(boolean clinicallyDetectedDuringCurrentStay) {
		this.clinicallyDetectedDuringCurrentStay = clinicallyDetectedDuringCurrentStay;
	}
	
	public boolean isInitialInfection() {
		return initialInfection;
	}
	
	public void setInitialInfection(boolean initialInfection) {
		this.initialInfection = initialInfection;
	}
	
	public int getDetectionCount() {
		return detectionCount;
	}
	
	public void setDetectionCount(int detectionCount) {
		this.detectionCount = detectionCount;
	}
	
	public String getDiseaseName() {
		return diseaseName;
	}
	
	public void setDiseaseName(String diseaseName) {
		this.diseaseName = diseaseName;
	}
	
	public double getTransmissionRateContribution() {
		return transmissionRateContribution;
	}
	
	public void setTransmissionRateContribution(double transmissionRateContribution) {
		this.transmissionRateContribution = transmissionRateContribution;
	}
	
	@Override
	public String toString() {
		return String.format("%d,%.2f,%.2f,%.2f,%b,%b,%b,%b,%b,%b,%d,%s,%.6f", 
				id, admissionTime, dischargeTime, los, isIsolated, isColonized, isDetected, 
				detectedBySurveillance, clinicallyDetectedDuringCurrentStay, initialInfection, 
				detectionCount, diseaseName, transmissionRateContribution);
	}
	
	/**
	 * Returns a CSV header string containing the names of all member variables.
	 * This can be used as the first line in a CSV file containing discharged patient data.
	 * 
	 * @return CSV header string with comma-separated field names
	 */
	public static String getHeader() {
		return "id,admissionTime,dischargeTime,los,isIsolated,isColonized,isDetected," +
				"detectedBySurveillance,clinicallyDetectedDuringCurrentStay,initialInfection," +
				"detectionCount,diseaseName,transmissionRateContribution";
	}
}