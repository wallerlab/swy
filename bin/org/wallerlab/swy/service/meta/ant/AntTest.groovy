package org.wallerlab.swy.service.meta.ant;

import org.wallerlab.swy.model.SearchSpace
import org.wallerlab.swy.service.utils.RandomNumberGenerator;

import groovy.mock.interceptor.StubFor
import spock.lang.*

public class AntTest extends Specification{
	private Ant ant
	
	private SearchSpace searchSpace
	private AntColony antColony
	
	private double[] randomNumbers
	private double[] solution
	
	@Unroll
	def "Solution generation of the ant. Random numbers #randomNumbers give solution #solution"() {
		
		given:
		ant = new Ant()
		searchSpace = new SearchSpace()
		searchSpace.numberOfDimensions = 3
		searchSpace.startValues = [0.0, 1.0, -1.0]
		searchSpace.stepSize = [0.1, 0.5, 1.0]
		ant.searchSpace = searchSpace
		antColony = new AntColony()
		antColony.probabilityDistribution = new ArrayList<double[]>(3)
		antColony.probabilityDistribution << (double[])[0.25, 0.25, 0.25, 0.25]
		antColony.probabilityDistribution << (double[])[0.75, 0.125, 0.125]
		antColony.probabilityDistribution << (double[])[0.5, 0.4, 0.1]
		ant.setAntColony(antColony)
		RandomNumberGenerator randomNumberGeneratorStub = Mock()
		randomNumberGeneratorStub.getNewDouble() >>> randomNumbers
		ant.setRandomNumberGenerator(randomNumberGeneratorStub)
		
		expect:
		ant.generateSolution() == solution
		
		cleanup:
		ant = null
		antColony = null
		randomNumberGeneratorStub = null
		searchSpace = null
		
		where:
		randomNumbers					|	solution
		[0.0, 0.0, 0.0]					|	[0.0, 1.0, -1.0]
		[1.0, 1.0, 1.0]					|	[0.4, 2.5, 2.0]
		[0.125, 0.875, 0.75]			|	[0.05, 2.0, 0.625]
		
	}	
}