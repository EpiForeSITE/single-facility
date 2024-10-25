package builders;

import disease.Disease;
import disease.FacilityOutbreak;
import processes.Admission;
import repast.simphony.context.Context;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import agentcontainers.Facility;
import agentcontainers.Region;

import java.io.PrintWriter;

public class SingleFacilityBuilder implements ContextBuilder<Object> {
	private ISchedule schedule;
	private double isolationEffectiveness = 0.5;
	private boolean doActiveSurveillance = false;
	private double daysBetweenTests = 14.0;
	private PrintWriter facilityPrevalenceData;
	private PrintWriter R0Data;
	private Region region;
	private double burnInTime = 10 * 365.0;
	private double postBurnInTime = 5 * 365.0;
	double totalTime = burnInTime + postBurnInTime;
	private Facility facility;
	boolean stop = false;

	@Override
	public Context<Object> build(Context<Object> context) {
		System.out.println("Starting simulation build.");
		schedule = repast.simphony.engine.environment.RunEnvironment.getInstance().getCurrentSchedule();
		facility = new Facility();
		region = new Region(facility);
		setupAgents();

		scheduleEvents();

		// Oct 4, 2024 WRR:Start admissions process
		Admission admit = new Admission(21.1199 / 75.0, facility);
		admit.start();

		// Oct 4, 2024 WRR: schedule annotated methods on this builder class.
		schedule.schedule(this);

		// Oct 4, 2024 WRR: return facility?
		return context;
	}

	// Oct 4, 2024 WRR: Here's one possible implementation of regular repeating
	// events,
	// example, like the Region.dailyPopulationTally even that Damon has described
	// in the text file.

	@ScheduledMethod(start = 1.0, interval = 1)
	public void dailyEvents() {
		if(facility.currentPopulationSize!=0) {
			region.doPopulationTally();
		}
	}

	public void setupAgents() {
		System.out.println("Setting up AGENTS");

		int numDiseases = 1;
		int[] diseaseList = { (int) Disease.CRE };
		for (int i = 0; i < numDiseases; i++) {
			Disease disease = new Disease();
			disease.simIndex = i;
			disease.type = diseaseList[i];
			region.diseases.add(disease);
		}

		int[] facilitySize = { 75 };
		int[] facilityType = { 0 };
		double[] meanLOS = { 27.1199026 };

		for (int i = 0; i < region.facilities.size(); i++) {
			Facility f = region.facilities.get(i);
			f.type = facilityType[i];
			f.avgPopTarget = facilitySize[i];
			f.meanLOS = meanLOS[i];
			f.betaIsolationReduction = 1 - isolationEffectiveness;
			f.newPatientAdmissionRate = facilitySize[i] / meanLOS[i];

			if (doActiveSurveillance) {
				f.timeBetweenMidstaySurveillanceTests = daysBetweenTests;
			}

			for (Disease d : region.diseases) {
				FacilityOutbreak fo = f.addOutbreaks();
				fo.disease = d;
				fo.diseaseName = d.getDiseaseName();
				fo.facility = f;
			}

			for (int j = 0; j < facilitySize[i]; j++) {
				region.addInitialFacilityPatient(f);
			}
			f.admitNewPatient(schedule);
			// Oct 25, 2024 WRR: I'm not seeing where you're adding the facility 
			// itself to region.facilities.  This is why 
			
		}
	}

	public void scheduleEvents() {
		System.out.println("Scheduling events.");
		schedule.schedule(ScheduleParameters.createOneTime(burnInTime), this, "doEndBurnInPeriod");

		System.out.println("Scheduled burn-in end at tick: " + burnInTime);
		System.out.println("Scheduled simulation end at tick: " + totalTime);
	}

	public void printCurrentTick() {
		double currentTick = schedule.getTickCount();
		System.out.println("Current tick: " + currentTick);
	}

	public void scheduleSimulationEnd() {
		// Oct 4, 2024 WRR:this should be rolled into scheduleEvents(). The schedule is
		// an
		// event queuing system. It holds and sorts as many events as you give it.
		if (schedule.getTickCount() == 3650) {
			schedule.schedule(ScheduleParameters.createOneTime(totalTime), this, "doSimulationEnd");
		}

	}

	public void doEndBurnInPeriod() {
		System.out.println("Burn-in period ended at tick: " + schedule.getTickCount());
		region.inBurnInPeriod = false;
		region.startDailyPopulationTallyTimer();
		doActiveSurveillance = true;
		if (!stop) {
			scheduleSimulationEnd();
		}
	}

	public void doSimulationEnd() {
		stop = true;
		System.out.println("Ending simulation at tick: " + schedule.getTickCount());
		// writeSimulationResults();
		region.finishSimulation();
		repast.simphony.engine.environment.RunEnvironment.getInstance().endAt(totalTime);

	}

	private void writeSimulationResults() {
		System.out.println("Writing simulation results.");
		for (Facility f : region.facilities) {
			facilityPrevalenceData.printf("%d %d %d", f.outbreaks.get(0).getNumColonized(), f.currentPopulationSize,
					f.outbreaks.get(0).transmissionsTally);
			facilityPrevalenceData.println();
		}
		R0Data.printf("%d", region.numTransmissionsFromInitialCase);
		R0Data.println();
	}
}
