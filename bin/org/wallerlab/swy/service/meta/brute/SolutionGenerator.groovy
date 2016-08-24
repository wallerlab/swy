//package org.wallerlab.swy.service.meta.brute
//
//import org.springframework.context.annotation.Profile;
//import org.springframework.context.annotation.Lazy
//
//import org.wallerlab.swy.model.SearchSpace
//
//import org.springframework.stereotype.Service
//import org.springframework.context.annotation.Lazy
//
//
///**
// * Automatically generates solutions for Brute force.
// * 
// */
//@Lazy
//@Service
//@Profile(["unstable"])
//class SolutionGenerator {
//
//	private static boolean moreSolutions = true
//	private static int thisDimension
////	private static Solution solution
//
//	/**
//	 *  Initialization	
//	 */
//	public static void init() {
////		solution = new Solution()
//		def solution
//		for (int dimension in 0..SearchSpace.numberOfDimensions-1) {
//			solution.components[dimension] = SearchSpace.startValues[dimension]
//		}
//		thisDimension = SearchSpace.numberOfDimensions-1
//	}
//
//	/**
//	*   If condition holds -> new solution found
//	*   if not the last decision point has changed reinitialize all later decision points
//	*	 -> i. e.: after [1, c, iii] should follow [2, a, i] and not [2, c, iii]
//	*	This means for the checked decision point no new solution could be generated
//	*	thisDimension -1 means finished
//	*/
//	private static void checkCondition() {
//		if (solution.components[thisDimension] <= SearchSpace.endValues[thisDimension]) {		
//			solution.components[thisDimension] += SearchSpace.stepSize[thisDimension]
//			while (thisDimension < SearchSpace.numberOfDimensions-1) {
//				thisDimension++
//				solution.components[thisDimension] = SearchSpace.startValues[thisDimension]
//			}
//		}else{
//			thisDimension--
//			if(thisDimension > -1) {
//				checkCondition()
//			}else{
//				moreSolutions = false
//			}
//		}
//		return
//	}
//
//	/**
//	 *  Check for new solutions
//	 */
//	static void getNextSolution() {
//		checkCondition()
//	}
//	
//	/**
//	*  Boolean for new solutions
//	*/
//	static boolean thereAreMoreSolutions() {
//		 moreSolutions
//	}
//}
