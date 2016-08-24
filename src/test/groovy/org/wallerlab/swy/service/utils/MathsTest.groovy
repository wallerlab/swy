package org.wallerlab.swy.service.utils

import spock.lang.*

class MathsTest extends Specification  {

	private double[] vec, vec1, vec2
	private double[] array
	private int[] integerArray
	private double[][] matrixOne, matrixTwo
	private double angle
	private ArrayList<Double> list
	
	@Unroll
	def "The magnitude of #vec vector is #result but not #badResult  "(){
				
		expect:
		badResult != Maths.absVector(vec)
		result == Maths.absVector(vec).round(4)
		where:
	    vec				|	badResult	|	result
		[0, 0, 0]		|	18			|	0.0
		[5, 0, 0]		|	0			|	5.0
		[0, 1, 1]		|	-1			|	1.4142
		[8, -2, -0.0171]|	0			|	8.2462
	}

	@Unroll
	def "The cross product of two 3D vectors #vec1 and #vec2 is #result"(){

		given:
		double[] resultList=Maths.crossProduct(vec1,vec2)
		for (int entry=0; entry < resultList.length; entry++) {
			resultList[entry] = resultList[entry].round(4)
		}
		
		expect:
		result == resultList

		where:
		vec1		|	vec2		|	result
		[1, 4, 6]	|	[6, 1, 6]	|	[18, 30, -23]
		[1, 1, 1]	|	[0, 0, 0]	|	[0, 0, 0]
		[0, 1, 1]	|	[1, 1, 0]	|	[-1, 1, -1]
		[0, 0, 0]	|	[0, 0, 0]	|	[0, 0, 0]
		[-1, -4, -6]|	[-6, -1, -6]|	[18, 30, -23]
		[6, 1, 6]	|	[1, 4, 6]	|	[-18, -30, 23]
		[-0.2, 1, 3]|	[-3, 0.8, 1]|	[-1.4, -8.8, 2.84]
	}

	@Unroll
	def "The dot product of two 3D vectors #vec1 and #vec2 is #result"(){

		expect:
		result == Maths.dotProduct(vec1,vec2).round(4)

		where:
		vec1		|	vec2		|   result
		[1, 4, 6]	|	[6, 1, 6]	|   46
		[1, 1, 1]	|	[0, 0, 0]	|   0
		[0, 1, 1]	|	[1, 1, 0]	|   1
		[0, 0, 0]	|	[0, 0, 0]	|	0
		[1, 4, 6]	|	[-6, -1, -6]|	-46
		[-0.2, 1, 3]|	[-3, 0.8, 1]|	4.4
	}

	@Unroll
	def "The Average of a List #array is #result" (){

		given:
		list = Arrays.asList(array)
		integerArray = new int[array.length]
		if(isIntegerArray) {
			for (int index=0; index < array.length; index++) {
				integerArray[index] = array[index]
			}
		}
		
		expect:
		result == Maths.average(array)
		result == Maths.average(list)
		result == Maths.average(integerArray) || !isIntegerArray

		where:
		array		| result		|	isIntegerArray
		[1, 2, 3]	|   2			|	true
		[1, 1, 1]	|   1			|	true
		[0.0000001]	|   0.0000001	|	false
		[0, 0]		|   0			|	true
		[-4, 4]		|	0			|	true
		[-0.3, -0.2]|	-0.25		|	false
		[1, 3, 4, 6]|	3.5			|	true
	}
	
	@Unroll
	def "The standard deviation of #array is #result" (){
		
		given:
		list = Arrays.asList(array)

		expect:
		result == Maths.standardDeviation(array).round(4)
		result == Maths.standardDeviation(list).round(4)
		
		where:
		array		| result
		[1, 3, 5]	|   1.633
		[1, 1, 1]	|   0
		[0.0000001]	|   0
		[0, 0]		|   0
		[-4, 4]		|	4
		[-0.3, -0.2]|	0.05
	}
	
	@Unroll
	def "The product of matrices #matrixOne and #matrixTwo is #result" (){
		
		expect:
		result == Maths.matrixMultiply(matrixOne, matrixTwo)
		
		where:
		matrixOne									|	matrixTwo								|	result
		[[0, 0], [0, 0]]							|	[[0, 0], [0, 0]]						|	[[0, 0], [0, 0]]
		[[1, 0], [0, 1]]							|	[[7, 6], [5, 4]]						|	[[7, 6], [5, 4]]
		[[1, 2, 3], [4, 5, 6]]						|	[[6, -1], [3, 2], [0, -3]]				|	[[12, -6], [39, -12]]
		[[-0.1, 2, 0.5], [-1.5, 2, 0], [0.2, -2, 0]]|	[[1, 0.5, -1], [-1, -2, 1], [0, 1, 0.5]]|	[[-2.1, -3.55, 2.35], [-3.5, -4.75, 3.5], [2.2, 4.1, -2.2]]
	}
	
	@Unroll
	def "the turn matrix for a turn around #vec by an angle of #angle is #result" (){
		
		given:
		double[][] resultMatrix = Maths.generateTurnMatrix(vec, angle)
		for (int row=0; row < 3; row++) {
			for (int column=0; column < 3; column++) {
				resultMatrix[row][column] = resultMatrix[row][column].round(8)
			}
		}
		
		expect:
		result == resultMatrix
		
		where:
		vec						|	angle		|	result
		[1.0, 0.0, 0.0]			|	0.5*Math.PI	|	[[1.0, 0.0, 0.0], [0.0, 0.0, -1.0], [0.0, 1.0, 0.0]]
		[-1.0, 0.0, 0.0]		|	0.5*Math.PI	|	[[1.0, 0.0, 0.0], [0.0, 0.0, 1.0], [0.0, -1.0, 0.0]]
		[0.5, -0.5, 0.70710678]	|	2*Math.PI	|	[[1.0, 0.0, 0.0], [0.0, 1.0, 0.0], [0.0, 0.0, 1.0]]
	}
}
