package org.wallerlab.swy.service.energy

import org.wallerlab.swy.model.SearchSpace
import spock.lang.*

class MathematicalFunctionTest extends Specification{

	private MathematicalFunction mathematicalFunction
	
	private SearchSpace searchSpace
	
	private double[] solution
	private double energy
	
	def "Test return values of the mathematical function." () {
		
		given:
		mathematicalFunction = new MathematicalFunction()
		searchSpace = new SearchSpace()
		searchSpace.startValues = [0.0, 0.0, 0.0]
		searchSpace.endValues = [2*Math.PI, 2*Math.PI, 2*Math.PI]
		mathematicalFunction.setSearchSpace(searchSpace)
		
		expect:
		mathematicalFunction.getEnergy(solution).round(4) == energy
		
		where:
		solution					|	energy
		[0.0, 0.0, 0.0]					|	-3.0
		[2*Math.PI, 2*Math.PI, 2*Math.PI]		|	-3.0
		[Math.PI, 0.0, 2*Math.PI]			|	-2.95
		[0.0, Math.PI, Math.PI]				|	-2.90
		[Math.PI, Math.PI, Math.PI]			|	-2.85
		[0.5*Math.PI, 0.5*Math.PI, 0.5*Math.PI]    	|	0.1125
		
	}
}
