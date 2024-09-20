package processes

import repast.simphony.engine.schedule.ScheduleParameters

class SimpleProcess extends Process {
    boolean stop = false
    Closure<Void> action

    SimpleProcess(double intra_event_time, Closure<Void> action) {
        super(intra_event_time)
        this.action = action
    }


    void start() {
        this.stop = false
        double currTime = schedule.getTickCount()
        double elapse = distro.sample()
        ScheduleParameters params = ScheduleParameters.createOneTime(currTime + elapse)
        nextAction = schedule.schedule(params, this, "fire")
    }


    void fire() {
        action.call()
        if (!this.stop) {
            start()
        }
    }


    void stop() {
        this.stop = true
    }
}
