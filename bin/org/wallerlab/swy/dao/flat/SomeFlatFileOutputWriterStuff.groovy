package org.wallerlab.swy.dao.flat

import org.wallerlab.swy.service.conformation.factory.DihedralConformation

import org.springframework.stereotype.Repository
import org.springframework.context.annotation.Profile

/**
 * A dump to store data that will be maybe needed sometime...later.
 * 
 */
@Repository
@Profile(["unstable"])
class SomeFlatFileOutputWriterStuff {
	
	// TODO methods to write out data of the running calculation to files. 
	// Should be somewhat different.
	File dataOutputFile

	void prepareDataOutput(int swyId) {
		dataOutputFile = new File("./${Input.swyName}Output/${swyId}.csv")
		// Clear file
		dataOutputFile.write("")
		for (dimension in 0..SearchSpace.numberOfDimensions-1) {
			dataOutputFile.append("${SearchSpace.caption[dimension]}, ")
		}
		dataOutputFile.append("Energy, ")
		dataOutputFile.append("\n")
	}
	
	void writeOutSolution(int swyId) {
		dataOutputFile = new File("./${Input.swyName}Output/${swyId}.csv")
		for (dimension in 0..SearchSpace.numberOfDimensions-1) {
			dataOutputFile.append("${Conformation.solution[dimension]}, ")
		}
		dataOutputFile.append("${Conformation.energy}, ")
		for (dimension in 0..SearchSpace.numberOfDimensions-1) {
			// TODO This is sooooooooooo wrong. But how to work around?
			dataOutputFile.append("${Conformation.pheromones[dimension]}, ")
		}
		dataOutputFile.append("\n")
	}

	// TODO completely wrong!
	void writeOutStructure(int swyId) {
		DihedralConformation dihedralConformation = new DihedralConformation()
		dihedralConformation.newConformation()
		//		CompleteMoleculeManipulation completeMoleculeManipulation = new CompleteMoleculeManipulation()
		//		completeMoleculeManipulation.newConformation()
		File xyzFile = new File("./${Input.swyName}Output/${swyId}.xyz")
		xyzFile.write("${MolecularSystem.numberOfAtomsTotal}\nEnergy=${Conformation.energy}")
		for (int molecule in 0..MolecularSystem.numberOfMolecules-1) {
			for (int atom in 0..MolecularSystem.numberOfAtomsInMolecule[molecule]-1){
				xyzFile.append("""\n${MolecularSystem.atomLabel[molecule][atom]} 
									 ${String.format("%-18f", MolecularSystem.xCoord[molecule][atom])}
									 ${String.format("%-18f", MolecularSystem.yCoord[molecule][atom])}
									 ${String.format("%-18f", MolecularSystem.zCoord[molecule][atom])}""")
			}
		}
	}
}
