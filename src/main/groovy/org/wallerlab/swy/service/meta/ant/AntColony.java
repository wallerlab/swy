package org.wallerlab.swy.service.meta.ant;

import java.util.ArrayList;

import org.wallerlab.swy.model.SearchSpace;
import org.wallerlab.swy.service.modeltools.SearchSpaceDiscretiser;
import org.wallerlab.swy.service.utils.Maths;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile({"untested"})
public class AntColony {

	private AntColonyOptimizer metaInstance;
	
	private SearchSpace searchSpace;

	/* Discrete search space (--> pheromones (tau) and probabilities are discrete) */
	private SearchSpaceDiscretiser searchSpaceDiscretiser;
	
	private int[] numberOfPossibleValues;
	/* needed for tauMin */
	private double averageNumberOfPossibleValues;

	/**
	 * fixed value!<br>
	 * Within 2*sigma of a gaussian function 95.45% of the area can be found.<br>
	 * More details: {@link #isConverged()}
	 */
	private final double convergenceThreshold = 0.9545;
	
	/**
	 * This is the probability distribution generated out of the {@link #tau pheromone levels}.<br>
	 * First dimension: the solution component<br>
	 * Second dimension: an index for a possible value of this solution component.<b>
	 * The probability distribution uses a discrete interpretation of the search space.
	 */
	public ArrayList<double[]> probabilityDistribution;
	
	/** minimum pheromone level */
	private double tauMin;
	/** sum of pheromones for each dimension */
	private double[] tauSum;
	/** maximum pheromone level */
	private double tauMax;
	/**
	 * This is a list of the pheromone levels.<br>
	 * First dimension: the solution component<br>
	 * Second dimension: an index for a possible value of this solution component.<b>
	 * tau uses a discrete interpretation of the search space.
	 */
	private ArrayList<double[]> tau;
	private boolean pheromonesInitialized;

	/* Calculation variables */
	private double[] tauConv;

	/* Constructor */
	public AntColony() {
	}
		
	/*
	 * Called by the antColonyOptimizer, due to lifeCycle problems this is not done via
	 * a @PostConstruct or InitializingBean
	 */
	/**
	 * This method sets up the arrays and sets them to initial values.
	 * Some values are set to NaN here to be able to check whether they
	 * have been set within the running algorithm.
	 */
	void initialize() {
		this.searchSpace = metaInstance.getProblemRepresentation().getSearchSpace();
		/* 
		 * Discrete search space: the probability distribution and pheromone
		 * levels are not continuous -> granularity
		 */
		// TODO implement possibility for constraints
		
		numberOfPossibleValues = searchSpaceDiscretiser.getNumberOfPossibleValues();
		averageNumberOfPossibleValues = Maths.average(numberOfPossibleValues);
		
		/* Pheromones and probabilityDistribution */
		tauMax = Double.NaN;
		tau = new ArrayList<double[]>(searchSpace.numberOfDimensions);
		probabilityDistribution = new ArrayList<double[]>(searchSpace.numberOfDimensions);
		for(int dimension=0; dimension < searchSpace.numberOfDimensions; dimension++) {
			tau.add(new double[numberOfPossibleValues[dimension]]);
			// one could think of making the probability distribution continuous
			probabilityDistribution.add(new double[numberOfPossibleValues[dimension]]);
		}
		tauSum = new double[searchSpace.numberOfDimensions];
		tauConv = new double[searchSpace.numberOfDimensions];
		
		/*
		 * Setting of initial Pheromone levels.
		 * Just an equal distribution of pheromones. Will be overridden after the first cycle
		 * anyway as soon as the first tauMax is calculated; will be reinitialized at tauMax then.
		 */
		initPheromones(1.0);
	}

