package org.wallerlab.swy.service.energy;

import org.wallerlab.swy.model.Coordinates;
import org.wallerlab.swy.service.conformation.factory.ConformationInterface;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;

/**
 * This class is the parent of the different types of calculations
 * which an external quantum chemical, force field or a similar program
 * can perform. It is capable of creating the needed conformation to
 * start calculations.
 */
@Service
public abstract class ExternalCodeJob implements EnergyInterface {
	
	protected ConformationInterface conformationFactory;
	
	protected Coordinates coordinates;
	
	@Required
	public void setConformationFactory(ConformationInterface conformationFactory) {
		this.conformationFactory = conformationFactory;
	}
	
	protected final void generateCoordinates(double[] solution) {
		coordinates = conformationFactory.newConformation(solution);
	}
}
