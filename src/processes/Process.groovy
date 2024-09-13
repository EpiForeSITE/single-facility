package processes

import org.apache.commons.math3.distribution.ExponentialDistribution;
import agents.Agent
import repast.simphony.engine.schedule.ISchedulableAction;
import repast.simphony.engine.schedule.ISchedule;
import java.lang.Math;


abstract class Process extends Agent{


	ISchedule schedule
	double meanIntraEventTime
	double nextEventTime
	ExponentialDistribution distro
	ISchedulableAction nextAction
	
	

	Process(double intra_event_time){
		if (intra_event_time > 0) {
			schedule = repast.simphony.engine.environment.RunEnvironment.getInstance().getCurrentSchedule();
			meanIntraEventTime = intra_event_time
			distro = new ExponentialDistribution(intra_event_time)
		}
	}

	abstract def start()
	
	abstract def fire()

	abstract def stop() 

	double getNextEventTime(){
		double currTime = schedule.getTickCount()
		double elapse = distro.sample()
		return Math.max(currTime+elapse,0)
	}
}
