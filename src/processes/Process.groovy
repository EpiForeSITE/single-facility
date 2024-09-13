/* Abstract Process.  This provides the implementation for a simple poisson-type
 * process, as well as abstract methods that will need to be implemented for 
 * concrete subclasses.
 * 
 * @Author:  Willy Ray
 * 
 */

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
	// Sep 13, 2024 WRR: distro can conceivably be overwritten in concrete subclasses 
	//to represent a different probability distribution
	ExponentialDistribution distro
	// this is the Repast object that actually represents the event on the scheduler queue.
	ISchedulableAction nextAction
	
	
	// Sep 13, 2024 WRR: Constructor.  
	
	Process(double intra_event_time){
		if (intra_event_time > 0) {
			// Sep 13, 2024 WRR: Get a reference to the system scheduler
			schedule = repast.simphony.engine.environment.RunEnvironment.getInstance().getCurrentSchedule()
			meanIntraEventTime = intra_event_time
			
			// Sep 13, 2024 WRR: instantiate a probability distribution random number generator
			// with a mean of the given intra-event time.  
			distro = new ExponentialDistribution(intra_event_time)
		}
	}
	
	// Sep 13, 2024 WRR: start the process
	abstract def start()
	
	// Sep 13, 2024 WRR: Do the action of the process
	abstract def fire()

	// Sep 13, 2024 WRR: Stop the process
	abstract def stop() 

	// Sep 13, 2024 WRR: Return the time that this process is going to fire next.
	double getNextEventTime(){
		return nextAction.nextTime()
	}
}
