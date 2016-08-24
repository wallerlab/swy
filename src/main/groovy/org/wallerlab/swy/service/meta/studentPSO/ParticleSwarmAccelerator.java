package org.wallerlab.swy.service.meta.studentPSO;

import org.springframework.context.annotation.Profile;
import org.wallerlab.swy.model.SearchSpace;
import org.wallerlab.swy.service.modeltools.SearchSpaceAnalyser;

/* TODO there are certainly a lot of things that can be abstracted out of
 * the different methods within this class. As well in the test.
 */
/**
 * To shorten the {@link org.wallerlab.swy.service.meta.studentPSO.ParticleSwarm}
 * all acceleration methods have been extracted into this class.
 * 
 * @author t_dres03
 */
@Profile({ "untested" })
public class ParticleSwarmAccelerator {
	
	private SearchSpace searchSpace;
	private ParticleSwarmHeuristic metaInstance;
	private double[] searchSpaceSize;

	public ParticleSwarmAccelerator(SearchSpace searchSpace,
			ParticleSwarmHeuristic metaInstance, double[] searchSpaceSize) {
		this.searchSpace = searchSpace;
		this.metaInstance = metaInstance;
		this.searchSpaceSize = searchSpaceSize;
	}

	void slowDown(ParticleSwarmParticle particleSwarmParticle,
			int dimension) {
		if (!particleSwarmParticle.lastSolutionImprovedParticle) {
			particleSwarmParticle.velocities[dimension] *= metaInstance.getAccelerationRatio();
		}		
	}
	
	void accelerateViaOwnBest(
			ParticleSwarmParticle particleSwarmParticle, int dimension) {
		if (!Double.isNaN(particleSwarmParticle.particleBestPosition[dimension])) {
		particleSwarmParticle.velocities[dimension] +=
			SearchSpaceAnalyser.distanceInSearchSpace(particleSwarmParticle.particleBestPosition[dimension],
				particleSwarmParticle.position[dimension], searchSpace.periodic[dimension],
				searchSpaceSize[dimension])
			*particleSwarmParticle.particleBestFitness
			/searchSpaceSize[dimension]*particleSwarmParticle.mood[0]*searchSpace.stepSize[dimension];
		}		
	}

	void accelerateViaNeighbour(
			ParticleSwarmParticle particleSwarmParticle,
			ParticleSwarmParticle bestNeighbourParticle, int dimension) {
		if (!Double.isNaN(bestNeighbourParticle.particleBestFitness)) {
			particleSwarmParticle.velocities[dimension] +=
				SearchSpaceAnalyser.distanceInSearchSpace(bestNeighbourParticle.position[dimension],
					particleSwarmParticle.position[dimension], searchSpace.periodic[dimension],
					searchSpaceSize[dimension])
				/searchSpaceSize[dimension]*particleSwarmParticle.mood[0]*searchSpace.stepSize[dimension];
		}		
	}
	
	void accelerateViaIterationBest(
			ParticleSwarmParticle particleSwarmParticle,
			double[] iterationBestSolution, int dimension) {
		if (!Double.isNaN(iterationBestSolution[dimension])) {
		/* ...the iteration best of all particles */
			particleSwarmParticle.velocities[dimension] +=
			SearchSpaceAnalyser.distanceInSearchSpace(iterationBestSolution[dimension],
					particleSwarmParticle.position[dimension], searchSpace.periodic[dimension],
					searchSpaceSize[dimension])
				*(1.0-metaInstance.getGlobalPercentage())
				/searchSpaceSize[dimension]*particleSwarmParticle.mood[1]*searchSpace.stepSize[dimension];
		}		
	}
	
	void accelerateViaGlobalBest(
			ParticleSwarmParticle particleSwarmParticle,
			double[] globalBestSolution, int dimension) {
		if (!Double.isNaN(globalBestSolution[dimension])) {
			/* ...the global best of all particles */
			particleSwarmParticle.velocities[dimension] +=
				SearchSpaceAnalyser.distanceInSearchSpace(globalBestSolution[dimension],
					particleSwarmParticle.position[dimension], searchSpace.periodic[dimension],
					searchSpaceSize[dimension])
				/searchSpaceSize[dimension]*particleSwarmParticle.mood[1]*searchSpace.stepSize[dimension]
				*(metaInstance.getGlobalPercentage());
		}		
	}

	void accelerateViaRandom(
			ParticleSwarmParticle particleSwarmParticle, int dimension) {
		particleSwarmParticle.velocities[dimension] +=
				particleSwarmParticle.mood[2+dimension]*searchSpace.stepSize[dimension];		
	}
	
	void limitMaxSpeed(
			ParticleSwarmParticle particleSwarmParticle, int dimension) {
		if (Math.abs(particleSwarmParticle.velocities[dimension]) >= 0.2*searchSpaceSize[dimension]) {
			particleSwarmParticle.velocities[dimension] =
					Math.signum(particleSwarmParticle.velocities[dimension]) * 
					0.1*searchSpaceSize[dimension];
		}
	}
}
