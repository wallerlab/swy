package org.wallerlab.swy.service.conformation.manipulation;

import org.wallerlab.swy.model.Coordinates
import org.wallerlab.swy.model.MolecularSystem
import org.wallerlab.swy.service.modeltools.CoordinateFactory

import spock.lang.*

class TurnAroundAxisTest extends Specification{

	private MolecularSystem molecularSystem
	private Coordinates coordinates
	
	private int[] atomsToRotate
	private double[] centerOfRotation
	private double[][] turnMatrix
	
	private double[] atomOneCoords, atomTwoCoords
	private double[] xCoords, yCoords, zCoords
	
	@Unroll
	def "Test to manipulate simple angle"() {
		
		given:
		molecularSystem = new MolecularSystem()
		molecularSystem.xCoordInitial = [[0.0, 1.0, 2.0]]
		molecularSystem.yCoordInitial = [[0.0, 0.0, 0.0]]
		molecularSystem.zCoordInitial = [[1.0, 1.0, 1.0]]
		coordinates = CoordinateFactory.newCoordinates(molecularSystem)

		turnMatrix = [[0.0, 0.0, 1.0], [0.0, 1.0, 0.0], [-1.0, 0.0, 0.0]]
		
		TurnAroundAxis.turnAroundAxis(coordinates, 0, atomsToRotate, turnMatrix, centerOfRotation)	
		
		expect:
		coordinates.xCoord[0][1] == atomOneCoords[0]
		coordinates.yCoord[0][1] == atomOneCoords[1]
		coordinates.zCoord[0][1] == atomOneCoords[2]
		coordinates.xCoord[0][2] == atomTwoCoords[0]
		coordinates.yCoord[0][2] == atomTwoCoords[1]
		coordinates.zCoord[0][2] == atomTwoCoords[2]
		
		cleanup:
		molecularSystem = null
		
		where:
		atomsToRotate		|	centerOfRotation	|	atomOneCoords	|	atomTwoCoords
		[2]					|	[1.0, 0.0, 1.0]		|	[1.0, 0.0, 1.0]	|	[1.0, 0.0, 0.0]
		[1, 2]				|	[0.0, 0.0, 1.0]		|	[0.0, 0.0, 0.0]	|	[0.0, 0.0, -1.0]
		[2]					|	[2.0, 0.0, 1.0]		|	[1.0, 0.0, 1.0]	|	[2.0, 0.0, 1.0]
	}
	
	@Unroll
	def "Test to rotate a cross around itself"() {
		
		given:
		molecularSystem = new MolecularSystem()
		molecularSystem.xCoordInitial = [[0.0, 1.0, 2.0, 1.0, 1.0]]
		molecularSystem.yCoordInitial = [[0.0, 0.0, 0.0, 1.0, -1.0]]
		molecularSystem.zCoordInitial = [[1.0, 1.0, 1.0, 1.0, 1.0]]
		molecularSystem.numberOfAtomsInMolecule = [5]
		coordinates = CoordinateFactory.newCoordinates(molecularSystem)

		centerOfRotation = [1.0, 0.0, 1.0]
		
		TurnAroundAxis.turnAroundAxis(molecularSystem, coordinates, 0, turnMatrix, centerOfRotation)
		
		expect:
		coordinates.xCoord[0] == xCoords
		coordinates.yCoord[0] == yCoords
		coordinates.zCoord[0] == zCoords
		
		cleanup:
		molecularSystem = null
		
		where:
		turnMatrix											|	xCoords						|	yCoords						|	zCoords
		[[1.0, 0.0, 0.0], [0.0, 0.0, -1.0], [0.0, 1.0, 0.0]]|	[0.0, 1.0, 2.0, 1.0, 1.0]	|	[0.0, 0.0, 0.0, 0.0, 0.0]	|	[1.0, 1.0, 1.0, 2.0, 0.0]
		[[0.0, 1.0, 0.0], [-1.0, 0.0, 0.0], [0.0, 0.0, 1.0]]|	[1.0, 1.0, 1.0, 2.0, 0.0]	|	[1.0, 0.0, -1.0, 0.0, 0.0]	|	[1.0, 1.0, 1.0, 1.0, 1.0]
		[[1.0, 0.0, 0.0], [0.0, 1.0, 0.0], [0.0, 0.0, 1.0]]	|	[0.0, 1.0, 2.0, 1.0, 1.0]	|	[0.0, 0.0, 0.0, 1.0, -1.0]	|	[1.0, 1.0, 1.0, 1.0, 1.0]
	}
}
