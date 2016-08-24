package org.wallerlab.swy.service.searchspace.factory

import org.wallerlab.swy.model.MolecularSystem
import org.wallerlab.swy.model.Coordinates
import org.wallerlab.swy.model.SearchSpace
import org.wallerlab.swy.service.conformation.analyser.*
import org.wallerlab.swy.service.modeltools.CoordinateFactory;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service;

/**
 * Z-matrix style searchSpace factory. Like the {@link org.wallerlab.swy.service.searchspace.factory.BondRotation},
 * but with additional flexibility for bond lengths and angles.
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
@Service("Z Matrix Style")
@Profile(["unstable"])
public class ZMatrixStyle implements SearchSpaceInterface {

	/** Which position in lists in the search space is to be filled/has just been filled */
	private int listIndex
	
	private MolecularSystem molecularSystem
	
	private SearchSpace searchSpace
	
	private Coordinates coordinates
	
	@Value('${swy.service.searchSpace.granularity}')
	private double granularity
	
	// Constructor
	public ZMatrixStyle() {
		listIndex = 0
	}
	
	public SearchSpace generateSearchSpace() {
		// Initialize
		searchSpace = new SearchSpace()
		searchSpace.numberOfDimensions = 0
		
		Rings.createRingList()
		
		// Bond length
		prepareBonds()
		// Angles
		prepareAngles()
		// Dihedrals
		prepareDihedrals()
		
		setupSearchSpace()
		
		println "After creating the searchSpace: numberOfDimensions = ${searchSpace.numberOfDimensions}"
		
		return searchSpace
	}
	
	private static void prepareBonds() {

		StretchableBonds.createListOfStretchableBonds()
		StretchableBonds.createAtomsToMoveList()
		
	}

	private static void prepareAngles() {
		BendableAngles.createListOfBendableAngles()
		BendableAngles.createAtomsToBendList()
	}

	private static void prepareDihedrals() {
		TurnableBonds.createListOfTurnableBonds()
		TurnableBonds.createAtomsToTurnList()
	}
	
	private void setupSearchSpace() {
		
		// TODO CHECK THAT: atm the bond and angle flexibility is set to arbitrary made-up numbers.
		
		for (int molecule in 0..molecularSystem.numberOfMolecules-1) {
			searchSpace.numberOfDimensions += molecularSystem.stretchableBonds[molecule].size()
			searchSpace.numberOfDimensions += molecularSystem.bendableAngles[molecule].size()
			searchSpace.numberOfDimensions += molecularSystem.turnableBonds[molecule].size()
		}
		
		searchSpace.startValues = new double[searchSpace.numberOfDimensions]
		searchSpace.endValues = new double[searchSpace.numberOfDimensions]
		searchSpace.periodic = new boolean[searchSpace.numberOfDimensions]
		searchSpace.stepSize = new double[searchSpace.numberOfDimensions]
		searchSpace.caption = new ArrayList<String>(searchSpace.numberOfDimensions)
		
		/* 
		 * Around which standard value is the searchSpace spanned 
		 * (to scale bond lengths rather than giving flexibility in an absolute fashion)
		 */
		double centerValue
		
		for (int molecule in 0..molecularSystem.numberOfMolecules-1) {
			for (def bond in molecularSystem.stretchableBonds[molecule]) {
				coordinates = CoordinateFactory.newCoordinates()
				centerValue =  Basics.getDistanceBetweenAtoms(coordinates, molecule, bond[0], molecule, bond[1])
				// Flexibility is fixed (atm); +/- 10% possible
				searchSpace.startValues[listIndex] = centerValue * 0.90 - centerValue
				searchSpace.endValues[listIndex] = centerValue * 1.1 - centerValue
//				// Sensitivity of bonds and angles is only a part of sensitivity of dihedrals. Atm fixed to 10 times less possibilities
//				// Make sure that step size is not greater than the difference between lowest and highest possible value
//				if (Input.standardStepSizeInPercent * 20 < (searchSpace.endValues[listIndex] - searchSpace.startValues[listIndex]) ) {
					searchSpace.stepSize[listIndex] = (searchSpace.endValues[listIndex] - searchSpace.startValues[listIndex]) * granularity
//				}else{
					// At least center should be reached. -> min 3 possibilities: lowest, center, highest
//					searchSpace.stepSize[listIndex] = (searchSpace.endValues[listIndex] - searchSpace.startValues[listIndex]) / 2
//				}
				listIndex++
			}
		}
		/* Fill search space with angle info */
		for (int molecule in 0..molecularSystem.numberOfMolecules-1) {
			for (def bendableAngle in molecularSystem.bendableAngles[molecule]) {
				setSearchSpaceDimensionToAngle(listIndex, searchSpace, granularity)
				listIndex++
			}
		}
		
		/* Fill search space with dihedral stuff */
		for (int molecule in 0..molecularSystem.numberOfMolecules-1) {
			for (turnableBond in molecularSystem.turnableBonds[molecule]) {
				BondRotation.setSearchSpaceDimensionToDihedral(listIndex, searchSpace, granularity)
				listIndex++
			}
		}
	}
	
	/**
	 *  Sensitivity of bonds and angles is only a part of sensitivity of dihedrals.
	 *  Atm fixed to 50 times less possibilities
	 *  Make sure that step size is not greater than the difference between 
	 *  lowest and highest possible value
	 *  Flexibility is fixed (atm); +/- 18° possible
	 *
	 *  @param listIndex
	 */
	public static void setSearchSpaceDimensionToAngle(int listIndex, \
			SearchSpace searchSpace, double granularity) {
		searchSpace.startValues[listIndex] = -Math.PI/10
		searchSpace.endValues[listIndex] = Math.PI/10
		searchSpace.periodic[listIndex] = false
//		if (Input.standardStepSizeInPercent * 40 < Math.PI/10 ) {
			searchSpace.stepSize[listIndex] = StepSizeCalculator.calculateStepSize(-Math.PI/10, Math.PI/10, granularity)
//		}else{
//			// At least center should be reached. -> min 3 possibilities: lowest, center, highest
//			searchSpace.stepSize[listIndex] = Math.PI/20
//		}
	}
	
	@Required
	public void setMolecularSystem (MolecularSystem molecularSystem) {
		this.molecularSystem = molecularSystem
	}
}
