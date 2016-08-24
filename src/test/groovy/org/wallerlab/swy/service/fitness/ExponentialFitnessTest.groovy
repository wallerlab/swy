package org.wallerlab.swy.service.fitness

import spock.lang.*

class ExponentialFitnessTest extends Specification{
	
	@Shared
	private ExponentialFitness exponentialFitness
	
	@Shared
	private double learningRateDecay = 1.0
	
	private double energy
	private double fitness
	
	def setupSpec() {
		exponentialFitness = new ExponentialFitness()
		exponentialFitness.setLearningRateDecay(learningRateDecay)
	}
	
	def cleanupSpec() {
		exponentialFitness = null
	}
	
	@Unroll
	def "Check the development of exponential fitness after several calls." () {
		
		expect:
		exponentialFitness.getFitness(energy).round(5) == fitness
			
		where:
		energy				|	fitness
		1.0				|	1.0
		-3.0				|	1.0
		-1.0				|	0.36788
		Double.NaN			|	0.0
	}
}
