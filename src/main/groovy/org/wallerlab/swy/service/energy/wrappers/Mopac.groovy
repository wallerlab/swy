package org.wallerlab.swy.service.energy.wrappers

import org.wallerlab.swy.model.Coordinates
import org.wallerlab.swy.model.MolecularSystem
import org.wallerlab.swy.service.batch.modelpopulator.AgentModelPopulator
import org.wallerlab.swy.service.energy.ModellingInterface
import org.wallerlab.swy.service.energy.OptimizationInterface

import org.springframework.beans.factory.annotation.Required
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Lazy
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

/**
 * Mopac wrapper.<br>
 * Is capable of singlePoint energy calculation as well as structural optimization.
 * Output values are in eV. Designed for mopac2009.<br>
 * Although mopac always makes frequency calculations as well and provides a 'heat of
 * formation' the total energy is used, because it is generally not the case that the
 * structures are in a local minimum, so the frequency calculations will be erroneous.
 * 
 * @NeededProperties
 * <b>swy.method</b> - The hamiltonian to use.<br>
 * <b>possible choices:</b> Please look into the mopac manual for the provided possibilities.<br>
 * <b>default:</b> <i>PM6-DH+</i><br><br>
 * 
 * <b><i><font color=purple>int</font></i> swy.charge</b> - The charge of the whole molecular system.<br>
 * <b>limits:</b> Every integer number should be ok. Is of course determined
 * by the molecular system. (High) charges can generally cause problems, so think of
 * counterions when having high charges. If not specified, no charge is assumed.<br>
 * <b>default:</b> <i>0</i><br><br>
 * 
 * <b><i><font color=purple>int</font></i> swy.multiplicity</b> - The multiplicity of
 * the whole molecular system.<br>
 * <b>limits:</b> 1, 2 or 3. If not specified or something different is provided a
 * singlet (1) state is assumed. Please contact us if you want to calculate systems
 * with higher multiplicity.<br>
 * <b>default:</b> <i>1</i><br><br>
 */
@Lazy
@Service("mopac")
@Profile(["untested"])
public class Mopac extends AbstractEnergyWrapper implements OptimizationInterface{
	
	public Mopac() {
		exeString="${System.getenv("MOPAC_EXE")} Swy.mop"
	}

	@Value('${swy.method}')
	private String method
	
	@Value('${swy.charge}')
	private int charge
	
	@Value('${swy.multiplicity}')
	private int multi

	private String[] outFile;
	
	private String multiString;
	private String singlePointString;
		
	public synchronized double getSinglePointEnergy(Coordinates coordinates) {
		this.coordinates = coordinates
		singlePointString = "1SCF"
		computeEnergy()
		tidyUp()
		return energy
	}
	
	public synchronized double optimizeStructure(Coordinates coordinates) {
		this.coordinates = coordinates
		singlePointString = ""
		computeEnergy()
		parseOptimizedCoordinates()
		tidyUp()
		return energy
	}
	
	@Override
	protected void writeInputFile() {
		/** set dummy variables */
		switch(multi){
		  case "1":
			multiString = "SINGLET"
		  break
		  case "2":
			multiString = "DOUBLET"
		  break
		  case "3":
			multiString = "TRIPLET"
		  break
		  default:
			//log.warn("WARNING: strange multiplicity in MOPAC calculation - setting to SINGLET")
			multiString = "SINGLET"
		  break
		}
		
		new File("Swy.mop", scrDir).delete()
		File w =new File("Swy.mop", scrDir)
		w.append( """${method} ${singlePointString} CHARGE=${charge} ${multiString} NOMM GEO-OK PRECISE\n\nSingle point\n""" )
		for (molecule in 0..molecularSystem.numberOfMolecules-1){
			for (atom in 0..molecularSystem.numberOfAtomsInMolecule[molecule]-1){
				w.append(""" ${molecularSystem.atomLabel[molecule][atom]}  ${coordinates.xCoord[molecule][atom]} ${coordinates.yCoord[molecule][atom]} ${coordinates.zCoord[molecule][atom]} \n """)
			}
		}
	}

	@Override
	protected boolean parseEnergy() {
		outFile = new File("Swy.out", scrDir).readLines()
		for (String line in outFile) {
			// ...really take the 'total energy'? and not some free energy??..... yes...
			if(line =~ "TOTAL ENERGY"){
				try{
					energy = line.stripIndent().split()[-2].toDouble()	// WARNING: ENERGY in EV!!!!
					if (energy <= 0) {
						return true
					}
				}catch(e){
					return false
				}
			}
		}
		return false
	}

	private void parseOptimizedCoordinates() {
		int coordsStartAtLineNumber
		for (int lineNumber in 0..outFile.length()-1) {
			if (outFile[lineNumber] =~ "CARTESIAN COORDINATES") {
				coordsStartAtLineNumber = lineNumber+4
				break
			}
		}
		for (int atomNumber in 0..molecularSystem.numberOfAtomsTotal-1 ) {
			String[] line = outFile[atomNumber+coordsStartAtLineNumber].stripIndent().split()
			coordinates.xCoord[atomNumber] = line[3]
			coordinates.yCoord[atomNumber] = line[4]
			coordinates.zCoord[atomNumber] = line[5]
		}
	}
		
	@Override
	public Coordinates getOptimizedCoordinates() {
		return coordinates;
	}
}

