package org.wallerlab.swy.service.conformation.factory

import org.wallerlab.swy.model.Coordinates
import org.wallerlab.swy.model.MolecularSystem
import org.wallerlab.swy.service.conformation.manipulation.TurnBond
import org.wallerlab.swy.service.modeltools.CoordinateFactory

import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service

/**
 * DihedralConformation class to create a new conformation
 * by varying the dihedral angles.
 */
@Service
@Profile(["untested"])
class DihedralConformation implements ConformationInterface {

	private MolecularSystem molecularSystem

	private Coordinates coordinates
	
	DihedralConformation() {
	}
	
	/**
	 * Generates a new conformation using a list of dihedral/turning angles
	 * 
	 * @param solution The angle values; how much to twist the molecule
	 * around a certain bond
	 */
	synchronized Coordinates newConformation (double[] solution){
		coordinates = CoordinateFactory.newCoordinates(molecularSystem)
		
		int solutionIndex = 0
		
		turnAllBonds(molecularSystem, coordinates, solution, solutionIndex)
		return coordinates
	}

	/**
	 * Turns bonds of molecules
	 * 
	 * @param coordinates The conformation before perturbing the dihedrals
	 * @param solution The angle values
	 * @param solutionIndex At which entry in solution do the dihedrals start
	 */
	static void turnAllBonds(MolecularSystem molecularSystem, Coordinates coordinates, \
			double[] solution, int solutionIndex) {
		// Loop over all molecules ... TODO ...not that this makes sense....
		for (int molecule in 0..molecularSystem.numberOfMolecules - 1) {
			// Rotate around all bonds subsequently
			for (int bondIndex=0; bondIndex < molecularSystem.turnableBonds[molecule].size(); bondIndex++){
				TurnBond.turnBond(coordinates, molecule, molecularSystem.turnableBonds[molecule][bondIndex][0], \
					molecularSystem.turnableBonds[molecule][bondIndex][1], \
					(int[])molecularSystem.atomsToTurn[molecule][bondIndex], \
					solution[solutionIndex])
				solutionIndex++
			}
		}
		return
	}
	
	@Required		
	public void setMolecularSystem(MolecularSystem molecularSystem) {
		this.molecularSystem = molecularSystem
	}

	public MolecularSystem getMolecularSystem() {
		return molecularSystem
	}
}
