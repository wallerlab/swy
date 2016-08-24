package org.wallerlab.swy.service.batch.modelpopulator;

import org.wallerlab.swy.model.main.Meta;
import org.wallerlab.swy.model.main.Run;
import org.wallerlab.swy.service.batch.BatchService;

import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Profile;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;

/**
 * This class populates a run model with only one meta.
 * That means no hyper is used.
 * 
 * @author t_dres03
 *
 */
@Service
@Profile({"untested"})
public class SingleMetaRunModelPopulator implements ModelPopulator<Run> {

	private BatchService<Meta> metaBatchService;

	@Override
	public void populateModel(Run model) {
		// TODO meta needs to be reinitialized after every run
		ArrayList<Meta> metaList = new ArrayList<Meta>(1);
		model.setMetas(metaList);
		metaList.add(new Meta());
		metaBatchService.populateAllModels(metaList.toArray(new Meta[1]));
	}

	@Required
	public void setMetaBatchService(BatchService<Meta> metaBatchService) {
		this.metaBatchService = metaBatchService;
	}

}
