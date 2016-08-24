package org.wallerlab.swy.service.meta;

import org.wallerlab.swy.service.energy.HyperHeuristicLoopback;

/**
 * An interface marking that a certain meta is capable of controlling other metas.
 * In principal every meta should be able to control other metas, but some additional
 * functionality might be needed for proper results.
 * In particular for the possibility of on-the-fly parameter control an asynchronous
 * parameter optimization is vital, which must not break the functionality of the
 * controlling meta.
 * 
 * @author t_dres03
 *
 */
public interface ControllingMeta extends MetaInterface {

	public void setEnergyCalculator(HyperHeuristicLoopback energyCalculator);

}
