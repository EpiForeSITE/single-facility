package disease;

import agentcontainers.Facility;
import agentcontainers.Region;
import agents.Person;

import java.io.IOException;
import java.io.PrintWriter;

import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.commons.math3.ode.events.EventHandler;

import repast.simphony.engine.schedule.ISchedulableAction;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import utils.TimeUtils;

public class FacilityOutbreak {
	private Disease disease;
	public Facility facility;
	private double numSusceptibleNow = 0;
	private int numColonizedNow = 0;
	private double numSusceptibleEffective;
	private double numContagiousEffective;
	private int transmissionsTally = 0;
	private int numAdmissionsColonized = 0;
	private double importationRate;
	private double prevalence;
	private String diseaseName;
	private int numAdmissionsTallied = 0;
	private int popTallied = 0;
	private double popTalliedColonized = 0;
	private double avgPrevalence;
	private int numSusceptibleNonIsoNow = 0;
	private int numSusceptibleIsoNow = 0;
	private int numColonizedNonIsoNow = 0;
	private int numColonizedIsoNow = 0;
	private double clinicalDetectionsTallied;
	private double clinicalDetectionsPer10000PatientDays;
	private int dischargesTallied = 0;
	private int colonizedDischargesTallied = 0;
	private double avgDischargePrevalence;
	private double transmissionRate = 0.0;
	private ISchedule schedule;
	private boolean stop = false;
	private Region region;

	ISchedulableAction nextAction;
	ExponentialDistribution distro;
	double meanIntraEventTime;
	private PrintWriter logWriter;

