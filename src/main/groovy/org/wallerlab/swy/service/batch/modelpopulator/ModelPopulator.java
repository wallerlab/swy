package org.wallerlab.swy.service.batch.modelpopulator;

/**
 * Interface for the ways to compute the models needed.
 */
public interface ModelPopulator<T> {
	
	 /** @param <T> the kind of model to be populated  */
	public void populateModel(T model);
	
}
