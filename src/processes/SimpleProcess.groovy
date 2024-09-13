/*This is a demonstration process that simply prints "Firing!" to the console
 * 
 * @Author: Willy Ray
 */

package processes

import processes.Process
import repast.simphony.engine.schedule.ScheduleParameters


class SimpleProcess extends Process {
	// Sep 13, 2024 WRR: stop member variable tracks the state of the object running/not running.
	Boolean stop = false

	
	public SimpleProcess(double intra_event_time) {
		// Sep 13, 2024 WRR: construct the Process parts of this object
		super(intra_event_time);
	}
	
	
	@Override
	public Object start() {
		// Sep 13, 2024 WRR: set the stop tracking member variable to false.  The process is running.
		this.stop = false
		// Sep 13, 2024 WRR: Start the process.  
		// get the current time from the scheduler
		double currTime = schedule.getTickCount()
		// Sep 13, 2024 WRR: get a sample from the distribution random number generator
		double elapse = distro.sample()
		// Sep 13, 2024 WRR: create a ScheduleParameters object to specify the time 
		// we want the event to fire, the current time, plus the time we want to elapse.
		ScheduleParameters params= ScheduleParameters.createOneTime(currTime+elapse)
		// Sep 13, 2024 WRR: Schedule the event.  this is this object.  "fire" is the 
		// method to call on the object when this event comes up in the scheduler.
		schedule.schedule(params, this, "fire")
		
	}

	@Override
	public Object fire() {
		// Sep 13, 2024 WRR: do some action.
		System.out.print("firing!")
		
		// Sep 13, 2024 WRR: If the event hasn't been stopped, reschedule it via the start() method.
		if (!this.stop) {
			start()
		}
	}

	// Sep 13, 2024 WRR: Stop the process.
	// NOTE:  In this implementation of the fire() method there may still be an event in the queue
	// in this implementation stop() prevents the process from getting restarted, it will not 
	// stop it from firing ONE LAST TIME if it's already been scheduled.
	@Override
	public Object stop() {
				this.stop = true
	}
}
