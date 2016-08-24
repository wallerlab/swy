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
public class DurationController {

	private int durationMax;
	private int durationMin;
	private int currentDuration;
	
	/** 
	 * Check that either swy.service.anneal.temperatureMax or swy.service.anneal.temperatureMin is set.
	 * The swy.service.anneal.temperatureMax as default
	 */
	public void simpleDuration(){ 	
		if (!durationMax == NULL){
				currentDuration= durationMax;
			}else{
			if(!durationMin == NULL){	
				currentDuration= durationMin;
			 	}
			}
		return;
	}
	
	@Override
	@Required
	@Value('${swy.service.anneal.durationMin}')
	public void setDurationMin(double newValue) {
		durationMin = newValue;
	}
	
	@Override
	@Required
	@Value('${swy.service.anneal.durationMax}')
	public void setDurationMax(double newValue) {
		durationMax = newValue;
	}

	public void setCurrenDuration(int currentDuration) {
		this.currentDuration = currentDuration;
	}
	
	public int getCurrentDuration(){	
		return currentDuration;
	}
	
}
