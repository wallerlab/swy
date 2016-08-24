package org.wallerlab.swy.service.energy.wrappers;

import org.wallerlab.swy.model.Coordinates;
import org.wallerlab.swy.model.MolecularSystem;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.annotation.Profile;

import org.apache.commons.io.FileUtils;

/**
 * This class should be the parent of all types
 * of external program wrappers which can generate an energy.
 * It is explicitly NOT tied to what kind of energy it is 
 *  (e.g. single point/optimization/out of an MD sim).
 */
public abstract class AbstractEnergyWrapper {
	
	protected String exeString;

	protected File scrDir;
	
	protected double energy;

	protected MolecularSystem molecularSystem;
	protected Coordinates coordinates;

	private Process proc;
	
	protected OutputStream stdOut;
	protected OutputStream errOut;
	
	protected synchronized void computeEnergy(){
		assert(coordinates)
		assert(exeString)
		
		scrDir = new File("scratch/${coordinates.hashCode()}");
		scrDir.mkdirs();
		
		writeInputFile();

		/** Execute */
		proc = exeString.execute(null, scrDir);
		proc.consumeProcessOutput(stdOut, errOut)
		proc.waitFor();

		/** Parse output: in case anything goes wrong the energy is set to NaN. */
		energy = Double.NaN
		if (!parseEnergy()) {
			// some warning should be thrown here
			energy=Double.NaN
		} else {
			assert(energy != Double.NaN)
		}
				
		return;
	}

	protected abstract void writeInputFile();
	
	/** Returns true if successful and false if not. */
	protected abstract boolean parseEnergy();
	
	/**
	 * Please don't forget to call this method!
	 * Is not called on the abstract level because some information might still be used.
	 */
	protected void tidyUp() {
		FileUtils.deleteDirectory(scrDir)
		proc.destroy()
	}
	
	@Required
	public void setMolecularSystem(MolecularSystem molecularSystem) {
		this.molecularSystem = molecularSystem
	}
}
