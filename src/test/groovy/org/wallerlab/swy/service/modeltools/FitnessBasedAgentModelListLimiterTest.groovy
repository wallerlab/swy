package org.wallerlab.swy.service.modeltools;

import org.wallerlab.swy.model.main.Agent
import spock.lang.*;

public class FitnessBasedAgentModelListLimiterTest extends Specification{

	private FitnessBasedAgentModelListLimiter instance
	
	private List<Agent> agentList
	private double[] fitnesses
	private double[] shortenedFitnesses
	
	@Unroll
	def "Test shortening an Agent list via energy" () {
		given:
		instance = new FitnessBasedAgentModelListLimiter()
		instance.maximumNumberOfAgentModelsPerMetaModel = 3
		agentList = new ArrayList<Agent>(fitnesses.length)
		for (int agentIndex=0; agentIndex < fitnesses.length; agentIndex++) {
			agentList[agentIndex] = new Agent()
			agentList[agentIndex].fitness = fitnesses[agentIndex]
		}
		instance.shortenAgentModelList(agentList)
		
		expect:
		agentList.fitness == shortenedFitnesses
		
		where:
		fitnesses					|	shortenedFitnesses
		[0.1, 0.2, 0.3, 0.4, 0.5]	|	[0.5, 0.4, 0.3]
		[0.0]						|	[0.0]
		[0.1, 0.1, 0.1]				|	[0.1, 0.1, 0.1]
		[0.4, 0.2, 0.4, 0.2]		|	[0.4, 0.4, 0.2]
	}
	
}
