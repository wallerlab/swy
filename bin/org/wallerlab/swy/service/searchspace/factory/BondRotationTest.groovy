package org.wallerlab.swy.service.searchspace.factory;

import org.wallerlab.swy.model.MolecularSystem
import org.wallerlab.swy.model.SearchSpace;

import spock.lang.*

/*
 * Further tests on the BondRotation class are also done in service.conformation.analyser.TurnableBondsTest
 * This test actually tests whether the searchSpace is created correctly while the TurnableBondsTest class
 * tests the functionality of the used static methods to get the information needed for the setup of the
 * search space.
 */
class BondRotationTest extends Specification{
	
	private MolecularSystem molecularSystem
	private SearchSpace searchSpace
	
	@Unroll
	def "Test to create a dihedral based search space for a simple system" () {
		
		given:
		/*
		 *       1 - 3 - 6
		 *      /
		 * 4 - 0
		 *      \
		 *       2 - 5
		 */
		molecularSystem = new MolecularSystem()
		molecularSystem.numberOfMolecules = 1
		molecularSystem.numberOfAtomsInMolecule = [6]
		molecularSystem.bonds = [[[1, 2, 4], [0, 3], [0, 5], [1, 6], [0], [2], [3]]]

		BondRotation bondRotation = new BondRotation()
		bondRotation.setGranularity(0.1)
		bondRotation.setMolecularSystem(molecularSystem)
		
		searchSpace = bondRotation.generateSearchSpace()

		expect:
		searchSpace.numberOfDimensions == 3
		searchSpace.startValues == [0.0, 0.0, 0.0]
		searchSpace.endValues == [2*Math.PI, 2*Math.PI, 2*Math.PI]
		searchSpace.periodic == [true, true, true]
		searchSpace.stepSize == [0.2*Math.PI, 0.2*Math.PI, 0.2*Math.PI]
		
		cleanup:
		molecularSystem = null
		searchSpace = null
	}

}
