package org.wallerlab.swy.service.batch.modelpopulator;

import org.wallerlab.swy.model.main.Run;

import org.springframework.context.annotation.Profile;

/**
 * A hyper is not more than a way to put more than one meta into a run.
 */
@Profile({ "unstable" })
public class HyperRunModelPopulator implements ModelPopulator<Run> {

	@Override
	public void populateModel(Run model) {
		// TODO not implemented
	}
}
