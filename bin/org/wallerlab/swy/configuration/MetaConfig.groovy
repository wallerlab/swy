package org.wallerlab.swy.configuration;

import org.wallerlab.swy.model.ParameterControlSearchSpace;
import org.wallerlab.swy.model.ProblemRepresentation;
import org.wallerlab.swy.model.main.Agent;
import org.wallerlab.swy.service.batch.BatchService;
import org.wallerlab.swy.service.batch.SimpleBatchService;
import org.wallerlab.swy.service.batch.modelpopulator.AgentModelPopulator;
import org.wallerlab.swy.service.batch.modelpopulator.ModelPopulator;
import org.wallerlab.swy.service.energy.HyperHeuristicLoopback;
import org.wallerlab.swy.service.meta.*;
import org.wallerlab.swy.service.meta.ant.*;
import org.wallerlab.swy.service.meta.studentPSO.*;
import org.wallerlab.swy.service.searchspace.factory.HyperHeuristicSearch;

import org.springframework.context.*;
import org.springframework.context.annotation.*;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

/**
 * The metaheuristic beans used in Swy are created and configured here.
 *
 */
@Lazy
@Configuration
public class MetaConfig implements ApplicationContextAware {

	private static ApplicationContext appContext
	
	@Autowired
	private SwyConfig swyConfig;

	@Autowired
	private BatchConfig batchConfig;

	/*
	 * Heuristics
	 */
	/**
	 * One of the chosable metaheuristics. Needs an {@link #antColony()}
	 * and enforces the use of the {@link #ant()} solution generator.
	 * 
	 * @see org.wallerlab.swy.service.meta.SolutionGenerator
	 * 
	 * @NeededBeans
	 * {@link #antColony()}<br>
	 * Also look into {@link #prepareMeta(MetaInterface)}.
	 */
	@Bean
	public AntColonyOptimizer antColonyOptimizer() {
		AntColonyOptimizer antColonyOptimizer = new AntColonyOptimizer()
		antColonyOptimizer.setAntColony(antColony())
		prepareMeta(antColonyOptimizer)
		return antColonyOptimizer
	}
	/**
	 * @return A bean needed by the {@link #antColonyOptimizer()}.
	 * 
	 * @NeededBeans
	 * ({@link #antColonyOptimizer()} - indirectly)
	 * {@link org.wallerlab.swy.configuration.SearchSpaceConfig#searchSpaceDiscretiser()}
	 */
	@Bean
	public AntColony antColony() {
		/*
		 * the 'metaInstance' is automatically injected from the optimizer itself.
		 * Reason: It is a circular dependency.
		 */
		AntColony antColony = new AntColony()
		antColony.setSearchSpaceDiscretiser(appContext.getBean("searchSpaceDiscretiser"))
		return antColony
	}
	
	/**
	 * One of the choosable metaheuristics. Needs an {@link #particleSwarm()}
	 * which is also used as the solution generator.
	 * 
	 * @see org.wallerlab.swy.service.meta.SolutionGenerator
	 * 
	 * @NeededBeans
	 * {@link #particleSwarm()}<br>
	 * Also look into {@link #prepareMeta(MetaInterface)}.
	 */
	@Bean
	public ParticleSwarmHeuristic studentPSO() {
		ParticleSwarmHeuristic studentPSO = new ParticleSwarmHeuristic()
		studentPSO.setParticleSwarm(particleSwarm())
		prepareMeta(studentPSO)
		return studentPSO
	}

	/**
	 * A method to wire in the objects which all metaheuristics need.
	 * 
	 * @param metaInterface The metaInterface being configured (it's not returned).
	 * 
	 * @NeededBeans
	 * {@link org.wallerlab.swy.configuration.BatchConfig#agentBatchService()} |
	 * {@link org.wallerlab.swy.configuration.SwyConfig#problemRepresentation()}
	 */
	private void prepareMeta(MetaInterface metaInterface) {
		metaInterface.setBatchService(batchConfig.agentBatchService())
		metaInterface.setProblemRepresentation(swyConfig.problemRepresentation())
		return
	}
	
