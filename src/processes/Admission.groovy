package processes

class Admission extends Process{

    public Admission(double intra_event_time) {
	super(intra_event_time);
	// TODO Auto-generated constructor stub
	// with whatever references are neceassry 
    }

    @Override
    public void start() {
	// TODO Auto-generated method stub
	// Oct 4, 2024 WRR: Schedule the next admission
	
    }

    @Override
    public void fire() {
	// TODO Auto-generated method stub
	// Oct 4, 2024 WRR: call a method on the facility or the region to set up a new patient 
	// and reschedule
	
    }

    @Override
    public void stop() {
	// TODO Auto-generated method stub
	
    }
}
