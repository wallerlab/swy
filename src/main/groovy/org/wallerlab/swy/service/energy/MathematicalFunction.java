package org.wallerlab.swy.service.energy;

import org.wallerlab.swy.model.SearchSpace;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 *  This class creates an energy just based on the solution and a mathematical function
 */
@Service
@Profile({"untested"})
public class MathematicalFunction implements EnergyInterface {

	private double energy;
	private SearchSpace searchSpace;
	
	public MathematicalFunction() {
	}
	
	@Override
	public synchronized double getEnergy(double[] solution) {
		
		energy = 0.0;
		
		for (int component=0; component < (solution.length); component++) {
			// normalized to searchSpace dimensions (function is designed for values between 0 and 1)
			energy += function(solution[component]
						/ (searchSpace.endValues[component] - searchSpace.startValues[component]));
		}
		return energy;
	}

	/** 
	 * Used function is the sum of a squared sinus function and a parabolic function
	 * to simulate a global and a local minimum.<br>
	 * global minimum: -1 at x=0, x=2*PI<br>
	 * local minimum: -0.95 at x=PI<br>
	 * global maximum: approx. 0.0125 at x close to PI/2 or 3*PI/2
	 */
	private double function(double x) {
		return ( -0.95 + Math.pow(Math.sin(x*Math.PI*2),2) - ( 0.2 * Math.pow((x-0.5),2) ));
	}

	@Required
	public void setSearchSpace(SearchSpace searchSpace) {
		this.searchSpace = searchSpace;
	}
	
}
