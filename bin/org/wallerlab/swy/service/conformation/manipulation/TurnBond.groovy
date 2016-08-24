package org.wallerlab.swy.service.conformation.manipulation;

import org.wallerlab.swy.model.Coordinates
import org.wallerlab.swy.service.conformation.manipulation.TurnAroundAxis
import org.wallerlab.swy.service.utils.Maths

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

/**
 * This class can twist a molecule around a specified bond.
 * 
 * @author t_dres03
 */
@Service 
@Profile(["untested"])
class TurnBond {
	
	private static double[] rotationAxis = new double[3]
	private static double[][] rotationMatrix = new double[3][3]
	private static double[] dummyAtom = new double[3]
	
	/**
	 * Coordinate manipulation: turn around one bond.
	 * 
	 * @param coordinates The coordinates under manipulation
	 * @param moleculeIndex
	 * @param bondFromAtom This atom is part of the bond which is twisted.
	 * @param bondToAtom This atom is part of the bond which is twisted.
	 * @param atomsToTurn
	 * @param turnAngle The amount of rotation (0..2*PI)
	 */
	public static void turnBond(Coordinates coordinates, int moleculeIndex, int bondFromAtom, \
			int bondToAtom, int[] atomsToTurn, double turnAngle){
	
		/*  define center of rotation = bondFromAtom */
		dummyAtom[0] = (coordinates.xCoord[moleculeIndex][bondFromAtom])
		dummyAtom[1] = (coordinates.yCoord[moleculeIndex][bondFromAtom])
		dummyAtom[2] = (coordinates.zCoord[moleculeIndex][bondFromAtom])
	
		/* Define turning Vector, i.e. the axis=bond around which the molecule should be twisted. */
		rotationAxis[0] = (coordinates.xCoord[moleculeIndex][bondToAtom] - \
			coordinates.xCoord[moleculeIndex][bondFromAtom])
		rotationAxis[1] = (coordinates.yCoord[moleculeIndex][bondToAtom] - \
			coordinates.yCoord[moleculeIndex][bondFromAtom])
		rotationAxis[2] = (coordinates.zCoord[moleculeIndex][bondToAtom] - \
			coordinates.zCoord[moleculeIndex][bondFromAtom])
		rotationAxis = Maths.normalizeVec(rotationAxis)
		
		/* Create turning matrix */
		rotationMatrix = Maths.generateTurnMatrix(rotationAxis, turnAngle)

		TurnAroundAxis.turnAroundAxis(coordinates, moleculeIndex, atomsToTurn, rotationMatrix, dummyAtom)
	}
}
