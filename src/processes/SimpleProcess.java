package processes;

import repast.simphony.engine.schedule.ScheduleParameters;

class SimpleProcess extends Process {
    private boolean stop = false;

    SimpleProcess(double intra_event_time) {
        super(intra_event_time);
    }


    public void start() {
        this.stop = false;
        double currTime = schedule.getTickCount();
        double elapse = distro.sample();
        ScheduleParameters params = ScheduleParameters.createOneTime(currTime + elapse);
        nextAction = schedule.schedule(params, this, "fire");
    }


    public void fire() {
        if (!this.stop) {
            start();
        }
    }


    public void stop() {
        this.stop = true;
    }
}
