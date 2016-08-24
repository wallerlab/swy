package org.wallerlab.swy.service.meta.studentPSO;

import org.wallerlab.swy.service.meta.ControllableMeta;

interface ParticleSwarmInterface extends ControllableMeta {

	void setAccelerationRatio(double newValue);

	double getAccelerationRatio();
	
	void setGlobalPercentage(double newValue);
	
	double getGlobalPercentage();
	
	void setCrossoverMutationProbability(double newValue);

	double getCrossoverMutationProbability();

	void setReinitializationExponent(double reinitializationExponent);

	int getKeepMood();

	void setKeepMood(int keepMood);

	double getReinitializationExponent();

	public ParticleSwarm getParticleSwarm();

}
