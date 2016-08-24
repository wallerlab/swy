package org.wallerlab.swy.configuration

import org.wallerlab.swy.aop.*
import org.wallerlab.swy.dao.SwyMoleculeDao;
import org.wallerlab.swy.model.*
import org.wallerlab.swy.model.main.Agent
import org.wallerlab.swy.service.*
import org.wallerlab.swy.service.batch.*
import org.wallerlab.swy.service.conformation.factory.ConformationInterface;
import org.wallerlab.swy.service.energy.*
import org.wallerlab.swy.service.fitness.*;
import org.wallerlab.swy.service.meta.MetaInterface;
import org.wallerlab.swy.service.meta.SolutionGenerator;
import org.wallerlab.swy.service.searchspace.factory.SearchSpaceInterface;
import org.wallerlab.swy.service.utils.RandomNumberGenerator;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.*
import org.springframework.beans.factory.annotation.*
import org.springframework.beans.factory.config.BeanReferenceFactoryBean
import org.springframework.context.annotation.*
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.*
import org.springframework.core.env.Environment

/*
* TODO the @Required annotations do not work. Tried with.......(see below)
* why is this important to work? Because it is the only way to check whether all necessary
* properties are there. "Fail fast" would otherwise not work and the problem might be seen
* far too late or maybe never.
...... switching completely to the javaConfig package. But that enforces a lot of changes
* and is by far not as neat as the parts that are in spring core.
*/
/**
 *  This is the main configuration file of the swy program. Basically all
 *  dependencies are set up in this class or the configuration classes
 *  imported to here.<br><br>
 *  
 *  Decisions are made based on the contents of a property source which is
 *  provided by the {@link org.wallerlab.swy.dao.SwyPropertyDao} in the
 *  {@link org.wallerlab.swy.Swy main class} of this program. The properties
 *  are part of the environment the program works in (except for the launching
 *  part).<br><br>
 *  
 *  What properties are needed can vary a lot with the configuration, but the
 *  decision tree can be easily followed by following the links in this documentation
 *  and especially looking at the 'NeededProperties' and 'NeededBeans' parts starting
 *  here. The <i>entries written in italics</i> of the <b>possible choices</b> should
 *  be seen as the default values.<br><br>
 *  
 *  Although mainly all the classes of the program have annotations of
 *  type {@link org.springframework.stereotype}, the component scanning
 *  is not used, but all beans are explicitly created in the way they are
 *  needed for a certain purpose.<br><br>
 *  
 *  The main properties that are (among others) always needed are:
 *
 *  @NeededProperties
 *  <b>{@link #metaInterface() swy.service.meta}</b> - Which metaheuristic is chosen
 *  to perform the optimizations.<br>
 *  <b>possible choices:</b> antColonyOptimizer | studentPSO | parameterControl<br><br>
 *  
 *  <b>{@link org.wallerlab.swy.configuration.SearchSpaceConfig#searchSpaceInterface() swy.service.searchspace}</b> -
 *  Determines the way the search space is created.<br>
 *  <b>possible choices:</b> bondRotation | supramolecular | supramolecularPosition
 *  | supramolecularAngles | zMatrixStyle<br><br>
 *  
 *  <b>{@link org.wallerlab.swy.configuration.EnergyConfig#energyInterface() swy.service.energy}</b> -
 *  Determines how energy requests are tackled.<br>
 *  <b>possible choices:</b> <i>singlePointEnergy</i> | mathematicalFunction | optimizedEnergy<br><br>
 *  
 *  <b>{@link #fitnessCalculator() swy.service.fitness}</b> - Determines how the energy is 
 *  converted into a fitness. This is a weighting for how good a certain solution
 *  has been.<br>
 *  <b>possible choices:</b> <i>exponentialFitness</i> | primitiveFitnessFunction
 *  | energyDifferenceFitness | normalizedFitness<br><br>
 */
