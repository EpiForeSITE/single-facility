package agents;

import agentcontainers.Facility;
import agentcontainers.Region;
import disease.Disease;
import disease.PersonDisease;

import java.util.ArrayList;

import org.apache.commons.math3.distribution.ExponentialDistribution;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.ScheduleParameters;

public class Person extends Agent {

	private Region region;
	private static ISchedule schedule;
	private Facility currentFacility;
	private boolean isolated = false;
	private double admissionTime;
	public ArrayList<PersonDisease> personDiseases = new ArrayList<PersonDisease>();
	private boolean stop = false;
	private double dischargeTime;
	private ArrayList<Facility> facilities = new ArrayList<>();
	private ArrayList<Disease> diseases = new ArrayList<>();
	private ArrayList<Person> people = new ArrayList<>();
	private ExponentialDistribution distro;

	public Person(Facility f) {
		super();
		currentFacility = f;
		schedule = repast.simphony.engine.environment.RunEnvironment.getInstance().getCurrentSchedule();
	}
	public static void setSchedule(ISchedule sched) {
		schedule = sched;
	}

	public void admitToFacility(Facility f) {
		currentFacility = f;
		admissionTime = schedule.getTickCount();
	}

	public void startDischargeTimer(double timeToDischarge) {
		dischargeTime = timeToDischarge;
		schedule.schedule(ScheduleParameters.createOneTime(schedule.getTickCount() + dischargeTime), this, "doPatientDischarge");
	}

	public void destroyMyself(Region r) {
	    	System.out.println("Destroy Myself!  " + this.hashCode());
		
		r.remove_people(this);
	}

	public void startNextPeriodicSurveillanceTimer() {
		double timeToNextSurveillance = currentFacility.getTimeBetweenMidstaySurveillanceTests();
		if (timeToNextSurveillance < dischargeTime) {
			schedule.schedule(ScheduleParameters.createOneTime(schedule.getTickCount() + timeToNextSurveillance), this, "doSurveillanceTest");
		}
	}

	public void isolate() {
		isolated = true;
	}

	public void updateAllTransmissionRateContributions() {
		for (PersonDisease pd : personDiseases) {
			pd.updateTransmissionRateContribution();
		}
		currentFacility.updateTransmissionRate();
	}
	public void doPatientDischarge() {
		currentFacility.dischargePatient(this);
	}
	public void discharge() {
		this.stop = false;
		double currTime = schedule.getTickCount();
		double elapse = distro.sample();
		ScheduleParameters params = ScheduleParameters.createOneTime(currTime + elapse);
		schedule.schedule(params, this, "doPatientDischarge");
	}

	public void surveillanceTest() {
		this.stop = false;
		double currTime = schedule.getTickCount();
		double elapse = distro.sample();
		ScheduleParameters params = ScheduleParameters.createOneTime(currTime + elapse);
		schedule.schedule(params, this, "doSurveillanceTest");
	}
	public void doSurveillanceTest() {
		for (PersonDisease pd : personDiseases) {
		    // Nov 1, 2024 WRR: Something wrong with this conditional.
			if (!pd.isDetected() && pd.getDisease().isActiveSurveillanceAgent()) {
				if (pd.isColonized() && uniform() < pd.getDisease().getProbSurveillanceDetection() * currentFacility.getMidstaySurveillanceAdherence()) {
					pd.setDetected(true);
					if (!isolated) {
						isolate();
						updateAllTransmissionRateContributions();
					}
				} else {
					startNextPeriodicSurveillanceTimer();
				}
			}
		}
	}

	public double uniform() {
		return Math.random();
	}
	public PersonDisease add_diseases() {
		
		Person person = region.people.get(region.people.size() - 1);
		Disease disease = region.diseases.get(0);

		PersonDisease pd = new PersonDisease(disease, person, schedule);
		pd.setDisease(disease);
		pd.setPerson(person);
		person.personDiseases.add(pd);
		return pd;
	}
	public Facility getCurrentFacility() {
		return currentFacility;
	}
	public boolean isIsolated() {
	   return isolated;
	}
	public double getCurrentLOS() {
	    if (getDischargeTime() == 0.0) {
		return schedule.getTickCount() - getAdmissionTime();
	    }
	    return getDischargeTime() - getAdmissionTime();
	}
	public Region getRegion() {
	    return region;
	}
	public void setRegion(Region region) {
	    this.region = region;
	}
	public double getAdmissionTime() {
	    return admissionTime;
	}
	public void setAdmissionTime(double admissionTime) {
	    this.admissionTime = admissionTime;
	}
	public ArrayList<PersonDisease> getDiseases() {
	    return personDiseases;
	}
	public void setDiseases(ArrayList<PersonDisease> diseases) {
	    this.personDiseases = diseases;
	}
	public boolean isStop() {
	    return stop;
	}
	public void setStop(boolean stop) {
	    this.stop = stop;
	}
	public double getDischargeTime() {
	    return dischargeTime;
	}
	public void setDischargeTime(double dischargeTime) {
	    this.dischargeTime = dischargeTime;
	}
	public ArrayList<Facility> getFacilities() {
	    return facilities;
	}
	public void setFacilities(ArrayList<Facility> facilities) {
	    this.facilities = facilities;
	}
	public ArrayList<Disease> getDiseasesNew() {
	    return diseases;
	}
	public void setDiseasesNew(ArrayList<Disease> diseases) {
	    this.diseases = diseases;
	}
	public ArrayList<Person> getPeople() {
	    return people;
	}
	public void setPeople(ArrayList<Person> people) {
	    this.people = people;
	}
	public ExponentialDistribution getDistro() {
	    return distro;
	}
	public void setDistro(ExponentialDistribution distro) {
	    this.distro = distro;
	}
	public static ISchedule getSchedule() {
	    return schedule;
	}
	public void setCurrentFacility(Facility currentFacility) {
	    this.currentFacility = currentFacility;
	}
	public void setIsolated(boolean isolated) {
	    this.isolated = isolated;
	}
	public void setCurrentLOS(double currentLOS) {
	}
}
