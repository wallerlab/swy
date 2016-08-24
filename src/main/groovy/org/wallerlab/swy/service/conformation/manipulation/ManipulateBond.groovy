package org.wallerlab.swy.service.conformation.manipulation;

import org.wallerlab.swy.model.Coordinates
import org.wallerlab.swy.model.MolecularSystem
import org.wallerlab.swy.service.conformation.analyser.Basics
import org.wallerlab.swy.service.utils.Maths

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service

/**
 *  Manipulate the bonds to change molecular geometry.
 */
@Service
@Profile(["untested"])
class ManipulateBond {
	
	private static Coordinates coordinates
	
	public ManipulateBond() {
	}
	
	/**
	 * Method call if atomsToMove is NOT known
	 * 
	 * @param coordinates The coordinates under manipulation
	 * @param moleculeOfAtomA
	 * @param indexOfAtomA
	 * @param moleculeOfAtomB
	 * @param indexOfAtomB
	 * @param moveValue How much the atoms on one side of the bond are moved.
	 */
	public static void manipulateBond(MolecularSystem molecularSystem, Coordinates coordinates, \
			int moleculeOfAtomA, int indexOfAtomA, int moleculeOfAtomB, int indexOfAtomB, double moveValue) {
		int[] atomsToMove = createAtomsToMove(molecularSystem, moleculeOfAtomA, indexOfAtomA, \
			moleculeOfAtomB, indexOfAtomB)
		manipulateBond(coordinates, moleculeOfAtomA, indexOfAtomA, \
			moleculeOfAtomB, indexOfAtomB, moveValue, atomsToMove)
	}

	/*
	 * For some funny reason this is giving trouble... so TODO hacked
	 * public static void manipulateBondWithList(Coordinates coordinates, int moleculeOfAtomA, int indexOfAtomA, \
	 * 		int moleculeOfAtomB, int indexOfAtomB, double moveValue, int[] atomsToMove) {
	 */
	/** 
	 * Method call if atomsToMove IS known
	 * 
	 * @param coordinates The coordinates under manipulation
	 * @param moleculeOfAtomA
	 * @param indexOfAtomA
	 * @param moleculeOfAtomB
	 * @param indexOfAtomB
	 * @param moveValue How much the atoms on one side of the bond are moved.
	 * @param atomsToMove
	 */
	public static void manipulateBond(Coordinates coordinates, int moleculeOfAtomA, int indexOfAtomA, \
			int moleculeOfAtomB, int indexOfAtomB, def moveValue, int[] atomsToMove) {
		this.coordinates = coordinates
		
		double[] moveVector = new double[3]

		moveVector[0] = coordinates.xCoord[moleculeOfAtomB][indexOfAtomB] - \
				coordinates.xCoord[moleculeOfAtomA][indexOfAtomA]
		moveVector[1] = coordinates.yCoord[moleculeOfAtomB][indexOfAtomB] - \
				coordinates.yCoord[moleculeOfAtomA][indexOfAtomA]
		moveVector[2] = coordinates.zCoord[moleculeOfAtomB][indexOfAtomB] - \
				coordinates.zCoord[moleculeOfAtomA][indexOfAtomA]

		moveVector = Maths.normalizeVec(moveVector)
		moveVector[0] *= moveValue
		moveVector[1] *= moveValue
		moveVector[2] *= moveValue

		moveAtoms(moleculeOfAtomB, atomsToMove, moveVector)
		return
	}

	/**
	 * Create a list of atoms that must be moved when manipulating the
	 * bond/distance between atoms A and B.
	 * 
	 * @param moleculeOfAtomA
	 * @param indexOfAtomA
	 * @param moleculeOfAtomB
	 * @param indexOfAtomB
	 * @return atomsToMove
	 */
	public static int[] createAtomsToMove(MolecularSystem molecularSystem, int moleculeOfAtomA, \
			int indexOfAtomA, int moleculeOfAtomB, int indexOfAtomB) {
		int[] atomsToMove

		// Only molecule of atom B is moved. If both atoms are from the same molecule only atoms on one side of bond are moved
		if (moleculeOfAtomA != moleculeOfAtomB) {
			atomsToMove = new int[molecularSystem.numberOfAtomsInMolecule[moleculeOfAtomB]]
			for (int atomIndex in 0..molecularSystem.numberOfAtomsInMolecule[moleculeOfAtomB]-1) {
				atomsToMove[atomIndex] = atomIndex
			}
		}else{
			// make sure there is a bond between the atoms if they are in the same molecule
			assert (molecularSystem.bonds[moleculeOfAtomB][indexOfAtomB].contains(indexOfAtomA))
			atomsToMove = (int[])Basics.getListOfAtomsOnOneSideOfBond(new ArrayList(), molecularSystem, \
				moleculeOfAtomB, indexOfAtomB, indexOfAtomA)
		}
		return atomsToMove
	}

	/**
	 * @param moleculeIndex
	 * @param atomsToMove
	 * @param moveVector The direction (and amount) of the movement
	 */
	private static void moveAtoms(int moleculeIndex, int[] atomsToMove, double[] moveVector) {
		for (atom in atomsToMove) {
			coordinates.xCoord[moleculeIndex][atom] += moveVector[0]
			coordinates.yCoord[moleculeIndex][atom] += moveVector[1]
			coordinates.zCoord[moleculeIndex][atom] += moveVector[2]
		}
		return
	}
}
