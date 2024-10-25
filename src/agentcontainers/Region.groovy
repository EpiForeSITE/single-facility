package agentcontainers;


import agents.Person;
import disease.Disease;
import disease.PersonDisease;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.ScheduleParameters;


import java.util.ArrayList;


public class Region extends AgentContainer{
	boolean stop = false

	public boolean inBurnInPeriod = true;
	int numImportations = 0;
	boolean useSingleImportation = false;
	public int numTransmissionsFromInitialCase = 0;
	double intra_event_time;
	ISchedule schedule;

	public ArrayList<Facility> facilities = new ArrayList<Facility>();
	public ArrayList<Disease> diseases = new ArrayList<Disease>();
	public ArrayList<Person> people = new ArrayList<Person>();




	public Region(Facility f) {
	   super()
	   schedule = repast.simphony.engine.environment.RunEnvironment.getInstance().getCurrentSchedule()
	   if(!facilities.contains(f)&&facilities.size()<1) {
			facilities.add(f);
		}
	   
   }
   
   // make int
   public dailyPopulationTally() {
       System.out.println("daily population: " + people.size())
		   	/*stop = false
			double currTime = schedule.getTickCount()
			double elapse = distro.sample()
			ScheduleParameters params = ScheduleParameters.createOneTime(currTime + elapse)
			nextAction = schedule.schedule(params, this, "doPopulationTally")
			*/
      return people.size()
   }


	void doPopulationTally(){
		for(Facility f : facilities) {
			f.updatePopulationTally() 
			System.out.println(f.currentPopulationSize)
		}
		//action.call()
		if(!stop) {
			dailyPopulationTally();
		}
		//stop=true;
	}
	public void remove_people(Person person) {
		if (people.remove(person)) {
			if (person.currentFacility != null) {
				person.currentFacility.dischargePatient(person); 
			}
			
		}
	}


    void importToFacility(Facility f){

		Person p = add_people(f);
		p.region = this;
		for(Disease d : diseases){
			PersonDisease pd = p.add_diseases();
			pd.disease = d;
			pd.person = p;
			if(useSingleImportation){
				if(!inBurnInPeriod){
					if(++numImportations == 1){
						pd.colonize();
						pd.initialInfection = true;
					}
				}
			}
			else{
				if(uniform() < d.getImportationProb()) pd.colonize();
			}
		}
		f.admitPatient(p);
		if(!facilities.contains(f)&&facilities.size()<1) {
			facilities.add(f);
		}
		System.out.println("Adding facility");
		System.out.println(facilities);
	}


    void addInitialFacilityPatient(Facility f){
		// Oct 4, 2024 WRR: This needs to be refactored to do non-Anylogic instantiation.
		Person p = add_people(f);
		p.region = this;
		for(Disease d : diseases){
			PersonDisease pd = p.add_diseases();
			pd.disease = d;
			pd.person = p;
			if(!useSingleImportation && uniform() < 0.456) pd.colonize();
		}
		f.admitInitialPatient(p);
		if(!facilities.contains(f)&&facilities.size()<1) {
			facilities.add(f);
		}
		// Oct 25, 2024 WRR: This should say "adding patient", right?
		System.out.println("Adding facility 2");
		System.out.println(facilities);
		// System.out.println(facilities.size);
	}

	void startActiveSurveillance(){
		for(Facility f : facilities) f.startActiveSurveillance();
	}
	double uniform() {
		return Math.random();
	}

	public Person add_people(Facility f) {
		Person newPerson = new Person(f);
		
		newPerson.region = this;
		people.add(newPerson);  
		return newPerson;
	}
	public Disease add_diseases() {
		
		Person person = people.get(people.size() - 1);
		Disease disease = diseases.get(0);
		
		for (int i=0;i<diseases.size()-1;i++) {
			person.diseases.add(disease);
		}
		return disease;
	}


	public void startDailyPopulationTallyTimer() {
		stop = false;
		double currTime = schedule.getTickCount();
		ScheduleParameters params = ScheduleParameters.createOneTime(currTime, intra_event_time);
		schedule.schedule(params, this, "doPopulationTally");
}


	public void finishSimulation() {
			stop = true;
			System.out.println("Simulation has been finished and cleaned up.");
			schedule.setFinishing(true);
		
	}


	public void addDisease(Disease disease) {
		diseases.add(disease)
		// TODO Auto-generated method stub
		
	}


	public Disease[] getDiseases() {
		// TODO Auto-generated method stub
		return diseases;
	}


	public void setInBurnInPeriod(boolean b) {
		inBurnInPeriod = b;
		
	}
   
}