package processes

import org.apache.commons.math3.distribution.ExponentialDistribution
import repast.simphony.engine.schedule.ISchedulableAction
import repast.simphony.engine.schedule.ISchedule

abstract class Process {

    ISchedule schedule
    double meanIntraEventTime
    ExponentialDistribution distro
    ISchedulableAction nextAction

    Process(double intra_event_time) {
        if (intra_event_time > 0) {
            schedule = repast.simphony.engine.environment.RunEnvironment.getInstance().getCurrentSchedule()
            meanIntraEventTime = intra_event_time
            distro = new ExponentialDistribution(intra_event_time)
        }
    }

    abstract void start()
    abstract void fire()
    abstract void stop()

    double getNextEventTime() {
        return nextAction.nextTime()
    }
}
