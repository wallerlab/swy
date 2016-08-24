package org.wallerlab.swy.service.conformation.analyser

import org.wallerlab.swy.model.Coordinates
import org.wallerlab.swy.model.MolecularSystem
import org.wallerlab.swy.service.conformation.PeriodicTable

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service

/**
 * A general class to analyze the geometry.
 */
@Service
@Profile(["untested"])
class Geometry {

	/** The default constructor is private because this class is not supposed to be instanciated.*/
	private Geometry() {
	}
	
	/**
	 * 
	 * @param coordinates
	 * @param moleculeIndex
	 * @return an array with the xyz coordinates of the centerOfMass
	 */
	public static double[] centerOfMass(MolecularSystem molecularSystem, \
			Coordinates coordinates, int moleculeIndex) {
		double[] returnValue = new double[3]
		double totalMass = 0
		double thisAtomsMass
		
		for (int coordinate in 0..2) {
			returnValue[coordinate] = 0
		}
		for (int atomIndex in 0..molecularSystem.numberOfAtomsInMolecule[moleculeIndex]-1) {
			thisAtomsMass = PeriodicTable.getMass(molecularSystem.atomLabel[moleculeIndex][atomIndex])
			returnValue[0] += coordinates.xCoord[moleculeIndex][atomIndex] * thisAtomsMass
			returnValue[1] += coordinates.yCoord[moleculeIndex][atomIndex] * thisAtomsMass
			returnValue[2] += coordinates.zCoord[moleculeIndex][atomIndex] * thisAtomsMass
			totalMass += thisAtomsMass
		}
		for (int coordinate in 0..2) {
			returnValue[coordinate] = returnValue[coordinate] / totalMass
		}
		
		return returnValue
	}
	
}
