package org.wallerlab.swy.service.searchspace.factory;

import org.springframework.context.annotation.Profile;

/**
 * This class ensures that the searchSpace can be parted into
 * equally wide steps. (Not intrinsically given if 1/granularity
 * is not approximately an integer.)
 * 
 * @author t_dres03
 */
@Profile({ "untested" })
public class StepSizeCalculator {

	private StepSizeCalculator(){
	}
	
	/**
	 * @param startValue Where the searchSpace starts (in a certain dimension)
	 * @param endValue Where the searchSpace ends (in a certain dimension)
	 * @param granularity A percentage number determining how fine the searchSpace
	 * is grained
	 * @return a stepSize which ensures equally wide steps
	 */
	public static double calculateStepSize(double startValue, double endValue, double granularity) {
		int numberOfSteps = (int)Math.ceil(1/granularity); 
		return (endValue-startValue)/numberOfSteps;
	}
}
