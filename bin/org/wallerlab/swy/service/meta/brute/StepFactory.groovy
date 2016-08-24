package org.wallerlab.swy.service.meta.brute

import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Lazy

import org.wallerlab.swy.model.MolecularSystem
import org.wallerlab.swy.service.conformation.factory.ConformationInterface;
import org.wallerlab.swy.service.conformation.factory.DihedralConformation
import org.wallerlab.swy.service.energy.ModellingInterface;
//import org.wallerlab.swy.service.meta.brute.BruteForce
//import org.wallerlab.swy.service.meta.brute.BruteForce.StepResult
import org.wallerlab.swy.service.utils.RandomNumberGenerator

import org.springframework.beans.BeansException
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Service
import org.springframework.context.annotation.Lazy
import org.springframework.beans.factory.annotation.Autowired

import org.springframework.beans.factory.annotation.Autowired

/**
 * Creates a new step for a brute force optimization
 *
 */
@Lazy
@Service
@Profile(["unstable"])
class StepFactory   {

	private int runNumber

	@Autowired
	ConformationInterface conformationInterface

	@Autowired
	ModellingInterface modellingInterface

	/**
	 *  Constructor
	 * 
	 */
	StepFactory(runNumber){
		this.runNumber = runNumber
	}

	/**
	 *  perform Step
	 *  
	 * @return result
	 */
/*	final StepResult call(){
		conformationInterface.newConformation();
		modellingInterface.getSinglePointEnergy(runNumber);
		println Solution.energy;
		return new StepResult(Solution.energy)
	}*/
}