@Configuration
@Import([org.wallerlab.swy.configuration.ModeConfig.class, \
		org.wallerlab.swy.configuration.SearchSpaceConfig.class, \
		org.wallerlab.swy.configuration.MetaConfig.class, \
		org.wallerlab.swy.configuration.BatchConfig.class, \
		org.wallerlab.swy.configuration.EnergyConfig.class])
/* @ComponentScan(value="org.wallerlab.swy", excludeFilters=@Filter(org.springframework.context.annotation.Configuration)) */
// (see above)....... external xml file. Effect: Only the beans defined IN THAT XML FILE are checked for @Required
@ImportResource(["classpath:beanPostProcessorConfig.xml"])
/*
 * Activate the aspects.
 * Actually I would like to switch to bytecode modification instead of proxies. ???I think this
 * is done using cglib by just switching proxyTargetClass to true. ???
 * why change: - efficiency
 *             - does not rely purely on interfaces (-> also methods NOT defined in an interface
 *               are possible joinPoints) ???(unsure of this limitation)???
 * why not:    - ??? does not work with several spring things like autowired ???
 * 
 * Done. In case of problems try setting to false again.
 */
@EnableAspectJAutoProxy(proxyTargetClass=true)
class SwyConfig implements EnvironmentAware, ApplicationContextAware {
	
	private static Environment env
	private static ApplicationContext appContext
	
	// A postProcessor. Is started (to check: error when defining it as static) but has no effect
	@Bean
	public RequiredAnnotationBeanPostProcessor rabpp() {
		return new RequiredAnnotationBeanPostProcessor()
	}
			
	// (see above)..... a javaConfig postProcessor. Has no effect
/*	@Bean
	public RequiredMethodInvocationTracker rmit() {
		RequiredMethodInvocationTracker rmit = new RequiredMethodInvocationTracker()
	}
*/
	/**
	 * @see org.wallerlab.swy.aop.AnalysisAspect
	 */
	@Bean
	AnalysisAspect analysisAspect() {
		return new AnalysisAspect()
	}

	/**
	 * @see org.wallerlab.swy.aop.LoggingAspect
	 */
	@Bean
	LoggingAspect loggingAspect() {
		return new LoggingAspect()
	}
	
	/**
	 * @return A bean with a fully configured {@link org.wallerlab.swy.model.ProblemRepresentation
	 * problemRepresentation}. It contains a {@link #searchSpace() searchSpace bean}, and the information
	 * how to get the energy and (optional?!?) how conformations are created.
	 * 
	 * @NeededBeans
	 * {@link #searchSpace()} |
	 * {@link org.wallerlab.swy.configuration.EnergyConfig#energyInterface()} |
	 * {@link org.wallerlab.swy.configuration.SearchSpaceConfig#conformationFactory()}
	 */
	@Bean
	public MolecularSystem molecularSystem() {
		SwyMoleculeDao swyMoleculeDao = appContext.getBean("swyMoleculeDao")
		return swyMoleculeDao.readIn()
	}
	
	@Bean
	@Primary
	public ProblemRepresentation problemRepresentation() {
		ProblemRepresentation problemRepresentation = new ProblemRepresentation()
		problemRepresentation.setSearchSpace(searchSpace())
		problemRepresentation.setConformationFactory(appContext.getBean("conformationFactory"))
		problemRepresentation.setEnergyCalculator(appContext.getBean("energyInterface"))
		return problemRepresentation
	}
	
