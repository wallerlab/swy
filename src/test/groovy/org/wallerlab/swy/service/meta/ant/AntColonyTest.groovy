package org.wallerlab.swy.service.meta.ant;

import org.wallerlab.swy.model.ProblemRepresentation;
import org.wallerlab.swy.model.SearchSpace
import org.wallerlab.swy.model.main.Agent
import org.wallerlab.swy.service.modeltools.SearchSpaceDiscretiser;

import spock.lang.*

public class AntColonyTest extends Specification{
	private AntColony antColony
	
	private AntColonyOptimizer antColonyOptimizer
	private SearchSpace searchSpace
	
	private double[] iterationBestSolution
	private double[] globalBestSolution
	private ArrayList<double[]> tau
	private ArrayList<double[]> expectedTau
	private ArrayList<double[]> expectedProbabilityDistribution
	private boolean isConverged
	private double alpha
	
	@Unroll
	def "ant colony initialisation test" () {
		given:
		antColony = new AntColony()
		antColonyOptimizer = new AntColonyOptimizer()
		searchSpace = new SearchSpace()
		searchSpace.numberOfDimensions = 3
		searchSpace.startValues = [0.0, -1.0, 1.0]
		searchSpace.endValues = [2.0, 1.0, 5.0]
		searchSpace.periodic = [true, true, false]
		searchSpace.stepSize = [0.5, 0.5, 1.0]
		ProblemRepresentation problemRepresentation = new ProblemRepresentation()
		problemRepresentation.setSearchSpace(searchSpace)
		antColonyOptimizer.setProblemRepresentation(problemRepresentation)
		antColony.setMetaInstance(antColonyOptimizer)
		antColony.setSearchSpaceDiscretiser(new SearchSpaceDiscretiser(searchSpace))
		
		antColony.initialize()
		
		expect:
		antColony.averageNumberOfPossibleValues == 4.0
		antColony.numberOfPossibleValues == (int[])[4, 4, 4]
		antColony.tau == (ArrayList<double[]>)[[1.0, 1.0, 1.0, 1.0], [1.0, 1.0, 1.0, 1.0], [1.0, 1.0, 1.0, 1.0]]
		antColony.tauSum.length == 3
	}
	
	@Unroll
	def "pheromone update test (part one: until evaporation)" () {
		
		given:
		antColony = new AntColony()
		antColonyOptimizer = new AntColonyOptimizer()
		Agent globalBestAgent = new Agent()
		globalBestAgent.fitness = 0.8
		globalBestAgent.lastReasonableSolution = globalBestSolution
		Agent iterationBestAgent = new Agent()
		iterationBestAgent.fitness = 0.5
		iterationBestAgent.lastReasonableSolution = iterationBestSolution
		
		antColonyOptimizer.globalBestAgent = globalBestAgent
		antColonyOptimizer.iterationBestAgent = iterationBestAgent
		antColonyOptimizer.setMaximumProbabilityToChooseGlobalBest(0.512)
		antColonyOptimizer.setPheromoneResistance(0.5)
		antColonyOptimizer.setGlobalPercentage(0.25)
		antColonyOptimizer.setTrailWidth(0.1)
		antColony.setMetaInstance(antColonyOptimizer)
		searchSpace = new SearchSpace()
		searchSpace.numberOfDimensions = 3
		searchSpace.startValues = [0.0, -1.0, 1.0]
		searchSpace.endValues = [2.0, 1.0, 5.0]
		searchSpace.periodic = [true, true, false]
		searchSpace.stepSize = [0.5, 0.5, 1.0]
		antColony.searchSpace = searchSpace
		antColony.setSearchSpaceDiscretiser(new SearchSpaceDiscretiser(searchSpace))
		antColony.numberOfPossibleValues = [4, 4, 4]
		antColony.averageNumberOfPossibleValues = 4
		antColony.pheromonesInitialized = true
		antColony.tau = new ArrayList<double[]>(3)
		antColony.tau.add((double[])[1.0, 1.0, 1.0, 1.0])
		antColony.tau.add((double[])[1.0, 1.0, 1.0, 1.0])
		antColony.tau.add((double[])[1.0, 1.0, 1.0, 1.0])
		antColony.updateTauMinMax()
		antColony.evaporatePheromones()
		
		expect:
		antColony.tauMax == 2.5
		antColony.tauMin.round(8) == 0.20833333
		antColony.tau == expectedTau
		
		cleanup:
		antColony = null
		antColonyOptimizer = null
		iterationBestSolution = null
		globalBestSolution = null
		
		where:
		iterationBestSolution	|	globalBestSolution	|	expectedTau
		[0.0, 0.0, 3.0]			|	[0.0, 0.0, 3.0]		|	[[0.5, 0.5, 0.5, 0.5], [0.5, 0.5, 0.5, 0.5], [0.5, 0.5, 0.5, 0.5]]
		
	}	
	
