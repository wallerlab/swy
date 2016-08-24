package org.wallerlab.swy.service.conformation.manipulation

import org.wallerlab.swy.model.Coordinates
import org.wallerlab.swy.model.MolecularSystem
import org.wallerlab.swy.service.modeltools.CoordinateFactory

import spock.lang.*

/*
 * This class tests the full functionality of the ManipulateAngle class, including all the methods
 * IT calls, meaning no method calls are stubbed away.
 */
class ManipulateAngleTest extends Specification{

	private MolecularSystem molecularSystem
	private Coordinates coordinates
	private int[] angle
	private int[] atomsToBend
	private double turnValue
	private double atomTwoX, atomTwoY
	
	@Unroll
	def "Test to manipulate water angle"() {
		
		given:
		molecularSystem = new MolecularSystem()
		molecularSystem.xCoordInitial = [[0.0, 1.0, 1.0]]
		molecularSystem.yCoordInitial = [[0.0, 0.0, 1.0]]
		molecularSystem.zCoordInitial = [[0.0, 0.0, 0.0]]
		coordinates = CoordinateFactory.newCoordinates(molecularSystem)
		angle = new int[3]
		angle[0] = 0
		angle[1] = 1
		angle[2] = 2
		atomsToBend = new int[1]
		atomsToBend[0] = 2
		ManipulateAngle.manipulateAngle(coordinates, 0, angle, atomsToBend, turnValue)
		for (int atom=0; atom < 3; atom++) {
			coordinates.xCoord[0][atom] = ((double)coordinates.xCoord[0][atom]).round(8)
			coordinates.yCoord[0][atom] = ((double)coordinates.yCoord[0][atom]).round(8)
			coordinates.zCoord[0][atom] = ((double)coordinates.zCoord[0][atom]).round(8)
		}
		
		expect:
		coordinates.xCoord[0][2] == atomTwoX
		coordinates.yCoord[0][2] == atomTwoY
		coordinates.zCoord[0][2] == 0.0
		
		where:
		turnValue		|	atomTwoX	|	atomTwoY
		0.0				|	1.0			|	1.0
		Math.PI/2		|	0.0			|	0.0
		-Math.PI/2		|	2.0			|	0.0
		2*Math.PI		|	1.0			|	1.0
	}
	
}
