package org.wallerlab.swy.service.conformation.analyser

import org.wallerlab.swy.model.Coordinates
import org.wallerlab.swy.model.MolecularSystem
import org.wallerlab.swy.service.conformation.analyser.Rings
import org.wallerlab.swy.service.utils.Maths

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

/**
 * Basics class takes care of general purpose routines to analyze geometries. 
 */
@Service
@Profile(["untested"])
public class Basics {

	/**
	 * Get the distance between atom pairs.
	 *
	 * @param coordinates
	 * @param moleculeOfAtomA
	 * @param indexOfAtomA
	 * @param moleculeOfAtomB
	 * @param indexOfAtomB
	 * @return Distance in Angstroms	
	 */
	public static double getDistanceBetweenAtoms(Coordinates coordinates, int moleculeOfAtomA, int indexOfAtomA, int moleculeOfAtomB, int indexOfAtomB) {
		double[] vectorBetweenAtoms = new double[3]
		vectorBetweenAtoms[0] = coordinates.xCoord[moleculeOfAtomB][indexOfAtomB] - coordinates.xCoord[moleculeOfAtomA][indexOfAtomA]
		vectorBetweenAtoms[1] = coordinates.yCoord[moleculeOfAtomB][indexOfAtomB] - coordinates.yCoord[moleculeOfAtomA][indexOfAtomA]
		vectorBetweenAtoms[2] = coordinates.zCoord[moleculeOfAtomB][indexOfAtomB] - coordinates.zCoord[moleculeOfAtomA][indexOfAtomA]
		return Maths.absVector(vectorBetweenAtoms)
	}
	
	/**
	 * List of atoms on one side of the bond
	 */
	private static ArrayList atomsOnOneSideOfBond
	
	/**
	 * Create a list of atoms on one side of a given bond.
	 * @param startingList
	 * @param molecule
	 * @param bondAtomOne
	 * @param bondAtomTwo
	 * @return sorted list of atoms on one side of a bond.	
	 */
	public static ArrayList getListOfAtomsOnOneSideOfBond(ArrayList startingList, MolecularSystem molecularSystem, \
			int molecule, int bondAtomOne, int bondAtomTwo) {
		atomsOnOneSideOfBond = startingList
		assert(molecularSystem.bonds[molecule][bondAtomOne].contains(bondAtomTwo)): \
			"ERROR: the two atoms are not bond to each other properly"
		// Ring check
		assert(Rings.isNotPartOfSameRing(molecularSystem, molecule, bondAtomOne, bondAtomTwo)): \
			"""ERROR: you try to find out which atoms are on one side of a bond for a bond inside a ring.
					This is neither possible, nor should you want to know that."""
		checkConnection(molecularSystem, molecule, bondAtomOne, bondAtomTwo)
		return atomsOnOneSideOfBond.sort()
	}
	
	/**
	 *  Recursive function to check which atoms are on the same side of the bond.
	 * 
	 * @param molecule
	 * @param thisAtom
	 * @param borderAtom	
	 */
	private static void checkConnection(MolecularSystem molecularSystem, \
			int molecule, int thisAtom, int borderAtom){
		if(thisAtom != borderAtom && ( !atomsOnOneSideOfBond.findResult{ thisAtom == it ? true : null } ) ){
			atomsOnOneSideOfBond.add(thisAtom)
			for (int nextAtom in molecularSystem.bonds[molecule][thisAtom]){
				checkConnection(molecularSystem, molecule, nextAtom, borderAtom)
			}
		}
		return
	}	
}