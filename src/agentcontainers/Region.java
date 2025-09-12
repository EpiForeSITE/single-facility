package agentcontainers;

import agents.Person;
import builders.SingleFacilityBuilder;
import disease.Disease;
import disease.FacilityOutbreak;
import disease.PersonDisease;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.ScheduleParameters;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Region extends AgentContainer {
	private boolean stop = false;

	private boolean inBurnInPeriod = true;
	private int numImportations = 0;
	private boolean useSingleImportation = false;
	public int numTransmissionsFromInitialCase = 0;
	public int colonizedCount;
	private double intra_event_time;
	private ISchedule schedule;

	private ArrayList<Facility> facilities = new ArrayList<Facility>();
	public ArrayList<Disease> diseases = new ArrayList<Disease>();
	public ArrayList<Person> people = new ArrayList<Person>();
	private int totalImports;
	private PrintWriter writer;

	public Region(Facility f) {
		super();
		schedule = repast.simphony.engine.environment.RunEnvironment.getInstance().getCurrentSchedule();
		if (!facilities.contains(f) && facilities.size() < 1) {
			facilities.add(f);
		}
		try {
			if(!SingleFacilityBuilder.isBatchRun) {

			writer = new PrintWriter("daily_population_stats.txt");
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// make int
	public int dailyPopulationTally() {
		return people.size();
	}

	public void doPopulationTally() {
		for (Facility f : facilities) {
			f.updatePopulationTally();

		}
		// action.call()
		if (!stop) {
			dailyPopulationTally();
		}
		// stop=true;
	}

	public void remove_people(Person person) {

		if (people.contains(person)) {
			people.remove(person);
			if (person.getCurrentFacility() != null) {
				person.getCurrentFacility().dischargePatient(person);
			}

		}
	}

	public void importToFacility(Facility f) {

		Person p = add_people(f);

		p.setRegion(this);
		for (Disease d : diseases) {
			PersonDisease pd = p.add_diseases();
			pd.setDisease(d);
			pd.setPerson(p);
			if (useSingleImportation) {
				if (!inBurnInPeriod) {
					if (++numImportations == 1) {
						pd.colonize();
						pd.setInitialInfection(true);
					}
				}
			} else {
				// Jan 10, 2025 WRR: This needs to go in Facility.admitPerson() at the top
				if (uniform() < d.getImportationProb()) {
					pd.colonize();
				}
			}
		}
		f.admitPatient(p);
		if (!facilities.contains(f) && facilities.size() < 1) {
			facilities.add(f);
		}

	}

	public void importToFacilityNew(Facility f, Person p) {

		p.setRegion(this);
		for (Disease d : diseases) {
			PersonDisease pd = p.add_diseases();
			pd.setDisease(d);
			pd.setPerson(p);
			if (uniform() < d.getImportationProb()) {
				p.setProperty("importation", true);
				pd.colonize();
				totalImports++;
			} else {
				p.setProperty("importation", false);
			}
		}
	}

	public void addInitialFacilityPatient(Facility f) {
		Person p = add_people(f);
		p.setRegion(this);
		for (Disease d : diseases) {
			PersonDisease pd = p.add_diseases();
			pd.setDisease(d);
			pd.setPerson(p);
			// todo get this parameterized
			if (!useSingleImportation && uniform() < 0.456)
				pd.colonize();
		}
		f.admitInitialPatient(p);
		if (!facilities.contains(f) && facilities.size() < 1) {
			facilities.add(f);
		}
	}

	public void startActiveSurveillance() {
		for (Facility f : facilities)
			f.startActiveSurveillance();
	}

	public double uniform() {
		return Math.random();
	}

	public Person add_people(Facility f) {
		Person newPerson = new Person(f);

		newPerson.setRegion(this);
		people.add(newPerson);
		return newPerson;
	}

	public Disease add_diseases() {

		Person person = people.get(people.size() - 1);
		Disease disease = diseases.get(0);

		for (int i = 0; i < diseases.size() - 1; i++) {
			// person.diseases.add(disease);
		}
		return disease;
	}

	public void startDailyPopulationTallyTimer() {
		stop = false;
		double currTime = schedule.getTickCount();
		ScheduleParameters params = ScheduleParameters.createOneTime(currTime, 1);
		schedule.schedule(params, this, "doPopulationTally");
	}

	public void logDailyPopulationStats() {
	    	int totalPopulation = people.size();
		int totalColonized = 0;
		int totalDetected = 0;
		int totalIsolated = 0;

		for (Person p : people) {
			for (PersonDisease pd : p.getDiseases()) {
				if (pd.isColonized()) {
					totalColonized++;
				}
				if (pd.isDetected()) {
					totalDetected++;
				}
			}
			if (p.isIsolated()) {
				totalIsolated++;
			}
		}

		double currentTime = schedule.getTickCount();
		if (currentTime > 3650) {
			if(!SingleFacilityBuilder.isBatchRun) {
			writer.printf("Time: %.2f, Total: %d, Colonized: %d, Detected: %d, Isolated: %d%n", currentTime, totalPopulation, totalColonized,
					totalDetected, totalIsolated);
			}
		}
	}

	public void finishSimulation() {
		stop = true;
		schedule.setFinishing(true);
		System.out.println("Simulation has been finished and cleaned up.");

	}

	public void addDisease(Disease disease) {
		diseases.add(disease);
		// TODO Auto-generated method stub

	}

	public ArrayList<Disease> getDiseases() {
		// TODO Auto-generated method stub
		return diseases;
	}

	public void setInBurnInPeriod(boolean b) {
		inBurnInPeriod = b;

	}

	public boolean isStop() {
		return stop;
	}

	public void setStop(boolean stop) {
		this.stop = stop;
	}

	public int getNumImportations() {
		return numImportations;
	}

	public void setNumImportations(int numImportations) {
		this.numImportations = numImportations;
	}

	public boolean isUseSingleImportation() {
		return useSingleImportation;
	}

	public void setUseSingleImportation(boolean useSingleImportation) {
		this.useSingleImportation = useSingleImportation;
	}

	public int getNumTransmissionsFromInitialCase() {
		return numTransmissionsFromInitialCase;
	}

	public void setNumTransmissionsFromInitialCase(int numTransmissionsFromInitialCase) {
		this.numTransmissionsFromInitialCase = numTransmissionsFromInitialCase;
	}

	public double getIntra_event_time() {
		return intra_event_time;
	}

	public void setIntra_event_time(double intra_event_time) {
		this.intra_event_time = intra_event_time;
	}

	public ISchedule getSchedule() {
		return schedule;
	}

	public void setSchedule(ISchedule schedule) {
		this.schedule = schedule;
	}

	public ArrayList<Facility> getFacilities() {
		return facilities;
	}

	public void setFacilities(ArrayList<Facility> facilities) {
		this.facilities = facilities;
	}

	public ArrayList<Person> getPeople() {
		return people;
	}

	public void setPeople(ArrayList<Person> people) {
		this.people = people;
	}

	public boolean isInBurnInPeriod() {
		return inBurnInPeriod;
	}

	public void setDiseases(ArrayList<Disease> diseases) {
		this.diseases = diseases;
	}

	public int getTotalImports() {
		return totalImports;
	}

}