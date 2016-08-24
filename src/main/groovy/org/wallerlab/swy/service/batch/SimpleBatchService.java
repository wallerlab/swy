package org.wallerlab.swy.service.batch;

import org.wallerlab.swy.service.batch.modelpopulator.ModelPopulator;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.annotation.Profile;

/**
 * Populates models sequentially, i.e. using a simple loop.
 * Like all BatchServices the corresponding bean(s) cannot be created automatically (Service/Autowired
 * annotations), because it is needed in different flavours at the same time.
 * 
 * @author t_dres03
 *
 * @param <T> the type of Model to work on
 * 
 */
@Profile({"untested"})
public class SimpleBatchService<T> implements BatchService<T> {
	
	private ModelPopulator<T> modelPopulator;
	
	@Override
	public void populateAllModels(T[] models) {
		for (T model: models) {
			modelPopulator.populateModel(model);
		}
	}

	public ModelPopulator<T> getModelPopulator() {
		return modelPopulator;
	}

	@Required
	public void setModelPopulator(ModelPopulator<T> modelPopulator) {
		this.modelPopulator = modelPopulator;
	}	
}