package org.wallerlab.swy.service.conformation.manipulation

import org.wallerlab.swy.model.Coordinates
import org.wallerlab.swy.model.MolecularSystem
import org.wallerlab.swy.service.modeltools.CoordinateFactory

import spock.lang.*

class TurnBondTest extends Specification{
	
	private MolecularSystem molecularSystem
	private Coordinates coordinates
	
	int atomA, atomB
	int[] atomsToTurn
	double turnAngle
	double[] xCoords, yCoords, zCoords
	
	@Unroll
	def "Test to turn a bond in a simple system" () {
		
		given:
		molecularSystem = new MolecularSystem()
		molecularSystem.xCoordInitial = [[-1.5, -0.5, 0.5, 1.5]]
		molecularSystem.yCoordInitial = [[0.5, 0.0, 0.0, 0.5]]
		molecularSystem.zCoordInitial = [[-1.5, -1.5, -1.5, -1.5]]
		coordinates = CoordinateFactory.newCoordinates(molecularSystem)
		
		TurnBond.turnBond(coordinates, 0, atomA, atomB, atomsToTurn, turnAngle)
		for (int atom=0; atom < 4; atom++) {
			coordinates.xCoord[0][atom] = ((double)coordinates.xCoord[0][atom]).round(8)
			coordinates.yCoord[0][atom] = ((double)coordinates.yCoord[0][atom]).round(8)
			coordinates.zCoord[0][atom] = ((double)coordinates.zCoord[0][atom]).round(8)
		}
		
		expect:
		coordinates.xCoord[0] == xCoords
		coordinates.yCoord[0] == yCoords
		coordinates.zCoord[0] == zCoords
		
		cleanup:
		molecularSystem = null
		coordinates = null
		
		where:
		atomA	|atomB	|atomsToTurn|turnAngle	|xCoords				|yCoords				|zCoords
		1		|2		|[3]		|0.0		|[-1.5, -0.5, 0.5, 1.5]	|[0.5, 0.0, 0.0, 0.5]	|[-1.5, -1.5, -1.5, -1.5]
		1		|2		|[3]		|Math.PI	|[-1.5, -0.5, 0.5, 1.5]	|[0.5, 0.0, 0.0, -0.5]	|[-1.5, -1.5, -1.5, -1.5]
		1		|2		|[3]		|Math.PI/2	|[-1.5, -0.5, 0.5, 1.5]	|[0.5, 0.0, 0.0, 0.0]	|[-1.5, -1.5, -1.5, -1.0]
		0		|1		|[2, 3]		|Math.PI	|[-1.5, -0.5, 0.1, 0.3]	|[0.5, 0.0, -0.8, -1.9]	|[-1.5, -1.5, -1.5, -1.5]
		0		|1		|[2, 3]		|2* Math.PI	|[-1.5, -0.5, 0.5, 1.5]	|[0.5, 0.0, 0.0, 0.5]	|[-1.5, -1.5, -1.5, -1.5]
		
	}
	
}
