package org.wallerlab.swy.service.conformation.manipulation

import org.wallerlab.swy.model.Coordinates
import org.wallerlab.swy.model.MolecularSystem

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

/**
 * This class is used to apply rotation matrices to a molecule
 * or parts of a molecule.
 * 
 * @author t_dres03
 */
@Service
@Profile(["untested"])
public class TurnAroundAxis {
	
	private static double[] dummyAtom = new double[3]
	
	/**
	 * Rotates a whole molecule.
	 * 
	 * @param coordinates The coordinates under manipulation
	 * @param moleculeIndex
	 * @param rotationMatrix
	 * @param centerOfRotation In xyz coordinates
	 */
	public static void turnAroundAxis(MolecularSystem molecularSystem, Coordinates coordinates, \
			int moleculeIndex, def rotationMatrix, double[] centerOfRotation) {
		int[] atomsToRotate = new int[molecularSystem.numberOfAtomsInMolecule[moleculeIndex]]
		for (int index in 0..molecularSystem.numberOfAtomsInMolecule[moleculeIndex]-1) {
			atomsToRotate[index] = index
		}
		turnAroundAxis(coordinates, moleculeIndex, atomsToRotate, rotationMatrix, centerOfRotation)
	}
	
	// TODO hacked
	/**
	 * Rotates a part of a molecule. E.g. a twist around a bond.
	 * 
	 * @param coordinates The coordinates under manipulation
	 * @param moleculeIndex
	 * @param atomsToRotate
	 * @param rotationMatrix
	 * @param centerOfRotation In xyz coordinates
	 */
	public static void turnAroundAxis(Coordinates coordinates, int moleculeIndex, int[] atomsToRotate, \
			def rotationMatrix, double[] centerOfRotation) {
		/* Move to centerOfRotation */
		for (int atom in atomsToRotate){
			coordinates.xCoord[moleculeIndex][atom] -= centerOfRotation[0]
			coordinates.yCoord[moleculeIndex][atom] -= centerOfRotation[1]
			coordinates.zCoord[moleculeIndex][atom] -= centerOfRotation[2]
		}
		/* Turn atoms */
		for(thisAtom in atomsToRotate){
			dummyAtom[0]=coordinates.xCoord[moleculeIndex][thisAtom]
			dummyAtom[1]=coordinates.yCoord[moleculeIndex][thisAtom]
			dummyAtom[2]=coordinates.zCoord[moleculeIndex][thisAtom]
			coordinates.xCoord[moleculeIndex][thisAtom] = rotationMatrix[0][0] * dummyAtom[0] + \
				rotationMatrix[0][1] * dummyAtom[1] + rotationMatrix[0][2] * dummyAtom[2]
			coordinates.yCoord[moleculeIndex][thisAtom] = rotationMatrix[1][0] * dummyAtom[0] + \
				rotationMatrix[1][1] * dummyAtom[1] + rotationMatrix[1][2] * dummyAtom[2]
			coordinates.zCoord[moleculeIndex][thisAtom] = rotationMatrix[2][0] * dummyAtom[0] + \
				rotationMatrix[2][1] * dummyAtom[1] + rotationMatrix[2][2] * dummyAtom[2]
		}
		/* Move back to original origin and set to xyzCoord[molecule] */
		for (int atom in atomsToRotate){
			coordinates.xCoord[moleculeIndex][atom] += centerOfRotation[0]
			coordinates.yCoord[moleculeIndex][atom] += centerOfRotation[1]
			coordinates.zCoord[moleculeIndex][atom] += centerOfRotation[2]
		}
	}
}
