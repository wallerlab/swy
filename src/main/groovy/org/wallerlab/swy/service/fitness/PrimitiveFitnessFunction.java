package org.wallerlab.swy.service.fitness;

import org.springframework.context.annotation.Profile;

/**
 * This fitness function returns one for every valid
 * energy.
 * 
 * @author t_dres03
 */
@Profile({"unstable"})
public class PrimitiveFitnessFunction implements FitnessCalculator {

	@Override
	public double getFitness(double energy) {
		if (energy != Double.NaN) {
			return 1.0;
		} else {
			return 0.0;
		}
	}

}
