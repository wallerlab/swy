package org.wallerlab.swy.service.meta

import org.wallerlab.swy.model.main.Agent
import org.wallerlab.swy.model.ProblemRepresentation
import org.wallerlab.swy.service.batch.BatchService

import org.springframework.beans.factory.annotation.Required
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

/*
 * The lifecycle of the beans of type AbstractMeta is a bit complicated. Here a little overview.<br>
 * 
 * NOT UP TO DATE!!!!!!!!
 * 
 * 1. Constructor: the parameter names are static, can be directly collected<br>
 * 2. Dependency Injection: the problemRepresentation (among some other properties) is injected<br>
 * 3. AbstractMetas initialize method: needs the problemRepresentation<br>
 * 4. the afterPropertiesSet method of the children: whatever they need, can access the problemRepresentation<br>
 * 
 */
/**
 * Typically a class containing a metaHeuristic algorithm extend this class. Almost all
 * necessities to fit into the program are already provided here.<br><br>
 * 
 * @NeededProperties
 * <b><i><font color=purple>int</font></i> swy.service.meta.numberOfAgents</b> -
 * Equals the number of population events within each metaCycle.<br>
 * <b>limits:</b> 1 to infinity.<br>
 * <b>default:</b> <i>20</i><br><br>
 * 
 * <b><i><font color=purple>int</font></i> swy.service.meta.metaCycles</b> -
 * The maximum numper of metaCycles that may be performed. Because the algorithms
 * should converge by themselves this only prevents the program from running forever.<br>
 * <b>limits:</b> 1 to infinity. High values (like 1000, for parameter optimisations
 * a lot more) are recommended.<br>
 * <b>default:</b> <i>1000</i><br><br>
 */
@Service
public abstract class AbstractMeta implements MetaInterface {

	protected ProblemRepresentation problemRepresentation

	protected BatchService<Agent> batchService
	
	protected int numberOfAgents
	protected int maximumNumberOfMetaCycles
	
	/**
	 * A counter to tell how often an agent model has been
	 * populated. Is normally equal to the number of energy
	 * requests.
	 */
	protected int numberOfPopulationEvents = 0
	/**
	 * An internal counter.
	 */
	protected int currentMetaCycle = 0
	protected boolean converged = false
	
	protected Agent[] agents
	protected Agent iterationBestAgent
	protected Agent globalBestAgent

	public AbstractMeta () {	
	}
	
	/**
	 * Creates empty new agent models
	 */
	protected void generateAgentModels() {
		assert(numberOfAgents > 0)
		agents = new Agent[numberOfAgents]
		for (int index in 0..numberOfAgents-1) {
			agents[index] = new Agent()
		}
	}
	
	/*
	 * In contrast to the earlier startSearch() this method only performs one metaCycle each
	 * time it is called. This is crucial for the proxy based aspects, because they could
	 * not work on an internal method call (like runNextMetaCycle()). Also the population
	 * of the (Agent) models could only happen after the algorithm has completely finished.
	 * Yet this method provides a main functionality the startSearch method also had: a check
	 * for the abort conditions. So even if this method is called infinitely often, the under-
	 * lying algorithm would still stop working some time.
	 */
	/**
	 * This is the central method to invoke from the outside to let the metaheuristic work.
	 */
	@Override
	public Agent[] getBetterAgents() {
			if (!converged && currentMetaCycle <= maximumNumberOfMetaCycles) {
					runNextMetaCycle();
					currentMetaCycle++
					return agents
			} else {
					return null
			}
	}
	
	/**
	 * populate the agent models
	 */
	protected void sendOutAgents() {
		batchService.populateAllModels(agents)
		numberOfPopulationEvents+=numberOfAgents
		for (Agent agent:agents) {
			updateBestValues(agent)
		}
	}
	
	private void updateBestValues(Agent newAgent) {
		if (newAgent.lastReasonableEnergy != Double.NaN) {
			if (!iterationBestAgent) {
				iterationBestAgent=newAgent
				if (!globalBestAgent) {
					globalBestAgent = newAgent
				}
			} else if (newAgent.lastReasonableEnergy <= iterationBestAgent.lastReasonableEnergy){
				iterationBestAgent = newAgent
				if (!globalBestAgent) {
					globalBestAgent = newAgent
				} else if (newAgent.lastReasonableEnergy <= globalBestAgent.lastReasonableEnergy){
					/*
					 * really keep the global best
					 */
					globalBestAgent = new Agent()
					globalBestAgent.fitness = newAgent.fitness
					globalBestAgent.lastReasonableEnergy = newAgent.lastReasonableEnergy
					globalBestAgent.lastReasonableSolution = new double[problemRepresentation.searchSpace.numberOfDimensions]
					for (int dimension=0; dimension < problemRepresentation.searchSpace.numberOfDimensions; dimension++) {
						globalBestAgent.lastReasonableSolution[dimension] = newAgent.lastReasonableSolution[dimension]
					}
				}
			}
		}
	}
	
	/**
	 * This method is supposed to contain the algorithm.
	 */
	public abstract int runNextMetaCycle()

	@Override
	public Agent getGlobalBestAgent() {
		return globalBestAgent
	}
	
	@Override
	public Agent getIterationBestAgent() {
		return iterationBestAgent
	}

	@Override
	public BatchService<Agent> getBatchService() {
		return batchService;
	}

	@Override
	@Required
	public void setBatchService(BatchService<Agent> batchService) {
		this.batchService = batchService;
	}
	
	@Override
	public ProblemRepresentation getProblemRepresentation() {
		return problemRepresentation;
	}

	@Override
	@Required
	public void setProblemRepresentation(ProblemRepresentation problemRepresentation) {
		this.problemRepresentation = problemRepresentation;
	}
	
	@Override
	public int getNumberOfAgents() {
		return numberOfAgents;
	}

	@Override
	@Required
	@Value('${swy.service.meta.numberOfAgents}')
	public void setNumberOfAgents(int newValue) {
		numberOfAgents = newValue
		return
	}

	@Override
	public int getMaximumNumberOfMetaCycles() {
		return maximumNumberOfMetaCycles;
	}

	@Override
	@Required
	@Value('${swy.service.meta.metaCycles}')
	public void setMaximumNumberOfMetaCycles(int newValue) {
		maximumNumberOfMetaCycles = newValue
		return
	}
	
	public Agent[] getAgents() {
		return agents
	}
	
	@Override
	public int getNumberOfPopulationEvents() {
		return numberOfPopulationEvents;
	}

	@Override
	public int getCurrentMetaCycle() {
		return currentMetaCycle;
	}

	@Override
	public boolean isConverged() {
		return converged;
	}
	
}