	public FacilityOutbreak(double intra_event_time, Disease disease2) {
		schedule = repast.simphony.engine.environment.RunEnvironment.getInstance().getCurrentSchedule();
		disease = disease2;
		try {
			logWriter = new PrintWriter("transmissions.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
		// transmission();
	}

	public void transmission() {
		stop = false;
		double currTime = schedule.getTickCount();
		double elapse = distro.sample();
		ScheduleParameters params = ScheduleParameters.createOneTime(currTime + elapse);
		nextAction = schedule.schedule(params, this, "doTransmission");
	}

	public void doTransmission() {
		PersonDisease pdS = null;
		PersonDisease pdC = null;
		double unifS = uniform() * numSusceptibleEffective;
		double unifC = uniform() * numContagiousEffective;
		double uS = 0.0;
		double uC = 0.0;
		for (Person p : facility.getCurrentPatients()) {
			PersonDisease pd = p.getDiseases().get(disease.getSimIndex());// Fix
			if (pd.isColonized() && uC < unifC) {
				uC += pd.getTransmissionRateContribution();
				if (uC > unifC) {
					pdC = pd;
				}
				System.out.println("pdC set");
			}
			if (!pd.isColonized() && uS <= unifS) {
				uS += pd.getTransmissionRateContribution();
				if (uS > unifS) {
					pdS = pd;
				}
				System.out.println("pdS set");
			}
			if (pdS != null) {
				pdS.colonize();
				pdS.addAcquisition();
			}
			if (uC > unifC && uS > unifS) {
				break;
			}

		}
		if (pdC == null || pdS == null) {
			error("Transmission pair choice failure\nuS = %f; unifS = %f; nS = %f;\nuC = %f; unifC = %f; nC = %f\n", uS,
					unifS, numSusceptibleEffective, uC, unifC, numContagiousEffective);
		} else if (pdC.isInitialInfection()) {
			facility.getRegion().numTransmissionsFromInitialCase++;
		}
		
		if (pdC != null && pdS != null) {
			transmissionsTally++;
			double transmissionTime = schedule.getTickCount();

			logWriter.printf("Time: %.2f, Patient1: %d, Patient2: %d%n", transmissionTime,
					pdC.hashCode(), pdS.hashCode());
		}
		transmissionsTally++;
	}

	public void updateTransmissionRate(Region r) {
		region = r;
		double newTransmissionRate;
		/*
		 * C = colonized S = susceptible I = isolated
		 */
		int nC = 0;
		int nS = 0;
		int nCI = 0;
		int nSI = 0;
		double cScore = 0.0;
		double sScore = 0.0;
		for (Person p : facility.getCurrentPatients()) {
			if (p.getDiseases().size() != 0) {
				PersonDisease pd = p.getDiseases().get(disease.getSimIndex());
				if (pd.isColonized()) {
					cScore += pd.getTransmissionRateContribution();
					if (p.isIsolated())
						nCI++;
					else
						nC++;
				} else {
					sScore += pd.getTransmissionRateContribution();
					if (p.isIsolated())
						nSI++;
					else
						nS++;
				}
			}
		}
		numSusceptibleNonIsoNow = nS;
		numColonizedNonIsoNow = nC;
		numSusceptibleIsoNow = nSI;
		numColonizedIsoNow = nCI;
		numSusceptibleNow = nS + nSI;
		numColonizedNow = nC + nCI;
		if (region.people.size() != 0) {
			prevalence = 1.0 * numColonizedNow / region.people.size();
		}
		numContagiousEffective = cScore;
		numSusceptibleEffective = sScore;
		newTransmissionRate = disease.getBaselineBetaValue(facility.getType()) * numContagiousEffective
				* numSusceptibleEffective / facility.getCurrentPatients().size();

		System.out.println("");
		System.out.println(TimeUtils.getSchedule().getTickCount());
		System.out.println("Cscore: " + cScore + ", sScore: " + sScore);
		System.out.println("Disease beta: " + disease.getBaselineBetaValue(facility.getType()));
		System.out.println("Contagious: " + numContagiousEffective);
		System.out.println("Subsceptible: " + numSusceptibleEffective);
		System.out.println("betaIsolationReductio: " + facility.getBetaIsolationReduction());
		setTransmissionRate(newTransmissionRate);
	}

	public void setTransmissionRate(double newTransmissionRate) {
		if (transmissionRate != newTransmissionRate) {
			if (nextAction != null) {
				schedule.removeAction(nextAction);
			}
			System.out.println("Transmission rate: " + this.transmissionRate);
			System.out.println("New Transmission rate: " + newTransmissionRate);
			transmissionRate = newTransmissionRate;
			if (transmissionRate > 0) {
				distro = new ExponentialDistribution(1 / transmissionRate);
				double timeToNextEvent = distro.sample();
				ScheduleParameters params = ScheduleParameters.createOneTime(schedule.getTickCount() + timeToNextEvent); // or
																															// any
				// time-based
				// logic
				nextAction = schedule.schedule(params, this, "doTransmission");
			}
		}
	}

	private void error(String message, double... values) {
		System.err.printf(message, values);
	}

	public void updatePrevalenceTally() {
		popTallied = region.people.size();
		popTalliedColonized += numColonizedNow;
		avgPrevalence = 1.0 * popTalliedColonized / (double) popTallied;
	}

	public void updateStayTally(PersonDisease pd) {
		if (pd.isClinicallyDetectedDuringCurrentStay()) {
			clinicalDetectionsTallied++;
		}
		clinicalDetectionsPer10000PatientDays = 10000 * clinicalDetectionsTallied / facility.getPatientDays();
		dischargesTallied++;
		if (pd.isColonized())
			colonizedDischargesTallied++;
		avgDischargePrevalence = 1.0 * colonizedDischargesTallied / dischargesTallied;
	}

	public void updateAdmissionTally(PersonDisease pd) {
		if (pd.isColonized()) {
			numAdmissionsColonized++;
		}
		importationRate = 1.0 * numAdmissionsColonized / facility.getNumAdmissions();
	}

	double uniform() {
		return Math.random();
	}

	public void setFacility(Facility f) {
		facility = f;
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public double getNumSusceptibleNow() {
		return numSusceptibleNow;
	}

	public void setNumSusceptibleNow(double numSusceptibleNow) {
		this.numSusceptibleNow = numSusceptibleNow;
	}

	public int getNumColonizedNow() {
		return numColonizedNow;
	}

	public double getNumSusceptibleEffective() {
		return numSusceptibleEffective;
	}

	public void setNumSusceptibleEffective(double numSusceptibleEffective) {
		this.numSusceptibleEffective = numSusceptibleEffective;
	}

	public double getNumContagiousEffective() {
		return numContagiousEffective;
	}

	public void setNumContagiousEffective(double numContagiousEffective) {
		this.numContagiousEffective = numContagiousEffective;
	}

	public int getTransmissionsTally() {
		return transmissionsTally;
	}

	public void setTransmissionsTally(int transmissionsTally) {
		this.transmissionsTally = transmissionsTally;
	}

	public int getNumAdmissionsColonized() {
		return numAdmissionsColonized;
	}

	public void setNumAdmissionsColonized(int numAdmissionsColonized) {
		this.numAdmissionsColonized = numAdmissionsColonized;
	}

	public double getImportationRate() {
		return importationRate;
	}

	public void setImportationRate(double importationRate) {
		this.importationRate = importationRate;
	}

	public double getPrevalence() {
		return prevalence;
	}

	public void setPrevalence(double prevalence) {
		this.prevalence = prevalence;
	}

	public String getDiseaseName() {
		return diseaseName;
	}

	public void setDiseaseName(String diseaseName) {
		this.diseaseName = diseaseName;
	}

	public int getNumAdmissionsTallied() {
		return numAdmissionsTallied;
	}

	public void setNumAdmissionsTallied(int numAdmissionsTallied) {
		this.numAdmissionsTallied = numAdmissionsTallied;
	}

	public int getPopTallied() {
		return popTallied;
	}

	public void setPopTallied(int popTallied) {
		this.popTallied = popTallied;
	}

	public double getPopTalliedColonized() {
		return popTalliedColonized;
	}

	public void setPopTalliedColonized(double popTalliedColonized) {
		this.popTalliedColonized = popTalliedColonized;
	}

	public double getAvgPrevalence() {
		return avgPrevalence;
	}

	public void setAvgPrevalence(double avgPrevalence) {
		this.avgPrevalence = avgPrevalence;
	}

	public int getNumSusceptibleNonIsoNow() {
		return numSusceptibleNonIsoNow;
	}

	public void setNumSusceptibleNonIsoNow(int numSusceptibleNonIsoNow) {
		this.numSusceptibleNonIsoNow = numSusceptibleNonIsoNow;
	}

	public int getNumSusceptibleIsoNow() {
		return numSusceptibleIsoNow;
	}

	public void setNumSusceptibleIsoNow(int numSusceptibleIsoNow) {
		this.numSusceptibleIsoNow = numSusceptibleIsoNow;
	}

	public int getNumColonizedNonIsoNow() {
		return numColonizedNonIsoNow;
	}

	public void setNumColonizedNonIsoNow(int numColonizedNonIsoNow) {
		this.numColonizedNonIsoNow = numColonizedNonIsoNow;
	}

	public int getNumColonizedIsoNow() {
		return numColonizedIsoNow;
	}

	public void setNumColonizedIsoNow(int numColonizedIsoNow) {
		this.numColonizedIsoNow = numColonizedIsoNow;
	}

	public double getClinicalDetectionsTallied() {
		return clinicalDetectionsTallied;
	}

	public void setClinicalDetectionsTallied(double clinicalDetectionsTallied) {
		this.clinicalDetectionsTallied = clinicalDetectionsTallied;
	}

	public double getClinicalDetectionsPer10000PatientDays() {
		return clinicalDetectionsPer10000PatientDays;
	}

	public void setClinicalDetectionsPer10000PatientDays(double clinicalDetectionsPer10000PatientDays) {
		this.clinicalDetectionsPer10000PatientDays = clinicalDetectionsPer10000PatientDays;
	}

	public int getDischargesTallied() {
		return dischargesTallied;
	}

	public void setDischargesTallied(int dischargesTallied) {
		this.dischargesTallied = dischargesTallied;
	}

	public int getColonizedDischargesTallied() {
		return colonizedDischargesTallied;
	}

	public void setColonizedDischargesTallied(int colonizedDischargesTallied) {
		this.colonizedDischargesTallied = colonizedDischargesTallied;
	}

	public double getAvgDischargePrevalence() {
		return avgDischargePrevalence;
	}

	public void setAvgDischargePrevalence(double avgDischargePrevalence) {
		this.avgDischargePrevalence = avgDischargePrevalence;
	}

	public ISchedule getSchedule() {
		return schedule;
	}

	public void setSchedule(ISchedule schedule) {
		this.schedule = schedule;
	}

	public boolean isStop() {
		return stop;
	}

	public void setStop(boolean stop) {
		this.stop = stop;
	}

	public ISchedulableAction getNextAction() {
		return nextAction;
	}

	public void setNextAction(ISchedulableAction nextAction) {
		this.nextAction = nextAction;
	}

	public ExponentialDistribution getDistro() {
		return distro;
	}

	public void setDistro(ExponentialDistribution distro) {
		this.distro = distro;
	}

	public double getMeanIntraEventTime() {
		return meanIntraEventTime;
	}

	public void setMeanIntraEventTime(double meanIntraEventTime) {
		this.meanIntraEventTime = meanIntraEventTime;
	}

	public Facility getFacility() {
		return facility;
	}

	public double getTransmissionRate() {
		return transmissionRate;
	}
}