	/* This method is/should be packageInternal */
	void updateProbabilityDistribution() {
		/* Calculate new tauSum[] and probabilityDistribution[][] */
		for(int dimension=0; dimension < searchSpace.numberOfDimensions; dimension++){
			tauSum[dimension] = 0;
			for(int possibleValue=0; possibleValue < numberOfPossibleValues[dimension]; possibleValue++){
				tauSum[dimension] += Math.pow(tau.get(dimension)[possibleValue], metaInstance.alpha);
			}
			for(int possibleValue=0; possibleValue < numberOfPossibleValues[dimension]; possibleValue++){
				probabilityDistribution.get(dimension)[possibleValue] =
					(Math.pow(tau.get(dimension)[possibleValue], metaInstance.alpha) / tauSum[dimension]);
			}
		}
	}
	
	void updatePheromones() {
		
		updateTauMinMax();

		evaporatePheromones();
		
		/* Override pheromone level after first cycle for real initialization */
		if (!pheromonesInitialized && tauMax != Double.NaN) {
			initPheromones(tauMax);
			pheromonesInitialized = true;
		}
		
		/* Deposit additional Pheromone */
		putPheromones();
		
		return;
	}
	
	private void updateTauMinMax() {
		if (metaInstance.getGlobalBestAgent() != null) {
			if (!Double.isNaN(metaInstance.getGlobalBestAgent().fitness)) {
				tauMax = ( 1 / (metaInstance.pheromoneResistance*metaInstance.getGlobalBestAgent().fitness) );
				tauMin = tauMax
					   * (1 - Math.pow(metaInstance.maximumProbabilityToChooseGlobalBest,
						   (1.0/searchSpace.numberOfDimensions)))
					   / ((averageNumberOfPossibleValues - 1)
					   * Math.pow(metaInstance.maximumProbabilityToChooseGlobalBest,
						   (1.0/searchSpace.numberOfDimensions)));
				if (tauMin > tauMax) {
					tauMin = tauMax;
				}
			}
		}
	}

	private void putPheromones() {
		// TODO check this!!!
		for(int dimension=0; dimension < searchSpace.numberOfDimensions; dimension++){
			for (int depositPoint=0; depositPoint < numberOfPossibleValues[dimension]; depositPoint++) {
				/* via iteration best Ant... */
				tau.get(dimension)[depositPoint] +=
					pheromoneRelease(dimension, depositPoint,
						metaInstance.getIterationBestAgent().lastReasonableSolution[dimension])
					* metaInstance.getIterationBestAgent().fitness * (1 - metaInstance.globalPercentage);
				/* ...and via global best Ant */
				tau.get(dimension)[depositPoint] +=
					pheromoneRelease(dimension, depositPoint,
						metaInstance.getGlobalBestAgent().lastReasonableSolution[dimension])
					* metaInstance.getGlobalBestAgent().fitness * metaInstance.globalPercentage;
			}
		}
		return;
	}
	
	private void evaporatePheromones() {
		for(int dimension=0; dimension < searchSpace.numberOfDimensions; dimension++){
			for(int possibleValue=0; possibleValue< numberOfPossibleValues[dimension]; possibleValue++){
				tau.get(dimension)[possibleValue] =
					tau.get(dimension)[possibleValue] * (1.0 - metaInstance.pheromoneResistance);
				// Check if pheromone level is in range of tauMin and tauMax
				if (!Double.isNaN(tauMax)) {
					if(tau.get(dimension)[possibleValue] < tauMin){
						tau.get(dimension)[possibleValue] = tauMin;
					}else if(tau.get(dimension)[possibleValue] > tauMax){
						tau.get(dimension)[possibleValue] = tauMax;
					}
				}
			}
		}
	}
	
