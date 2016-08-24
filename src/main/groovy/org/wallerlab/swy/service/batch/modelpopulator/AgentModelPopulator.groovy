package org.wallerlab.swy.service.batch.modelpopulator

import org.wallerlab.swy.model.main.Agent
import org.wallerlab.swy.service.energy.EnergyInterface
import org.wallerlab.swy.service.fitness.FitnessCalculator
import org.wallerlab.swy.service.meta.SolutionGenerator

import org.springframework.beans.factory.annotation.Required
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

/**
 * Does the calculations needed to populate the agent models
 */
@Service
@Profile(["untested"])
public class AgentModelPopulator implements ModelPopulator<Agent> {
	
	private double[] solution
	
	private double energy
	
	private SolutionGenerator solutionGenerator
	
	private EnergyInterface energyCalculator
	
	private FitnessCalculator fitnessCalculator
	
	public AgentModelPopulator() {
	}

	// TODO WARNING: NOT threadSafe!
	@Override	
	public void populateModel(Agent agent) {
		solution = solutionGenerator.generateSolution()
		assert (solution != null)
		for (double component: solution) {
			assert(!Double.isNaN(component))
		}
		energy = energyCalculator.getEnergy(solution)
		if (energy != Double.NaN) {
			agent.lastReasonableSolution = solution
			agent.lastReasonableEnergy = energy
			agent.fitness=fitnessCalculator.getFitness(energy)
		} else {
			agent.fitness=0.0
		}
		return
	}
	
	@Required
	public void setSolutionGenerator(SolutionGenerator solutionGenerator) {
		this.solutionGenerator = solutionGenerator
	}
	
	@Required
	private void setEnergyCalculator(EnergyInterface energyCalculator) {
		this.energyCalculator = energyCalculator
	}
	
	@Required
	private void setFitnessCalculator(FitnessCalculator fitnessCalculator) {
		this.fitnessCalculator = fitnessCalculator
	}

}
