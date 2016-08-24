package org.wallerlab.swy.service.modeltools;

import org.wallerlab.swy.model.Coordinates;
import org.wallerlab.swy.model.MolecularSystem;

import org.springframework.context.annotation.Profile;

import com.rits.cloning.Cloner;

/**
 * Provides fresh coordinate models with the initial coordinates.
 * 
 * @author t_dres03
 */
@Profile({ "untested" })
public class CoordinateFactory {
	
	private static Cloner cloner = new Cloner();
		
	/**
	 *  Clone initial coordinates
	 */
	public static Coordinates newCoordinates(MolecularSystem molecularSystem) {
		Coordinates coordinates = new Coordinates();
		coordinates.xCoord = cloner.deepClone(molecularSystem.xCoordInitial);
		coordinates.yCoord = cloner.deepClone(molecularSystem.yCoordInitial);
		coordinates.zCoord = cloner.deepClone(molecularSystem.zCoordInitial);
		return coordinates;
	}
	
}
