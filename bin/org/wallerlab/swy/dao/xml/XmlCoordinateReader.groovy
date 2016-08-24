package org.wallerlab.swy.dao.xml

import org.wallerlab.swy.dao.SwyMoleculeDao
import org.wallerlab.swy.model.SearchSpace
import org.wallerlab.swy.model.MolecularSystem

import org.springframework.stereotype.Repository
import org.springframework.context.annotation.Profile
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value

import groovy.sql.Sql
import javax.sql.DataSource

/**
 *  Read in the molecular system from an xml file
 *  e.g. for development mode or off-line jobs.
 *  
 *  @NeededProperties
 * <b>swy.xmlMoleculeFile</b> - The path to the molecule input file in xml(/cml) format.<br>
 * <b>default:</b> no default specified<br><br>
 */
@Repository
@Profile(["untested"])
class XmlCoordinateReader implements SwyMoleculeDao {

	@Value('${swy.xmlMoleculeFile}')
	private String inputLocation
	
	private def parsedInputFile
	
	private MolecularSystem molecularSystem
	/**
	 * xml file parser 
	 * @param location of the file.
	 */
	public MolecularSystem readIn() {
		assert(inputLocation)
		File infile = new File(inputLocation)

		molecularSystem = new MolecularSystem()
		
		parsedInputFile = new XmlSlurper().parse(infile)
		if(parsedInputFile.molecule.size() > 1){
			molecularSystem.numberOfMolecules = parsedInputFile.molecule.size()
			molecularSystem.numberOfAtomsInMolecule = new int[molecularSystem.numberOfMolecules]
			molecularSystem.numberOfBondsInMolecule = new int[molecularSystem.numberOfMolecules]
			molecularSystem.bonds.ensureCapacity(molecularSystem.numberOfMolecules)
			for (int moleculeIndex in 0..molecularSystem.numberOfMolecules - 1) {
				readInMolecule(moleculeIndex, parsedInputFile.molecule[moleculeIndex])
			}
		}else{
			molecularSystem.numberOfMolecules = 1
			molecularSystem.numberOfAtomsInMolecule = new int[1]
			molecularSystem.numberOfBondsInMolecule = new int[1]
			molecularSystem.bonds.ensureCapacity(1)
			readInMolecule(0, parsedInputFile)
		}

		println ("${this}: numberOfAtomsInMolecule = ${molecularSystem.numberOfAtomsInMolecule}")
		molecularSystem.numberOfAtomsTotal=  molecularSystem.numberOfAtomsInMolecule.toList().sum()
		
		return molecularSystem
	}

	/**
	 * Method to get atomLabels and Coordinates from the xml file
	 */
	private readInMolecule(int moleculeIndex, def moleculeInXml) {
		/* Loop over the coordinates */
		molecularSystem.numberOfAtomsInMolecule[moleculeIndex]=moleculeInXml.atomArray.atom.size()
		molecularSystem.atomLabel[moleculeIndex] = moleculeInXml.atomArray.atom.@elementType.toString().toList()
		molecularSystem.xCoordInitial[moleculeIndex] = (double[])moleculeInXml.atomArray.atom.@x3.collect{it.toFloat()}
		molecularSystem.yCoordInitial[moleculeIndex] = (double[])moleculeInXml.atomArray.atom.@y3.collect{it.toFloat()}
		molecularSystem.zCoordInitial[moleculeIndex] = (double[])moleculeInXml.atomArray.atom.@z3.collect{it.toFloat()}

		/* prepareBondList */
		molecularSystem.bonds[moleculeIndex] = new ArrayList(molecularSystem.numberOfAtomsInMolecule[moleculeIndex])
		parseBondInformation(moleculeIndex, moleculeInXml)
	}

	/**
	 * Method to create the molecularSystem.bonds list out of the .xml input file
	 */
	public void parseBondInformation(int moleculeIndex, def moleculeInXml) {
		int[] formattedBond = new int[2]
		// Fill the bond list with all the bonds
		for (def inputBond in moleculeInXml.bondArray.bond) {
			formattedBond = getFormattedBond(inputBond)
			// Put the information that atom formattedBond[0] is bond to formattedBond[1] into the list of bonds...
			if (!molecularSystem.bonds[moleculeIndex][ formattedBond[0] ]) {
				molecularSystem.bonds[moleculeIndex][ formattedBond[0] ] = []
			}
			molecularSystem.bonds[moleculeIndex][ formattedBond[0] ].add(formattedBond[1])
			// ...and vice versa to be able to access the bond information from both sides of a bond.
			if (!molecularSystem.bonds[moleculeIndex][ formattedBond[1] ]) {
				molecularSystem.bonds[moleculeIndex][ formattedBond[1] ] = []
			}
			molecularSystem.bonds[moleculeIndex][ formattedBond[1] ].add(formattedBond[0])
		}
		/* Sort the bond list and get the total number of Bonds */
		molecularSystem.numberOfBondsInMolecule[moleculeIndex] = 0
		for (def bondsWithThisStartingAtom in molecularSystem.bonds[moleculeIndex]) {
			bondsWithThisStartingAtom.sort()
			/*
			 * TODO atm all bonds are double counted (because of the necessary redundancy).
			 * Check whether the resulting values for numberOfBondsInMolecule can be safely
			 * halved.
			 */
			molecularSystem.numberOfBondsInMolecule[moleculeIndex] += bondsWithThisStartingAtom.size()
		}
	}

	/**
	 * Method to turn the bond information of the .xml file into an integer format
	 */
	private int[] getFormattedBond(def inputBond) {
		int[] formattedBond = new int[2]
		String[] bondInStringFormat = inputBond.@atomRefs2.toString().tokenize()
		for (int i in 0..1){
			formattedBond[i] = (bondInStringFormat[i] - "a").toInteger() - 1
		}
		return formattedBond
	}


	/**
	 * Method to write out the domain model in xml file format.
	 * TODO not in a working state.
	 */
	public void writeOut(String location)  {
		def fw = new FileWriter("./${Input.swyName}Output/output.xml")
		def xml = new groovy.xml.MarkupBuilder(fw)
		xml.swy(id:'$Input.swyId'){
			resultList.each {thisResult ->
				result() {
					solution("${thisResult.solution.toString()}")
					energy("${thisResult.energy}")
				}
			}
		}
	}
	
	public void setInputLocation(String inputLocation) {
		this.inputLocation = inputLocation
	}
}
