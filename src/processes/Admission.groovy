package processes;

import agentcontainers.Facility;
import repast.simphony.engine.schedule.ScheduleParameters;

public class Admission extends Process {

	private Facility facility;

	public Admission(double intra_event_time, Facility facility) {
		super(intra_event_time);
		this.facility = facility;  // Associate admission with a facility
	}

	@Override
	public void start() {
		// Schedule the first admission event
		double nextAdmissionTime = distro.sample();
		schedule.schedule(ScheduleParameters.createOneTime(schedule.getTickCount() + nextAdmissionTime), this, "fire");
	}

	@Override
	public void fire() {
		// Trigger patient admission and reschedule next admission
		facility.admitNewPatient(schedule); // Admit a new patient to the facility

		// Reschedule the next admission
		double nextAdmissionTime = distro.sample();
		schedule.schedule(ScheduleParameters.createOneTime(schedule.getTickCount() + nextAdmissionTime), this, "fire");
	}

	@Override
	public void stop() {
		// Logic to stop the admission process if needed
		System.out.println("Stopping admission process.");
	}
}
