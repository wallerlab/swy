package org.wallerlab.swy.service.conformation.manipulation

import org.wallerlab.swy.model.Coordinates
import org.wallerlab.swy.model.MolecularSystem
import org.wallerlab.swy.service.modeltools.CoordinateFactory

import spock.lang.*

class ManipulateBondTest extends Specification{

	private MolecularSystem molecularSystem
	private Coordinates coordinates
	
	private double moveValue
	private int atomA, atomB
	
	private double[] xCoords
	private double[] yCoords
	
	private double[] atomCoords
	
	@Unroll
	def "Test to move half of a small molecule"() {
		
		given:
		molecularSystem = new MolecularSystem()
		molecularSystem.xCoordInitial = [[-1.0, -1.0, 0.0, 1.0, 2.0, 2.0]]
		molecularSystem.yCoordInitial = [[1.0, -1.0, 0.0, 0.0, 1.0, -1.0]]
		molecularSystem.zCoordInitial = [[1.0, 1.0, 1.0, 1.0, 1.0, 1.0]]
		molecularSystem.bonds = [[[2], [2], [0, 1, 3], [2, 4, 5], [3], [3]]]
		
		coordinates = CoordinateFactory.newCoordinates(molecularSystem)
		
		ManipulateBond.manipulateBond(molecularSystem, coordinates, 0, atomA, 0, atomB, moveValue)
		for (int atom=0; atom < 6; atom++) {
			coordinates.xCoord[0][atom] = ((double)coordinates.xCoord[0][atom]).round(8)
			coordinates.yCoord[0][atom] = ((double)coordinates.yCoord[0][atom]).round(8)
			coordinates.zCoord[0][atom] = ((double)coordinates.zCoord[0][atom]).round(8)
		}
		
		expect:
		coordinates.xCoord[0] == xCoords
		coordinates.yCoord[0] == yCoords
		
		cleanup:
		coordinates = null
		molecularSystem = null
		
		where:
		atomA	|	atomB	|	moveValue	|	xCoords							|	yCoords
		2		|	3		|	0.0			|	[-1.0, -1.0, 0.0, 1.0, 2.0, 2.0]|	[1.0, -1.0, 0.0, 0.0, 1.0, -1.0]
		2		|	3		|	-0.5		|	[-1.0, -1.0, 0.0, 0.5, 1.5, 1.5]|	[1.0, -1.0, 0.0, 0.0, 1.0, -1.0]
		2		|	3		|	1.0			|	[-1.0, -1.0, 0.0, 2.0, 3.0, 3.0]|	[1.0, -1.0, 0.0, 0.0, 1.0, -1.0]
		0		|	2		|	1.414213562	|	[-1.0, 0.0, 1.0, 2.0, 3.0, 3.0]|	[1.0, -2.0, -1.0, -1.0, 0.0, -2.0]
		

	}
	
	@Unroll
	def "Test to move two molecules apart"() {
		
		given:
		molecularSystem = new MolecularSystem()
		molecularSystem.xCoordInitial = [[-1.0, -1.0],[1.0]]
		molecularSystem.yCoordInitial = [[0.5, -0.5],[0.0]]
		molecularSystem.zCoordInitial = [[-2.5, -2.5],[-2.5]]
		molecularSystem.numberOfAtomsInMolecule = [2, 1]
		
		coordinates = CoordinateFactory.newCoordinates(molecularSystem)

		ManipulateBond.manipulateBond(molecularSystem, coordinates, 0, atomA, 1, 0, moveValue)
		coordinates.xCoord[1][0] = ((double)coordinates.xCoord[1][0]).round(8)
		coordinates.yCoord[1][0] = ((double)coordinates.yCoord[1][0]).round(8)
		coordinates.zCoord[1][0] = ((double)coordinates.zCoord[1][0]).round(8)
		
		expect:
		coordinates.xCoord[1][0] == atomCoords[0]
		coordinates.yCoord[1][0] == atomCoords[1]
		coordinates.zCoord[1][0] == atomCoords[2]
		
		cleanup:
		molecularSystem = null
		
		where:
		atomA	|	moveValue	|	atomCoords
		0		|	0			|	[1.0, 0.0, -2.5]
		0		|	2.061552813	|	[3.0, -0.5, -2.5]
		1		|	2.061552813	|	[3.0, 0.5, -2.5]
	}
}
