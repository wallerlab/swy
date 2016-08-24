package org.wallerlab.swy.service.fitness;

/**
 * Every more complex fitness function needs
 * information about its history, meaning what
 * energies have already been there. This class
 * provides some handy values for that purpose
 * and also makes sure that only positive (or 0)
 * fitness values are generated.
 * 
 * @author t_dres03
 */
public abstract class RememberingFitnessCalculator implements FitnessCalculator {

	protected double sumOfAllEnergies;
	protected double bestEncounteredEnergy = Double.NaN;
	protected double averageEnergy;
	private int timesCalled;
	
	private void updateMemory(double newEnergy) {
		if (newEnergy != Double.NaN) {
			timesCalled++;
			sumOfAllEnergies+=newEnergy;
			if(newEnergy <= bestEncounteredEnergy || Double.isNaN(bestEncounteredEnergy)) {
				bestEncounteredEnergy = newEnergy;
			}
			averageEnergy=sumOfAllEnergies/timesCalled;
		}
	}
	
	@Override
	public final double getFitness(double energy) {
		if (!Double.isNaN(energy)) {
			updateMemory(energy);
			return function(energy);
		} else {
			return 0.0;
		}
	}
	
	protected abstract double function(double energy);
}
