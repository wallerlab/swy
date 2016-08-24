package org.wallerlab.swy.service.meta.studentPSO;

import org.wallerlab.swy.service.meta.AbstractControllableMeta;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* original class comment */
//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
//		THIS PROGRAM WAS WRITTEN DURING THE TC-MASTER-MODULE		//
//							IN APRIL 2012						//
//																//
//       THIS PROGRAM DOES A PARTICLE-SWARM-OPTIMIZATION			//
//		WITH THE PARAMETERS OF THE ANT-COLONY-OPTIMIZATION 		//
//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

/**
 * An implementation of a particle swarm optimisation algorithm.
 * 
 * @author t_dres03, f_lied01, d_jans04
 * 
 * @NeededProperties
 * <b><i><font color=purple>double</font></i> swy.service.meta.accelerationRatio</b> -
 * This factor is multiplied to the current velocity during the acceleration.<br>
 * <b>limits:</b> 0 to 1. 0 means that the particles old velocity is completely erased
 * before the acceleration is added. Values close to 1 can let the particles become
 * too fast and shoot them quickly to the searchSpace borders, or in case of perioidic
 * dimensions the particles can continue accelerating until they reach the speed of
 * light.<br>
 * <b>default:</b> <i>0.3</i><br><br>
 * 
 * <b><i><font color=purple>double</font></i> swy.service.meta.globalPercentage</b> -
 * Equal to the property of {@link org.wallerlab.swy.service.meta.ant.AntColonyOptimizer}.<br>
 * The influence on this algorithm is by far not as big.<br>
 * <b>limits:</b> 0 to 1.<br>
 * <b>default:</b> <i>0.3</i><br><br>
 * 
 * <b><i><font color=purple>double</font></i> swy.service.meta.crossoverMutationProbability</b> -
 * Determines how often crossoverMutation may happen.<br>
 * <b>limits:</b> 0 to 1. 0 means switched off, 1 means one crossoverMutation happens
 * in every metaCycle.<br>
 * <b>default:</b> <i>0.0</i><br><br>
 * 
 * <b><i><font color=purple>double</font></i> swy.service.meta.reinitializationExponent</b> -
 * WARNING: THE USAGE OF THIS PARAMETER HAS NOT YES SETTLED.<br>
 * Determines how often reinitialisation happens.<br>
 * <b>limits:</b> >0 to infinity. Values close to 0 mean almost always, infinity means never.
 * Small values prevent convergence.<br>
 * <b>default:</b> <i>9999999999999999</i><br><br>
 * 
 * <b><i><font color=purple>int</font></i> swy.service.meta.keepMood</b> -
 * Determines how many metaCycles a particle is in the same mood.<br>
 * (See {@link org.wallerlab.swy.service.meta.studentPSO.ParticleSwarmParticle#mood}.)
 * <b>limits:</b> 0 to infinity.<br>
 * <b>default:</b> <i>0</i><br><br>
 * 
 * Also look at {@link org.wallerlab.swy.service.meta.AbstractMeta}.<br><br>
 */
@Service("studentPSO")
@Profile({"untested"})
public class ParticleSwarmHeuristic extends AbstractControllableMeta implements ParticleSwarmInterface {

	private ParticleSwarm particleSwarm;
	
	/* PSO-Parameters (internal) */
	private double accelerationRatio;
	
	private double globalPercentage;
	
	private double crossoverMutationProbability;
	
	private double reinitializationExponent;
	
	private int keepMood;
	
	/* fixed! */
	private double convergenceThreshold = 0.01;
	
	public ParticleSwarmHeuristic() {
		super(collectParameterNames());
	}

	private static final List<String> collectParameterNames() {
		ArrayList<String> psoParameters = new ArrayList<String>(2);
		psoParameters.add("accelerationRatio");
		psoParameters.add("globalPercentage");
//		psoParameters.add("crossoverMutationProbability");
//		psoParameters.add("reinitializationExponent");
//		psoParameters.add("keepMood");
		return Collections.unmodifiableList(psoParameters);
	}

	@Override
	public void afterPropertiesSet() {
		generateAgentModels();
		particleSwarm.initialize();
	}

	@Override
	public int runNextMetaCycle() {
		iterationBestAgent = null;
		sendOutAgents();
		particleSwarm.updateParticleBestValues();
		/*
		 * CALCULATE THE PSO-PARTICLE- MOVEMENTS IN EACH METACYCLE AND UPDATE POSITIONS
		 */
		particleSwarm.accelerateParticles(iterationBestAgent.lastReasonableSolution, globalBestAgent.lastReasonableSolution);
		particleSwarm.moveParticles();

		checkConvergence();

		return numberOfPopulationEvents;
	}

	/*
	 * Calculation of the searchspace-scaled Standard-deviation
	 * of the dimensions as the convergence criterion
	 */
	/**
	 * Convergence is reached as soon as all particles are approximately
	 * at the same position.
	 */
	private void checkConvergence() {
		converged = true;
		if (currentMetaCycle < maximumNumberOfMetaCycles) {
			for (int dimension = 0; dimension < problemRepresentation.getSearchSpace().numberOfDimensions; dimension++) {
				double thisSD = particleSwarm.getStandardDeviationOfDimension(dimension);
				if (thisSD >= convergenceThreshold || Double.isNaN(thisSD)) {
					converged = false;
					return;
				}
			}
		}
		if (converged) {
			System.out.println(this+": Converged after "+currentMetaCycle+"  cycles!!");
		}
	}

	@Override
	public double getAccelerationRatio() {
		return accelerationRatio;
	}

	@Override
	@Required
	@Value("${swy.service.meta.accelerationRatio}")
	public void setAccelerationRatio(double newValue) {
		accelerationRatio = newValue;
	}
	
	@Override
	public double getGlobalPercentage() {
		return globalPercentage;
	}

	@Override
	@Required
	@Value("${swy.service.meta.globalPercentage}")
	public void setGlobalPercentage(double newValue) {
		globalPercentage = newValue;
	}
	
	@Override
	public double getCrossoverMutationProbability() {
		return crossoverMutationProbability;
	}
	
	@Override
	@Required
	@Value("${swy.service.meta.crossoverMutationProbability}")
	public void setCrossoverMutationProbability(double newValue) {
		crossoverMutationProbability = newValue;
	}
	
	@Override
	public double getReinitializationExponent() {
		return reinitializationExponent;
	}

	@Override
	@Required
	@Value("${swy.service.meta.reinitializationExponent}")
	public void setReinitializationExponent(double reinitializationExponent) {
		this.reinitializationExponent = reinitializationExponent;
	}

	@Override
	public int getKeepMood() {
		return keepMood;
	}

	@Override
	@Required
	@Value("${swy.service.meta.keepMood}")
	public void setKeepMood(int keepMood) {
		this.keepMood = keepMood;
	}

	@Required
	public void setParticleSwarm(ParticleSwarm particleSwarm) {
		this.particleSwarm = particleSwarm;
		particleSwarm.setMetaInstance(this);
	}
	
	public ParticleSwarm getParticleSwarm() {
		return particleSwarm;
	}
}
