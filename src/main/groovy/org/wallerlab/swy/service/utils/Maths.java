package org.wallerlab.swy.service.utils;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Maths: a utility script for maths type subroutines.
 *
 * @author    Thomas Dresselhaus <t_dres03@uni-muenster.de>
 */
@Service
@Profile({"untested"})
public class Maths {

	/** The default constructor is private because this class is not supposed to be instanciated.*/
	private Maths() {
	}
	
	/**
	 * @param array
	 * @return Standard Deviation of the entries in array
	 */
	public static double standardDeviation(double[] array) {
		double returnValue=0;
		double ave=average(array);
		for (double element: array) {
			returnValue+=Math.pow((element-ave), 2);
		}
		return Math.sqrt( returnValue/array.length );
	}

	/**
	 * @param list
	 * @return Standard Deviation of the entries in list
	 */
	public static double standardDeviation(List<Double> list) {
		double returnValue=0;
		double ave=average(list);
		for (double element: list) {
			returnValue+=Math.pow((element-ave),2);
		}
		return Math.sqrt( returnValue/list.size() );
	}

	/**
	 * @param array
	 * @return average of the entries in array
	 */
	public static double average(int[] array) {
		double returnValue=0;
		for(int value:array) {
			returnValue+=value;
		}
		return returnValue/array.length;
	}
	
	/**
	 * @param array
	 * @return average of the entries in array
	 */
	public static double average(double[] array) {
		double returnValue=0;
		for(double value:array) {
			returnValue+=value;
		}
		return returnValue/array.length;
	}

	/**
	 * @param list
	 * @return average of the entries in list
	 */
	public static double average(List<Double> list) {
		double returnValue=0;
		for(double value:list) {
			returnValue+=value;
		}	
		return returnValue/list.size();
	}

	/**
	 * @param vec1
	 * @param vec2
	 * @return cross product of the vectors
	 */
	public static double[] crossProduct(double[] vec1,double[] vec2){
		double[] returnValue = new double[3];
		returnValue[0] = (vec1[1]*vec2[2] - vec1[2]*vec2[1]);
		returnValue[1] = (vec1[2]*vec2[0] - vec1[0]*vec2[2]);
		returnValue[2] = (vec1[0]*vec2[1] - vec1[1]*vec2[0]);
		return returnValue;
	}

	/**
	 * @param vec1
	 * @param vec2
	 * @return dot product of the vectors
	 */
	public static double dotProduct(double[] vec1,double[] vec2){
		double result=0;
		for (int i=0; i < vec1.length; i++){
			result += vec1[i]*vec2[i];
		}
		return result;
	}

	/**
	 *@param vec
	 *@return the absolute of vec
	 */
	public static double absVector(double[] vec){
		assert(vec.length==3);
		return Math.sqrt(vec[0]*vec[0]+vec[1]*vec[1]+vec[2]*vec[2]);
	}

	/**
	 * @param vec
	 * @return vec, normalised to 1
	 */
	public static double[] normalizeVec(double[] vec){
		assert(vec.length==3);
		double absolute=Maths.absVector(vec);
		double[] returnValue = new double[3];
		returnValue[0] = vec[0]/absolute;
		returnValue[1] = vec[1]/absolute;
		returnValue[2] = vec[2]/absolute;
		return returnValue;
	}

	public static double[][] matrixMultiply(double[][] firstMatrix, double[][] secondMatrix) {
		double[][] returnValue = new double[firstMatrix.length][secondMatrix[0].length];
		assert(firstMatrix[0].length == secondMatrix.length): "Maths: WARNING - Matrix multiplication failed.";
		for (int i=0; i < firstMatrix.length; i++) {
			for (int j=0; j < secondMatrix[0].length; j++) {
				returnValue[i][j] = 0.0;
				for (int k=0; k < firstMatrix[0].length; k++) {
					returnValue[i][j] += firstMatrix[i][k] * secondMatrix[k][j];
				}
			}
		}
		return returnValue;
	}

	/**
	 * @param rotationAxis
	 * @param turnAngle
	 * @return rotation matrix
	 */
	public static double[][] generateTurnMatrix(double[] rotationAxis, double turnAngle) {
		assert(rotationAxis.length==3);
		assert(Math.round(absVector(rotationAxis)*1000)==1000);
		
		double sinAng=Math.sin(turnAngle);
		double cosAng=Math.cos(turnAngle);

		double[][] rotationMatrix = new double[3][3];

		rotationMatrix[0][0] = cosAng + rotationAxis[0]*rotationAxis[0]*(1.0 - cosAng);
		rotationMatrix[0][1] = rotationAxis[0]*rotationAxis[1]*(1.0- cosAng) - rotationAxis[2]*sinAng;
		rotationMatrix[0][2] = rotationAxis[0]*rotationAxis[2]*(1.0 - cosAng) + rotationAxis[1]*sinAng;
		
		rotationMatrix[1][0] = rotationAxis[1]*rotationAxis[0]*(1.0 - cosAng) + rotationAxis[2]*sinAng;
		rotationMatrix[1][1] = cosAng + rotationAxis[1]*rotationAxis[1]*(1.0 - cosAng);
		rotationMatrix[1][2] = rotationAxis[1]*rotationAxis[2]*(1.0- cosAng) - rotationAxis[0]*sinAng;
		
		rotationMatrix[2][0] = rotationAxis[2]*rotationAxis[0]*(1.0- cosAng) - rotationAxis[1]*sinAng;
		rotationMatrix[2][1] = rotationAxis[2]*rotationAxis[1]*(1.0 - cosAng) + rotationAxis[0]*sinAng;
		rotationMatrix[2][2] = cosAng + rotationAxis[2]*rotationAxis[2]*(1.0 - cosAng);

		return rotationMatrix;
	}
}
