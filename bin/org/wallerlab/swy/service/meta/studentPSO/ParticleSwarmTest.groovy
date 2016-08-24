package org.wallerlab.swy.service.meta.studentPSO;

import org.wallerlab.swy.model.ProblemRepresentation;
import org.wallerlab.swy.model.SearchSpace
import org.wallerlab.swy.model.main.Agent
import org.wallerlab.swy.service.utils.RandomNumberGenerator
import spock.lang.*

/* TODO all parts of the acceleration are tested, but not the acceleration in total */
public class ParticleSwarmTest extends Specification{

	private ParticleSwarm particleSwarm
	
	private RandomNumberGenerator randomNumberGenerator
	private ParticleSwarmHeuristic particleSwarmHeuristic
	private ProblemRepresentation problemRepresentation
	private SearchSpace searchSpace
	
	private Agent agent
	private Agent anotherAgent
	private ParticleSwarmParticle particleSwarmParticle
	private ParticleSwarmParticle anotherParticleSwarmParticle
	
	private int particleCounter
	
	private double[] solution
	private double[] anotherSolution
	private double[] fitnesses
	
	private double somePosition
	private double[] somePositions	
	private double expectedPosition
	private double someVelocity
	
	private double expectedValue
	
	private int dimension
	
	private int someParticleIndex
	private int expectedNeighbourIndex
	
	private int moodChangeCounter
	private int runs
	
	@Unroll
	def "Test init" () {
		
		given:
		particleSwarm = new ParticleSwarm()
		searchSpace = new SearchSpace()
		searchSpace.numberOfDimensions = 3
		searchSpace.startValues = [0.0, -1.0, 1.0]
		searchSpace.endValues = [1.0, 1.0, 5.0]
		problemRepresentation = new ProblemRepresentation()
		problemRepresentation.setSearchSpace(searchSpace)
		particleSwarmHeuristic = new ParticleSwarmHeuristic()
		particleSwarmHeuristic.setNumberOfAgents(3)
		particleSwarmHeuristic.agents = new Agent[3]
		particleSwarmHeuristic.setProblemRepresentation(problemRepresentation)
		agent = new Agent()
		anotherAgent = new Agent()
		particleSwarmHeuristic.agents[0] = agent
		particleSwarmHeuristic.agents[2] = anotherAgent
		particleSwarm.setMetaInstance(particleSwarmHeuristic)
		randomNumberGenerator = new RandomNumberGenerator()
		particleSwarm.setRandomNumberGenerator(randomNumberGenerator)
		
		particleSwarm.initialize()
		
		expect:
		particleSwarm.getParticles().length == 3
		particleSwarm.getParticles()[1].position[2] >= 1.0
		particleSwarm.getParticles()[1].position[2] <= 5.0
		particleSwarm.getParticles()[0].position[1] >= -1.0
		particleSwarm.getParticles()[0].position[1] <= 1.0
		particleSwarm.getParticles()[0].agent == agent
		particleSwarm.getParticles()[2].agent == anotherAgent
		particleSwarm.searchSpaceSize == (double[])[1.0, 2.0, 4.0]
	}
	
	@Unroll
	def "test solution generation" () {
		
		given:
		particleSwarm = new ParticleSwarm()
		particleSwarm.particleCounter = -1
		searchSpace = new SearchSpace()
		searchSpace.numberOfDimensions = 3
		searchSpace.startValues = [0.0, -1.0, 1.0]
		searchSpace.endValues = [1.0, 1.0, 5.0]
		particleSwarm.searchSpace = searchSpace
		particleSwarm.particles = new ParticleSwarmParticle[2]
		agent = new Agent()
		randomNumberGenerator = new RandomNumberGenerator()
		particleSwarmParticle = new ParticleSwarmParticle(searchSpace, agent, randomNumberGenerator)
		particleSwarmParticle.position = (double[])[-1.0, 0.0, 1.0]
		anotherParticleSwarmParticle = new ParticleSwarmParticle(searchSpace, agent, randomNumberGenerator)
		anotherParticleSwarmParticle.position = (double[])[9.0, -3.456, 0.123]
		particleSwarm.particles[0] = particleSwarmParticle
		particleSwarm.particles[1] = anotherParticleSwarmParticle
		
		solution = (double[])[-1.0, 0.0, 1.0]
		anotherSolution = (double[])[9.0, -3.456, 0.123]
		
		expect:
		particleSwarm.generateSolution() == solution
		particleSwarm.generateSolution() == anotherSolution
		particleSwarm.generateSolution() == solution
		particleSwarm.generateSolution() == anotherSolution

	}
	
	@Unroll
	def "checkMood" () {
		
		given:
		particleSwarm = new ParticleSwarm()
		particleSwarmParticle = Mock()
		anotherParticleSwarmParticle = Mock()
		moodChangeCounter = 0
		particleSwarmParticle.changeMood() >> {moodChangeCounter++}
		anotherParticleSwarmParticle.changeMood() >> {moodChangeCounter+=2}
		particleSwarm.particles = [particleSwarmParticle, anotherParticleSwarmParticle]
		particleSwarmHeuristic = Mock()
		particleSwarmHeuristic.getKeepMood() >> 1
		particleSwarm.setMetaInstance(particleSwarmHeuristic)
		particleSwarm.keepMoodCounter = 0
		
		for (int timesRun=0; timesRun < runs; timesRun++) {
			particleSwarm.checkMood()
		}
				
		expect:
		moodChangeCounter == (int)(runs/2)*3
		
		where:
		runs << [1, 2, 3, 4, 5, 6, 7]
		
	}
	