	/**
	 * The expected results from this test are just copied from what the actual
	 * results are, due to too high complexity of the tau generation. But the
	 * numbers have been checked for correct treatment of periodicity, for
	 * global/iteration weighting, for strong decay and equality of same distances.
	 */
	@Unroll
	def "pheromone update test (part two: put pheromones)" () {
		
		given:
		antColony = new AntColony()
		antColonyOptimizer = new AntColonyOptimizer()
		Agent globalBestAgent = new Agent()
		globalBestAgent.fitness = 0.8
		globalBestAgent.lastReasonableSolution = globalBestSolution
		Agent iterationBestAgent = new Agent()
		iterationBestAgent.fitness = 0.5
		iterationBestAgent.lastReasonableSolution = iterationBestSolution
		
		antColonyOptimizer.globalBestAgent = globalBestAgent
		antColonyOptimizer.iterationBestAgent = iterationBestAgent
		antColonyOptimizer.setMaximumProbabilityToChooseGlobalBest(0.512)
		antColonyOptimizer.setPheromoneResistance(0.5)
		antColonyOptimizer.setGlobalPercentage(0.25)
		antColonyOptimizer.setTrailWidth(0.1)
		antColony.setMetaInstance(antColonyOptimizer)
		searchSpace = new SearchSpace()
		searchSpace.numberOfDimensions = 3
		searchSpace.startValues = [0.0, -1.0, 1.0]
		searchSpace.endValues = [2.0, 1.0, 5.0]
		searchSpace.periodic = [true, true, false]
		searchSpace.stepSize = [0.5, 0.5, 1.0]
		antColony.searchSpace = searchSpace
		antColony.setSearchSpaceDiscretiser(new SearchSpaceDiscretiser(searchSpace))
		antColony.numberOfPossibleValues = [4, 4, 4]
		antColony.averageNumberOfPossibleValues = 4
		antColony.pheromonesInitialized = true
		antColony.tau = new ArrayList<double[]>(3)
		antColony.tau.add((double[])[1.0, 1.0, 1.0, 1.0])
		antColony.tau.add((double[])[1.0, 1.0, 1.0, 1.0])
		antColony.tau.add((double[])[1.0, 1.0, 1.0, 1.0])
		antColony.updatePheromones()
				
		expect:
		antColony.tauMax == 2.5
		antColony.tauMin.round(8) == 0.20833333
		antColony.tau == expectedTau
		
		cleanup:
		antColony = null
		antColonyOptimizer = null
		iterationBestSolution = null
		globalBestSolution = null
		
		where:
		iterationBestSolution	|	globalBestSolution	|	expectedTau
		[0.0, 0.0, 3.0]			|	[0.0, 0.0, 3.0]		|	[[1.550232240986876, 0.5020274251736129, 0.5020274251736129, 1.550232240986876], [0.5020274251736129, 1.550232240986876, 1.550232240986876, 0.5020274251736129], [0.5020274251736129, 1.550232240986876, 1.550232240986876, 0.5020274251736129]]
		[1.0, -1.0, 1.0]		|	[1.0, -1.0, 1.0]	|	[[0.5020274251736129, 1.550232240986876, 1.550232240986876, 0.5020274251736129], [1.550232240986876, 0.5020274251736129, 0.5020274251736129, 1.550232240986876], [1.550232240986876, 0.5020274251736129, 0.5000000075555104, 0.5]]
		[0.0, -1.0, 1.0]		|	[1.0, 0.0, 3.0]		|	[[1.1856392615735671, 0.8666204045869218, 0.8666204045869218, 1.1856392615735671], [1.1856392615735671, 0.8666204045869218, 0.8666204045869218, 1.1856392615735671], [1.1856392615735671, 0.8666204045869218, 0.8652981757055507, 0.5007051913647349]]
	}