	/**
	 * @return A bean of the solution generators corresponding to the metas.
	 * At the moment it can be {@link #ant()} or {@link #particleSwarm()}.
	 * 
	 * @NeededProperties
	 * <b>swy.service.meta</b> - Which metaheuristic is chosen to perform the optimizations.<br>
	 * <b>possible choices:</b> {@link org.wallerlab.swy.configuration.MetaConfig#antColonyOptimizer() antColonyOptimizer} |
	 * {@link org.wallerlab.swy.configuration.MetaConfig#studentPSO() studentPSO} |
	 * {@link org.wallerlab.swy.configuration.MetaConfig#parameterControl() parameterControl}<br><br>
	 */
	@Bean
	@Primary
	@Lazy(false)
	public SolutionGenerator solutionGenerator() {
		switch(swyConfig.safeGetProperty("swy.service.meta")) {
			case "antColonyOptimizer":
				return ant()
				break
			case "studentPSO":
				return particleSwarm()
				break
		}
	}
	
	/**
	 * The {@link org.wallerlab.swy.service.meta.SolutionGenerator} of
	 * the {@link #antColonyOptimizer()}.
	 * 
	 * @NeededBeans
	 * {@link org.wallerlab.swy.configuration.SwyConfig#searchSpace()} |
	 * {@link #antColony()}
	 */
	@Bean
	public Ant ant() {
		Ant ant = new Ant()
		ant.setSearchSpace(swyConfig.searchSpace())
		ant.setAntColony(antColony())
		ant.setRandomNumberGenerator(appContext.getBean("randomNumberGenerator"))
		return ant
	}
	
	/**
	 * The {@link org.wallerlab.swy.service.meta.SolutionGenerator} of
	 * the {@link #studentPSO()}.
	 */
	@Bean
	public ParticleSwarm particleSwarm() {
		ParticleSwarm particleSwarm = new ParticleSwarm()
		particleSwarm.setRandomNumberGenerator(appContext.getBean("randomNumberGenerator"))
		return particleSwarm
	}
	
	/*
	 * TODO refactor out.
	 * Parameter control section.
	 */
	/**
	 * @return A bean containing the fully configured metaheuristic, which is
	 * controlling the parameters of another metaheuristic. This heuristic is
	 * configured a lot different from a standard one. At the moment only a
	 * {@link #CONTROLLINGstudentPSO() particle swarm algorithm} can be used
	 * for this purpose.
	 * 
	 * @NeededProperties
	 * <b>swy.service.meta.controller</b> - The metaheuristic controlling parameters
	 * of another one.<br>
	 * <b>possible choices:</b> <i>{@link #CONTROLLINGstudentPSO() studentPSO}</i>
	 * 
	 * @NeededBeans
	 * {@link #hyperHeuristicLoopback()}
	 */
	@Bean
	public MetaInterface parameterControl() {
		ControllingMeta controllingMeta = \
			appContext.getBean("CONTROLLING"+swyConfig.safeGetProperty("swy.service.meta.controller"))
		controllingMeta.setEnergyCalculator(hyperHeuristicLoopback())
		return controllingMeta
	}
		
	/**
	 * @return A bean similar to {@link org.wallerlab.swy.configuration.SwyConfig#problemRepresentation()},
	 * but especially configured for parameter optimizations. That means it uses
	 * a {@link #parameterControlSearchSpace()} and the {@link #hyperHeuristicLoopback()}
	 * as the energy calculator.
	 * 
	 * @NeededBeans
	 * {@link #parameterControlSearchSpace()} |
	 * {@link #hyperHeuristicLoopback()}
	 */
	@Bean
	public ProblemRepresentation parameterControlProblemRepresentation() {
		ProblemRepresentation problemRepresentation = new ProblemRepresentation()
		problemRepresentation.setSearchSpace(parameterControlSearchSpace())
		problemRepresentation.setEnergyCalculator(hyperHeuristicLoopback())
		return problemRepresentation
	}
	
