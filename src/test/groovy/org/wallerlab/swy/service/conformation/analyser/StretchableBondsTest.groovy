package org.wallerlab.swy.service.conformation.analyser

import spock.lang.*

import org.wallerlab.swy.model.MolecularSystem

class StretchableBondsTest extends Specification{
	
	private  StretchableBonds stretchableBonds
	
	private MolecularSystem molecularSystem
	
	@Unroll
	def "Test to create a list of stretchable bonds for a simple system"() {
		
		given:
		molecularSystem = new MolecularSystem()
		stretchableBonds = new StretchableBonds()
		/*
		 *  Set up the fake system:
		 *  2
		 *   \
		 *    0--1--4--5
		 *   /
		 *  3
		 */ 
		molecularSystem.numberOfMolecules = 1;
		molecularSystem.numberOfAtomsInMolecule = [6];
		molecularSystem.bonds = [[[1, 2, 3], [0, 4], [0], [0], [1, 5], [4]]];
		molecularSystem.numberOfBondsInMolecule = [10];
		molecularSystem.rings = [];
		// Run the class/method
		stretchableBonds.createListOfStretchableBonds(molecularSystem);
		stretchableBonds.createAtomsToMoveList(molecularSystem);
		
		expect:
		molecularSystem.stretchableBonds == [[[0, 1], [0, 2], [0, 3], [1, 4], [4, 5]]]
		molecularSystem.atomsToMove == [[[1, 4, 5], [2], [3], [4, 5], [5]]]
		
		cleanup:
		molecularSystem = null
	}
}
