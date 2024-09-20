package agentcontainers

import org.apache.commons.math3.distribution.ExponentialDistribution

import repast.simphony.engine.schedule.ISchedulableAction
import repast.simphony.engine.schedule.ISchedule

abstract class AgentContainer {
	ISchedule schedule
	double meanIntraEventTime
	ExponentialDistribution distro
	ISchedulableAction nextAction

	AgentContainer(double intra_event_time) {
		if (intra_event_time > 0) {
			if (repast.simphony.engine.environment.RunEnvironment.getInstance() != null) {
				schedule = repast.simphony.engine.environment.RunEnvironment.getInstance().getCurrentSchedule()
			} else {
            println("RunEnvironment is not initialized. Schedule will be set later.")
            schedule = null
        }
        meanIntraEventTime = intra_event_time
        distro = new ExponentialDistribution(intra_event_time)
    }
	}

	double getNextEventTime() {
		return nextAction.nextTime()
	}
	
}
