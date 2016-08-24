package org.wallerlab.swy.service.searchspace.factory.supramolecular;

import org.wallerlab.swy.model.MolecularSystem
import org.wallerlab.swy.model.SearchSpace
import spock.lang.*

class AnglesTest extends Specification{
	
	private MolecularSystem molecularSystem
	private SearchSpace searchSpace
	
	@Unroll
	def "Test to create an Angle-based supramolecular searchSpace" (){
		
		given:
		molecularSystem = new MolecularSystem()
		molecularSystem.numberOfMolecules = 3
		Angles angles = new Angles()
		angles.setGranularity(0.1)
		angles.setMolecularSystem(molecularSystem)
		
		SearchSpace searchSpace = angles.generateSearchSpace()
		
		expect:
		searchSpace.numberOfDimensions == 6
		searchSpace.startValues == [0.0, 0.0, 0.0, 0.0, 0.0, 0.0]
		searchSpace.endValues == [2*Math.PI, 2*Math.PI, 2*Math.PI, 2*Math.PI, 2*Math.PI, 2*Math.PI]
		searchSpace.periodic == [true, true, true, true, true, true]
		searchSpace.stepSize == [0.2*Math.PI, 0.2*Math.PI, 0.2*Math.PI, 0.2*Math.PI, 0.2*Math.PI, 0.2*Math.PI]
		
		cleanup:
		molecularSystem = null
		searchSpace = null
	}
	
}
