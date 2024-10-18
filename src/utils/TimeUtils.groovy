package utils

import repast.simphony.engine.schedule.ISchedule

class TimeUtils {
	public static final double DAY = 1
	public static final double HOUR = DAY/24.0
	public static final double MINUTE = HOUR/60.0
	public static final double SECOND = MINUTE/60.0
	
	
	public static String tickToTime(double tick){
		def currTick = tick
		int day = currTick.toInteger()
		currTick-=day
		currTick = currTick/HOUR
		int hour = currTick.toInteger()
		currTick-=hour
		currTick = currTick*60
		int minute = currTick.toInteger()
		String m1 = "" + minute + ""
		if (minute < 10){
			m1 = "0" + m1
		}
		
		
		return "Day " + day + "," + hour + ":" + m1
	}
	
	public static ISchedule getSchedule() {
		return repast.simphony.engine.environment.RunEnvironment.getInstance().getCurrentSchedule();
	}
	
}
