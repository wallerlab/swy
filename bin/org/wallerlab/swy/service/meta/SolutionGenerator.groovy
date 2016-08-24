package org.wallerlab.swy.service.meta;

/**
 * Whenever an energy is supposed to be calculated, a
 * solution is needed. Every metaHeuristic algorithm of
 * this program goes along with a way to generate solutions.
 * 
 * @author t_dres03
 */
public interface SolutionGenerator {

	public double[] generateSolution();
	
}
