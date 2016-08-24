package org.wallerlab.swy.service.conformation.analyser

import org.wallerlab.swy.model.MolecularSystem

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service

/**
 * A class which can analyse a molecular system for rings it contains based on a connectivity.
 * 
 * @author t_dres03
 *
 */
@Profile(["untested"])
class Rings {
	
	/**
	 * This method really takes a lot of time due to heavy recursiveness. !!!
	 * I guess it scales with something like numberOfBonds.FACTORIAL !!!!!! 
	 */
	public static void createRingList(MolecularSystem molecularSystem) {
		molecularSystem.rings.ensureCapacity(molecularSystem.numberOfMolecules)
		for (int molecule in 0..molecularSystem.numberOfMolecules-1) {
			molecularSystem.rings[molecule] = new ArrayList()
			for (int atom in 0..molecularSystem.numberOfAtomsInMolecule[molecule]-1) {
				for (int bondAtom in molecularSystem.bonds[molecule][atom]) {
					ArrayList inputList = new ArrayList()
					inputList << bondAtom
					getRingsOfAtom(molecularSystem, molecule, atom, bondAtom, bondAtom, inputList)
				}
			}
			molecularSystem.rings[molecule].unique()
		}
		return
	}
	
	/**
	 * This recursive method stepwise creates a list of atoms
	 * that are connected in a chain. Whenever this chain ends
	 * up at the startAtom again there must be a ring which is
	 * stored in the {@link org.wallerlab.swy.model.MolecularSystem#rings ring list}
	 *
	 * @param moleculeIndex
	 * @param startAtom This does not change within the method.
	 * It is needed to check whether a ring has been found.
	 * @param bondAtom This also does not change within the method
	 * but is needed to prevent the method from finding two-membered-rings.
	 * @param activeAtom This atom is the last one in the current chain.
	 * @param inputList The current list of atoms which are connected
	 * in a chain. It is cloned each time the recursion goes one
	 * step deeper.
	 */
	private static void getRingsOfAtom(MolecularSystem molecularSystem, \
			int moleculeIndex, int startAtom, int bondAtom, int activeAtom, \
			ArrayList inputList) {
		for (int bondPartnerOfActiveAtom in molecularSystem.bonds[moleculeIndex][activeAtom]) {
			if (bondPartnerOfActiveAtom == startAtom) {
				if (activeAtom != bondAtom) {
					// RING FOUND!!!
					inputList << startAtom
					molecularSystem.rings[moleculeIndex] << inputList.sort()
				}
			}else if( !inputList.contains(bondPartnerOfActiveAtom) ) {
				ArrayList newInputList = inputList.clone()
				newInputList << bondPartnerOfActiveAtom
				getRingsOfAtom(molecularSystem, moleculeIndex, startAtom, bondAtom, \
					bondPartnerOfActiveAtom, newInputList)
			}
		}
		return
	}
	
	/**
	 * This method is called whenever one needs to check
	 * whether two atoms are part of a ring (meaning both
	 * atoms belong to the same ring). A lot of things
	 * cannot be done in that case.
	 * 
	 * @param molecule
	 * @param atomA
	 * @param atomB
	 * @return true if the atoms do not belong to a ring, false
	 * if they do
	 */
	public static boolean isNotPartOfSameRing(MolecularSystem molecularSystem, \
			int molecule, int atomA, int atomB) {
		boolean ringSafetyFlag = true
		
		for (int[] ring in molecularSystem.rings[molecule]) {
			if (java.util.Arrays.asList(ring).contains(atomA) && java.util.Arrays.asList(ring).contains(atomB)) {
				ringSafetyFlag = false
				break
			}
		}
		return ringSafetyFlag
	}
}