	/**
	 * @return A bean that generates an energy expression for the parameter
	 * search. It works on the {@link #parameterControlSearchSpace()} and uses
	 * {@link #controlledMetas()}.
	 * 
	 * @NeededBeans
	 * {@link #parameterControlSearchSpace()} |
	 * {@link #controlledMetas()}
	 */
	@Bean
	public HyperHeuristicLoopback hyperHeuristicLoopback() {
		HyperHeuristicLoopback hyperHeuristicLoopback = new HyperHeuristicLoopback()
		hyperHeuristicLoopback.setSearchSpace(parameterControlSearchSpace())
		hyperHeuristicLoopback.setMetaInstances(controlledMetas())
		return hyperHeuristicLoopback
	}
	
	/**
	 * @return A bean containing the search space for a parameter search.
	 * It uses the {@link #parameterControlSearchSpaceInterface()} to generate
	 * itself.
	 * 
	 * @NeededBeans
	 * {@link #parameterControlSearchSpaceInterface()}
	 */
	@Bean
	public ParameterControlSearchSpace parameterControlSearchSpace() {
		return parameterControlSearchSpaceInterface().generateSearchSpace()
	}
	/**
	 * @return A bean that can generate a {@link org.wallerlab.swy.model.ParameterControlSearchSpace}.
	 * It is set up with the parameters that can be optimized for a certain
	 * {@link #controllableMeta()}.
	 * 
	 * @NeededBeans
	 * {@link #controllableMeta()}
	 */
	@Bean
	public HyperHeuristicSearch parameterControlSearchSpaceInterface() {
		HyperHeuristicSearch parameterControlSearchSpaceInterface = new HyperHeuristicSearch()
		parameterControlSearchSpaceInterface.setParameterNames(controllableMeta().getParameterNames())
		return parameterControlSearchSpaceInterface
	}
	
	
	// TODO generalize....and make less ugly (in fact the whole file/config)
	/**
	 * @return A bean containing a list of {@link #controllableMeta() controllableMetas}.
	 * 
	 * @NeededProperties
	 * <b><i><font color=purple>int</font></i> swy.service.meta.numberOfControlledMetas</b> -
	 * This is the number of independent metaheuristic optimisations that are controlled
	 * in an automatic parameter search. These optimisations will all run simultaneously.<br>
	 * <b>limits:</b> 1 to infinity. Numbers above 100 are NOT recommended.<br>
	 * <b>default:</b> <i>10</i>
	 * 
	 * @NeededBeans
	 * {@link #controllableMeta()}
	 */
	@Bean
	public ArrayList<ControllableMeta> controlledMetas() {
		int numberOfControlledMetas = \
			swyConfig.safeGetProperty("swy.service.meta.numberOfControlledMetas").toInteger()
		ArrayList<ControllableMeta> controlledMetas = new ArrayList<ControllableMeta>(numberOfControlledMetas)
		for (int metaIndex=0; metaIndex<numberOfControlledMetas; metaIndex++) {
			controlledMetas << controllableMeta()
		}
		return controlledMetas
	}
	
	/**
	 * @return A bean with a fully configured {@link org.wallerlab.swy.service.meta.MetaInterface},
	 * which can be controlled (meaning its parameters can be changed)
	 * from the outside. At the moment the metaheuristics which can be
	 * controlled are an {@link #CONTROLLEDantColonyOptimizer() ant colony}
	 * and a {@link #CONTROLLEDstudentPSO() particle swarm optimizer}.
	 * 
	 * @NeededProperties
	 * <b>swy.service.meta.controlled</b> - The type of metaheuristic under
	 * parameter control.<br>
	 * <b>possible choices:</b> <i>{@link #CONTROLLEDantColonyOptimizer() antColonyOptimizer}</i> |
	 * {@link #CONTROLLEDstudentPSO() studentPSO}
	 */
	@Bean
	@Scope("prototype")
	public ControllableMeta controllableMeta() {
		ControllableMeta controllableMeta = \
			appContext.getBean("CONTROLLED"+swyConfig.safeGetProperty("swy.service.meta.controlled"))
		return controllableMeta

	}

