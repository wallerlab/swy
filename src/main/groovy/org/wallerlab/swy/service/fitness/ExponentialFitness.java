package org.wallerlab.swy.service.fitness;

import org.wallerlab.swy.annotations.Cite;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Profile;

/**
 * This fitness calculator only returns high values
 * for very good energies. Due to exponential decay
 * not very good energies very quickly get lower fitness
 * values.
 * 
 * @NeededProperties
 * <b><i><font color=purple>double</font></i> swy.service.fitness.learningRateDecay</b> -
 * A factor in the exponent to modify how quickly the function decays.<br>
 * <b>limits:</b> 0 to infinity. 0 means that always 1 is returned. Infinity would result
 * in a delta function.<br>
 * <b>default:</b> <i>3.0</i><br><br>
 */
@Cite(authors={"Daeyaert, F", "de Jonge, M.", "Koymans, L.", "Vinkers, M."}, journal = "J Comput Chem",
		pages = "887-891", year = 2008)
@Service
@Profile({"untested"})
public class ExponentialFitness extends RememberingFitnessCalculator {

	private double learningRateDecay;

	/**
	 * To ensure that the denominator does not become 0 (would return NaN) a weak different
	 * fitness function is used in those cases. Should only happen after the first energy calculation.
	 */
	@Override
	protected double function(double energy) {

		if (averageEnergy == bestEncounteredEnergy) {
			return (energy/bestEncounteredEnergy);
		} else {
			return Math.exp(-learningRateDecay *
					( (energy - bestEncounteredEnergy) / (averageEnergy - bestEncounteredEnergy) ) );
		}
	}

	@Required
	@Value("${swy.service.fitness.learningRateDecay}")
	public void setLearningRateDecay(double learningRateDecay) {
		this.learningRateDecay = learningRateDecay;
	}
	
}