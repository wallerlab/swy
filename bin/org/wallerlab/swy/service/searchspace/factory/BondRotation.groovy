package org.wallerlab.swy.service.searchspace.factory

import org.wallerlab.swy.model.MolecularSystem
import org.wallerlab.swy.model.SearchSpace
import org.wallerlab.swy.service.conformation.analyser.Rings
import org.wallerlab.swy.service.conformation.analyser.TurnableBonds

import org.springframework.context.annotation.Profile
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

/**
 * Sets up a dihedral searchSpace.
 * 
 * @author t_dres03
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
@Service
@Profile(["untested"])
class BondRotation implements SearchSpaceInterface {

	private MolecularSystem molecularSystem
	
	private SearchSpace searchSpace

	@Value('${swy.service.searchSpace.granularity}')
	private double granularity
	
	public SearchSpace generateSearchSpace() {

		Rings.createRingList(molecularSystem)

		TurnableBonds.createListOfTurnableBonds(molecularSystem)
		TurnableBonds.createAtomsToTurnList(molecularSystem)

		searchSpace = new SearchSpace()
		setupSearchSpace()

		return searchSpace
	}

	/**
	 *  SearchSpace will consist of angles, thus from 0 to 360 degrees (means 2*PI) and periodic
	 */
	private void setupSearchSpace() {

		// Clear the number of dimensions
		searchSpace.numberOfDimensions = 0

		// !!!!!!!!!!!!!!!!!!!!!!!!!!! Check whether this makes sense !!!!!!!!!!!!!!!!!!!!!!!!!!!!
		for (int molecule in 0..molecularSystem.numberOfMolecules-1) {
			searchSpace.numberOfDimensions += molecularSystem.turnableBonds[molecule].size()
		}

		searchSpace.startValues = new double[searchSpace.numberOfDimensions]
		searchSpace.endValues = new double[searchSpace.numberOfDimensions]
		searchSpace.periodic = new boolean[searchSpace.numberOfDimensions]
		searchSpace.stepSize = new double[searchSpace.numberOfDimensions]
		searchSpace.caption = new ArrayList<String>(searchSpace.numberOfDimensions)
		for (int dimension in 0..searchSpace.numberOfDimensions-1) {
			setSearchSpaceDimensionToDihedral(dimension, searchSpace, granularity)
		}
		/* set the caption */
		for (int molecule in 0..molecularSystem.numberOfMolecules-1) {
			for (def turnableBond in molecularSystem.turnableBonds[molecule]) {
				searchSpace.caption << "Dihedral_${turnableBond[0]}_to_${turnableBond[1]}"
			}
		}
	}

	public static void setSearchSpaceDimensionToDihedral(int listIndex, \
			SearchSpace searchSpace, double granularity) {
		searchSpace.startValues[listIndex] = 0.0
		searchSpace.endValues[listIndex] = 2*Math.PI
		searchSpace.periodic[listIndex] = true
		searchSpace.stepSize[listIndex] = StepSizeCalculator.calculateStepSize(0.0, 2*Math.PI, granularity)
	}
	
	public void setGranularity(double granularity) {
		this.granularity = granularity
	}

	@Required
	public void setMolecularSystem (MolecularSystem molecularSystem) {
		this.molecularSystem = molecularSystem
	}
}