	/**
	 * This is the parameter control equivalent to {@link org.wallerlab.swy.configuration.BatchConfig#agentBatchService()}
	 * for the {@link #controllableMeta()}.
	 * 
	 * @NeededBeans
	 * {@link #controlledAgentModelPopulator()}
	 */
	@Bean
	@Scope("prototype")
	public BatchService<Agent> controlledAgentBatchService() {
		SimpleBatchService<Agent> agentBatchService = new SimpleBatchService<Agent>()
		agentBatchService.setModelPopulator(controlledAgentModelPopulator())
		return agentBatchService
	}
	/**
	 * This is the parameter control equivalent to {@link org.wallerlab.swy.configuration.BatchConfig#agentModelPopulator()}
	 * for the {@link #controllableMeta()}.
	 * 
	 * @NeededBeans
	 * {@link org.wallerlab.swy.configuration.EnergyConfig#energyInterface()} |
	 * {@link org.wallerlab.swy.configuration.SwyConfig#fitnessCalculator()}
	 */
	@Bean
	@Scope("prototype")
	public ModelPopulator<Agent> controlledAgentModelPopulator() {
		AgentModelPopulator controlledAgentModelPopulator = new AgentModelPopulator()
		controlledAgentModelPopulator.setSolutionGenerator(null)
		controlledAgentModelPopulator.setEnergyCalculator(appContext.getBean("energyInterface"))
		controlledAgentModelPopulator.setFitnessCalculator(appContext.getBean("fitnessCalculator"))
		return controlledAgentModelPopulator
	}
	
	/**
	 * This is the parameter control equivalent to {@link org.wallerlab.swy.configuration.BatchConfig#agentBatchService()}
	 * for the {@link #parameterControl() controlling meta}.
	 * 
	 * @NeededBeans
	 * {@link #parameterControlAgentModelPopulator()}
	 */
	@Bean
	public BatchService<Agent> parameterControlAgentBatchService() {
		SimpleBatchService<Agent> agentBatchService = new SimpleBatchService<Agent>()
		agentBatchService.setModelPopulator(parameterControlAgentModelPopulator())
		return agentBatchService
	}
	/**
	 * This is the parameter control equivalent to {@link org.wallerlab.swy.configuration.BatchConfig#agentModelPopulator()}
	 * for the {@link #parameterControl() controlling meta}.
	 * 
	 * @NeededBeans
	 * {@link #particleSwarm()} |
	 * {@link #hyperHeuristicLoopback()} |
	 * {@link org.wallerlab.swy.configuration.SwyConfig#fitnessCalculator()}
	 */
	@Bean
	public ModelPopulator<Agent> parameterControlAgentModelPopulator() {
		AgentModelPopulator parameterControlAgentModelPopulator = new AgentModelPopulator()
		/*
		 * TODO unfix, is not always particleSwarm.
		 */
		parameterControlAgentModelPopulator.setSolutionGenerator(appContext.getBean("particleSwarm"))
		parameterControlAgentModelPopulator.setEnergyCalculator(appContext.getBean("hyperHeuristicLoopback"))
		parameterControlAgentModelPopulator.setFitnessCalculator(appContext.getBean("fitnessCalculator"))
		return parameterControlAgentModelPopulator
	}
	
	/**
	 * @return A bean like {@link #studentPSO()}, but configured to control
	 * parameters of a different heuristic. It is set up with the
	 * {@link #parameterControlProblemRepresentation()}, the {@link #parameterControlAgentBatchService()},
	 * the regular {@link #particleSwarm()}
	 * 
	 * @NeededBeans
	 * {@link #parameterControlProblemRepresentation()} |
	 * {@link #parameterControlAgentBatchService()} |
	 * {@link #particleSwarm()} |
	 * {@link #parameterControlSearchSpace()}
	 */
	@Bean
	public ControllingMeta CONTROLLINGstudentPSO() {
		ControllingParticleSwarmHeuristic pso = new ControllingParticleSwarmHeuristic()
		pso.setProblemRepresentation(parameterControlProblemRepresentation())
		pso.setBatchService(parameterControlAgentBatchService())
		pso.setParticleSwarm(particleSwarm())
		pso.setExpectationValueForNumberOfAgents\
			(parameterControlSearchSpace().expectationValueForCalculationsPerMetaCycle)
		return pso
	}

