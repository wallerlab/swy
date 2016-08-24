package org.wallerlab.swy.service.meta.ant;

import org.wallerlab.swy.annotations.Cite;
import org.wallerlab.swy.service.meta.AbstractControllableMeta;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author t_dres03
 * 
 * @NeededProperties
 * <b><i><font color=purple>double</font></i> swy.service.meta.maximumProbabilityToChooseGlobalBest</b> -
 * Needed to determine minimum pheromone level.<br>
 * <b>limits:</b> >0 to 1. 0 would result in immediate convergence because tauMin would be
 * equal to tauMax. 1 results in tauMin=0, so that the MinMax ant system is switched off.<br>
 * <b>default:</b> <i>0.3</i><br><br>
 * 
 * <b><i><font color=purple>double</font></i> swy.service.meta.pheromoneResistance</b> -
 * The percentage of pheromones evaporated after each metaCycle.<br>
 * <b>limits:</b> 0 to 1. 0 means keep all pheromones (would push the algorithm towards
 * early found, most probably bad solutions), 1 means erase all pheromone information after
 * every cycle (causes too early convergence; some information is still kept by the ants of
 * the current metaCycle).<br>
 * <b>default:</b> <i>0.2</i><br><br>
 * 
 * <b><i><font color=purple>double</font></i> swy.service.meta.globalPercentage</b> -
 * Amount of globalBestSolution update in contrast to iterationBestSolution update.<br>
 * <b>limits:</b> 0 to 1. 0 means that the update procedure is based ONLY on the best
 * solution found in the current metaCycle (the algorithm could quickly move away from
 * good solutions), 1 means that the update procedure is based only on the overall best
 * solution found so far (causes too early convergence).<br>
 * <b>default:</b> <i>0.3</i><br><br>
 * 
 * <b><i><font color=purple>double</font></i> swy.service.meta.alpha</b> -
 * An exponent used in the calculation of the probability distribution meaning how strong
 * the probability distribution is influenced by the pheromone level.<br>
 * <b>limits:</b> 0 to infinity. Too high values (already at around 1.0) can cause
 * too early convergence.<br>
 * <b>default:</b> <i>1.0</i><br><br>
 * 
 * <b><i><font color=purple>double</font></i> swy.service.meta.trailWidth</b> -
 * The standard deviation (sigma) of the gaussian function in the pheromone release
 * (will be normalized by the search space size).<br>
 * <b>limits:</b> 0 to infinity. Look at gaussian distributions for more information.<br>
 * <b>default:</b> <i>0.1</i><br><br>
 * 
 * Also look at {@link org.wallerlab.swy.service.meta.AbstractMeta}.<br><br>
 */
@Cite(authors={"Dorigo...And the MinMax ant system"}, journal="FunnyJournal", year=9595, pages="4 to the 5")
@Service
@Profile({"untested"})
public class AntColonyOptimizer extends AbstractControllableMeta implements AntColonyInterface{

	private AntColony antColony;
		
	/* Parameters */
	double pheromoneResistance;
	double maximumProbabilityToChooseGlobalBest;
	double globalPercentage;
	double alpha;
	double trailWidth;
		
	public AntColonyOptimizer() {
		super(collectParameterNames());
	}

	@Required
	public void setAntColony(AntColony antColony) {
		this.antColony = antColony;
		antColony.setMetaInstance(this);
	}
	
	private static final List<String> collectParameterNames() {
		List<String> antParameters = new ArrayList<String>(6);
		antParameters.add("numberOfAgents");
		antParameters.add("pheromoneResistance");
		antParameters.add("maximumProbabilityToChooseGlobalBest");
		antParameters.add("globalPercentage");
		antParameters.add("alpha");
		antParameters.add("trailWidth");
		return Collections.unmodifiableList(antParameters);
	}
	
	@Override
	public void afterPropertiesSet() {
		antColony.initialize();
	}

	@Override
	public int runNextMetaCycle() {
		generateAgentModels();
		antColony.updateProbabilityDistribution();
		if (currentMetaCycle >= 2){
			converged = antColony.isConverged();
			if (converged){
				System.out.println(this+": CONVERGED!! after cycle "+(currentMetaCycle-1));
				return numberOfPopulationEvents;
			}
		}
		iterationBestAgent = null;
		sendOutAgents();
		antColony.updatePheromones();
		return numberOfPopulationEvents;
	}
	
	@Override
	public double getMaximumProbabilityToChooseGlobalBest() {
		return maximumProbabilityToChooseGlobalBest;
	}

	@Override
	@Required
	@Value("${swy.service.meta.maximumProbabilityToChooseGlobalBest}")
	public void setMaximumProbabilityToChooseGlobalBest(double newValue) {
		maximumProbabilityToChooseGlobalBest = newValue;
	}

	@Override
	public double getPheromoneResistance() {
		return pheromoneResistance;
	}
	
	@Override
	@Required
	@Value("${swy.service.meta.pheromoneResistance}")
	public void setPheromoneResistance(double newValue) {
		pheromoneResistance = newValue;
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
	public double getAlpha() {
		return alpha;
	}
	
	@Override
	@Required
	@Value("${swy.service.meta.alpha}")
	public void setAlpha(double newValue) {
		alpha = newValue;
	}

	@Override
	public double getTrailWidth() {
		return trailWidth;
	}

	@Override
	@Required
	@Value("${swy.service.meta.trailWidth}")
	public void setTrailWidth(double newValue) {
		trailWidth = newValue;
	}
	
}

