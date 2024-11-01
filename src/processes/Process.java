package processes;

import org.apache.commons.math3.distribution.ExponentialDistribution;
import repast.simphony.engine.schedule.ISchedulableAction;
import repast.simphony.engine.schedule.ISchedule;

abstract class Process {

    protected ISchedule schedule;
    protected double meanIntraEventTime;
    protected ExponentialDistribution distro;
    protected ISchedulableAction nextAction;

    Process(double intra_event_time) {
        if (intra_event_time > 0) {
            schedule = repast.simphony.engine.environment.RunEnvironment.getInstance().getCurrentSchedule();
            meanIntraEventTime = intra_event_time;
            distro = new ExponentialDistribution(intra_event_time);
        }
    }

    abstract void start();
    abstract void fire();
    abstract void stop();

    public double getNextEventTime() {
	return 0;
	// Nov 1, 2024 WRR: This should either return the time the ISchedulableAction is going to fire,
	// or calculate the next time that it should fire (but that should be done in Start?).
        //return nextAction.nextTime();
    }
}
