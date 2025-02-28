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
import utils.TimeUtils;
import agentcontainers.Facility;
import agentcontainers.Region;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class SingleFacilityBuilder implements ContextBuilder<Object> {
	private ISchedule schedule;
	private double isolationEffectiveness = 0.5;
	private boolean doActiveSurveillance = false;
	private boolean doActiveSurveillanceAfterBurnIn = true;
	private double daysBetweenTests = 14.0;
	private PrintWriter facilityPrevalenceData;
	private PrintWriter R0Data;
	private Region region;
	private double burnInTime = 10 * 365.0;
	private double postBurnInTime = 5 * 365.0;
	private double totalTime = burnInTime + postBurnInTime;
	public Facility facility;
	private boolean stop = false;

	@Override
	public Context<Object> build(Context<Object> context) {
		// System.out.println("Starting simulation build.");
		schedule = repast.simphony.engine.environment.RunEnvironment.getInstance().getCurrentSchedule();
		facility = new Facility();
		this.region = new Region(facility);
		facility.setRegion(region);
		setupAgents();

		scheduleEvents();

		// Oct 4, 2024 WRR:Start admissions process
		Admission admit = new Admission(21.1199 / 75.0, facility);
		admit.start();

		// Oct 4, 2024 WRR: schedule annotated methods on this builder class.
		schedule.schedule(this);
		context.add(region);
		// Oct 4, 2024 WRR: return facility?
		return context;
	}

	// Oct 4, 2024 WRR: Here's one possible implementation of regular repeating
	// events,
	// example, like the Region.dailyPopulationTally even that Damon has described
	// in the text file.

	@ScheduledMethod(start = 1.0, interval = 1)
	public void dailyEvents() {
		if(facility.getPopulationSize()!=0) {
			region.doPopulationTally();
		}
	}

	public void setupAgents() {
		// System.out.println("Setting up AGENTS");

		int numDiseases = 1;
		int[] diseaseList = { (int) Disease.getCRE() };
		for (int i = 0; i < numDiseases; i++) {
			Disease disease = new Disease();
			disease.setSimIndex(i);
			disease.setType(diseaseList[i]);
			region.getDiseases().add(disease);
		}

		int[] facilitySize = { 75 };
		int[] facilityType = { 0 };
		double[] meanLOS = { 27.1199026 };

		for (int i = 0; i < region.getFacilities().size(); i++) {
			Facility f = region.getFacilities().get(i);
			f.setType(facilityType[i]);
			f.setAvgPopTarget(facilitySize[i]);
			f.setMeanLOS(meanLOS[i]);
			
			f.setBetaIsolationReduction(1 - isolationEffectiveness);
			f.setNewPatientAdmissionRate(facilitySize[i] / meanLOS[i]);

			if (doActiveSurveillance) {
				f.setTimeBetweenMidstaySurveillanceTests(daysBetweenTests);
			}

			for (Disease d : region.getDiseases()) {
				FacilityOutbreak fo = f.addOutbreaks(d);
				fo.setDisease(d);
				fo.setDiseaseName(d.getDiseaseName());
				fo.facility = f;
			}

			for (int j = 0; j < facilitySize[i]; j++) {
				region.addInitialFacilityPatient(f);
			}
			f.admitNewPatient(schedule);
		
			
		}
	}

	public void scheduleEvents() {
		// System.out.println("Scheduling events.");
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
		region.setInBurnInPeriod(false);
		region.startDailyPopulationTallyTimer();
		doActiveSurveillance = doActiveSurveillanceAfterBurnIn;
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
	/*

	private void writeSimulationResults() {
		System.out.println("Writing simulation results.");
		for (Facility f : region.getFacilities()) {
			facilityPrevalenceData.printf("%d %d %d", f.getOutbreaks().get(0).getNumColonizedNow(), f.getCurrentPopulationSize(),
					f.getOutbreaks().get(0).getTransmissionsTally());
			facilityPrevalenceData.println();
		}
		R0Data.printf("%d", region.numTransmissionsFromInitialCase);
		R0Data.println();
	}
	*/

	public ISchedule getSchedule() {
	    return schedule;
	}

	public void setSchedule(ISchedule schedule) {
	    this.schedule = schedule;
	}

	public double getIsolationEffectiveness() {
	    return isolationEffectiveness;
	}

	public void setIsolationEffectiveness(double isolationEffectiveness) {
	    this.isolationEffectiveness = isolationEffectiveness;
	}

	public boolean isDoActiveSurveillance() {
	    return doActiveSurveillance;
	}

	public void setDoActiveSurveillance(boolean doActiveSurveillance) {
	    this.doActiveSurveillance = doActiveSurveillance;
	}

	public double getDaysBetweenTests() {
	    return daysBetweenTests;
	}

	public void setDaysBetweenTests(double daysBetweenTests) {
	    this.daysBetweenTests = daysBetweenTests;
	}

	public PrintWriter getFacilityPrevalenceData() {
	    return facilityPrevalenceData;
	}

	public void setFacilityPrevalenceData(PrintWriter facilityPrevalenceData) {
	    this.facilityPrevalenceData = facilityPrevalenceData;
	}

	public PrintWriter getR0Data() {
	    return R0Data;
	}

	public void setR0Data(PrintWriter r0Data) {
	    R0Data = r0Data;
	}

	public Region getRegion() {
	    return region;
	}

	public void setRegion(Region region) {
	    this.region = region;
	}

	public double getBurnInTime() {
	    return burnInTime;
	}

	public void setBurnInTime(double burnInTime) {
	    this.burnInTime = burnInTime;
	}

	public double getPostBurnInTime() {
	    return postBurnInTime;
	}

	public void setPostBurnInTime(double postBurnInTime) {
	    this.postBurnInTime = postBurnInTime;
	}

	public double getTotalTime() {
	    return totalTime;
	}

	public void setTotalTime(double totalTime) {
	    this.totalTime = totalTime;
	}

	public Facility getFacility() {
	    return facility;
	}

	public void setFacility(Facility facility) {
	    this.facility = facility;
	}

	public boolean isStop() {
	    return stop;
	}

	public void setStop(boolean stop) {
	    this.stop = stop;
	}
	
}
