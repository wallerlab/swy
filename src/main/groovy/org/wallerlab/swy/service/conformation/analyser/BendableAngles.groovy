package org.wallerlab.swy.service.conformation.analyser

import org.wallerlab.swy.model.MolecularSystem

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service


/**
 * Bendable Angles class is used to determine which angles to perturb.<br>
 *
 * This is what an angle looks like:<br><br>
 * <pre>
                              angleCenterAtom
                                   /   \
  (this Bond is in the bonds list)/     \(this Bond is in the bonds list)
                                 /       \
                  angleComesFromAtom    angleGoesToAtom
   </pre>
 */
@Service
@Profile(["untested"])
class BendableAngles {

	/**
	 * The startingAtom of a certain molecule is the one with the
	 * lowest index which is only bond to ONE other atom
	 */
	private static int[] startingAtom

	private static ArrayList visited

	/**
	 * Generate a list of Bendable Angles.
	 */
	public static void createListOfBendableAngles(MolecularSystem molecularSystem) {
		findStartingAtoms(molecularSystem)
		// Prepare the list of the molecular system
		molecularSystem.bendableAngles.ensureCapacity(molecularSystem.numberOfMolecules)
		molecularSystem.atomsToBend.ensureCapacity(molecularSystem.numberOfMolecules)
		// Loop over all molecules
		for (int moleculeIndex in 0..molecularSystem.numberOfMolecules - 1) {
			molecularSystem.bendableAngles[moleculeIndex] = new ArrayList()
			// Start the recursive procedure to retrieve the bendableAngles
			visited = new ArrayList()
			getAnglesOfNextAtom(molecularSystem, moleculeIndex, startingAtom[moleculeIndex], \
				molecularSystem.bonds[moleculeIndex][ startingAtom[moleculeIndex] ][0])
		}
		return		
	}

	public static void createAtomsToBendList(MolecularSystem molecularSystem) {
		for (int molecule in 0..molecularSystem.numberOfMolecules - 1) {
			// Clear the atomsToBend list
			molecularSystem.atomsToBend[molecule] = \
				new ArrayList(molecularSystem.bendableAngles[molecule].size())
			for (def thisAngle in molecularSystem.bendableAngles[molecule]){
				molecularSystem.atomsToBend[molecule] << \
					Basics.getListOfAtomsOnOneSideOfBond(new ArrayList(), molecularSystem, \
						molecule, thisAngle[2], thisAngle[1])
			}
		}
		return
	}


	/**
	 * This method populates the {@link #startingAtom} list by finding
	 * one atom (in each molecule) which has only one bond.
	 */
	private static void findStartingAtoms(MolecularSystem molecularSystem) {
		startingAtom = new int[molecularSystem.numberOfMolecules]
		for (int moleculeIndex in 0..molecularSystem.numberOfMolecules-1) {
			// Scan through the atoms...
			for (indexOfBondFromAtom in 0..molecularSystem.bonds[moleculeIndex].size() - 1) {
				// ...until one is found which is only bond to ONE atom, meaning that this is an 'ending' atom
				if (molecularSystem.bonds[moleculeIndex][indexOfBondFromAtom].size() == 1) {
					startingAtom[moleculeIndex] = indexOfBondFromAtom
					break
				}
			}
		}
		return
	}

	/**
	 * This recursive method finds the angles that two given atoms can form
	 * with a third one and then moves forward by one bond to find the angles
	 * that the 'following' atoms can form.
	 *
	 * @param moleculeIndex
	 * @param angleComesFromAtom
	 * @param angleCenterAtom
	 */
	private static void getAnglesOfNextAtom(MolecularSystem molecularSystem, \
			int moleculeIndex, int angleComesFromAtom, int angleCenterAtom) {
		visited << angleCenterAtom
		for (int angleGoesToAtom in molecularSystem.bonds[moleculeIndex][angleCenterAtom]) {
			// That this condition should hold is obvious, right?
			if ( !(angleGoesToAtom==angleComesFromAtom) ) {
				// Whenever three atoms are in one connection line they form a bendable angle
				/* 
				 * Ring check
				 * !! Rings are FIXED !!
				 */
				if ( Rings.isNotPartOfSameRing(molecularSystem, moleculeIndex, \
						angleCenterAtom, angleGoesToAtom) ) {
					molecularSystem.bendableAngles[moleculeIndex] << [
						angleComesFromAtom,
						angleCenterAtom,
						angleGoesToAtom
					]
				}
				if (!visited.contains(angleGoesToAtom)) {
					getAnglesOfNextAtom(molecularSystem, moleculeIndex, angleCenterAtom, angleGoesToAtom)
				}
			}
		}
		return	
	}
}