	/*
	 * Convergence is reached if (within a threshold) only pheromone (> tauMin) is present
	 * within 4*trailWidth (the standard deviation of the gaussian shaped pheromone release),
	 * scaled by the size of the search space, of the iterationBest solution for all dimensions
	 * at the same time.
	 * The threshold is set to the percentage of how much area is within 2*sigma in a gaussian
	 * function; fluctuations in the size of 2*sigma are accepted at maximum for each dimension
	 * and of sigma on the average over all dimensions.
	 */
	boolean isConverged() {
		boolean converged=true;
		/* Maximum fluctuation per dimension */
		for(int dimension=0; dimension < searchSpace.numberOfDimensions; dimension++){
			int checkingWidth =
				searchSpaceDiscretiser.toDiscreteSpace(dimension, searchSpace.startValues[dimension]
						+ 3.0*metaInstance.trailWidth
						*(searchSpace.endValues[dimension] - searchSpace.startValues[dimension]));
			tauConv[dimension] = getConvergenceScoreForDimension(dimension, checkingWidth);
			if(!(tauConv[dimension]	>= convergenceThreshold)){
				converged=false;
				break;
			}
		}
//		if (converged) {
//			for(int dimension=0; dimension < searchSpace.numberOfDimensions; dimension++){
//				int checkingWidth =
//					searchSpaceDiscretiser.toDiscreteSpace(dimension, searchSpace.startValues[dimension]
//							+ 3.0*metaInstance.trailWidth
//							*(searchSpace.endValues[dimension] - searchSpace.startValues[dimension]));
//				tauConv[dimension] = getConvergenceScoreForDimension(dimension, checkingWidth);
//			}
//			System.out.println(Maths.average(tauConv)+" Fitness of iteration best"+
//					metaInstance.getIterationBestAgent().fitness);
//			if (!(Maths.average(tauConv) >= 0.9747)) {
//				converged = false;
//			}
//		}
		return converged;
	}
	
	private double getConvergenceScoreForDimension(int dimension, int checkingWidth) {
		double score = 0;
		for(int j= -checkingWidth; j <= checkingWidth; j++){
			int possibleValue = (searchSpaceDiscretiser.toDiscreteSpace(dimension,
					metaInstance.getIterationBestAgent().lastReasonableSolution[dimension]) +j);
			if (searchSpace.periodic[dimension] || 
					(possibleValue >= 0 && possibleValue < numberOfPossibleValues[dimension])) {
				score +=
					Math.pow(tau.get(dimension)[((possibleValue + numberOfPossibleValues[dimension])
					% numberOfPossibleValues[dimension])], metaInstance.alpha)
					- Math.pow(tauMin, metaInstance.alpha);
			}
		}
		score = score / (tauSum[dimension] - (Math.pow(tauMin, metaInstance.alpha) * 
				numberOfPossibleValues[dimension]));
		return score;
	}
	
	private void initPheromones(double initValue) {
		for (int dimension=0; dimension < searchSpace.numberOfDimensions; dimension++) {
			for (int possibleValue=0; possibleValue < numberOfPossibleValues[dimension]; possibleValue++) {
				tau.get(dimension)[possibleValue] = initValue;
			}
		}
	}
	
	/**
	 * Determines how much pheromone should be put on one 'field'.
	 * 
	 * @param dimension
	 * @param depositPoint On which point in the discrete searchSpace
	 * should pheromones be put.
	 * @param solutionPoint What is the actual origin of the pheromone
	 * release, i.e. what is the actual tested solution (for this dimension).
	 * It's a value in the continuous searchSpace.
	 * @return An amount of pheromones to add to what is already there
	 */
	private double pheromoneRelease(int dimension, int depositPoint, double solutionPoint) {
		double distance = Math.abs(searchSpaceDiscretiser.toContinuousSpace(dimension,depositPoint)-solutionPoint);
		if (searchSpace.periodic[dimension]) {
			if (distance >= ( (searchSpace.endValues[dimension]
					- searchSpace.startValues[dimension]) /2.0) ) {
				distance = (searchSpace.endValues[dimension]
					- searchSpace.startValues[dimension]) - distance;
			}
		}
		/*
		 * normalize distance
		 */
		distance /= (searchSpace.endValues[dimension] - searchSpace.startValues[dimension]);
		
		return Math.exp( -0.5 * Math.pow(distance / metaInstance.trailWidth, 2) )
					/ (Math.sqrt(2*Math.PI)*metaInstance.trailWidth);
	}
	
	@Required
	public void setMetaInstance(AntColonyOptimizer metaInstance) {
		this.metaInstance = metaInstance;
	}
	
	@Required
	public void setSearchSpaceDiscretiser (SearchSpaceDiscretiser searchSpaceDiscretiser) {
		this.searchSpaceDiscretiser = searchSpaceDiscretiser;
	}
}