package org.wallerlab.swy.service.searchspace.factory;

import spock.lang.*;

public class StepSizeCalculatorTest extends Specification{
	
	double startValue
	double endValue
	double granularity
	double stepSize
	
	
	@Unroll
	def "Test the functionality of the stepSizeCalculator" () {
		
		expect:
		StepSizeCalculator.calculateStepSize(startValue, endValue, granularity).round(8) == stepSize
		
		where:
		startValue	|	endValue	|	granularity	|	stepSize
		0.0			|	0.9000000001|	0.1			|	0.09
		0.0			|	0.99999999	|	0.1			|	0.1
		-5.0		|	5.0			|	0.101		|	1.0
		-5.0		|	6.0			|	0.099		|	1.0
		0.1			|	0.2			|	0.75		|	0.05
	}
}
