package org.wallerlab.swy.service.fitness;

import org.springframework.context.annotation.Profile;

/**
 * This fitness function is not normalised to return
 * values between 0 and 1. It simply takes the difference
 * of the actual energy to the average.
 * 
 * @author t_dres03
 */
@Profile({"unstable"})
public class EnergyDifferenceFitness extends RememberingFitnessCalculator {

	@Override
	protected double function(double energy) {
		return (averageEnergy - energy);
	}

}
