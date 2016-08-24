package org.wallerlab.swy.service.meta.studentPSO;

import org.wallerlab.swy.model.SearchSpace;
import org.wallerlab.swy.service.meta.SolutionGenerator;
import org.wallerlab.swy.service.modeltools.SearchSpaceAnalyser;
import org.wallerlab.swy.service.utils.RandomNumberGenerator;
import org.wallerlab.swy.service.utils.Maths;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * This is the solution generator of the {@link org.wallerlab.swy.service.meta.studentPSO.ParticleSwarmHeuristic}.
 * It also holds all information concerning the
 * {@link org.wallerlab.swy.service.meta.studentPSO.ParticleSwarmParticle particles},
 * the particles themselves and all the methods that work on
 * particles which those cannot do themselves.
 * 
 * @author t_dres03
 */
@Service
@Profile({ "untested" })
public class ParticleSwarm implements SolutionGenerator {

	private ParticleSwarmHeuristic metaInstance;
	// WHY DID THIS GO???????
	private SearchSpace searchSpace;
	
	private RandomNumberGenerator randomNumberGenerator;
	
	private ParticleSwarmParticle[] particles;
	
	private ParticleSwarmAccelerator particleSwarmAccelerator;
	
	/**
	 * An internal counter. The number of particles must not
	 * be changed during a run of the algorithm.
	 */
	private int particleCounter=-1;
	
	/**
	 * An internal counter to measure how long the particles
	 * have been in the same mood.
	 */
	private int keepMoodCounter=0;
	
	/** SearchSpace */
	private double [] searchSpaceSize;
	
	public ParticleSwarm() {

	}

	/**
	 * Called by the particleSwarmHeuristic, due to lifeCycle problems this is
	 * not done via a \@PostConstruct or InitializingBean.<br>
	 * Main job: create the particles.
	 */
	void initialize() {
		this.searchSpace = metaInstance.getProblemRepresentation()
				.getSearchSpace();
		particles = new ParticleSwarmParticle[metaInstance.getNumberOfAgents()];
		for (int particleIndex = 0; particleIndex < metaInstance
				.getNumberOfAgents(); particleIndex++) {
			particles[particleIndex] = new ParticleSwarmParticle(searchSpace,
					metaInstance.getAgents()[particleIndex], randomNumberGenerator);
		}
		searchSpaceSize = new double [searchSpace.numberOfDimensions];
		for (int dimension=0;dimension<searchSpace.numberOfDimensions; dimension++){
			searchSpaceSize[dimension] = searchSpace.endValues[dimension] - searchSpace.startValues[dimension];
		}
		particleSwarmAccelerator = new ParticleSwarmAccelerator(searchSpace, metaInstance, searchSpaceSize);
	}

	/**
	 * Just returns a particles position as the solution.
	 */
	@Override
	public double[] generateSolution() {
		if (particleCounter == particles.length-1) {
			particleCounter = 0;
		} else {
			particleCounter++;
		}
		double[] newSolution = new double[searchSpace.numberOfDimensions];
		for (int dimension=0; dimension < searchSpace.numberOfDimensions; dimension++) {
			newSolution[dimension] = particles[particleCounter].position[dimension];
			assert(!Double.isNaN(newSolution[dimension]));
		}
		return newSolution;
	}
	
	void updateParticleBestValues() {
		for (int particleIndex = 0; particleIndex < particles.length; particleIndex++) {
			particles[particleIndex].updateBestValues();
		}
	}

	/**
	 * This is the heart of the algorithm. The particles are
	 * accelerated with respect to the information the algorithm
	 * just gained in this metaCycle.<br>
	 * Additionally other components are part of the acceleration:
	 * the particle's neighbourhood and a random component.<br>
	 * Changes here are very likely to occur over different program
	 * versions.
	 * 
	 * @param iterationBestSolution The best (valid) solution a particle
	 * has gained in the current metaCycle.
	 * @param globalBestSolution The best (valid) solution the algorithm
	 * has ever seen (in this run).
	 */
	void accelerateParticles(double[] iterationBestSolution, double[] globalBestSolution) {
		
		checkMood();
		
		for (int particleIndex = 0; particleIndex < particles.length; particleIndex++) {
			
			/*
			 * Determining best neighbour
			 */
			ParticleSwarmParticle bestNeighbourParticle = getNeighbour(particleIndex);

			for (int dimension = 0; dimension < searchSpace.numberOfDimensions; dimension++) {
				/*
				 * Accelerate via...
				 */
				/* ...keeping the old acceleration partly */
				particleSwarmAccelerator.slowDown(particles[particleIndex], dimension);

				/* ...the particles own best ever tried solution; not used atm. */
//				particleSwarmAccelerator.accelerateViaOwnBest(particles[particleIndex], dimension);

				/* 
				 * ...a neighbourhood's best.
				 * The neighbourhood is in a 'ring'/'social' style. A particle's neighbours are simply the next
				 * and previous particle of the swarm. Thus all particles are connected with each other like the
				 * elements of a chain, independent of the distance of the particles.
				 */
				particleSwarmAccelerator.accelerateViaNeighbour(particles[particleIndex],
						bestNeighbourParticle, dimension);

				particleSwarmAccelerator.accelerateViaIterationBest(particles[particleIndex],
						iterationBestSolution, dimension);

				particleSwarmAccelerator.accelerateViaGlobalBest(particles[particleIndex],
						globalBestSolution, dimension);

				/* not used atm */
//				particleSwarmAccelerator.accelerateViaRandom(particles[particleIndex], dimension);

				/* Limit max speed */
				particleSwarmAccelerator.limitMaxSpeed(particles[particleIndex], dimension);

				assert(!Double.isNaN(particles[particleIndex].velocities[dimension]));
			}
		}
	}
	
