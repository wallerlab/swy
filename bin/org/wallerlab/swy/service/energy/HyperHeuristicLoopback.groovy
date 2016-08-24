package org.wallerlab.swy.service.energy

import org.wallerlab.swy.model.ParameterControlSearchSpace
import org.wallerlab.swy.service.meta.ControllingMeta
import org.wallerlab.swy.service.meta.ControllableMeta
import org.wallerlab.swy.service.meta.MetaInterface

import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service

import org.apache.commons.beanutils.PropertyUtils

/**
 * This class is used in automatic parameter search calculations. It returns
 * the best energy of an underlying {@link org.wallerlab.swy.service.meta.ControllableMeta}
 * within one metaCycle. But not every energy request on this class starts a
 * metaCycle in a controlled meta, but those are run in an asynchronous way to
 * ensure that at every point of the calculations all underlying metas have performed
 * (approximately) the same number of energy requests.
 * 
 * @author t_dres03, d_jans04, f_lied01
 *
 * @NeededProperties
 * <b><i><font color=purple>int</font></i> swy.service.meta.numberOfControlledMetas</b> -
 * This is the number of independent metaheuristic optimisations that are controlled
 * in an automatic parameter search. These optimisations will all run simultaneously.<br>
 * <b>limits:</b> 1 to infinity. Numbers above 100 are NOT recommended.<br>
 * <b>default:</b> <i>10</i>
 */
@Service
@Profile(["untested"])
public class HyperHeuristicLoopback implements EnergyInterface{
	
	private ParameterControlSearchSpace searchSpace
	
	private ArrayList<ControllableMeta> metaInstances
		
	@Value('${swy.service.meta.numberOfControlledMetas}')
	private int numberOfControlledMetas
		
	private int index = -1
	private int metaCycleCounter = 0
	
	private boolean allConverged
	
	public int getIndex() {
		return index
	}
		
	/**
	 * This method counts internally which metaheuristic is to be called
	 * next, compares the number of energy requests on itself so far with
	 * the number of energy requests the underlying meta has done so far
	 * and decides whether it is allowed to perform another cycle. If so,
	 * it does that using the parameter values in solution.<br>
	 * Also checks for convergence of the underlying metas.
	 * 
	 * @param In this case the solution array is interpreted as a
	 * set of parameter values for the underlying metaheuristic which
	 * is called during this energy request.
	 * 
	 * @return The best energy during one metacycle of the underlying
	 * metaheuristic only if it is allowed to perform calculations
	 * at the moment. Else NaN.
	 */
	@Override
	public synchronized double getEnergy(double[] solution){
		index++
		if (index == numberOfControlledMetas) {
			metaCycleCounter++
			index = 0
			boolean allConverged=true
			for (int instance=0; instance < numberOfControlledMetas; instance++) {
				if (!metaInstances[instance].isConverged()) {
					allConverged=false
					break
				}
			}

		}
		if (metaCycleCounter >= metaInstances[index].numberOfPopulationEvents \
			&& !metaInstances[index].converged) {
			for (int solutionComponent in 0..searchSpace.numberOfDimensions-1) {
				if ( PropertyUtils.getPropertyType(metaInstances[index], \
					searchSpace.caption[solutionComponent])==int ) {
					PropertyUtils.setProperty(metaInstances[index], \
						searchSpace.caption[solutionComponent], \
						(int)solution[solutionComponent].round())
				} else {
					PropertyUtils.setProperty(metaInstances[index], \
						searchSpace.caption[solutionComponent], \
						solution[solutionComponent])
				}
			}
			metaInstances[index].runNextMetaCycle()
			metaInstances[index].currentMetaCycle++
			return metaInstances[index].iterationBestAgent.lastReasonableEnergy
		} else {
			return Double.NaN
		}
	}
	
	@Required
	public void setMetaInstances (ArrayList<ControllableMeta> metaInstances) {
		this.metaInstances = metaInstances
	}
	
	public ArrayList<ControllableMeta> getMetaInstances() {
		return metaInstances
	}
	
	@Required
	public void setSearchSpace(ParameterControlSearchSpace searchSpace) {
		this.searchSpace = searchSpace
	}
	
	public boolean isConverged() {
		return allConverged
	}
}
