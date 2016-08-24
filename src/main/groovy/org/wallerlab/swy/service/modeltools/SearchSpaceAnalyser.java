package org.wallerlab.swy.service.modeltools;

import org.springframework.context.annotation.Profile;

@Profile({ "untested" })
public class SearchSpaceAnalyser {
	/**
	 * This method calculates the real distance of two points (in one dimension), taking into account
	 * the possibility of periodicity.
	 * The sign of the return value determines the direction (distance(A,B)=-distance(B,A)).
	 */
	public static double distanceInSearchSpace(double pointA, double pointB, boolean periodic, double searchSpaceSize) {
		double returnValue;
		if (periodic) {
			if (Math.abs(pointA - pointB) <= 0.5*searchSpaceSize) {
				returnValue= pointA - pointB;
			} else {
				returnValue= (Math.signum(pointB - pointA)*(searchSpaceSize-Math.abs(pointA - pointB)));
			}
		} else {
			returnValue= pointA - pointB;
		}
		return returnValue;
	}
}
