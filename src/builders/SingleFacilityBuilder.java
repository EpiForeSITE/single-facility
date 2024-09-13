package builders;

import agents.Agent;
import processes.SimpleProcess;
import repast.simphony.context.Context;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.ScheduleParameters;



public class SingleFacilityBuilder implements ContextBuilder<Object> {
	
	ISchedule schedule ;

	// Sep 13, 2024 WRR: This is the build method that will pull all your model
	// components together.
	
	@Override
	public Context<Object> build(Context<Object> context) {

		// Sep 13, 2024 WRR: This is a reference to the main scheduler for the
		// whole simulation.
		schedule = repast.simphony.engine.environment.RunEnvironment.getInstance().getCurrentSchedule();

		// Sep 13, 2024 WRR: This is an example of a poission-process kind of event.
		// we create a new SimpleProcess.
	

		// Sep 13, 2024 WRR: if you look at processes.Process and
		// processes.SimpleProcess, you should
		// be able to see that the constructor argument specifies the mean time between
		// occurrences of this event
		SimpleProcess sp = new SimpleProcess(1.0);
		// Sep 13, 2024 WRR: Then we call the start method on the process.
		sp.start();

		// Sep 13, 2024 WRR: This is an example of a one-time and one-time-only event.
		// we need to stop the process, or it will continue repeating forever.
		// All of the ScheduleParameters.create...() method are "factory" methods.
		// @see: https://repast.github.io/docs/api/repast_simphony/index.html
		// @see:
		// https://repast.github.io/docs/api/repast_simphony/repast/simphony/engine/schedule/ScheduleParameters.html
		ScheduleParameters params = ScheduleParameters.createOneTime(10);

		// Sep 13, 2024 WRR: Schedule a call to the stop() method on the SimpleProcess
		// instance sp.
		// params is the object that indicates when the action will run. sp is the
		// SimpleProcess object
		// "stop" is the name of the function on sp to call. This is a classic example
		// of introspection.
		// At runtime, the JVM will look for a method named "stop" on the object and if
		// it's there it will
		// call it. The compiler doesn't have anything to say about this, so it will
		// compile, even if the
		// method specified doesn't exist.
		schedule.schedule(params, sp, "stop");

		return context;
	}

}
