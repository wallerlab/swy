package org.wallerlab.swy.dao.flat

import org.wallerlab.swy.model.Coordinates
import org.wallerlab.swy.model.MolecularSystem


/**
 * XYZWriter is used to persist geometries in .xyz format.
 */
class XYZWriter {

	/**
	 * This class is supposed to NEVER be instanciated.
	 */
	private XYZWriter() {
	}
	
	static void writeXYZ(File file, MolecularSystem molecularSystem, Coordinates coordinates) {
		writeXYZ(file, coordinates, "  ")
	}

	static void writeXYZ(File file, MolecularSystem molecularSystem, Coordinates coordinates, String title) {
		file.append("${molecularSystem.numberOfAtomsTotal}\n${title}")
		for (int moleculeIndex in 0..molecularSystem.numberOfMolecules-1) {
			for (int atomIndex in 0..molecularSystem.numberOfAtomsInMolecule[moleculeIndex]-1) {
				file.append("\n${molecularSystem.atomLabel[moleculeIndex][atomIndex]} \
${String.format("%-18f", coordinates.xCoord[moleculeIndex][atomIndex])} \
${String.format("%-18f", coordinates.yCoord[moleculeIndex][atomIndex])} \
${String.format("%-18f", coordinates.zCoord[moleculeIndex][atomIndex])}")
			}
		}
		file.append("\n")
	}
}
