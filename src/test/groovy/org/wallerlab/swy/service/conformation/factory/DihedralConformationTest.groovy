package org.wallerlab.swy.service.conformation.factory;

import org.wallerlab.swy.model.MolecularSystem
import org.wallerlab.swy.model.Coordinates
import org.wallerlab.swy.service.modeltools.CoordinateFactory

import spock.lang.*

class DihedralConformationTest extends Specification{

	private DihedralConformation dihedralConformation
	private MolecularSystem molecularSystem
	private Coordinates coordinates
	private double[] solution

	/*
	 * The test methods also test the primitive conformation manipulation tools and the needed
	 * mathematical tools, because these features are not stubbed out.
	 */
	@Unroll
	def "Test of generating new conformations via twisting"() {
		
		given:
		dihedralConformation = new DihedralConformation()
		molecularSystem = new MolecularSystem()
		dihedralConformation.setMolecularSystem(molecularSystem)
		molecularSystem.numberOfMolecules = 1
		molecularSystem.numberOfAtomsInMolecule = [4]
		molecularSystem.turnableBonds = [[[1, 2]]]
		molecularSystem.atomsToTurn = [[[3]]]
		solution = new double[1]
		solution[0] = Math.PI/2.0
		molecularSystem.xCoordInitial = [[0.0, 1.0, 2.0, 2.0]]
		molecularSystem.yCoordInitial = [[0.0, 0.0, 0.0, 1.0]]
		molecularSystem.zCoordInitial = [[0.0, 0.0, 0.0, 0.0]]
		coordinates = dihedralConformation.newConformation(solution)
		for (int atom=0; atom < 4; atom++) {
			coordinates.xCoord[0][atom] = ((double)coordinates.xCoord[0][atom]).round(8)
			coordinates.yCoord[0][atom] = ((double)coordinates.yCoord[0][atom]).round(8)
			coordinates.zCoord[0][atom] = ((double)coordinates.zCoord[0][atom]).round(8)
		}
		
		expect:
		coordinates.xCoord == [[0.0, 1.0, 2.0, 2.0]]
		coordinates.yCoord == [[0.0, 0.0, 0.0, 0.0]]
		coordinates.zCoord == [[0.0, 0.0, 0.0, 1.0]]
		
		cleanup:
		dihedralConformation = null
		molecularSystem = null
		solution = null
		coordinates = null
		
	}
	
	/*
	 * Actually the class under test does not care about rings...
	 */
	@Unroll
	def "Test of generating new conformations via twisting phenole"() {
		
		given:
		dihedralConformation = new DihedralConformation()
		molecularSystem = new MolecularSystem()
		dihedralConformation.setMolecularSystem(molecularSystem)
		molecularSystem.numberOfMolecules = 1
		molecularSystem.numberOfAtomsInMolecule = [13];
		molecularSystem.turnableBonds = [[[0, 6]]]
		molecularSystem.atomsToTurn = [[[0, 1, 2, 3, 4, 5, 8, 9, 10, 11, 12]]]
		solution = new double[1]
		solution[0] = Math.PI/2.0
		molecularSystem.xCoordInitial = [[0.0, 0.5, 0.5, 0.0, -0.5, -0.5, 0.0, 0.5, 1.0, 1.0, 0.0, -1.0, -1.0]]
		molecularSystem.yCoordInitial = [[0.0, -0.5, -1.0, -1.5, -1.0, -0.5, 0.5, 1.0, 0.0, -1.5, -2.0, -1.5, 0.0]]
		molecularSystem.zCoordInitial = [[0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0]]
		coordinates = dihedralConformation.newConformation(solution)
		for (int atom=0; atom < 13; atom++) {
			coordinates.xCoord[0][atom] = ((double)coordinates.xCoord[0][atom]).round(8)
			coordinates.yCoord[0][atom] = ((double)coordinates.yCoord[0][atom]).round(8)
			coordinates.zCoord[0][atom] = ((double)coordinates.zCoord[0][atom]).round(8)
		}
		
		expect:
		coordinates.xCoord == [[0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.5, 0.0, 0.0, 0.0, 0.0, 0.0]]
		coordinates.yCoord == [[0.0, -0.5, -1.0, -1.5, -1.0, -0.5, 0.5, 1.0, 0.0, -1.5, -2.0, -1.5, 0.0]]
		coordinates.zCoord == [[0.0, -0.5, -0.5, 0.0, 0.5, 0.5, 0.0, 0.0, -1.0, -1.0, 0.0, 1.0, 1.0]]
		
		cleanup:
		dihedralConformation = null
		molecularSystem = null
		solution = null
		coordinates = null
	}
}
