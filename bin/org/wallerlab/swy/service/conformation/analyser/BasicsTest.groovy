package org.wallerlab.swy.service.conformation.analyser;

import spock.lang.*

import org.wallerlab.swy.model.Coordinates
import org.wallerlab.swy.model.MolecularSystem

public class BasicsTest extends Specification{

	private MolecularSystem molecularSystem
	
	private Coordinates coordinates

	/*
	 * This test is not using a stub and though also dependent on Maths.absVector().
	 */
	@Unroll
	def "Get distance between two Atoms within one molecule"() {
		
		given:
		molecularSystem = new MolecularSystem()
		double[][] emptyCoord = new double[1][1]
		emptyCoord[0][0]=0.0
		molecularSystem.xCoordInitial = emptyCoord
		molecularSystem.yCoordInitial = emptyCoord
		molecularSystem.zCoordInitial = emptyCoord
		coordinates = new Coordinates()
		coordinates.xCoord = [[-1.0, 1.0]]
		coordinates.yCoord = [[0.0, 0.0]]
		coordinates.zCoord = [[0.0, 0.0]]
		
		expect:
		Basics.getDistanceBetweenAtoms(coordinates, 0, 0, 0, 1).round(8) == 2.0
		Basics.getDistanceBetweenAtoms(coordinates, 0, 1, 0, 0).round(8) == 2.0
		Basics.getDistanceBetweenAtoms(coordinates, 0, 0, 0, 0).round(8) == 0.0
		
		cleanup:
		coordinates = null
		molecularSystem = null
	}

	/*
	 * This test is not using a stub and though also dependent on Maths.absVector().
	 */
	@Unroll
	def "Get distance between two Atoms of two different molecules"() {
		
		given:
		molecularSystem = new MolecularSystem()
		double[][] emptyCoord = new double[1][1]
		emptyCoord[0][0]=0.0
		molecularSystem.xCoordInitial = emptyCoord
		molecularSystem.yCoordInitial = emptyCoord
		molecularSystem.zCoordInitial = emptyCoord
		coordinates = new Coordinates()
		coordinates.xCoord = [[-0.5], [2.5]]
		coordinates.yCoord = [[0.5], [-0.9]]
		coordinates.zCoord = [[0.4], [2.0]]
		
		expect:
		Basics.getDistanceBetweenAtoms(coordinates, 0, 0, 1, 0).round(4) == 3.6770
		Basics.getDistanceBetweenAtoms(coordinates, 1, 0, 0, 0).round(4) == 3.6770
		
		cleanup:
		coordinates = null
		molecularSystem = null
	}

	@Unroll
	def "Test to create a list of atoms on one side of a bond in a simple system"() {
		
		given:
		molecularSystem = new MolecularSystem()
		molecularSystem.rings = [[[]]]
		molecularSystem.bonds = [[[1, 2, 3], [0, 4], [0], [0], [1, 5], [4]]];
		
		expect:
		Basics.getListOfAtomsOnOneSideOfBond(new ArrayList(), molecularSystem, 0, 2, 0) == [2]
		Basics.getListOfAtomsOnOneSideOfBond(new ArrayList(), molecularSystem, 0, 0, 2) == [0, 1, 3, 4, 5]
		Basics.getListOfAtomsOnOneSideOfBond(new ArrayList(), molecularSystem, 0, 0, 1) == [0, 2, 3]
		
		cleanup:
		molecularSystem = null
	}
	
	@Unroll
	def "Test to create a list of atoms on one side of a bond in a ring system"() {
		
		given:
		molecularSystem = new MolecularSystem()
		molecularSystem.rings = [[[0, 1, 2, 3, 4, 5]]]
		molecularSystem.bonds = [ [ [1, 5, 6], [0, 2, 8], [1, 3, 9], [2, 4, 10], [3, 5, 11], [0, 4, 12], [0, 7], [6], [1], [2], [3], [4], [5] ] ];
		
		expect:
		Basics.getListOfAtomsOnOneSideOfBond(new ArrayList(), molecularSystem, 0, 6, 0) == \
			[6, 7]
		Basics.getListOfAtomsOnOneSideOfBond(new ArrayList(), molecularSystem, 0, 0, 6) == \
			[0, 1, 2, 3, 4, 5, 8, 9, 10, 11, 12]
		
		cleanup:
		molecularSystem = null
	}
	
}
