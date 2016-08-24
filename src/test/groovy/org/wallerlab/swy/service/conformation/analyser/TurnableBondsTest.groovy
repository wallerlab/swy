package org.wallerlab.swy.service.conformation.analyser

import static org.junit.Assert.*

import org.junit.Test
import org.junit.Before
import org.junit.After

import org.wallerlab.swy.model.SearchSpace
import org.wallerlab.swy.model.MolecularSystem
import org.wallerlab.swy.service.searchspace.factory.BondRotation

// actually this class tests both, the bondRotation search space AND the turnableBondsAnalyser

//class TurnableBondsTest extends GroovyTestCase {
class TurnableBondsTest{
	
	private BondRotation bondRotation
	
	private MolecularSystem molecularSystem
	private SearchSpace searchSpace
	
	@Before
	public void setUp() {
		molecularSystem = new MolecularSystem()
		bondRotation = new BondRotation()
		bondRotation.setMolecularSystem(molecularSystem)
	}
	
	@After
	public void tearDown() {
		molecularSystem = null
		bondRotation = null
		searchSpace = null
	}
	
	@Test
	public void testCreateListOfTurnableBonds() {
		
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
		molecularSystem.numberOfBondsInMolecule = [10]
		// Run the class/method
		searchSpace = bondRotation.generateSearchSpace();
		
		// Check the results
		assert (molecularSystem.turnableBonds == [[[0, 1], [1, 4]]])
		assert (searchSpace.numberOfDimensions == 2)
		assert (molecularSystem.atomsToTurn == [[[2, 3], [0, 2, 3]]])
				
	}
	
	@Test
	public void testCreateListOfTurnableBondsTwoMolecules() {
		
		// Set up the fake system (e.g. two Methanols)
		molecularSystem.numberOfMolecules = 2
		molecularSystem.numberOfAtomsInMolecule = [6, 6];
		molecularSystem.bonds = [ [ [1, 2, 3, 4], [0, 5], [0], [0], [0], [1] ], [ [1, 2, 3, 4], [0, 5], [0], [0], [0], [1] ] ];
		molecularSystem.numberOfBondsInMolecule = [5, 5];
		// Run the class/method
		searchSpace = bondRotation.generateSearchSpace();
		
		// Check the results
		assert (molecularSystem.turnableBonds == [ [[0, 1]], [[0, 1]] ])
		assert (searchSpace.numberOfDimensions == 2)
		assert (molecularSystem.atomsToTurn[0] == [[2, 3, 4]])
		assert (molecularSystem.atomsToTurn[1] == [[2, 3, 4]])
				
	}
	
	@Test
	public void testCreateListOfTurnableBondsRings() {
		
		// Set up the fake system (e.g. Phenol)
		molecularSystem.numberOfMolecules = 1
		molecularSystem.numberOfAtomsInMolecule = [13];
		molecularSystem.bonds = [ [ [1, 5, 6], [0, 2, 8], [1, 3, 9], [2, 4, 10], [3, 5, 11], [0, 4, 12], [0, 7], [6], [1], [2], [3], [4], [5] ] ];
		molecularSystem.numberOfBondsInMolecule = [25]
		// Run the class/method
		searchSpace = bondRotation.generateSearchSpace();
		
		// Check the results
		assert (molecularSystem.turnableBonds == [[[0, 6]]])
		assert (searchSpace.numberOfDimensions == 1)
		assert (molecularSystem.atomsToTurn == [[[1, 2, 3, 4, 5, 8, 9, 10, 11, 12]]])
	}

}
