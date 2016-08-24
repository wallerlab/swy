package org.wallerlab.swy.service.searchspace.factory.supramolecular;

import org.wallerlab.swy.model.MolecularSystem
import org.wallerlab.swy.model.SearchSpace
import spock.lang.*

class PositionTest extends Specification{
	
	private MolecularSystem molecularSystem
	private SearchSpace searchSpace
	
	@Unroll
	def "Test to create an Position-based supramolecular searchSpace" (){
		
		given:
		molecularSystem = new MolecularSystem()
		molecularSystem.numberOfMolecules = 3
		Position position = new Position()
		position.setGranularity(0.1)
		position.setMolecularSystem(molecularSystem)
		
		searchSpace = position.generateSearchSpace()
		
		expect:
		searchSpace.numberOfDimensions == 6
		searchSpace.startValues.size() == 6
		searchSpace.endValues.size() == 6
		searchSpace.periodic == [false, false, false, false, false, false]
		searchSpace.stepSize.size() == 6
	
		cleanup:
		molecularSystem = null
		searchSpace = null
			
	}
	
}
