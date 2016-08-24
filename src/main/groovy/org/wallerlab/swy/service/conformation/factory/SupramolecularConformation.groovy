package org.wallerlab.swy.service.conformation.factory

import org.wallerlab.swy.model.Coordinates
import org.wallerlab.swy.model.MolecularSystem
import org.wallerlab.swy.service.utils.Maths
import org.wallerlab.swy.service.conformation.analyser.Geometry
import org.wallerlab.swy.service.conformation.manipulation.TurnAroundAxis
import org.wallerlab.swy.service.modeltools.CoordinateFactory

import org.springframework.beans.factory.annotation.Required
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

/**
* Supramolecular Conformation class to create new Coordinates
* by varying the translation vector and rotation (Euler) angles.
*/
@Service
@Profile(["untested"])
class SupramolecularConformation implements ConformationInterface{
	
	private MolecularSystem molecularSystem

	private Coordinates coordinates
	
	private int listIndex
	
	private boolean adjustPositions = false
	private boolean adjustAngles = false
	
	private double[] solution
	 
	/**
	 * Create conformation
	 * 
	 * @param solution An array containing the values how far
	 * to move / how much to rotate the molecules
	 */
	public synchronized Coordinates newConformation(double[] solution){	  
		coordinates = CoordinateFactory.newCoordinates(molecularSystem)
		this.solution = solution
		
		listIndex = 0
		if (adjustPositions) {
			for (moleculeIndex in 1..molecularSystem.numberOfMolecules-1){
				listIndex = moveMolecule(listIndex, moleculeIndex)
			}
		}
		if (adjustAngles) {
			for (moleculeIndex in 1..molecularSystem.numberOfMolecules-1){
				listIndex = rotateMolecule(listIndex, moleculeIndex)
			}
		}
		return coordinates	
	}
	
	/**
	 * translates molecules
	 * 
	 * @param listIndex Where in solution to start looking for the
	 * translation vector
	 * @param moleculeIndex
	 * 
	 * @return The new listIndex, meaning where in the solution array
	 * to find the next entry. 
	 */
	private int moveMolecule(int listIndex, int moleculeIndex) {
		for (int atomIndex in 0..molecularSystem.numberOfAtomsInMolecule[moleculeIndex]-1) {
			coordinates.xCoord[moleculeIndex][atomIndex] += solution[listIndex]
			coordinates.yCoord[moleculeIndex][atomIndex] += solution[listIndex+1]
			coordinates.zCoord[moleculeIndex][atomIndex] += solution[listIndex+2]
		}
		return listIndex+3
	}
	/**
	 * rotates molecules
	 * 
	 * @param listIndex Where in solution to start looking for the
	 * rotation angles
	 * @param moleculeIndex
	 * 
	 * @return The new listIndex, meaning where in the solution array
	 * to find the next entry. 
	 */
	private int rotateMolecule(int listIndex, int moleculeIndex) {
		double[] turnVector = [1.0, 0.0, 0.0]
		double[][] turnMatrix = Maths.generateTurnMatrix(turnVector, solution[listIndex])
		turnVector = [0.0, 1.0, 0.0]
		turnMatrix = Maths.matrixMultiply( turnMatrix, Maths.generateTurnMatrix(turnVector, solution[listIndex+1]) )
		turnVector = [0.0, 0.0, 1.0]
		turnMatrix = Maths.matrixMultiply( turnMatrix, Maths.generateTurnMatrix(turnVector, solution[listIndex+2]) )
		TurnAroundAxis.turnAroundAxis(molecularSystem, coordinates, moleculeIndex, turnMatrix, \
			Geometry.centerOfMass(molecularSystem, coordinates, moleculeIndex))
		return listIndex+3
	}
	
	@Required
	public void setMolecularSystem(MolecularSystem molecularSystem) {
		this.molecularSystem = molecularSystem
	}
	
	public MolecularSystem getMolecularSystem() {
		return molecularSystem
	}
}