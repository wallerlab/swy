package org.wallerlab.swy.service.energy;

import org.wallerlab.swy.model.Coordinates;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * A structure optimiser. Can be used to optimize structures
 * and get its energy.
 */
@Service
@Profile({"unstable"})
public class StructureOptimizer extends ExternalCodeJob {
	
	private OptimizationInterface modelFactory;
		
	/**
	 * @param solution Values that are used for the conformation
	 * generation.
	 * @return The structure with optimised coordinates.
	 */
	Coordinates getOptimizedStructure(double[] solution) {
		getEnergy(solution);
		coordinates = modelFactory.getOptimizedCoordinates();
		return coordinates;
	}

	/**
	 * @param solution Values that are used for the conformation
	 * generation.
	 * @return The energy of a structure with optimised coordinates.
	 * The unit needs to be looked up in the specific code used.
	 */
	@Override
	public double getEnergy(double[] solution) {
		generateCoordinates(solution);
		return modelFactory.optimizeStructure(coordinates);
	}
}
