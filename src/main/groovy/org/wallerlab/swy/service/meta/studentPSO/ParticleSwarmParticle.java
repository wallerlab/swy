package org.wallerlab.swy.service.meta.studentPSO;

import org.wallerlab.swy.model.SearchSpace;
import org.wallerlab.swy.model.main.Agent;
import org.wallerlab.swy.service.utils.RandomNumberGenerator;

import org.springframework.context.annotation.Profile;

@Profile({"untested"})
class ParticleSwarmParticle {
	
	private Agent agent;
	private SearchSpace searchSpace;
	
	private RandomNumberGenerator randomNumberGenerator;
	
	double[] position;
	double[] velocities;
	double[] particleBestPosition;
	double particleBestFitness = Double.NaN;
	boolean lastSolutionImprovedParticle = false;
	/**
	 * A particle follows different things. Its own best position, good positions of other particles
	 * and also random movement. The mood is a weighting among these different parts.
	 */
	double[] mood;

	/**
	 * Particles are initialised with random values.
	 * 
	 * @param searchSpace
	 * @param agent A particle is tied to an agent model which is
	 * updated whenever the particle improves.
	 */
	ParticleSwarmParticle(SearchSpace searchSpace, Agent agent, RandomNumberGenerator randomNumberGenerator) {
		this.agent = agent;
		this.searchSpace = searchSpace;
		this.randomNumberGenerator = randomNumberGenerator;
		
		position = new double[searchSpace.numberOfDimensions];
		velocities = new double[searchSpace.numberOfDimensions];
		particleBestPosition = new double[searchSpace.numberOfDimensions];
		mood = new double[2+searchSpace.numberOfDimensions];
		changeMood();
		
		for (int dimension=0; dimension < searchSpace.numberOfDimensions; dimension++) {
			velocities[dimension] = 0.0;
			particleBestPosition[dimension] = Double.NaN;		
			/* Initialize with random values */
			position[dimension] = (searchSpace.endValues[dimension] - searchSpace.startValues[dimension])
					*randomNumberGenerator.getNewDouble() + searchSpace.startValues[dimension];
			assert(!Double.isNaN(position[dimension]));
		}
	}
	
	void updateBestValues() {
		if ((particleBestFitness < agent.fitness || Double.isNaN(particleBestFitness)) &&
				!Double.isNaN(agent.fitness)) {
			particleBestFitness = agent.fitness;
			for (int dimension=0; dimension < searchSpace.numberOfDimensions; dimension++) {
				particleBestPosition[dimension] = agent.lastReasonableSolution[dimension];
			}
			lastSolutionImprovedParticle = true;
		} else {
			lastSolutionImprovedParticle = false;
		}
	}
	
	/**
	 * Mood values are random.
	 */
	void changeMood() {
		mood[0] = 2 * randomNumberGenerator.getNewDouble();
		mood[1] = 2 * randomNumberGenerator.getNewDouble();
		/* This is not active atm. (Random acceleration) */
		for (int dimension=0; dimension < searchSpace.numberOfDimensions; dimension++) {
			mood[2+dimension] = (randomNumberGenerator.getNewDouble() - 0.5);
		}
	}
}
