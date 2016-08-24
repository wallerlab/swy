package org.wallerlab.swy.service.modeltools;

import org.wallerlab.swy.model.SearchSpace;

import org.springframework.context.annotation.Profile;

/**
 * Whenever a discrete version of the {@link org.wallerlab.swy.model.SearchSpace}
 * is needed, this class can provide proper conversions
 * of values both ways. The discrete numbers are just
 * an index (will never be negative).
 * 
 * @author t_dres03
 */
@Profile({ "untested" })
public class SearchSpaceDiscretiser{
	
	private SearchSpace searchSpace;
	
	private int[] numberOfPossibleValues;
	
	/**
	 * This class needs a searchSpace to work with...obvious, right?
	 * 
	 * @param searchSpace
	 */
	public SearchSpaceDiscretiser(SearchSpace searchSpace) {
		this.searchSpace = searchSpace;
		numberOfPossibleValues = new int[searchSpace.numberOfDimensions];
		for(int dimension=0; dimension < searchSpace.numberOfDimensions; dimension++) {
			assert ( ( searchSpace.endValues[dimension]- searchSpace.startValues[dimension]+0.00000005)
					% searchSpace.stepSize[dimension] <= 0.0000001): "It is not possible to divide the searchSpace" +
							"into equally wide steps of stepSize.";
			numberOfPossibleValues[dimension] = 
					(int)Math.round( ( searchSpace.endValues[dimension]- searchSpace.startValues[dimension])
						/ searchSpace.stepSize[dimension]);
		}
	}
	
	/**
	 * @param dimension
	 * @param discreteValue a field in the discrete search space
	 * @return the corresponding value in continuous space to discreteValue
	 */
	public double toContinuousSpace(int dimension, int discreteValue){
		return (searchSpace.startValues[dimension]
		 + (0.5+discreteValue)*searchSpace.stepSize[dimension]);
	}
	
	/**
	 * @param dimension
	 * @param continuousValue a value in the continuous search space
	 * @return the corresponding field in discrete space to continuousValue
	 */
	public int toDiscreteSpace(int dimension, double continuousValue){
		int returnValue = (int) Math.floor( (continuousValue - searchSpace.startValues[dimension])
						  / searchSpace.stepSize[dimension] );
		if (returnValue == numberOfPossibleValues[dimension]) {
			if (searchSpace.periodic[dimension]) {
				return 0;
			} else {
				return numberOfPossibleValues[dimension]-1;
			}
		} else {
			assert(0 <= returnValue && returnValue < numberOfPossibleValues[dimension]);
			return returnValue;
		}
	}

	public int[] getNumberOfPossibleValues() {
		return numberOfPossibleValues;
	}
}
