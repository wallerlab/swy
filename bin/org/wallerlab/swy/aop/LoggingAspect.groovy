package org.wallerlab.swy.aop

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import org.apache.log4j.Logger
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.After
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.AfterReturning

/**
 * The logging aspect for the Two-up project.
 */
@Aspect
@Component
@Profile(["untested"])
class LoggingAspect {

	private static final Logger LOG = Logger.getLogger("org.wallerlab.swy")
	
	/*
	 * Model populators
	 */
	/**
	 * This method logs the start of a new calculation part,
	 * meaning a population of one of the main models.
	 * 
	 * @AnnotatedMethod
	 * {@link org.wallerlab.swy.service.batch.BatchService#populateAllModels(Object[])}
	 */
	@Before("execution(void populateAllModels(*))")
	public void loggingBeforePopulateAllModels(JoinPoint jp) {
		/* Find out what type of model will be populated */
		Object[] modelList = jp.getArgs()[0]
		String populatedModelType = (modelList[0].getClass().toString() - \
			"class org.wallerlab.swy.model.main.")
		switch(populatedModelType) {
			case "Run":
			LOG.info("New batch of ${modelList.size()} runs started.")
			break
			case "Meta":
			LOG.info("New batch of ${modelList.size()} metas started.")
			break
			case "Agent":
			LOG.debug("New batch of ${modelList.size()} agents started.")
			break
			/* Print out especially the object identifiers */
			LOG.trace(modelList.toString())
		}
	}
	/**
	 * This method logs the completion of a new calculation part,
	 * meaning a population of one of the main models.
	 * 
	 * @AnnotatedMethod
	 * {@link org.wallerlab.swy.service.batch.BatchService#populateAllModels(Object[])}
	 */
	@After("execution(void populateAllModels(*))")
	public void loggingAfterPopulateAllModels(JoinPoint jp) {
		/* Find out what type of model will be populated */
		String populatedModelType = (jp.getArgs()[0][0].getClass().toString() - \
			"class org.wallerlab.swy.model.main.")
		switch(populatedModelType) {
			case "Run":
			LOG.info("Batch of runs back.")
			break
			case "Meta":
			LOG.info("Batch of metas back.")
			break
			case "Agent":
			LOG.debug("Batch of agents back.")
			break
			/* Print out especially the object identifiers */
			LOG.trace(jp.getArgs()[0].toString())
		}
	}
	
	
	/*
	 * Energy calculation
	 */
	/**
	 * Logs new energy requests.
	 * 
	 * @AnnotatedMethod
	 * {@link org.wallerlab.swy.service.energy.EnergyInterface#getEnergy(double...)}
	 */
	@Before("execution(double getEnergy(..))")
	void loggingBeforeGetEnergy() {
		LOG.trace("Energy calculation requested.")
	}

	/**
	 * Prints a just calculated energy.
	 * 
	 * @AnnotatedMethod
	 * {@link org.wallerlab.swy.service.energy.EnergyInterface#getEnergy(double...)}
	 */
	@AfterReturning(value="execution(double getEnergy(..))", returning="energy")
	void loggingAfterGetEnergy(double energy){
		LOG.debug("Energy was Computed: ${energy}")
	}

	@Before("execution(*  org.wallerlab.swy.service.energy.wrappers.DftbPlus.computeEnergy(..))")
	public void LoggingAdviceDftbPlus(){
		LOG.debug(" DFT-B - tight binding DFT approximation - Prof. Frauenheim  www.dftb.org ")
	}

	/*
	 * References: energy wrappers
	 */
	@Before("execution(*  org.wallerlab.swy.service.energy.wrappers.Mopac.computeEnergy(..))")
	public void LoggingAdviceMopac(){
		LOG.debug(" Mopac - J. Stewart")
	}

	@Before("execution(*  org.wallerlab.swy.service.energy.wrappers.Orca.computeEnergy(..))")
	public void LoggingAdviceNamd(){
		LOG.debug("ORCA - F. Neese mpi-muelheim")
	}

	@Before("execution(*  org.wallerlab.swy.service.energy.wrappers.Gaussian.computeEnergy(..))")
	public void LoggingAdviceGromacs(){
		LOG.debug(" Gaussian J. Pople www.gaussian.com ")
	}
}