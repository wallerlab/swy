package org.wallerlab.swy.configuration;

import org.wallerlab.swy.model.main.Agent;
import org.wallerlab.swy.model.main.Meta;
import org.wallerlab.swy.model.main.Run;
import org.wallerlab.swy.service.batch.*;
import org.wallerlab.swy.service.batch.modelpopulator.*;
import org.wallerlab.swy.service.modeltools.AgentModelListLimiter;
import org.wallerlab.swy.service.modeltools.FitnessBasedAgentModelListLimiter;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/*
 * TODO should not be fixed to the simpleBatchService.
 */
/**
 * Configuration of how batches are processed. Each {@link org.wallerlab.swy.service.batch.BatchService}
 * is listed in different flavours for the different models (members of the {@link org.wallerlab.swy.model.main
 * model subpackage}, which contains the models we want to populate with this program).
 * At the moment only the {@link org.wallerlab.swy.service.batch.SimpleBatchService} is
 * available.
 */
@Configuration
@Profile(["untested"])
public class BatchConfig implements ApplicationContextAware {
	private static ApplicationContext appContext
	
	@Autowired
	private SwyConfig swyConfig
		
	/**
	 * @NeededBeans
	 * {@link #singleMetaRunModelPopulator()}
	 */
	@Bean
	public BatchService<Run> runBatchService() {
		SimpleBatchService<Run> runBatchService = new SimpleBatchService<Run>()
		runBatchService.setModelPopulator(singleMetaRunModelPopulator())
		return runBatchService
	}
	/**
	 * @NeededBeans
	 * {@link #metaBatchService()}
	 */
	@Bean
	public SingleMetaRunModelPopulator singleMetaRunModelPopulator() {
		SingleMetaRunModelPopulator singleMetaRunModelPopulator = new SingleMetaRunModelPopulator()
		singleMetaRunModelPopulator.setMetaBatchService(metaBatchService())
		return singleMetaRunModelPopulator
	}
	
	/**
	 * @NeededBeans
	 * {@link #metaInterfaceMetaModelPopulator()}
	 */
	@Bean
	public BatchService<Meta> metaBatchService() {
		SimpleBatchService<Meta> metaBatchService = new SimpleBatchService<Meta>()
		metaBatchService.setModelPopulator(metaInterfaceMetaModelPopulator())
		return metaBatchService
	}
	/**
	 * @NeededBeans
	 * {@link org.wallerlab.swy.configuration.MetaConfig#metaInterface()}
	 * {@link #agentModelListLimiter()}
	 */
	@Bean
	public MetaInterfaceMetaModelPopulator metaInterfaceMetaModelPopulator() {
		MetaInterfaceMetaModelPopulator metaInterfaceMetaModelPopulator = new MetaInterfaceMetaModelPopulator()
		metaInterfaceMetaModelPopulator.setMetaInterface(appContext.getBean("metaInterface"))
		metaInterfaceMetaModelPopulator.setAgentModelListLimiter(agentModelListLimiter())
		return metaInterfaceMetaModelPopulator
	}
	
	/**
	 * @return at the moment fixed to the (only existing)
	 * {@link org.wallerlab.swy.service.modeltools.FitnessBasedAgentModelListLimiter energy based}
	 * version.
	 */
	@Bean
	public AgentModelListLimiter agentModelListLimiter() {
		return new FitnessBasedAgentModelListLimiter()
	}
	
	/**
	 * @NeededBeans
	 * {@link #agentModelPopulator()}
	 */
	@Bean
	@Primary
	public BatchService<Agent> agentBatchService() {
		SimpleBatchService<Agent> agentBatchService = new SimpleBatchService<Agent>()
		agentBatchService.setModelPopulator(agentModelPopulator())
		return agentBatchService
	}
	/**
	 * @NeededBeans
	 * {@link org.wallerlab.swy.configuration.EnergyConfig#energyInterface()} |
	 * {@link org.wallerlab.swy.configuration.SwyConfig#fitnessCalculator()} |
	 * {@link org.wallerlab.swy.configuration.MetaConfig#solutionGenerator()}
	 */
	@Bean
	AgentModelPopulator agentModelPopulator() {
		AgentModelPopulator agentModelPopulator = new AgentModelPopulator()
		agentModelPopulator.setEnergyCalculator(appContext.getBean("energyInterface"))
		agentModelPopulator.setFitnessCalculator(appContext.getBean("fitnessCalculator"))
		agentModelPopulator.setSolutionGenerator(appContext.getBean("solutionGenerator"))
		return agentModelPopulator
	}
	
	@Override
	@Required
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.appContext = applicationContext	
	}
}
