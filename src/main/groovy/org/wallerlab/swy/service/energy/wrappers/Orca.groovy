package org.wallerlab.swy.service.energy.wrappers

import org.wallerlab.swy.model.Coordinates
import org.wallerlab.swy.model.MolecularSystem
import org.wallerlab.swy.service.batch.modelpopulator.AgentModelPopulator
import org.wallerlab.swy.service.energy.ModellingInterface

import org.springframework.stereotype.Service
import org.springframework.context.annotation.Lazy
import org.springframework.context.annotation.Profile

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Orca wrapper
 */
@Lazy
@Service("Orca")
@Profile(["unstable"])
public class Orca implements ModellingInterface{
	
	private String scrDir
	private Coordinates coordinates
	private double energy
	
	/**
	 * Get Energy in a.u.
	 *
	 * @param Coordinates coordinates
	 */
	public synchronized double getSinglePointEnergy(Coordinates coordinates) {	
		
		this.scrDir = scrDir
		this.coordinates = coordinates
		createInputOrca()
		executeOrca()
		cleanUpOrca()
		
		// println ("${this}: energy calculated. E=${energy}")
		return energy;
	}

	public void createOrcaInput(){
		// Create input file for Orca
		new File(scrDir+"Swy.inp").delete()
		File w =new File(scrDir+"Swy.inp")
		// TODO what about basis set?
		w.append( """! ${Input.method}\n  * xyz ${Input.charge} ${Input.multi}\n """ )
		for (i in 0..MolecularSystem.numberOfMolecules-1){
			for (j in 0..MolecularSystem.numberOfAtomsInMolecule[i]-1){
				w.append(""" ${MolecularSystem.atomLabel[i][j]}  ${coordinates.xCoord[i][j]} ${coordinates.yCoord[i][j]} ${coordinates.zCoord[i][j]} \n """)
			}
		}
		w.append("*")
	}

	public void executeOrca(){
		StringBuffer sout = new StringBuffer()
		StringBuffer serr = new StringBuffer()
		def soutarray = []
		def proc = "${System.getenv("ORCA_EXE")} Swy.inp  ".execute(null, new File(scrDir))
		
		// Extract Energy
		proc.consumeProcessOutput(sout, serr)
		proc.waitFor()
		boolean energyFound = false
		sout.eachLine { line ->
			soutarray = line.split(" ")
			if(soutarray.grep(~/^FINAL.*/).size() > 0){
				soutarray = soutarray.reverse()
				// perhaps this is better?
				//Conformation.energy = soutarray.last()
				energy = soutarray[0].toFloat()
				//println """ The Orca energy is : ${energy} """
				energyFound = true
			}
		}	
		if (!energyFound) {
			energy = Double.NaN
		}
	}

	public void cleanupOrca(){
		def deleteFiles = ["start","Swy.gbw","Swy.ges","Swy.prop","Swy.log","Swy.results","results"]
		for (i in 0..deleteFiles.size()-1){"""rm ${scrDir+deleteFiles[i]}""".execute().text }
		
	}
}
