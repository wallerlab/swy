package org.wallerlab.swy.service.energy.wrappers

import org.wallerlab.swy.model.Coordinates
import org.wallerlab.swy.model.MolecularSystem
import org.wallerlab.swy.service.batch.modelpopulator.AgentModelPopulator;
import org.wallerlab.swy.service.energy.ModellingInterface;
import org.wallerlab.swy.service.utils.RandomNumberGenerator

import org.springframework.stereotype.Service
import org.springframework.context.annotation.Lazy
import org.springframework.context.annotation.Profile;

import java.io.RandomAccessFile
import java.util.regex.Matcher
import java.util.regex.Pattern
import org.codehaus.groovy.control.customizers.ImportCustomizer.Import

/**
 * This is the Gaussian Class for the Swy project
 * 
 */
@Lazy
@Service("gaussian")
@Profile(["unstable"])
public class Gaussian implements ModellingInterface {
	
	private String hamiltonian
	private double energy
	
	/**
	* Get Eneregy in a.u.
	*
	* @param coordinates
	*/
	public synchronized double getSinglePointEnergy(Coordinates coordinates) {

		switch(Input.method) {
			case ["AM1", "am1", "PM3", "pm3", "PM6", "pm6"]:
				hamiltonian = Input.method
			break
			
			default:
				hamiltonian = "${Input.method}/${Input.basis}"
			break
		}
		
		//write a .com file
		new File(scrDir+"Swy.com").delete()
		File w =new File(scrDir+"Swy.com")
		w.append("%CHK=Swy.chk  \n ")
		w.append("%MEM=900Mb \n ")
		w.append("# ${hamiltonian}\n\n")
		w.append("Swy single point calculation\n\n")
		w.append("${Input.charge} ${Input.multi}\n")
		for (i in 0..MolecularSystem.numberOfMolecules-1){
			for (j in 0..MolecularSystem.numberOfAtomsInMolecule[i]-1){
				w.append("""${MolecularSystem.atomLabel[i][j]}  ${coordinates.xCoord[i][j]} ${coordinates.yCoord[i][j]} ${coordinates.zCoord[i][j]} \n """)
			}
		}

		//run Gaussian
		def exec=""" ${System.getenv("GAUSSIAN_EXE")} Swy.com Swy.log  """.execute(null, new File("${scrDir}"))
		exec.waitFor()
		exec.destroy()
		new File("Swy.chk").delete()

		boolean energyFound = false
		String[] outFile = new File(scrDir+"Swy.log").readLines()
		for (String line in outFile) {
			if(line =~ "SCF Done:"){
				try{
					energy = line.stripIndent().split()[4].toFloat()
					if (energy <= 100000) {
						energyFound = true
						break
					}
				}catch(e){
					//log.debug("${e}")
					energy = Double.NaN
				}
			}
		}

		if (!energyFound) {
			energy = Double.NaN
		}
		
		println ("${this}: energy calculated. E=${energy}")
		
		return energy
	}
}

