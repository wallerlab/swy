package org.wallerlab.swy.service.energy

import org.wallerlab.swy.model.Coordinates
import org.wallerlab.swy.service.batch.modelpopulator.AgentModelPopulator;

import org.springframework.context.annotation.Profile;

/**
 * This interface is to be used for external programs which can perform
 * single point calculations as well as structural optimisations.
 */
interface OptimizationInterface extends ModellingInterface {
	
	double optimizeStructure(Coordinates coordinates);
	
	Coordinates getOptimizedCoordinates();
}
