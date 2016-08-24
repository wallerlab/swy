package org.wallerlab.swy.service.fitness;

import org.springframework.context.annotation.Profile;

/**
 * This fitness function is the normalized version of
 * {@link org.wallerlab.swy.service.fitness.EnergyDifferenceFitness},
 * and can only return values between 0 (energy is worse
 * than or equal to the average) and 1 (energy is the
 * best found so far)
 * 
 * @author t_dres03
 */
@Profile({"unstable"})
public class NormalizedFitness extends RememberingFitnessCalculator {

	@Override
	protected double function(double energy) {
		return ((averageEnergy - energy) / (averageEnergy - bestEncounteredEnergy));
	}
	
}
