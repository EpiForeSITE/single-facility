package agents;

import agentcontainers.Facility
import agentcontainers.Region
import disease.Disease
import disease.PersonDisease
import org.apache.commons.math3.distribution.ExponentialDistribution
import repast.simphony.engine.schedule.ISchedule
import repast.simphony.engine.schedule.ScheduleParameters;

public class Person extends Agent {

	Region region;
	private static ISchedule schedule
	Facility currentFacility;
	boolean isolated = false;
	double currentLOS = -1.0;
	double admissionTime;
	ArrayList<PersonDisease> diseases = new ArrayList<PersonDisease>();
	boolean stop = false;
	double dischargeTime;
	ArrayList<Facility> facilities = new ArrayList<>();
	ArrayList<Disease> diseasesNew = new ArrayList<>();
	ArrayList<Person> people = new ArrayList<>();
	ExponentialDistribution distro;

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
		region=r
		region.remove_people(this);
	}

	public void startNextPeriodicSurveillanceTimer() {
		double timeToNextSurveillance = currentFacility.timeBetweenMidstaySurveillanceTests;
		if (timeToNextSurveillance < dischargeTime) {
			schedule.schedule(ScheduleParameters.createOneTime(schedule.getTickCount() + timeToNextSurveillance), this, "doSurveillanceTest");
		}
	}

	public void isolate() {
		isolated = true;
	}

	public void updateAllTransmissionRateContributions() {
		for (PersonDisease pd : diseases) {
			pd.updateTransmissionRateContribution();
		}
		currentFacility.updateTransmissionRate();
	}
	public void doPatientDischarge() {
		currentFacility.dischargePatient(this);
	}
	public void discharge() {
		this.stop = false
		double currTime = schedule.getTickCount()
		double elapse = distro.sample()
		ScheduleParameters params = ScheduleParameters.createOneTime(currTime + elapse)
		schedule.schedule(params, this, "doPatientDischarge")
	}

	public void surveillanceTest() {
		this.stop = false
		double currTime = schedule.getTickCount()
		double elapse = distro.sample()
		ScheduleParameters params = ScheduleParameters.createOneTime(currTime + elapse)
		schedule.schedule(params, this, "doSurveillanceTest")
	}
	public void doSurveillanceTest() {
		for (PersonDisease pd : diseases) {
			if (!pd.detected && uniform()<pd.disease.isActiveSurveillanceAgent()) {
				if (pd.colonized && uniform() < pd.disease.getProbSurveillanceDetection() * currentFacility.midstaySurveillanceAdherence) {
					pd.detected = true;
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

	double uniform() {
		return Math.random();
	}
	public PersonDisease add_diseases() {
		
		Person person = region.people.get(region.people.size() - 1);
		Disease disease = region.diseases[0];

		PersonDisease pd = new PersonDisease(disease, person, schedule);
		pd.disease = disease;
		pd.person = person;
		person.diseases.add(pd);
		return pd;
	}
	public Facility getCurrentFacility() {
		return currentFacility;
	}
}