	@Unroll
	def "update probability distribution" () {
		given:
		antColony = new AntColony()
		searchSpace = new SearchSpace()
		searchSpace.numberOfDimensions = 3
		antColony.searchSpace = searchSpace
		antColony.tauSum = new double[3]
		antColony.numberOfPossibleValues = [4, 4, 4]
		antColonyOptimizer = new AntColonyOptimizer()
		antColonyOptimizer.setAlpha(alpha)
		antColony.setMetaInstance(antColonyOptimizer)
		antColony.probabilityDistribution = new ArrayList<double[]>(3)
		antColony.probabilityDistribution << new double[4]
		antColony.probabilityDistribution << new double[4]
		antColony.probabilityDistribution << new double[4]
		antColony.tau = tau
		
		antColony.updateProbabilityDistribution()
		
		expect:
		antColony.probabilityDistribution == expectedProbabilityDistribution
		
		where:
		alpha	|	tau																									|	expectedProbabilityDistribution
		1.0		|	[(double[])[1.0, 0.5, 0.1, 0.4], (double[])[0.5, 0.5, 0.5, 0.5], (double[])[5.0, 2.0, 1.0, 0.0]]	|	[(double[])[0.5, 0.25, 0.05, 0.2], (double[])[0.25, 0.25, 0.25, 0.25], (double[])[0.625, 0.25, 0.125, 0.0]]
		2.0		|	[(double[])[1.0, 0.5, 0.1, 0.4], (double[])[0.5, 0.5, 0.5, 0.5], (double[])[1.0, 1.0, 0.0, 0.0]]	|	[(double[])[0.7042253521126761, 0.17605633802816903, 0.0070422535211267625, 0.1126760563380282], (double[])[0.25, 0.25, 0.25, 0.25], (double[])[0.5, 0.5, 0.0, 0.0]]
	}
	
	/*
	 * This test is not well designed.
	 */
        @Ignore
	@Unroll
	def "convergence check"() {
		given:
		antColony = new AntColony()
		antColonyOptimizer = new AntColonyOptimizer()
		antColonyOptimizer.setAlpha(1.0)
		antColonyOptimizer.setTrailWidth(0.1)
		Agent iterationBestAgent = new Agent()
		iterationBestAgent.fitness = 0.5
		iterationBestAgent.lastReasonableSolution = iterationBestSolution
		antColonyOptimizer.iterationBestAgent = iterationBestAgent
		antColony.setMetaInstance(antColonyOptimizer)
		searchSpace = new SearchSpace()
		searchSpace.numberOfDimensions = 3
		searchSpace.startValues = [0.0, -1.0, 1.0]
		searchSpace.endValues = [2.0, 1.0, 5.0]
		searchSpace.periodic = [true, true, false]
		searchSpace.stepSize = [0.5, 0.5, 1.0]
		antColony.searchSpace = searchSpace
		SearchSpaceDiscretiser searchSpaceDiscretiser = new SearchSpaceDiscretiser(searchSpace)
		antColony.setSearchSpaceDiscretiser(searchSpaceDiscretiser)
		antColony.numberOfPossibleValues = searchSpaceDiscretiser.getNumberOfPossibleValues()
		antColony.tauMin = 0.1
		antColony.tau = tau
		antColony.tauSum = new double[3]
		for (int dimension=0; dimension < 3; dimension++) {
			double sum = 0.0
			for (int item:tau.get(dimension)){
				sum += item
			}
			antColony.tauSum[dimension] = sum
		}
		
		expect:
		antColony.isConverged() == isConverged
		
		where:
		iterationBestSolution	|	tau																									|	isConverged
		[0.4, -0.51, 1.4]		|	[(double[])[1.0, 0.1, 0.1, 0.1], (double[])[1.0, 0.1, 0.1, 0.1], (double[])[1.0, 0.1, 0.1, 0.1]]	|	true
		[1.0, 0.0, 4.5]			|	[(double[])[1.0, 0.1, 0.1, 0.1], (double[])[1.0, 0.1, 0.1, 0.1], (double[])[1.0, 0.1, 0.1, 0.1]]	|	false
		[0.49, -0.51, 1.4]		|	[(double[])[1.0, 0.5, 0.7, 0.5], (double[])[1.0, 0.3, 1.0, 0.3], (double[])[1.0, 0.1, 0.1, 0.1]]	|	false
	}
}
