package org.wallerlab.swy.service.meta

import org.wallerlab.swy.model.ProblemRepresentation
import org.wallerlab.swy.model.SearchSpace
import org.wallerlab.swy.model.main.Agent;
import org.wallerlab.swy.service.batch.BatchService;

import org.springframework.beans.factory.InitializingBean
import org.springframework.context.annotation.Profile;

/**
 * This interface is to be used by all metaHeuristic algorithms,
 * as well as by other algorithms (like brute force), which ACTUALLY
 * are no metaHeuristics. Those algorithms are the heart of this
 * program.
 */
public interface MetaInterface extends InitializingBean {
	/**
	 * @return hopefully improved agents or null if no more meta is allowed.
	 * Reasons for null: converged or maximumNumberOfMetaCycles reached.
	 */
	public Agent[] getBetterAgents();
		
	public int runNextMetaCycle();
	
	public Agent getGlobalBestAgent();
	
	public Agent getIterationBestAgent();
		
	public BatchService getBatchService();
	
	public void setBatchService(BatchService<Agent> batchService);
	
	public ProblemRepresentation getProblemRepresentation();
	
	public void setProblemRepresentation(ProblemRepresentation problemRepresentation);
			
	public int getNumberOfAgents();
	
	public void setNumberOfAgents(int newValue);
	
	public int getMaximumNumberOfMetaCycles();
	
	public void setMaximumNumberOfMetaCycles(int newValue);
	
	public int getNumberOfPopulationEvents();
	
	public int getCurrentMetaCycle();
	
	public boolean isConverged();
	
}
