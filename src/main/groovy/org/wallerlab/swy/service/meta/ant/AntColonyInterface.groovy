package org.wallerlab.swy.service.meta.ant

import org.wallerlab.swy.service.meta.ControllableMeta;

interface AntColonyInterface extends ControllableMeta {
	
	 double getMaximumProbabilityToChooseGlobalBest();
	 void setMaximumProbabilityToChooseGlobalBest(double newValue);

	 double getPheromoneResistance();
	 void setPheromoneResistance(double newValue);
	
	 double getGlobalPercentage();
	 void setGlobalPercentage(double newValue);
	
	 double getAlpha();
	 void setAlpha(double newValue);
	
	 double getTrailWidth();
	 void setTrailWidth(double newValue);

}
