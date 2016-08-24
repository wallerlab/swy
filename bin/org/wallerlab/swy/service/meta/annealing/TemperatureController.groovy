package org.wallerlab.swy.service.meta.annealing;

import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.annotation.Lazy;

/*@Lazy
@Service
@Profile(["unstable"])*/
public class TemperatureController {

	private int temperatureMax;
	private int temperatureMin;
	private int currentTemperature;
	private int currentMetaCycle;
	
	public int getCurrentTemperature() {
		this.currentMetaCycle = currentMetaCycle
		return currentTemperature;
	}
	
	/**
	 * Simple Oscillating temperature for Annealing
	 */
	public void simpleTemperatureOscillator(){ 		
		if (SimulatedAnnealing.getCurrentMetaCycle() %2 != 0){
				currentTemperature= temperatureMax;
			}else{
				currentTemperature= temperatureMin;
			}
		return;
	}
	
	@Override
	@Required
	@Value('${swy.service.anneal.temperatureMin}')
	public void setTemperatureMin(double newValue) {
		temperatureMin = newValue;
	}
	
	@Override
	@Required
	@Value('${swy.service.anneal.temperatureMax}')
	public void setTemperatureMax(double newValue) {
		temperatureMax = newValue;
	}	

	public void setTemperatureCurrent(int temperatureCurrent) {
		this.temperatureCurrent = temperatureCurrent;
	}
}