	/**
	 * @return A bean containing a {@link org.wallerlab.swy.service.fitness.FitnessCalculator}.
	 * 
	 * @NeededProperties
	 * <b>swy.service.fitness</b> - Determines how the energy is 
	 * converted into a fitness. This is a weighting for how good a certain solution
	 * has been.<br>
	 * <b>possible choices:</b> <i>{@link #exponentialFitness() exponentialFitness}</i> |
	 * {@link #primitiveFitnessFunction() primitiveFitnessFunction} |
	 * {@link #energyDifferenceFitness() energyDifferenceFitness} |
	 * {@link #normalizedFitness() normalizedFitness}<br><br>
	 */
	@Bean
	@Primary
	public FitnessCalculator fitnessCalculator() {
		BeanReferenceFactoryBean factory = new BeanReferenceFactoryBean()
		factory.setTargetBeanName(env.getProperty("swy.service.fitness"))
		factory.setBeanFactory(appContext)
		return factory.getObject()
	}
	/**
	 * @see org.wallerlab.swy.service.fitness.EnergyDifferenceFitness
	 */
	@Bean
	@Lazy
	EnergyDifferenceFitness energyDifferenceFitness() {
		return new EnergyDifferenceFitness()
	}
	/**
	 * @see org.wallerlab.swy.service.fitness.ExponentialFitness
	 */
	@Bean
	@Lazy
	ExponentialFitness exponentialFitness() {
		return new ExponentialFitness()
	}
	/**
	 * @see org.wallerlab.swy.service.fitness.NormalizedFitness
	 */
	@Bean
	@Lazy
	NormalizedFitness normalizedFitness() {
		return new NormalizedFitness()
	}
	/**
	 * @see org.wallerlab.swy.service.fitness.PrimitiveFitnessFunction
	 */
	@Bean
	@Lazy
	PrimitiveFitnessFunction primitiveFitnessFunction() {
		return new PrimitiveFitnessFunction()
	}
	
	/**
	 * @return A bean containing a fully configured {@link org.wallerlab.swy.service.meta.MetaInterface}.
	 * The choices working at the moment are {@link org.wallerlab.swy.configuration.MetaConfig#antColonyOptimizer()}
	 * and {@link org.wallerlab.swy.configuration.MetaConfig#studentPSO()}. Additionally
	 * it is possible to choose {@link org.wallerlab.swy.configuration.MetaConfig#parameterControl()}
	 * for an automated way to control the parameters. Soon also brute force and simulated
	 * annealing will be possible.
	 * 
	 * @NeededProperties
	 * <b>swy.service.meta</b> - Which metaheuristic is chosen to perform the optimizations.<br>
	 * <b>possible choices:</b> {@link org.wallerlab.swy.configuration.MetaConfig#antColonyOptimizer() antColonyOptimizer} |
	 * {@link org.wallerlab.swy.configuration.MetaConfig#studentPSO() studentPSO} |
	 * {@link org.wallerlab.swy.configuration.MetaConfig#parameterControl() parameterControl}<br><br>
	 */
	@Bean
	@Primary
	public MetaInterface metaInterface(){
		BeanReferenceFactoryBean factory = new BeanReferenceFactoryBean()
		factory.setTargetBeanName(safeGetProperty("swy.service.meta"))
		factory.setBeanFactory(appContext)
		return factory.getObject()
	}

	/**
	 * @return A bean containing a fully configured {@link org.wallerlab.swy.model.SearchSpace}.
	 * It uses the chosen {@link org.wallerlab.swy.configuration.SearchSpaceConfig#searchSpaceInterface()}
	 * to generate itself.
	 * 
	 * @NeededBeans
	 * {@link org.wallerlab.swy.configuration.SearchSpaceConfig#searchSpaceInterface()}
	 */
	@Bean
	@Primary
	public SearchSpace searchSpace() {
		SearchSpaceInterface searchSpaceInterface = appContext.getBean("searchSpaceInterface")
		return searchSpaceInterface.generateSearchSpace()
	}

	@Bean
	public RandomNumberGenerator randomNumberGenerator() {
		return new RandomNumberGenerator()
	}
	
	@Override
	@Required
	public void setEnvironment(Environment environment) {
		this.env = environment
	}
	
	public static Environment getEnv() {
		return env
	}

	@Override
	@Required
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.appContext = applicationContext	
	}
			
	public String safeGetProperty(String propertyName) throws MissingPropertyException{
		String value = env.getProperty(propertyName)
		if (value == "NO_DEFAULT_SPECIFIED" || !value) {
			throw new MissingPropertyException(propertyName)
		} else {
			return value
		}
	}
}