package org.wallerlab.swy.service.energy.wrappers

import org.wallerlab.swy.model.Coordinates
import org.wallerlab.swy.service.energy.OptimizationInterface;
import org.wallerlab.swy.dao.flat.XYZWriter

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.context.annotation.Lazy
import org.springframework.context.annotation.Profile;

/**
 * Dftb+ wrapper
 * Returns energy values in atomic units.
 */
@Lazy
@Service("dftbPlus")
@Profile(["unstable"])
class DftbPlus extends AbstractEnergyWrapper implements OptimizationInterface{

	private String optimizeAttachment
	
	// TODO charge and multiplicity are ignored atm
/*	@Value('${swy.multiplicity}')
	private int multi

	@Value('${swy.charge}')
	private int charge
*/
	public DftbPlus() {
		exeString="""chmod +x ./runDftb.scr
./runDftb.scr"""
	}
	
	public synchronized double getSinglePointEnergy(Coordinates coordinates) {
		this.coordinates = coordinates
		optimizeAttachment=""
		computeEnergy()
		tidyUp()
		return energy
	}

	public synchronized double optimizeStructure(Coordinates coordinates) {
		this.coordinates = coordinates
		optimizeAttachment="""
echo " Driver = ConjugateGradient{" >> dftb_in.hsd
echo "  MaxSteps = 10000" >> dftb_in.hsd
echo " }" >> dftb_in.hsd"""
		computeEnergy()
		parseOptimizedCoordinates()
		return energy
	}
	
	@Override
	protected void writeInputFile() {
		/* create xyz file */
		new File("Swy.xyz", scrDir).delete()
		File w =new File("Swy.xyz", scrDir)
		XYZWriter.writeXYZ(molecularSystem, w, coordinates, "DFTB+ calculation for swy")

		/* Create dftb+ script */
		File script = new File("runDftb.scr", scrDir)
		// TODO could be parallelized here (OMP...)
		script.write("""echo dftb > INPUT
${System.getenv("GCOORD_EXE")} < ./Swy.xyz
${optimizeAttachment}
export OMP_NUM_THREADS=1
${System.getenv("DFTBPLUS_EXE")}
""")
	}

	@Override
	protected boolean parseEnergy() {
		String outLine
		stdOut.eachLine { line ->
			outLine = line.stripIndent().toString()
			// TODO ...really take the 'total energy'? and not some free energy??
			if(outLine =~ "Total Energy"){
				try{
					energy = outLine.split().last().toFloat()
					return true
				}catch(e){
					return false
				}
			}else if(outLine =~ "ERROR"){
				return false
			}
		}
		return false;
	}
	
	private void parseOptimizedCoordinates() {
		// TODO not implemented
	}
	
	public Coordinates getOptimizedCoordinates() {
		return coordinates
	}
}
