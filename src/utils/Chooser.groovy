package utils

import repast.simphony.random.RandomHelper

class Chooser {
	
	
   
	public static choose(List options, List density) {
		def testValue = RandomHelper.getUniform().nextDoubleFromTo(0.0, 1.0)
		
		def cumulative = 0
		def step = 0
		def choiceStep = -1
		for (i in density) {
			cumulative += i
			if (testValue<=cumulative) {
				choiceStep = step
				break
			}
			step += 1
		}
		//assert choiceStep != -1
		
		return options[choiceStep]
		
		
	}
	
	public static chooseOne(List options) {
		def index = RandomHelper.getUniform().nextIntFromTo(0, options.size()-1)
		def selection = options[index]
		return selection
	}
	
	public static boolean coinFlip() {
		return (boolean)chooseOne([true, false])
	}
	
	public static boolean randomTrue(Double test) {
		if (test < 0 || test > 1) {
			throw new Exception("value " + test + " out of bounds [0,1] ")
		}
		assert test >= 0.0
		assert test <= 1.0
		return choose([true, false], [test, 1.0-test])
	}
	
}