	private ParticleSwarmParticle getNeighbour(int particleIndex) {
		ParticleSwarmParticle bestNeighbour = 
				particles[(particleIndex-1+particles.length) % particles.length];
		if (bestNeighbour.particleBestFitness <
				particles[(particleIndex+1) % particles.length].particleBestFitness) {
				bestNeighbour = particles[(particleIndex+1) % particles.length];
		}
		return bestNeighbour;
	}

	private void checkMood() {
		if (keepMoodCounter >= metaInstance.getKeepMood()) {
			keepMoodCounter = 0;
			for (int particleIndex = 0; particleIndex < particles.length; particleIndex++) {
				particles[particleIndex].changeMood();
			}
		} else {
			keepMoodCounter++;
		}
	}

	/**
	 * The particles are not only moved here, but also checked
	 * to stay within a searchSpace and can also (depending on
	 * specified parameters) reinitialise or mutate particle positions.
	 */
	void moveParticles() {
		for (int particleIndex = 0; particleIndex < particles.length; particleIndex++) {
			for (int dimension = 0; dimension < searchSpace.numberOfDimensions; dimension++) {
				/*
				 * Make sure the particle stays within searchSpace.
				 * For a periodic search space shift the position by the size of the searchSpace dimension
				 * if a particle leaves the borders.
				 * For a non-periodic searchSpace reinitialise the particle in that case.
				 */
				if (particles[particleIndex].position[dimension]
						+ particles[particleIndex].velocities[dimension] > searchSpace.endValues[dimension]) {
					if (searchSpace.periodic[dimension]) {
						particles[particleIndex].position[dimension] += particles[particleIndex].velocities[dimension]
							- searchSpaceSize[dimension];
					} else {
						particles[particleIndex] = new ParticleSwarmParticle(searchSpace,
								metaInstance.getAgents()[particleIndex], randomNumberGenerator);
					}
				} else if (particles[particleIndex].position[dimension]
						+ particles[particleIndex].velocities[dimension] < searchSpace.startValues[dimension]) {
					if (searchSpace.periodic[dimension]) {
						particles[particleIndex].position[dimension] += particles[particleIndex].velocities[dimension]
							+ searchSpaceSize[dimension];
					} else {
						particles[particleIndex] = new ParticleSwarmParticle(searchSpace,
								metaInstance.getAgents()[particleIndex], randomNumberGenerator);
					}
				} else {
					/* perform actual movement */
					particles[particleIndex].position[dimension] += particles[particleIndex].velocities[dimension];
				}
				assert(!Double.isNaN(particles[particleIndex].position[dimension]));
			}
		}
		
		/*
		 * Crossover mutation
		 */
		if (randomNumberGenerator.getNewDouble() <= metaInstance.getCrossoverMutationProbability()) {
			crossoverMutate();
		}
		
		/*
		 * Reinitialization of bad particles (only reinits a particles
		 * position, and only in one dimension).
		 */
		for (int particleIndex = 0; particleIndex < particles.length; particleIndex++) {
			if (randomNumberGenerator.getNewDouble() <= 1.0/metaInstance.getReinitializationExponent()) {
				int dimension = (int)(randomNumberGenerator.getNewDouble()*searchSpace.numberOfDimensions);
				particles[particleIndex].position[dimension] =
						searchSpaceSize[dimension]*randomNumberGenerator.getNewDouble()
						+ searchSpace.startValues[dimension];
			}
		}
	}

	/**
	 * This method is used in the convergence check.
	 * 
	 * @param dimension
	 * @return the sd of the particles position in the dimension.
	 */
	double getStandardDeviationOfDimension(int dimension) {
		double[] dummyArray = new double[particles.length];
		for (int particleIndex = 0; particleIndex < particles.length; particleIndex++) {
			dummyArray[particleIndex] = SearchSpaceAnalyser.distanceInSearchSpace(
					particles[0].position[dimension], particles[particleIndex].position[dimension],
					searchSpace.periodic[dimension], searchSpaceSize[dimension]);
		}
		return (Maths.standardDeviation(dummyArray)/searchSpaceSize[dimension]);
	}
	
	/**
	 * Two particles switch their position in one dimension.
	 */
	private void crossoverMutate() {
		int particleOneIndex = (int)(particles.length * randomNumberGenerator.getNewDouble());
		int particleTwoIndex = (int)(particles.length * randomNumberGenerator.getNewDouble());
		if (particleOneIndex != particleTwoIndex) {
			int positionIndex = (int)(searchSpace.numberOfDimensions * randomNumberGenerator.getNewDouble());
			double parameterValueBackup = particles[particleOneIndex].position[positionIndex];
			particles[particleOneIndex].position[positionIndex] = particles[particleTwoIndex].position[positionIndex];
			particles[particleTwoIndex].position[positionIndex] = parameterValueBackup;
		}
	}
	
	@Required
	public void setMetaInstance(ParticleSwarmHeuristic metaInstance) {
		this.metaInstance = metaInstance;
	}
	
	@Required
	public void setRandomNumberGenerator(RandomNumberGenerator randomNumberGenerator) {
		this.randomNumberGenerator = randomNumberGenerator;
	}

	public ParticleSwarmParticle[] getParticles() {
		return particles;
	}

}
