package org.wallerlab.swy.service.meta.studentPSO;

import org.wallerlab.swy.model.SearchSpace
import org.wallerlab.swy.service.utils.RandomNumberGenerator
import spock.lang.*;

public class ParticleSwarmAcceleratorTest extends Specification{

	private ParticleSwarmAccelerator particleSwarmAccelerator
	private ParticleSwarmHeuristic metaInstance
	private ParticleSwarmParticle particle
	private RandomNumberGenerator randomNumberGenerator
	private SearchSpace searchSpace
	private double[] searchSpaceSize
	
	private double accelerationRatio
	
	private double[] particleBestPosition
	private double particleBestFitness
	private double[] mood
	private double moodValue
	private double[] velocities
	private double expectedVelo
	private boolean didImprove
	private double[] position
	private double globalPercentage
	
	private double[] someBestSolution
	
	def setup() {
		searchSpace = new SearchSpace()
		searchSpace.periodic = [true, false]
		searchSpace.stepSize = [0.1, 0.1]
		searchSpaceSize = (double[])[1.0, 2.0]
		metaInstance = new ParticleSwarmHeuristic()
		particleSwarmAccelerator = new ParticleSwarmAccelerator(searchSpace, metaInstance, searchSpaceSize)
		randomNumberGenerator = Mock()
		randomNumberGenerator.getNewDouble() >> 0.0
		particle = new ParticleSwarmParticle(searchSpace, null, randomNumberGenerator)
		mood = new double[4]
		particle.mood = mood
	}
	
	def cleanup() {
		searchSpace = null
		metaInstance = null
		searchSpaceSize = null
		particleSwarmAccelerator = null
		particle = null
	}
	
	@Unroll
	def "test slow down" () {
		
		given:
		metaInstance.setAccelerationRatio(accelerationRatio)
		particle.velocities = new double[1]
		particle.velocities[0] = velocities[0]
		particle.lastSolutionImprovedParticle = didImprove
		particleSwarmAccelerator.slowDown(particle, 0)
		expect:
		(didImprove && particle.velocities[0] == velocities[0]) ||
			(!didImprove && particle.velocities[0] == accelerationRatio * velocities[0])
			
		where:
		accelerationRatio	|	velocities	|	didImprove
		0.5					|	2.0			|	false
		0.5					|	2.0			|	true
		0.5					|	-1.7		|	false
		0.5					|	-1.7		|	true
		1.0					|	0.1			|	false
		1.0					|	0.1			|	true
		0.0					|	0.1			|	false
		0.0					|	0.1			|	true
		
	}
	
	@Unroll
	def "test accelerate via own best" () {
		given:
		particle.velocities = new double[1]
		particle.velocities[0] = velocities[0]
		particle.mood[0] = moodValue
		particle.particleBestPosition = particleBestPosition
		particle.position = position
		particle.particleBestFitness = particleBestFitness
		
		particleSwarmAccelerator.accelerateViaOwnBest(particle, 0)
		
		expect:
		particle.velocities[0].round(8) == expectedVelo
		
		where:
		velocities		|	moodValue	|	particleBestPosition	|	position	|	particleBestFitness	|	expectedVelo
		[0.1]			|	0.5			|	[0.5]					|	[0.5]		|	1.0					|	0.1
		[0.1]			|	0.5			|	[0.5]					|	[0.1]		|	1.0					|	0.12
		[0.1]			|	0.5			|	[0.5]					|	[0.9]		|	1.0					|	0.08
		[-0.1]			|	0.5			|	[0.5]					|	[0.1]		|	1.0					|	-0.08
		[-0.1]			|	0.5			|	[0.5]					|	[0.9]		|	1.0					|	-0.12
		[0.0]			|	0.5			|	[0.5]					|	[0.1]		|	1.0					|	0.02
		[0.0]			|	0.5			|	[0.5]					|	[0.9]		|	1.0					|	-0.02
		[0.1]			|	0.5			|	[0.3]					|	[0.1]		|	1.0					|	0.11
		[0.1]			|	1.5			|	[0.3]					|	[0.1]		|	1.0					|	0.13
		[0.1]			|	0.5			|	[0.5]					|	[0.1]		|	0.5					|	0.11
		
	}
	
	@Unroll
	def "accelerate via neighbour" () {
		given:
		ParticleSwarmParticle neighbour = new ParticleSwarmParticle(searchSpace, null, randomNumberGenerator)
		neighbour.particleBestFitness = particleBestFitness
		neighbour.position = [0.5]
		particle.velocities = new double[1]
		particle.velocities[0] = velocities[0]
		particle.mood[0] = moodValue
		particle.position = position
		
		particleSwarmAccelerator.accelerateViaNeighbour(particle, neighbour, 0)
		
		expect:
		particle.velocities[0].round(8) == expectedVelo
		
		where:
		velocities		|	moodValue	|	position	|	particleBestFitness	|	expectedVelo
		[0.1]			|	0.5			|	[0.5]		|	1.0					|	0.1
		[0.1]			|	0.5			|	[0.1]		|	1.0					|	0.12
		[0.1]			|	0.5			|	[0.9]		|	1.0					|	0.08
		[-0.1]			|	0.5			|	[0.1]		|	1.0					|	-0.08
		[-0.1]			|	0.5			|	[0.9]		|	1.0					|	-0.12
		[0.0]			|	0.5			|	[0.1]		|	1.0					|	0.02
		[0.0]			|	0.5			|	[0.9]		|	1.0					|	-0.02
		[0.1]			|	1.5			|	[0.1]		|	1.0					|	0.16
		
	}
	
