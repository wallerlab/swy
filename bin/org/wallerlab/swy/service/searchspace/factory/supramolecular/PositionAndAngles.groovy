package org.wallerlab.swy.service.searchspace.factory.supramolecular

import org.wallerlab.swy.model.MolecularSystem
import org.wallerlab.swy.model.SearchSpace
import org.wallerlab.swy.service.searchspace.factory.SearchSpaceInterface
import org.wallerlab.swy.service.searchspace.factory.supramolecular.Angles
import org.wallerlab.swy.service.searchspace.factory.supramolecular.Position

import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

/**
 * Creates a complete supramolecular searchSpace.
 * 
 * @NeededProperties
 * <b><i><font color=purple>double</font></i> swy.service.searchSpace.granularity</b> -
 * Grid size/fineness of the search space. Although the search space is continuous this
 * value still influences the step size with which the searchSpace is explored.<br>
 * <b>limits:</b> >0 to 1. Values very very close to 0 will force the algorithms to not
 * really explore the search space any more. Could also cause memory problems and is
 * also not necessary because the search space is continuous (numerical accuracy) anyway.
 * 1 just does not make sense.<br>
 * <b>default:</b> <i>1/360=0.0027777778</i><br><br>
 */
@Service("supramolecular")
@Profile(["unstable"])
public class PositionAndAngles implements SearchSpaceInterface {
	
	private MolecularSystem molecularSystem
	private SearchSpace searchSpace
	
	@Value('${swy.service.searchSpace.granularity}')
	private double granularity
	
	/**
	 * Set the values in the SearchSpace model
	 */
	public SearchSpace generateSearchSpace() {
		searchSpace = new SearchSpace()
		// Search space is x y z coordinates for each molecule
		searchSpace.numberOfDimensions = 6*(molecularSystem.numberOfMolecules-1)
		searchSpace.startValues = new double[searchSpace.numberOfDimensions]
		searchSpace.endValues = new double[searchSpace.numberOfDimensions]
		searchSpace.periodic = new boolean[searchSpace.numberOfDimensions]
		searchSpace.stepSize = new double[searchSpace.numberOfDimensions]
		searchSpace.caption = new double[searchSpace.numberOfDimensions]
		
		int listIndex = 0
		
		// The listIndex = is here for a reason.
		listIndex = Position.addMoleculeCoordinatesToSearchSpace(molecularSystem, \
			listIndex, searchSpace, granularity)
		Angles.addMoleculeAnglesToSearchSpace(molecularSystem, listIndex, searchSpace, granularity)
		
		return searchSpace
	}
	
	public void setGranularity(double granularity) {
		this.granularity = granularity
	}
	
	@Required
	public void setMolecularSystem (MolecularSystem molecularSystem) {
		this.molecularSystem = molecularSystem
	}
}
