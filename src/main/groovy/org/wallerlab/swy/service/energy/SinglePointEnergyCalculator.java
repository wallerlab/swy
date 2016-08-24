package org.wallerlab.swy.service.energy;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * A Single Point Calculator. Used when energy requests should be
 * answered with a single point calculation.
 */
@Service
@Profile({"untested"})
public class SinglePointEnergyCalculator extends ExternalCodeJob{
	
	private ModellingInterface modelFactory;
	
	/**
	 * Just a method similar to {@link StructureOptimizer#getEnergy(double[])}
	 * 
	 * @param solution Values that are used for the conformation
	 * generation.
	 * 
	 * @return A single point energy. The unit needs to be looked up
	 * in the specific code used.
	 */
	public synchronized double getEnergy(double[] solution) {
		generateCoordinates(solution);
		return modelFactory.getSinglePointEnergy(coordinates);
	}
	
	public void setModelFactory(ModellingInterface modelFactory) {
		this.modelFactory = modelFactory;
	}
}
