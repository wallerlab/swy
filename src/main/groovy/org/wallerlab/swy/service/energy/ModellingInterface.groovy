package org.wallerlab.swy.service.energy

import org.wallerlab.swy.model.Coordinates

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service


/**
 * This standard interface for external programs is to be used
 * if the external code can produce (only) single point energies.
 */
public interface ModellingInterface {
	
	public double getSinglePointEnergy(Coordinates coordinates);

}
