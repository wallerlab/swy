package org.wallerlab.swy.service.conformation.factory;

import org.wallerlab.swy.model.Coordinates

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service

/**
 * Classes implementing this interface can interpret an array of doubles
 * as decisions to create one specific conformation/set of Coordinates.
 */
public interface ConformationInterface {	
	
	public Coordinates newConformation(double[] solution);
	
}
