package org.wallerlab.swy.service.conformation.manipulation

import org.wallerlab.swy.model.Coordinates
import org.wallerlab.swy.model.MolecularSystem
import org.wallerlab.swy.service.conformation.manipulation.TurnAroundAxis
import org.wallerlab.swy.service.utils.Maths

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

/**
 * Manipulate Angle 
 * 
 * using the cross product of the two vectors angle[0]->angle[1] and angle[1]->angle[2] to define the turn axis
 * because remember: the cross product of two vectors is a new vector which is orthogonal to both
 * in other words the turn axis points into the same direction as a normal vector to the area spanned by the
 * vectors described above.
 *
 */
@Service
@Profile(["untested"])
class ManipulateAngle {
	
	private static double[] centerOfRotation = new double[3]
	private static double[] dummyVectorOne = new double[3]
	private static double[] dummyVectorTwo = new double[3]
	private static double[] turnAxis = new double[3]
	private static double[][] turnMatrix = new double[3][3]
	
	/**
	 * @param coordinates The coordinates under manipulation
	 * @param molecule
	 * @param angle The list of atom indexes which form the angle to be manipulated
	 * @param atomsToBend These atoms need to be displaced when manipulating the angle
	 * @param turnValue How much to bend the angle (0..2*PI)
	 */
	public static void manipulateAngle(Coordinates coordinates, int moleculeIndex, int[] angle, \
		int[] atomsToBend, double turnValue) {
		
		dummyVectorOne[0] = coordinates.xCoord[moleculeIndex][ angle[1] ] \
			- coordinates.xCoord[moleculeIndex][ angle[0] ]
		dummyVectorOne[1] = coordinates.yCoord[moleculeIndex][ angle[1] ] \
			- coordinates.yCoord[moleculeIndex][ angle[0] ]
		dummyVectorOne[2] = coordinates.zCoord[moleculeIndex][ angle[1] ] \
			- coordinates.zCoord[moleculeIndex][ angle[0] ]
		
		dummyVectorTwo[0] = coordinates.xCoord[moleculeIndex][ angle[2] ] \
			- coordinates.xCoord[moleculeIndex][ angle[1] ]
		dummyVectorTwo[1] = coordinates.yCoord[moleculeIndex][ angle[2] ] \
			- coordinates.yCoord[moleculeIndex][ angle[1] ]
		dummyVectorTwo[2] = coordinates.zCoord[moleculeIndex][ angle[2] ] \
			- coordinates.zCoord[moleculeIndex][ angle[1] ]
		
		turnAxis = Maths.crossProduct(dummyVectorOne, dummyVectorTwo)
		/* 
		 * Behold the gimbal lock! If the dummy vectors are parallel just
		 * take the first dummy vector rotated by 90
		 */
		if(turnAxis[0].round(8) == 0.0 && turnAxis[1].round(8) == 0.0 && turnAxis[2].round(8) == 0.0) {
			turnAxis[0] = -dummyVectorOne[1]
			turnAxis[1] = dummyVectorOne[0]
			turnAxis[2] = dummyVectorOne[2]
		}
		
		turnAxis = Maths.normalizeVec(turnAxis)
		
		/* create a turnMatrix  */
 		turnMatrix = Maths.generateTurnMatrix(turnAxis, turnValue)
				
		/* define center atom of the angle as centerOfRotation */
		centerOfRotation[0] = coordinates.xCoord[moleculeIndex][ angle[1] ]
		centerOfRotation[1] = coordinates.yCoord[moleculeIndex][ angle[1] ]
		centerOfRotation[2] = coordinates.zCoord[moleculeIndex][ angle[1] ]
		
		TurnAroundAxis.turnAroundAxis(coordinates, moleculeIndex, atomsToBend, turnMatrix, centerOfRotation)
		return
	}
}
