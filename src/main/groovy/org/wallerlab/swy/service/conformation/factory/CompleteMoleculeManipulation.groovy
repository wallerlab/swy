package org.wallerlab.swy.service.conformation.factory

import org.wallerlab.swy.model.Coordinates
import org.wallerlab.swy.model.MolecularSystem
import org.wallerlab.swy.service.conformation.manipulation.ManipulateAngle
import org.wallerlab.swy.service.conformation.manipulation.ManipulateBond
import org.wallerlab.swy.service.conformation.manipulation.TurnBond
import org.wallerlab.swy.service.modeltools.CoordinateFactory;

import org.springframework.beans.factory.annotation.Required
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

/**
 * Manipulate all Degrees of Freedom (no constraints) 
 */
@Service
@Profile(["unstable"])
class CompleteMoleculeManipulation implements ConformationInterface {

	private static int solutionIndex
	
	private MolecularSystem molecularSystem

	private Coordinates coordinates

	CompleteMoleculeManipulation() {
	}

	/**
	 * @param solution An array of doubles which are interpreted as
	 * bond length/angle/dihedral manipulations.
	 * 
	 * @return The new conformation.
	 */
	synchronized Coordinates newConformation(double[] solution) {
		coordinates = CoordinateFactory.newCoordinates()
		solutionIndex = 0

		/* adjust bond lengths */
		for (molecule in 0..molecularSystem.numberOfMolecules-1) {
			for (stretchableBond in molecularSystem.stretchableBonds[molecule]) {
				ManipulateBond.manipulateBond(coordinates, molecule, stretchableBond[0], molecule, stretchableBond[1], solution[solutionIndex], MolecularSystem.atomsToMove[molecule][solutionIndex])
				solutionIndex++
			}
		}

		/* adjust bond angles */
		solutionIndex = bendAllAngles(coordinates, solution, solutionIndex)

		/* adjust dihedrals */
		DihedralConformation.turnAllBonds(coordinates, solution, solutionIndex)

		return coordinates
	}
	
	/**
	 * Perturbs the coordinates by manipulating angles.
	 *
	 * @param coordinates before the angle perturbation
	 * @param solution An array of double values among others containing the delta(angle)
	 * value for all angles.
	 * @param solutionIndex At which entry in solution do the angles start.
	 * @return The new solutionIndex, meaning which entry in solution contains
	 * the next element (e.g. a dihedral).
	 */
	private static int bendAllAngles(MolecularSystem molecularSystem, Coordinates coordinates, \
			double[] solution, int solutionIndex) {
		for (int molecule in 0..molecularSystem.numberOfMolecules - 1) {
			// Rotate around all bonds subsequently
			for (int angleIndex=0; angleIndex < molecularSystem.bendableAngles[molecule].size(); angleIndex++){
				ManipulateAngle.manipulateAngle(coordinates, molecule, (int[])molecularSystem.bendableAngles[molecule][angleIndex], (int[])MolecularSystem.atomsToBend[molecule][angleIndex], solution[solutionIndex])
				solutionIndex++
			}
		}
		return solutionIndex
	}
	
	@Required
	public void setMolecularSystem(MolecularSystem molecularSystem) {
		this.molecularSystem = molecularSystem
	}
	
	public MolecularSystem getMolecularSystem() {
		return molecularSystem
	}
}
