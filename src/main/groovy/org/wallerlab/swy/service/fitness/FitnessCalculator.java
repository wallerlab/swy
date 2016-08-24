package org.wallerlab.swy.service.fitness;

/**
 * FitnessCalculators can transform energy values into
 * fitness values. The fitness is a score (normally) between
 * 0 (bad) and 1 (perfect) which makes it easy to compare
 * different energy values in a context.<br>
 * Better (more negative) energies should produce a higher
 * fitness. The fitness should NOT be influenced much by
 * features of the energy like order of magnitude or range.
 */
public interface FitnessCalculator {

	public double getFitness (double energy);
	
}
