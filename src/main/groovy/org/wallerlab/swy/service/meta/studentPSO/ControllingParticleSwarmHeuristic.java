package org.wallerlab.swy.service.meta.studentPSO;

import org.wallerlab.swy.service.energy.HyperHeuristicLoopback;
import org.wallerlab.swy.service.meta.ControllingMeta;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * To use the {@link org.wallerlab.swy.service.meta.studentPSO.ParticleSwarmHeuristic}
 * for parameter optimisations a few parameters need to be
 * scaled. That is done automatically by using this slightly
 * modified class.
 * 
 * @author t_dres03, d_jans04, f_lied01
 * 
 * @NeededProperties
 * Look at {@link org.wallerlab.swy.service.meta.studentPSO.ParticleSwarmHeuristic}
 */
@Service("controllingStudentPSO")
@Profile({ "untested" })
public class ControllingParticleSwarmHeuristic extends ParticleSwarmHeuristic
		implements ControllingMeta {
	
	private int expectationValueForNumberOfAgents;
	
	private HyperHeuristicLoopback energyCalculator;

	@Value("${swy.service.meta.keepMood}")
	private int originalKeepMood;
	
	@Value("${swy.service.meta.crossoverMutationProbability}")
	private double originalCrossoverMutationProbability;
	
	@Value("${swy.service.meta.reinitializationExponent}")
	private double originalReinitializationExponent;
	
	@Value("${swy.service.meta.numberOfControlledMetas}")
	private int numberOfControlledMetas;
	
	@Override
	public void afterPropertiesSet() {
		super.setNumberOfAgents(numberOfControlledMetas);
		super.afterPropertiesSet();
		super.setKeepMood((originalKeepMood+1) * expectationValueForNumberOfAgents -1);
		super.setCrossoverMutationProbability(originalCrossoverMutationProbability
				/ expectationValueForNumberOfAgents);
		super.setReinitializationExponent(originalReinitializationExponent
				* expectationValueForNumberOfAgents);
	}
	
	@Override
	public int runNextMetaCycle() {
		int returnValue = super.runNextMetaCycle();
		converged = energyCalculator.isConverged();
		return returnValue;
	}
	
	@Required
	public void setExpectationValueForNumberOfAgents(
			int expectationValueForNumberOfAgents) {
		this.expectationValueForNumberOfAgents = expectationValueForNumberOfAgents;
	}

	@Override
	public void setEnergyCalculator(HyperHeuristicLoopback energyCalculator) {
		this.energyCalculator = energyCalculator;
	}
}
