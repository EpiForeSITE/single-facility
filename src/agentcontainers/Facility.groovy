package agentcontainers;
import agents.Person;
import disease.FacilityOutbreak;
import disease.PersonDisease;
import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.commons.math3.distribution.GammaDistribution
import repast.simphony.engine.schedule.ISchedulableAction;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.random.RandomHelper;
import java.util.ArrayList;
import java.util.LinkedList;
public class Facility extends AgentContainer{

	public int currentPopulationSize = 0;
	public double betaIsolationReduction;
	public double timeBetweenMidstaySurveillanceTests = -1.0;
	boolean onActiveSurveillance = false;
	public int type;
	public Region region;
	public double newPatientAdmissionRate;
	public double avgPopTarget;
	public double meanLOS;
	double avgPopulation;
	int numDaysTallied = 0;
	double patientDays;
	int numAdmissions = 0;
	double admissionSurveillanceAdherence = 0.911;
	double midstaySurveillanceAdherence = 0.954;
	ExponentialDistribution distro;
	ISchedule schedule;
	ISchedulableAction nextAction;
	public ArrayList<FacilityOutbreak> outbreaks = new ArrayList<>();
	public LinkedList<Person> currentPatients = new LinkedList<>();
	boolean stop = false
	double meanIntraEventTime;
	
	public Facility() {
		super()
		schedule = repast.simphony.engine.environment.RunEnvironment.getInstance().getCurrentSchedule();
		
		
	}

	public void admitNewPatient(ISchedule sched) {
		schedule = sched;
		Person newPatient = new Person(this);
		admitPatient(newPatient);
		System.out.println("New patient admitted. Current population: " + ++currentPopulationSize);
	}
	
	void admitPatient(Person p){
		p.admitToFacility(this);

		p.startDischargeTimer(getRandomLOS());

		

		for(PersonDisease pd : p.diseases){
			if(pd.colonized){
				if(pd.disease.isActiveSurveillanceAgent() && onActiveSurveillance){
					if(uniform() < pd.disease.getProbSurveillanceDetection() * admissionSurveillanceAdherence){
						pd.detected = true;
						if(pd.disease.isolatePatientWhenDetected()) p.isolate();
					}
				}
				pd.startClinicalDetectionTimer();
			}
		}
		currentPatients.add(p);
		region.people.add(p);

		if(onActiveSurveillance && !p.isolated && timeBetweenMidstaySurveillanceTests > 0)
			p.startNextPeriodicSurveillanceTimer();

		p.updateAllTransmissionRateContributions();

		if(!region.inBurnInPeriod) updateAdmissionTally(p);
	}
	void dischargePatient(Person p){
		currentPopulationSize--;
		currentPatients.remove(p);
		updateTransmissionRate();
		// Oct 4, 2024 WRR: This isn't deleting the patient from anywhere but this currentPatients collection.
		if(!region.inBurnInPeriod) updateStayTally(p);

		p.destroyMyself(region);
	}

	void updateTransmissionRate(){
		for(FacilityOutbreak fo : outbreaks) fo.updateTransmissionRate();
	}

	double getRandomLOS(){
		if(type==0){

			double shape1 = 7.6019666;
			double scale1 = 3.4195217;
			double shape2 = 1.2327910;
			double scale2 = 23.5214724;
			double prob1 = 0.6253084;

			if(uniform() < prob1) return gamma(shape1,scale1);
			else return gamma(shape2,scale2);
		}
		else{
			return -1.0;
		}
	}

	void admitInitialPatient(Person p){
		p.admitToFacility(this);
		p.startDischargeTimer(exponential(1.0/meanLOS));

		currentPopulationSize++;

		boolean doSurveillanceTest = false;
		if(onActiveSurveillance) doSurveillanceTest = true;

		for(PersonDisease pd : p.diseases){
			if(pd.colonized){
				pd.startClinicalDetectionTimer();
			}
		}
		currentPatients.add(p);

		p.updateAllTransmissionRateContributions();
		currentPopulationSize++;
	}

	void updatePopulationTally(){
		avgPopulation = (avgPopulation * numDaysTallied + currentPopulationSize) / (numDaysTallied + 1);
		numDaysTallied++;

		for(FacilityOutbreak fo : outbreaks) fo.updatePrevalenceTally();
	}

	void updateStayTally(Person p){
		patientDays += p.currentLOS;

		for(int i=0; i<outbreaks.size(); i++)
			outbreaks.get(i).updateStayTally(p.diseases.get(i));
	}

	void updateAdmissionTally(Person p){
		numAdmissions++;

		for(int i=0; i<outbreaks.size(); i++)
			outbreaks.get(i).updateAdmissionTally(p.diseases.get(i));
	}

	void startActiveSurveillance(){
		onActiveSurveillance = true;
	}
	double uniform() {
		return Math.random();
	}
	double gamma(double shape, double scale) {
		GammaDistribution gammaDistribution = new GammaDistribution(shape, scale);
		return gammaDistribution.sample();
	}
	double exponential(double rate) {
		ExponentialDistribution exponentialDistribution = new ExponentialDistribution(rate);
		return exponentialDistribution.sample();
	}
	public FacilityOutbreak addOutbreaks() {
		FacilityOutbreak newOutbreak = new FacilityOutbreak(meanIntraEventTime);
		newOutbreak.setFacility(this);

		outbreaks.add(newOutbreak);

		return newOutbreak;
	}
	public int getType() {
		return type;
	}

	public void addOutbreak(FacilityOutbreak outbreak) {
		outbreaks.add(outbreak)
	}

	public int getCapacity() {
		return capacity;
	}

	public void setIsolationEffectiveness(double isolationEffectiveness) {
		this.isolationEffectiveness = isolationEffectiveness;
	}
	public int getPopulationSize() {
		return currentPatients.size();
	}
}
