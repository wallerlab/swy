package org.wallerlab.swy.service.conformation.analyser

import org.wallerlab.swy.model.MolecularSystem
import org.wallerlab.swy.service.conformation.manipulation.ManipulateBond

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service


/**
 * Stretchable bonds is used to construct list of bonds which's length may be changed.
 */
@Service
@Profile(["untested"])
class StretchableBonds {

	/**
	 *  A list of the bonds that can be manipulated
	 */
	public static void createListOfStretchableBonds(MolecularSystem molecularSystem) {
		// Prepare the list of the molecular system
		molecularSystem.stretchableBonds.ensureCapacity(molecularSystem.numberOfMolecules)
		molecularSystem.atomsToMove.ensureCapacity(molecularSystem.numberOfMolecules)

		for (int molecule in 0..molecularSystem.numberOfMolecules-1) {
			molecularSystem.stretchableBonds[molecule] = new ArrayList()
			for (int atom in 0..molecularSystem.numberOfAtomsInMolecule[molecule]-1) {
				for (int bond in molecularSystem.bonds[molecule][atom]) {
					// Because the bond list is redundant only bonds from a lower index to a higher index are counted
					if(bond > atom) {
						/* 
						 * Ring check
						 * !! Rings are FIXED !!
						 */
						if ( Rings.isNotPartOfSameRing(molecularSystem, molecule, atom, bond) ) {
							// Add it
							molecularSystem.stretchableBonds[molecule] << [atom, bond]
						}
					}
				}
			}
		}
		return
	}

	/**
	 * Atoms that need to be moved during stretching of a Bond
	 */
	public static void createAtomsToMoveList(MolecularSystem molecularSystem) {
		for (int molecule in 0..molecularSystem.numberOfMolecules - 1) {
			// Clear the atomsToMove list
			molecularSystem.atomsToMove[molecule] = new ArrayList(molecularSystem.stretchableBonds[molecule].size())
			for (def thisBond in molecularSystem.stretchableBonds[molecule]){
				molecularSystem.atomsToMove[molecule] << \
					ManipulateBond.createAtomsToMove(molecularSystem, molecule, thisBond[0], molecule, thisBond[1])
			}
		}
		return
	}
}