	@Unroll
	def "accelerate via iterationBest" () {
		given:
		metaInstance.setGlobalPercentage(globalPercentage)
		particle.velocities = new double[1]
		particle.velocities[0] = velocities[0]
		particle.mood[1] = moodValue
		particle.position = position
		
		particleSwarmAccelerator.accelerateViaIterationBest(particle, someBestSolution, 0)
		
		expect:
		particle.velocities[0].round(8) == expectedVelo
		
		where:
		velocities		|	moodValue	|	globalPercentage	|	position	|	someBestSolution	|	expectedVelo
		[0.1]			|	0.5			|	0.5					|	[0.5]		|	[0.5]				|	0.1
		[0.1]			|	0.5			|	0.5					|	[0.1]		|	[0.5]				|	0.11
		[0.1]			|	0.5			|	0.5					|	[0.9]		|	[0.5]				|	0.09
		[-0.1]			|	0.5			|	0.5					|	[0.1]		|	[0.5]				|	-0.09
		[-0.1]			|	0.5			|	0.5					|	[0.9]		|	[0.5]				|	-0.11
		[0.0]			|	0.5			|	0.5					|	[0.1]		|	[0.5]				|	0.01
		[0.0]			|	0.5			|	0.5					|	[0.9]		|	[0.5]				|	-0.01
		[0.1]			|	0.5			|	0.3					|	[0.1]		|	[0.5]				|	0.114
		[0.1]			|	1.5			|	0.3					|	[0.1]		|	[0.5]				|	0.142
		[0.1]			|	0.5			|	0.5					|	[0.1]		|	[0.1]				|	0.1
		[0.1]			|	0.5			|	0.5					|	[0.1]		|	[0.3]				|	0.105
		
	}
	
	@Unroll
	def "accelerate via globalBest" () {
		given:
		metaInstance.setGlobalPercentage(globalPercentage)
		particle.velocities = new double[1]
		particle.velocities[0] = velocities[0]
		particle.mood[1] = moodValue
		particle.position = position
		
		particleSwarmAccelerator.accelerateViaGlobalBest(particle, someBestSolution, 0)
		
		expect:
		particle.velocities[0].round(8) == expectedVelo
		
		where:
		velocities		|	moodValue	|	globalPercentage	|	position	|	someBestSolution	|	expectedVelo
		[0.1]			|	0.5			|	0.5					|	[0.5]		|	[0.5]				|	0.1
		[0.1]			|	0.5			|	0.5					|	[0.1]		|	[0.5]				|	0.11
		[0.1]			|	0.5			|	0.5					|	[0.9]		|	[0.5]				|	0.09
		[-0.1]			|	0.5			|	0.5					|	[0.1]		|	[0.5]				|	-0.09
		[-0.1]			|	0.5			|	0.5					|	[0.9]		|	[0.5]				|	-0.11
		[0.0]			|	0.5			|	0.5					|	[0.1]		|	[0.5]				|	0.01
		[0.0]			|	0.5			|	0.5					|	[0.9]		|	[0.5]				|	-0.01
		[0.1]			|	0.5			|	0.3					|	[0.1]		|	[0.5]				|	0.106
		[0.1]			|	1.5			|	0.3					|	[0.1]		|	[0.5]				|	0.118
		[0.1]			|	0.5			|	0.5					|	[0.1]		|	[0.1]				|	0.1
		[0.1]			|	0.5			|	0.5					|	[0.1]		|	[0.3]				|	0.105
		
	}
	
	@Unroll
	def "accelerate via random" () {
		given:
		particle.mood[2] = moodValue
		particle.velocities = new double[1]
		particle.velocities[0] = velocities[0]
		
		particleSwarmAccelerator.accelerateViaRandom(particle, 0)
		
		expect:
		particle.velocities[0].round(8) == expectedVelo
		
		where:
		velocities	|	moodValue	|	expectedVelo
		[0.1]		|	0.5			|	0.15
		[0.1]		|	-0.5		|	0.05
		[-0.1]		|	0.5			|	-0.05
		[-0.1]		|	-0.5		|	-0.15
		[0.7]		|	1.2			|	0.82
		[0.0]		|	0.0			|	0.0
		
	}
	
	@Unroll
	def "limit max speed" () {
		given:
		particle.velocities = new double[1]
		particle.velocities[0] = velocities[0]
		
		particleSwarmAccelerator.limitMaxSpeed(particle, 0)
		
		expect:
		particle.velocities[0].round(8) == expectedVelo
		
		where:
		velocities	|	expectedVelo
		[0.1]		|	0.1
		[-0.1]		|	-0.1
		[0.7]		|	0.1
		[-0.7]		|	-0.1
		[0.15]		|	0.15
		[-0.15]		|	-0.15
		[0.0]		|	0.0
		
	}
}
