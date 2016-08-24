package org.wallerlab.swy.service.meta.annealing

import org.wallerlab.swy.model.SearchSpace;
import org.wallerlab.swy.model.main.Agent;
import org.wallerlab.swy.service.meta.AbstractMeta;
import org.wallerlab.swy.service.meta.MetaInterface;
import org.wallerlab.swy.service.energy.ModellingInterface;
import org.wallerlab.swy.service.conformation.factory.ConformationInterface;

import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.annotation.Lazy;


/* class too large, refactor out the temperatureController and durationController */
/** 
 * Simulated annealing implementation (primarily for comparisons to other metas)
 * 
 * A meta cycle is defined as an initial conformation, a temperature, and a duration.
 * 
 * WARNING: at the moment sendOutAgents should send a single agent! because next cycle starts with 
 * conformation from end of previous cycle. 
 * 
 * In the future parallelization may be possible, but for the moment only one agent should be used.
 * What should be possible is that an agent remembers its position from cycle to cycle (i.e. no update).
 *
 * Need to wire in the BomdGaussian wrapper(plugin) to add durationCurrent and temperatureCurrent.
 *
 * @NeededBeans
 * {@link #bombGaussian()}	 
 */
/*@Lazy
@Service
@Profile(["unstable"])*/
class SimulatedAnnealing extends AbstractMeta {
	
	private Agent agent;
	private SearchSpace searchSpace;
	private TemperatureController temperatureController;
	private DurationController durationController;
	
	public SimulatedAnnealing(SearchSpace searchSpace, Agent agent) {
		this.agent = agent;
		this.searchSpace = searchSpace;
	}
	
	@Override
	public int runNextMetaCycle() {
		iterationBestAgent = null;
		sendOutAgents();
		updateBomdParameters();
		return numberOfPopulationEvents;
	}
	
	/* A simple way to select how BOMD parameters are set */
	public void updateBomdParameters(){
		durationController.getCurrentDuration();
		temperatureController.getCurrentTemperature();
		return; 
	}
	
	@Override
	protected void collectParameterNames() {
		// TODO Auto-generated method stub	
	}

	@Override
	public void afterPropertiesSet() {
		// TODO Auto-generated method stub
	}
	
	@Override
	@Required
	@Value('${swy.service.anneal.durationController}')
	public void setdurationController(String newValue) {
		durationController = newValue;
	}
	
	@Override
	@Required
	@Value('${swy.service.anneal.temperatureController}')
	public void setTemperatureController(String newValue) {
		temperatureController = newValue;
	}
}
