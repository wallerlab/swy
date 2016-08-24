package org.wallerlab.swy.service.searchspace.factory.supramolecular;

import org.wallerlab.swy.model.MolecularSystem
import org.wallerlab.swy.model.SearchSpace
import spock.lang.*

class PositionAndAnglesTest extends Specification{
	
	private MolecularSystem molecularSystem
	private SearchSpace searchSpace
	
	@Unroll
	def "Test to create a supramolecular searchSpace" (){
		
		given:
		molecularSystem = new MolecularSystem()
		molecularSystem.numberOfMolecules = 3
		PositionAndAngles positionAndAngles = new PositionAndAngles()
		positionAndAngles.setGranularity(0.1)
		positionAndAngles.setMolecularSystem(molecularSystem)
		
		searchSpace = positionAndAngles.generateSearchSpace()
		
		expect:
		searchSpace.numberOfDimensions == 12
		searchSpace.startValues.size() == 12
		searchSpace.startValues[6..11] == [0.0, 0.0, 0.0, 0.0, 0.0, 0.0]
		searchSpace.endValues.size() == 12
		searchSpace.endValues[6..11] == [2*Math.PI, 2*Math.PI, 2*Math.PI, 2*Math.PI, 2*Math.PI, 2*Math.PI]
		searchSpace.periodic == [false, false, false, false, false, false, true, true, true, true, true, true]
		searchSpace.stepSize.size() == 12
		searchSpace.stepSize[6..11] == [0.2*Math.PI, 0.2*Math.PI, 0.2*Math.PI, 0.2*Math.PI, 0.2*Math.PI, 0.2*Math.PI]
		
		cleanup:
		molecularSystem = null
		searchSpace = null
	}
	
}
