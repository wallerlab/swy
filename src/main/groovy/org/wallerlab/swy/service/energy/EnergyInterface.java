package org.wallerlab.swy.service.energy;

public interface EnergyInterface {
	
	/**
	 * This method is called whenever anywhere in the program
	 * an energy is requested.
	 * 
	 * @param solution All choices that have been made for one solution.
	 * Normally (not necessarily) a conformation is generated out of this.
	 * @return An 'energy' value; the unit can vary a lot or be arbitrary.
	 */
	double getEnergy(double[] solution);

}