package org.wallerlab.swy.service.conformation.analyser

import spock.lang.*

import org.wallerlab.swy.model.MolecularSystem

// actually this class tests both, the bondRotation search space AND the bendableAnglesAnalyser
class BendableAnglesTest extends Specification{

	private MolecularSystem molecularSystem
	
	public BendableAngles bendableAngles
	
	@Unroll
	def "Simple system test to create list of bendableAngles"() {
		
		given:
		bendableAngles = new BendableAngles()
		/*
		 *   Set up the fake system:
		 *  2
		 *   \
		 *    0--1--4--5
		 *   /
		 *  3
		 */
		
		molecularSystem = new MolecularSystem()
		molecularSystem.numberOfMolecules = 1;
		molecularSystem.numberOfAtomsInMolecule = [6];
		molecularSystem.bonds = [[[1, 2, 3], [0, 4], [0], [0], [1, 5], [4]]];
		molecularSystem.numberOfBondsInMolecule = [10]
		// Run the class/method
		bendableAngles.createListOfBendableAngles(molecularSystem);
		bendableAngles.createAtomsToBendList(molecularSystem);
		
		expect:
		molecularSystem.bendableAngles == [[[2, 0, 1], [0, 1, 4], [1, 4, 5], [2, 0, 3]]]
		molecularSystem.atomsToBend == [[[1, 4, 5], [4, 5], [5], [3]]]
		
		cleanup:
		molecularSystem = null
	}
	
	@Unroll
	def "Two molecule system test to create list of bendableAngles"() {
		
	given:
	bendableAngles = new BendableAngles()
	// Set up the fake system (e.g. two Methanols)
	molecularSystem = new MolecularSystem()
	molecularSystem.numberOfMolecules = 2
	molecularSystem.numberOfAtomsInMolecule = [6, 6];
	molecularSystem.bonds = [ [ [1, 2, 3, 4], [0, 5], [0], [0], [0], [1] ], [ [1, 2, 3, 4], [0, 5], [0], [0], [0], [1] ] ];
	molecularSystem.numberOfBondsInMolecule = [5, 5];
	// Run the class/method
	bendableAngles.createListOfBendableAngles(molecularSystem);
	bendableAngles.createAtomsToBendList(molecularSystem);

	expect:
	molecularSystem.bendableAngles == [[[2, 0, 1],[0, 1, 5],[2, 0, 3],[2, 0, 4]],[[2, 0, 1],[0, 1, 5],[2, 0, 3],[2, 0, 4]] ]
	molecularSystem.atomsToBend == [[[1, 5], [5], [3], [4]], [[1, 5], [5], [3], [4]]]
	
	cleanup:
	molecularSystem = null
	}
	
	@Unroll
	def "Ring system test to create list of bendableAngles"() {
		
	given:
	bendableAngles = new BendableAngles()
	// Set up the fake system (e.g. Phenol)
	molecularSystem = new MolecularSystem()
	molecularSystem.numberOfMolecules = 1
	molecularSystem.numberOfAtomsInMolecule = [13];
	molecularSystem.bonds = [ [ [1, 5, 6], [0, 2, 8], [1, 3, 9], [2, 4, 10], [3, 5, 11], [0, 4, 12], [0, 7], [6], [1], [2], [3], [4], [5] ] ];
	molecularSystem.numberOfBondsInMolecule = [25]
	// Run the class/method
	Rings.createRingList(molecularSystem)
	bendableAngles.createListOfBendableAngles(molecularSystem);
	bendableAngles.createAtomsToBendList(molecularSystem);
	
	expect:
	molecularSystem.bendableAngles == [[[7, 6, 0], [4, 5, 12], [3, 4, 11], [2, 3, 10], [1, 2, 9], [0, 1, 8]]]
	molecularSystem.atomsToBend == [ [[0, 1, 2, 3, 4, 5, 8, 9, 10, 11, 12], [12], [11], [10], [9], [8]] ]
	
	cleanup:
	molecularSystem = null
	}

}
