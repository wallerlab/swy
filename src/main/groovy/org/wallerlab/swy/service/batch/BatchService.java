package org.wallerlab.swy.service.batch;

import org.wallerlab.swy.service.batch.modelpopulator.ModelPopulator;

/** 
 * Classes implementing this interface are capable of running batches of jobs, where each job is
 * the population of a domain model. A good point to hook in parallelization.
 * 
 * @author t_dres03
 *
 * @param <T> the type of Model to work on
 * 
 */
public interface BatchService<T> {

	public void populateAllModels(T[] models);
	
	public void setModelPopulator(ModelPopulator<T> modelPopulator);
	
	public ModelPopulator<T> getModelPopulator();

}
