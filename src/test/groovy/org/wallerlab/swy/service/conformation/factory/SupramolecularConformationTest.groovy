package org.wallerlab.swy.service.conformation.factory;

import static org.junit.Assert.*;

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.wallerlab.swy.model.Coordinates
import org.wallerlab.swy.model.MolecularSystem

/*
 * This test class tests the complete functionality of the SupramolecularConformation class,
 * including all functionality IT needs. So no method calls are stubbed away.
 */
class SupramolecularConformationTest {

	SupramolecularConformation supramolecularConformation

	private MolecularSystem molecularSystem
	private Coordinates coordinates
	private double[] solution
		
	@Before
	void setUp() {
		molecularSystem = new MolecularSystem()
		molecularSystem.numberOfMolecules = 2
		molecularSystem.numberOfAtomsInMolecule = [0, 4]
		molecularSystem.xCoordInitial = [[], [0.0, 1.0, 2.0, 2.0]]
		molecularSystem.yCoordInitial = [[], [0.0, 0.0, 0.0, 1.0]]
		molecularSystem.zCoordInitial = [[], [0.0, 0.0, 0.0, 0.0]]
		molecularSystem.atomLabel = [[],["C", "C", "C", "C"]]
		supramolecularConformation = new SupramolecularConformation()
		supramolecularConformation.setMolecularSystem(molecularSystem)
		
	}
	
	
	@After
	void tearDown() {
		molecularSystem = null
		supramolecularConformation.finalize()
	
	}
	
	@Test
	void zeroTranslation(){
		supramolecularConformation.adjustPositions = true
		supramolecularConformation.adjustAngles = false
		solution = new double[3]
		solution[0] = 0.0
		solution[1] = 0.0
		solution[2] = 0.0
		coordinates = supramolecularConformation.newConformation(solution)
		assert (coordinates.xCoord == [[], [0.0, 1.0, 2.0, 2.0]])
		assert (coordinates.yCoord == [[], [0.0, 0.0, 0.0, 1.0]])
		assert (coordinates.zCoord == [[], [0.0, 0.0, 0.0, 0.0]])
		}
	
	@Test
	void zeroRotation(){
		supramolecularConformation.adjustPositions = false
		supramolecularConformation.adjustAngles = true
		solution = new double[3]
		solution[0] = 0.0
		solution[1] = 0.0
		solution[2] = 0.0
		coordinates = supramolecularConformation.newConformation(solution)
		assert (coordinates.xCoord == [[], [0.0, 1.0, 2.0, 2.0]])
		assert (coordinates.yCoord == [[], [0.0, 0.0, 0.0, 1.0]])
		assert (coordinates.zCoord == [[], [0.0, 0.0, 0.0, 0.0]])
	}
	
	@Test
	void transAndZeroRot(){
		supramolecularConformation.adjustPositions = true
		supramolecularConformation.adjustAngles = true
		solution = new double[6]
		solution[0] = 1.0
		solution[1] = 1.0
		solution[2] = 1.0
		solution[3] = 0.0
		solution[4] = 0.0
		solution[5] = 0.0
		coordinates = supramolecularConformation.newConformation(solution)
		assert (coordinates.xCoord == [[], [1.0, 2.0, 3.0, 3.0]])
		assert (coordinates.yCoord == [[], [1.0, 1.0, 1.0, 2.0]])
		assert (coordinates.zCoord == [[], [1.0, 1.0, 1.0, 1.0]])
	}
	
	@Test
	void transTo001AndRot(){
		supramolecularConformation.adjustPositions = true
		supramolecularConformation.adjustAngles = true
		solution = new double[6]
		solution[0] = -1.25
		solution[1] = -0.25
		solution[2] = 1.0
		solution[3] = 0.0
		solution[4] = Math.PI/2
		solution[5] = 0.0
		coordinates = supramolecularConformation.newConformation(solution)
		for (int atom=0; atom < 4; atom++) {
			coordinates.xCoord[1][atom] = ((double)coordinates.xCoord[1][atom]).round(8)
			coordinates.yCoord[1][atom] = ((double)coordinates.yCoord[1][atom]).round(8)
			coordinates.zCoord[1][atom] = ((double)coordinates.zCoord[1][atom]).round(8)
		}
		assert (coordinates.xCoord == [[], [0.0, 0.0, 0.0, 0.0]])
		assert (coordinates.yCoord == [[], [-0.25, -0.25, -0.25, 0.75]])
		assert (coordinates.zCoord == [[], [2.25, 1.25, 0.25, 0.25]])
	}
}
