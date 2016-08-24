package org.wallerlab.swy.service.meta.ant;

import org.wallerlab.swy.service.energy.HyperHeuristicLoopback;
import org.wallerlab.swy.service.meta.ControllingMeta;

import org.springframework.context.annotation.Profile;

@Profile({ "unstable" })
public class ControllingAntColonyOptimizer extends AntColonyOptimizer implements
		ControllingMeta {

	@SuppressWarnings("unused")
	private HyperHeuristicLoopback energyCalculator;
	
	@Override
	public void setEnergyCalculator(HyperHeuristicLoopback energyCalculator) {
		this.energyCalculator = energyCalculator;
	}

}