	/**
	 * @return A bean like {@link #studentPSO()}, but configured to have
	 * its parameters controlled by a different heuristic. It is set up very
	 * similar to the normal metaheuristic, but in a prototype way using
	 * the {@link #controlledParticleSwarm()}.
	 * 
	 * @NeededBeans
	 * {@link #controlledParticleSwarm()}<br>
	 * Also look into {@link #prepareControlledMeta(ControllableMeta)}.
	 */
	@Bean
	@Scope("prototype")
	public ParticleSwarmHeuristic CONTROLLEDstudentPSO() {
		ParticleSwarmHeuristic studentPSO = new ParticleSwarmHeuristic()
		ParticleSwarm particleSwarm = controlledParticleSwarm()
		particleSwarm.setMetaInstance(studentPSO)
		prepareControlledMeta(studentPSO)
		studentPSO.setParticleSwarm(particleSwarm)
		studentPSO.getBatchService().getModelPopulator().setSolutionGenerator(particleSwarm)
		return studentPSO
	}
	
	/**
	 * @return A bean like {@link #antColonyOptimizer()}, but configured to have
	 * its parameters controlled by a different heuristic. It is set up very
	 * similar to the normal metaheuristic, but in a prototype way using
	 * the {@link #controlledAnt} and creating its own AntColony instance rather
	 * than using the {@link #antColony()}.
	 * 
	 * @NeededBeans
	 * {@link #controlledAnt()}<br>
	 * Also look into {@link #prepareControlledMeta(ControllableMeta)}.
	 */
	@Bean
	@Scope("prototype")
	public AntColonyOptimizer CONTROLLEDantColonyOptimizer() {
		AntColonyOptimizer antColonyOptimizer = new AntColonyOptimizer()
		AntColony antColony = controlledAntColony()
		Ant ant = controlledAnt()
		ant.setAntColony(antColony)
		prepareControlledMeta(antColonyOptimizer)
		antColonyOptimizer.setAntColony(antColony)
		antColonyOptimizer.getBatchService().getModelPopulator().setSolutionGenerator(ant)
		return antColonyOptimizer
	}
	/**
	 * The prototype version of {@link #antColony()}
	 */
	@Bean
	@Scope("prototype")
	public AntColony controlledAntColony() {
		AntColony antColony = new AntColony()
		antColony.setSearchSpaceDiscretiser(appContext.getBean("searchSpaceDiscretiser"))
		return antColony
	}
	
	/**
	 * This method is the parameter control equivalent to {@link #prepareMeta(MetaInterface)}
	 * for the {@link #controllableMeta()}.
	 * 
	 * @param controllableMeta The metaheuristic under configuration.
	 * 
	 * @NeededBeans
	 * {@link org.wallerlab.swy.configuration.SwyConfig#problemRepresentation()} |
	 * {@link #controlledAgentBatchService()}
	 */
	private void prepareControlledMeta(ControllableMeta controllableMeta) {
		controllableMeta.setProblemRepresentation(appContext.getBean("problemRepresentation"))
		controllableMeta.setBatchService(controlledAgentBatchService())
	}
	
	/**
	 * This is the parameter control equivalent to {@link #ant()}
	 * for the {@link #controllableMeta()}.
	 * 
	 * @NeededBeans
	 * {@link org.wallerlab.swy.configuration.SwyConfig#searchSpace()}
	 */
	@Bean
	@Scope("prototype")
	public Ant controlledAnt() {
		Ant ant = new Ant()
		ant.setSearchSpace(swyConfig.searchSpace())
		ant.setAntColony(null)
		ant.setRandomNumberGenerator(appContext.getBean("randomNumberGenerator"))
		return ant
	}
	
	/**
	 * This is the parameter control equivalent to {@link #particleSwarm()}
	 * for the {@link #controllableMeta()}.
	 */
	@Bean
	@Scope("prototype")
	public ParticleSwarm controlledParticleSwarm() {
		ParticleSwarm particleSwarm = new ParticleSwarm()
		particleSwarm.setMetaInstance(null)
		particleSwarm.setRandomNumberGenerator(appContext.getBean("randomNumberGenerator"))
		return particleSwarm
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.appContext = applicationContext
	}
}
