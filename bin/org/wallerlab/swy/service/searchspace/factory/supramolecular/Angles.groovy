package org.wallerlab.swy.service.searchspace.factory.supramolecular

import org.wallerlab.swy.model.SearchSpace
import org.wallerlab.swy.model.MolecularSystem
import org.wallerlab.swy.service.searchspace.factory.SearchSpaceInterface
import org.wallerlab.swy.service.searchspace.factory.StepSizeCalculator;

import org.springframework.beans.factory.annotation.Required
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

/**
 * Determines a supramolecular searchSpace for Angles.
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
@Service("supramolecularAngles")
@Profile(["unstable"])
public class Angles implements SearchSpaceInterface {
	
	private MolecularSystem molecularSystem
	
	private SearchSpace searchSpace
	
	@Value('${swy.service.searchSpace.granularity}')
	private double granularity
	
	/**
	 *  Search space is the euler angles for each molecule
	 */
	public SearchSpace generateSearchSpace() {
		searchSpace = new SearchSpace()
		searchSpace.numberOfDimensions = 3*(molecularSystem.numberOfMolecules-1)
		searchSpace.startValues = new double[searchSpace.numberOfDimensions]
		searchSpace.endValues = new double[searchSpace.numberOfDimensions]
		searchSpace.periodic = new boolean[searchSpace.numberOfDimensions]
		searchSpace.stepSize = new double[searchSpace.numberOfDimensions]
		searchSpace.caption = new double[searchSpace.numberOfDimensions]
		
		int listIndex = 0
		
		addMoleculeAnglesToSearchSpace(molecularSystem, listIndex, searchSpace, granularity)
		
		return searchSpace
	}
	
	/**
	 * @param listIndex the last position in the list that has been filled. -1 at start.
	 * @param searchSpace
	 * @param granularity
	 * 
	 * @return the new listIndex
	 */
	public static int addMoleculeAnglesToSearchSpace(MolecularSystem molecularSystem, \
			int listIndex, SearchSpace searchSpace, double granularity) {
		for (int molecule in 1..molecularSystem.numberOfMolecules - 1) {
			searchSpace.caption[listIndex] = "alpha"
			searchSpace.caption[listIndex+1] = "beta"
			searchSpace.caption[listIndex+2] = "gamma"
			for (int coordinate in 0..2) {
				searchSpace.startValues[listIndex] = 0.0
				searchSpace.endValues[listIndex] = 2*Math.PI
				searchSpace.periodic[listIndex] = true
				searchSpace.stepSize[listIndex] = StepSizeCalculator.calculateStepSize(0, 2*Math.PI, granularity)
				listIndex++
			}
		}
		return listIndex
	}
	
	public void setGranularity(double granularity) {
		this.granularity = granularity
	}
	
	@Required
	public void setMolecularSystem (MolecularSystem molecularSystem) {
		this.molecularSystem = molecularSystem
	}
}
