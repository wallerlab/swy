package org.wallerlab.swy.service.conformation.analyser;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.wallerlab.swy.model.Coordinates
import org.wallerlab.swy.model.MolecularSystem

public class GeometryTest {

	private MolecularSystem molecularSystem
	
	private Coordinates coordinates
	
	@Before
	public void setUp() {
		double[][] emptyCoord = new double[1][1]
		emptyCoord[0][0]=0.0
		molecularSystem = new MolecularSystem()
		molecularSystem.xCoordInitial = emptyCoord
		molecularSystem.yCoordInitial = emptyCoord
		molecularSystem.zCoordInitial = emptyCoord
		coordinates = new Coordinates()
	}

	@After
	public void tearDown() {
		coordinates = null
		molecularSystem = null
	}

	/*
	 * The geometry class is not stubbed and therefore uses the PeriodicTable (which should not cause trouble because
	 * it should be extreeeemely static).
	 */
	@Test
	public void centerOfMassTest() {
		molecularSystem = new MolecularSystem()
		molecularSystem.numberOfAtomsInMolecule = [2]
		molecularSystem.atomLabel = [["C", "C"]]
		coordinates.xCoord = [[-1.0, 1.0]]
		coordinates.yCoord = [[0.0, 0.0]]
		coordinates.zCoord = [[0.0, 0.0]]
		double[] result = Geometry.centerOfMass(molecularSystem, coordinates, 0)
		for (int coordinate=0; coordinate < 3; coordinate++) {
			result[coordinate] = result[coordinate].round(8)
		}
		assert (result == [0.0, 0.0, 0.0])
	}
	
	@Test
	public void centerOfMassTestTwo() {
		molecularSystem = new MolecularSystem()
		molecularSystem.numberOfAtomsInMolecule = [5]
		molecularSystem.atomLabel = [["C", "F", "C", "C", "C"]]
		coordinates.xCoord = [[0.0, 1.0, 2.0, 1.0, 1.0]]
		coordinates.yCoord = [[1.0, 1.0, 1.0, 2.0, 0.0]]
		coordinates.zCoord = [[-1.0, -1.0, -1.0, -1.0, -1.0]]
		double[] result = Geometry.centerOfMass(molecularSystem, coordinates, 0)
		for (int coordinate=0; coordinate < 3; coordinate++) {
			result[coordinate] = result[coordinate].round(8)
		}
		assert (result == [1.0, 1.0, -1.0])
	}
	
	@Test
	public void centerOfMassTestTwoMolecules() {
		molecularSystem = new MolecularSystem()
		molecularSystem.numberOfAtomsInMolecule = [1, 5]
		molecularSystem.atomLabel = [["C"], ["H", "H", "C", "H", "H"]]
		coordinates.xCoord = [[-5.0], [0.0, 1.0, 2.0, 1.0, 1.0]]
		coordinates.yCoord = [[-5.0], [1.0, 1.0, 1.0, 2.0, 0.0]]
		coordinates.zCoord = [[-5.0], [-1.0, -1.0, -1.0, -1.0, -1.0]]
		double[] result = Geometry.centerOfMass(molecularSystem, coordinates, 1)
		for (int coordinate=0; coordinate < 3; coordinate++) {
			result[coordinate] = result[coordinate].round(4)
		}
		assert (result == [1.6858, 1.0, -1.0])
	}

}
