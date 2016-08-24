package org.wallerlab.swy.service.meta.ant;

import org.wallerlab.swy.annotations.Cite;
import org.wallerlab.swy.model.SearchSpace;
import org.wallerlab.swy.service.meta.SolutionGenerator;
import org.wallerlab.swy.service.utils.RandomNumberGenerator;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;

/**
 * This is the solution generator belonging to
 * the {@link org.wallerlab.swy.service.meta.ant.AntColonyOptimizer}.
 * It creates solutions randomly but with a specific
 * probability distribution provided by an
 * {@link org.wallerlab.swy.service.meta.ant.AntColony}.
 */
@Service
public class Ant implements SolutionGenerator {
	
	private AntColony antColony;

	private SearchSpace searchSpace;
	
	private RandomNumberGenerator randomNumberGenerator;
	
	private double[] solution;
		
	@Override
	public double[] generateSolution() {
		solution = new double[searchSpace.numberOfDimensions];
		for(int dimension=0; dimension < searchSpace.numberOfDimensions; dimension++){
			solution[dimension] = searchSpace.startValues[dimension] +
						(searchSpace.stepSize[dimension] * generateChoice(dimension));
		}
		return solution;
	}
	
	/**
	 * Random solutionComponent generator with respect to the probability caused
	 * by the pheromones. It uses a roulette wheel selection algorithm just like
	 * the cited ant algorithm.
	 */
	@Cite(authors={"Daeyaert, F", "de Jonge, M.", "Koymans, L.", "Vinkers, M."}, journal = "J Comput Chem",
			pages = "887-891", year = 2008)
	private double generateChoice(Integer dimension) {
		double rndNumber = randomNumberGenerator.getNewDouble();
		int counter = 0;
	
		/*
		 * Subtract the probability from a random number until the difference becomes smaller than
		 * the probability for THAT solution, where probability is based upon pheromone levels. 
		 * I.e. we are looping over "numberOfPossibleValues".
		 * For transformation into continuous solution space the 'rest' of the random number is
		 * taken to determine a certain value within the width of the solutions covered by that
		 * pheromone value.
		 */
		while (rndNumber > antColony.probabilityDistribution.get(dimension)[counter]) {
			rndNumber -= antColony.probabilityDistribution.get(dimension)[counter];
			counter++;
		}
		return ((double)counter+rndNumber/antColony.probabilityDistribution.get(dimension)[counter]);
	}

	@Required
	public void setAntColony(AntColony antColony) {
		this.antColony = antColony;
	}

	@Required
	public void setSearchSpace(SearchSpace searchSpace) {
		this.searchSpace = searchSpace;
	}

	@Required
	public void setRandomNumberGenerator(RandomNumberGenerator randomNumberGenerator) {
		this.randomNumberGenerator = randomNumberGenerator;
	}
}
