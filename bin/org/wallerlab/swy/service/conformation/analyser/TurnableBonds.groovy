package org.wallerlab.swy.service.conformation.analyser

import org.wallerlab.swy.model.MolecularSystem
import org.wallerlab.swy.service.conformation.analyser.Basics
import org.wallerlab.swy.service.conformation.analyser.Rings

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

/**
 * Turnable Bonds is used to determine around which bonds in a
 * molecular system rotations are possible.
 */
@Service
@Profile(["untested"])
class TurnableBonds {

	/**
	 * Method to find all bonds around which turning changes the molecular conformation
	 * without breaking the molecule.
	 */	
	public static void createListOfTurnableBonds(MolecularSystem molecularSystem) {
		// Prepare the list of turnable bonds (and atomsToTurn)
		molecularSystem.turnableBonds.ensureCapacity(molecularSystem.numberOfMolecules)
		molecularSystem.atomsToTurn.ensureCapacity(molecularSystem.numberOfMolecules)
		// Loop over all molecules
		for (int molecule in 0..molecularSystem.numberOfMolecules - 1) {
			molecularSystem.turnableBonds[molecule] = new ArrayList()
			// Loop over all atoms...
			for (int atom in 0..(molecularSystem.numberOfAtomsInMolecule[molecule] - 1) ) {
				// ...and this atoms bond partners
				for (int bondPartner in molecularSystem.bonds[molecule][atom]) {
					/*
					 * Both atoms are at least connected to one other atom (i.e. to each other).
					 * The bond is "turnable" if each of both atoms is also connected to at least one further atom.
					 */
					if (molecularSystem.bonds[molecule][atom].size() > 1 && molecularSystem.bonds[molecule][bondPartner].size() > 1) {
						/* 
						 * Ring check
						 * !! Rings are FIXED !!
						 */
						if ( Rings.isNotPartOfSameRing(molecularSystem, molecule, atom, bondPartner) ) {
							// Put this bond in the turnableBonds list (sorted!)
							molecularSystem.turnableBonds[molecule] << [atom, bondPartner].sort()
						}
					}
				}
			}
			// Remove double entries
			molecularSystem.turnableBonds[molecule].unique(true)
		}
		return
	}

	/**
	 * Create list of atoms that are displaced when turning around a bond.
	 */
	public static void createAtomsToTurnList(MolecularSystem molecularSystem) {
		/* Loop over molecules */
		for (int molecule in 0..molecularSystem.numberOfMolecules - 1) {
			/* Clear the atomsToTurn list */
			molecularSystem.atomsToTurn[molecule] = new ArrayList(molecularSystem.turnableBonds[molecule].size())
			for (int thisBondIndex in 0..molecularSystem.turnableBonds[molecule].size()-1){
				def thisBond = molecularSystem.turnableBonds[molecule][thisBondIndex]
				molecularSystem.atomsToTurn[molecule] << new ArrayList()
//				for (int bondPartner in molecularSystem.bonds[molecule][ thisBond[0] ]) {
//					if ( thisBond[1] != bondPartner ) {
//						molecularSystem.atomsToTurn[molecule][thisBondIndex] = Basics.getListOfAtomsOnOneSideOfBond(molecularSystem.atomsToTurn[molecule][thisBondIndex], molecule, bondPartner, thisBond[1])
				molecularSystem.atomsToTurn[molecule][thisBondIndex] =\
					Basics.getListOfAtomsOnOneSideOfBond(molecularSystem.atomsToTurn[molecule][thisBondIndex], \
						molecularSystem, molecule, thisBond[0], thisBond[1]) \
					- thisBond[0]
//					}
//				}
			}
		}
		return
	}
}
