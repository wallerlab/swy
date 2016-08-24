package org.wallerlab.swy.service.modeltools;

import org.wallerlab.swy.model.SearchSpace
import spock.lang.*;

public class SearchSpaceDiscretiserTest extends Specification{
	private SearchSpaceDiscretiser searchSpaceDiscretiser
	
	private SearchSpace searchSpace
	private int[] numberOfPossibleValues
	
	private int[] discreteValues
	private double[] continuousValues
	
	private double[] startValues
	private double[] endValues
	private double[] stepSize
	
	@Unroll
	def "Determine numberOfPossibleValues" () {
		given:
		searchSpace = new SearchSpace()
		searchSpace.numberOfDimensions = 2
		searchSpace.startValues = startValues
		searchSpace.endValues = endValues
		searchSpace.stepSize = stepSize
		searchSpace.periodic = [false, true]
		searchSpaceDiscretiser = new SearchSpaceDiscretiser(searchSpace)
		
		expect:
		searchSpaceDiscretiser.getNumberOfPossibleValues() == numberOfPossibleValues
		
		cleanup:
		searchSpaceDiscretiser = null
		searchSpace = null
		
		where:
		startValues			|	endValues		|	stepSize		|	numberOfPossibleValues
		[0.0, 1.0]			|	[1.0, 5.0]		|	[0.1, 1.0]		|	[10, 4]
		[-4.9, 0.5]			|	[2.1, 0.6]		|	[1.0, 0.1]		|	[7, 1]
		
	}
	
	@Unroll
	def "Transform values #continuousValues to discrete space #discreteValues" () {
		given:
		searchSpace = new SearchSpace()
		searchSpace.numberOfDimensions = 3
		searchSpace.startValues = [0.0, 1.0, -1.0]
		searchSpace.endValues = [1.0, 5.0, 1.0]
		searchSpace.stepSize = [0.1, 1.0, 0.25]
		searchSpace.periodic = [false, false, true]
		searchSpaceDiscretiser = new SearchSpaceDiscretiser(searchSpace)
		
		expect:
		searchSpaceDiscretiser.toDiscreteSpace(0, continuousValues[0]) == discreteValues[0]
		searchSpaceDiscretiser.toDiscreteSpace(1, continuousValues[1]) == discreteValues[1]
		searchSpaceDiscretiser.toDiscreteSpace(2, continuousValues[2]) == discreteValues[2]
		
		cleanup:
		searchSpaceDiscretiser = null
		searchSpace = null
		
		where:
		continuousValues				|	discreteValues
		[0.0, 1.0, -1.0]				|	[0, 0, 0]
		[0.4, 3.0, 0.0]					|	[4, 2, 4]
		[1.0, 5.0, 1.0]					|	[9, 3, 0]
		[0.99, 4.99, 0.99]				|	[9, 3, 7]
		[0.49, 1.7, 0.73]				|	[4, 0, 6]
		[0.51, 2.1, 0.77]				|	[5, 1, 7]
	}
	
	@Unroll
	def "Transform values #discreteValues to continuous space #continuousValues" () {
		given:
		searchSpace = new SearchSpace()
		searchSpace.numberOfDimensions = 3
		searchSpace.startValues = [0.0, 1.0, -1.0]
		searchSpace.endValues = [1.0, 5.0, 1.0]
		searchSpace.stepSize = [0.1, 1.0, 0.25]
		searchSpace.periodic = [false, false, true]
		searchSpaceDiscretiser = new SearchSpaceDiscretiser(searchSpace)
		
		expect:
		searchSpaceDiscretiser.toContinuousSpace(0, discreteValues[0]).round(8) == continuousValues[0]
		searchSpaceDiscretiser.toContinuousSpace(1, discreteValues[1]).round(8) == continuousValues[1]
		searchSpaceDiscretiser.toContinuousSpace(2, discreteValues[2]).round(8) == continuousValues[2]
		/* Backtransformation must work as well in this case */
		searchSpaceDiscretiser.toDiscreteSpace(0, continuousValues[0]) == discreteValues[0]
		searchSpaceDiscretiser.toDiscreteSpace(1, continuousValues[1]) == discreteValues[1]
		searchSpaceDiscretiser.toDiscreteSpace(2, continuousValues[2]) == discreteValues[2]
		
		cleanup:
		searchSpaceDiscretiser = null
		searchSpace = null
		
		where:
		discreteValues					|	continuousValues
		[0, 0, 0]						|	[0.05, 1.5, -0.875]
		[4, 2, 4]						|	[0.45, 3.5, 0.125]
		[9, 3, 0]						|	[0.95, 4.5, -0.875]
		[9, 3, 7]						|	[0.95, 4.5, 0.875]
		[4, 0, 6]						|	[0.45, 1.5, 0.625]
		[5, 1, 7]						|	[0.55, 2.5, 0.875]
	}
	
}
