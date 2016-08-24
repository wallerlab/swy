package org.wallerlab.swy.service.conformation.analyser;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;

import org.wallerlab.swy.model.MolecularSystem

import org.wallerlab.swy.service.conformation.analyser.Rings

public class RingTest {

	private MolecularSystem molecularSystem
	
	@Before
	public void setUp() {
		molecularSystem = new MolecularSystem()
		molecularSystem.numberOfMolecules = 1
		
	}
	
	@After
	public void tearDown() {
		molecularSystem = null
	}
	
	@Test
	public void testCreateRingList() {
		// Phenole
		molecularSystem.numberOfAtomsInMolecule = [13]
		molecularSystem.bonds = [ [ [1, 5], [0, 2, 8], [1, 3, 9], [2, 4, 10], [3, 5, 11], [0, 4, 12], [0, 7], [6], [1], [2], [3], [4], [5] ] ];
		
		// run the method under test
		Rings.createRingList(molecularSystem)
		
		// Check results
		assert (molecularSystem.rings == [ [ [0, 1, 2, 3, 4, 5] ] ])
	}
	
	@Test
	public void testCreateRingListMultiRingSystem() {
		// "Phenole with bridge" 
		molecularSystem.numberOfAtomsInMolecule = [14]
		molecularSystem.bonds = [ [ [1, 5], [0, 2, 8, 13], [1, 3, 9], [2, 4, 10], [3, 5, 11], [0, 4, 12, 13], [0, 7], [6], [1], [2], [3], [4], [5], [1, 5] ] ];
		
		// run the method under test
		Rings.createRingList(molecularSystem)
		
		// Check results
		assert ( molecularSystem.rings[0].size() == 3)
		assert ( molecularSystem.rings[0].contains([0, 1, 2, 3, 4, 5]) )
		assert ( molecularSystem.rings[0].contains([0, 1, 5, 13]) )
		assert ( molecularSystem.rings[0].contains([1, 2, 3, 4, 5, 13]) )
	}
	
	@Test
	public void testIsNotPartOfSameRing() {
		molecularSystem.rings = [ [ [2, 3, 4, 6], [5] ] ]
		
		assert (Rings.isNotPartOfSameRing(molecularSystem, 0, 0, 1))
		assert (Rings.isNotPartOfSameRing(molecularSystem, 0, 1, 2))
		assert (Rings.isNotPartOfSameRing(molecularSystem, 0, 2, 5))
		assert (!Rings.isNotPartOfSameRing(molecularSystem, 0, 2, 3))
		assert (!Rings.isNotPartOfSameRing(molecularSystem, 0, 2, 4))
		assert (!Rings.isNotPartOfSameRing(molecularSystem, 0, 4, 6))
	}

}
