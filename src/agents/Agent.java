package agents;

import org.apache.commons.math3.distribution.ExponentialDistribution;

import repast.simphony.engine.schedule.ISchedulableAction;
import repast.simphony.engine.schedule.ISchedule;

abstract class Agent {
    private static int idCounter = 0;
    protected int id;
	

	Agent() {
	this.id = idCounter++;
		
	}


	@Override
	public int hashCode() {
	    // TODO Auto-generated method stub
	    return this.id;
		    }

	
}