	@Unroll
	def "get neighbour" () {
		given:
		searchSpace = new SearchSpace()
		searchSpace.numberOfDimensions = 3
		searchSpace.startValues = [0.0, -1.0, 1.0]
		searchSpace.endValues = [1.0, 1.0, 5.0]
		particleSwarm = new ParticleSwarm()
		particleSwarm.particles = new ParticleSwarmParticle[5]
		randomNumberGenerator = Mock()
		randomNumberGenerator.getNewDouble() >> 0.0
		for (int particleIndex=0; particleIndex < 5; particleIndex++) {
			particleSwarm.particles[particleIndex] = new ParticleSwarmParticle(
				searchSpace, null, randomNumberGenerator)
			particleSwarm.particles[particleIndex].particleBestFitness = fitnesses[particleIndex]
		}
		
		expect:
		particleSwarm.getNeighbour(someParticleIndex) == particleSwarm.particles[expectedNeighbourIndex]
		
		where:
		someParticleIndex	|	fitnesses					|	expectedNeighbourIndex
		0					|	[0.1, 0.2, 0.3, 0.4, 0.5]	|	4
		1					|	[0.1, 0.2, 0.3, 0.4, 0.5]	|	2
		2					|	[0.1, 0.2, 0.3, 0.4, 0.5]	|	3
		4					|	[0.1, 0.2, 0.3, 0.4, 0.5]	|	3
		4					|	[0.6, 0.1, 0.1, 0.1, 0.1]	|	0
		1					|	[0.5, 0.5, 0.5, 0.5, 0.5]	|	0
	}
	
	@Unroll
	def "move" () {
		given:
		searchSpace = new SearchSpace()
		searchSpace.numberOfDimensions = 3
		searchSpace.startValues = [0.0, -1.0, 1.0]
		searchSpace.endValues = [1.0, 1.0, 5.0]
		searchSpace.periodic = [true, true, false]
		particleSwarm = new ParticleSwarm()
		particleSwarm.searchSpace = searchSpace
		particleSwarm.searchSpaceSize = (double[])[1.0, 2.0, 5.0]
		particleSwarmHeuristic = new ParticleSwarmHeuristic()
		particleSwarm.metaInstance = particleSwarmHeuristic
		particleSwarmHeuristic.setCrossoverMutationProbability(0.0)
		particleSwarmHeuristic.setReinitializationExponent(9999999999999)
		particleSwarmHeuristic.agents = new Agent[1]
		particleSwarm.particles = new ParticleSwarmParticle[1]
		randomNumberGenerator = Mock()
		randomNumberGenerator.getNewDouble() >> 0.5
		particleSwarm.setRandomNumberGenerator(randomNumberGenerator)
		particleSwarm.particles[0] = new ParticleSwarmParticle(searchSpace, null, randomNumberGenerator)
		particleSwarm.particles[0].position[dimension] = somePosition
		particleSwarm.particles[0].velocities[dimension] = someVelocity
		
		particleSwarm.moveParticles()
		
		expect:
		particleSwarm.particles[0].position[dimension].round(8) == expectedPosition
		
		where:
		somePosition	|	someVelocity	|	expectedPosition	|	dimension
		0.1				|	0.1				|	0.2					|	0
		0.9				|	0.2				|	0.1					|	0
		0.1				|	-0.1			|	0.0					|	0
		0.1				|	-0.5			|	0.6					|	0
		1.1				|	-0.2			|	3.0					|	2
		4.5				|	0.2				|	4.7					|	2
		4.9				|	0.2				|	3.0					|	2
		
	}
	
	@Unroll
	def "getStandardDeviationOfDimension" () {
		given:
		searchSpace = new SearchSpace()
		searchSpace.numberOfDimensions = 3
		searchSpace.startValues = [0.0, -1.0, 1.0]
		searchSpace.endValues = [1.0, 1.0, 5.0]
		searchSpace.periodic = [true, true, false]
		particleSwarm = new ParticleSwarm()
		particleSwarm.searchSpace = searchSpace
		particleSwarm.particles = new ParticleSwarmParticle[5]
		particleSwarm.searchSpaceSize = (double[])[1.0, 2.0, 5.0]
		randomNumberGenerator = Mock()
		randomNumberGenerator.getNewDouble() >> 0.0
		for (int particleIndex=0; particleIndex < 5; particleIndex++) {
			particleSwarm.particles[particleIndex] = new ParticleSwarmParticle(
				searchSpace, null, randomNumberGenerator)
			particleSwarm.particles[particleIndex].position[dimension] = somePositions[particleIndex]
		}
		
		expect:
		particleSwarm.getStandardDeviationOfDimension(dimension).round(8) == expectedValue
		
		where:
		somePositions				|	dimension		|	expectedValue
		[0.1, 0.2, 0.3, 0.4, 0.5]	|	0				|	0.14142136
		[0.4, 0.4, 0.4, 0.4, 0.4]	|	0				|	0.0
		[0.1, 0.2, 0.8, 0.9, 1.0]	|	0				|	0.14142136
		[1.0, 1.0, 5.0, 5.0, 5.0]	|	2				|	0.39191836
		[4.7, 4.85, 4.75, 4.8, 4.9]	|	2				|	0.01414214
		
	}
}
