package org.wallerlab.swy.service.meta.studentPSO;

import org.wallerlab.swy.model.SearchSpace
import org.wallerlab.swy.model.main.Agent
import org.wallerlab.swy.service.utils.RandomNumberGenerator;

import spock.lang.*;

public class ParticleSwarmParticleTest extends Specification {

	private ParticleSwarmParticle particleSwarmParticle
	
	private RandomNumberGenerator randomNumberGenerator
	
	private SearchSpace searchSpace
	private Agent agent
	
	private double[] randomNumbers
	private double[] expectedPosition
	private double[] expectedMood
	
	private double agentFitness
	private double particleFitness
	
	private boolean didImprove
	
	def "Check initialisation including mood change" () {
		
		given:
		searchSpace = new SearchSpace()
		searchSpace.numberOfDimensions = 3
		searchSpace.startValues = [0.0, -1.0, 1.0]
		searchSpace.endValues = [1.0, 1.0, 5.0]
		agent = new Agent()
		
		randomNumberGenerator = Mock()
		randomNumberGenerator.getNewDouble() >>> randomNumbers
		particleSwarmParticle = new ParticleSwarmParticle(searchSpace, agent, randomNumberGenerator)
		
		expect:
		particleSwarmParticle.position == expectedPosition
		particleSwarmParticle.mood == expectedMood
		particleSwarmParticle.velocities == (double[])[0.0, 0.0, 0.0]
		particleSwarmParticle.particleBestPosition.length == 3
		
		where:
		randomNumbers								|	expectedPosition	|	expectedMood
		[0.1, 0.2, 0.25, 0.5, 0.75, 1.0, 0.0, 0.25]	|	[1.0, -1.0, 2.0]	|	[0.2, 0.4, -0.25, 0.0, 0.25]
		[1, 0, 1, 0, 0, 0.25, 0.5, 0.1]				|	[0.25, 0.0, 1.4]	|	[2, 0, 0.5, -0.5, -0.5]
		
	}
	
	def "Check updateBestSolution" () {
		
		given:
		searchSpace = new SearchSpace()
		searchSpace.numberOfDimensions = 3
		searchSpace.startValues = [0.0, -1.0, 1.0]
		searchSpace.endValues = [1.0, 1.0, 5.0]
		agent = new Agent()
		
		randomNumberGenerator = Mock()
		randomNumberGenerator.getNewDouble() >>> randomNumbers
		particleSwarmParticle = new ParticleSwarmParticle(searchSpace, agent, randomNumberGenerator)
		
		agent.fitness = agentFitness
		agent.lastReasonableSolution = (double[]) [1.0, 1.0, 1.0]
		particleSwarmParticle.particleBestFitness = particleFitness
		particleSwarmParticle.particleBestPosition = new double[3]
		
		particleSwarmParticle.updateBestValues()
		
		
		expect:
		particleSwarmParticle.lastSolutionImprovedParticle == didImprove
		(didImprove && particleSwarmParticle.particleBestPosition == agent.lastReasonableSolution
			) || (!didImprove && particleSwarmParticle.particleBestPosition != agent.lastReasonableSolution)
		(didImprove && particleSwarmParticle.particleBestFitness == agentFitness
			) || (!didImprove && particleSwarmParticle.particleBestFitness == particleFitness)
		
		where:
		agentFitness		|	particleFitness		|	didImprove
		0.5					|	0.9					|	false
		0.1					|	Double.NaN			|	true
		Double.NaN			|	0.23				|	false
		0.8					|	0.4					|	true
		
	}
}
