package org.wallerlab.swy.configuration;

import org.wallerlab.swy.service.energy.*;
import org.wallerlab.swy.service.energy.wrappers.*

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.config.BeanReferenceFactoryBean
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.*;


@Configuration
@Lazy
public class EnergyConfig implements ApplicationContextAware {
	private static ApplicationContext appContext
	
	@Autowired
	private SwyConfig swyConfig
	
	/**
	 * @return A bean containing a fully configured {@link org.wallerlab.swy.service.energy.EnergyInterface}.
	 * The current possibilities to request an energy calculation are {@link #mathematicalFunction()}
	 * and {@link #singlePointEnergyCalculator()}. The {@link #optimizedEnergyCalculator()}
	 * should NOT be used.
	 * 
	 * @NeededProperties
	 * <b>swy.service.energy</b> - Determines how energy requests are tackled.<br>
	 * <b>possible choices:</b> <i>{@link #singlePointEnergyCalculator() singlePointEnergy}</i> 
	 * | {@link #mathematicalFunction() mathematicalFunction}
	 * | {@link #optimizedEnergyCalculator() optimizedEnergy}<br><br>
	 */
	@Bean
	@Primary
	@Lazy(false)
	public EnergyInterface energyInterface(){
		switch (swyConfig.safeGetProperty("swy.service.energy")) {
			case "mathematicalFunction":
				return mathematicalFunction()
				break
			case "singlePointEnergy":
				return singlePointEnergyCalculator()
				break
			case "optimizedEnergy":
				return optimizedEnergyCalculator()
				break
		}
	}

	/**
	 * @NeededBeans
	 * {@link org.wallerlab.swy.configuration.SwyConfig#searchSpace()}
	 */
	@Bean
	public MathematicalFunction mathematicalFunction() {
		MathematicalFunction mathematicalFunction = new MathematicalFunction()
		mathematicalFunction.setSearchSpace(appContext.getBean("searchSpace"))
		return mathematicalFunction
	}
	
	/**
	 * @return A bean that is able to create energies using single point calculations.
	 * A {@link #singlePointModel()} needs to be chosen to use this bean.
	 * 
	 * @NeededBeans
	 * {@link #singlePointModel()} | {@link org.wallerlab.swy.configuration.SearchSpaceConfig#conformationFactory()}
	 */
	@Bean
	public SinglePointEnergyCalculator singlePointEnergyCalculator() {
		SinglePointEnergyCalculator singlePointEnergyCalculator = new SinglePointEnergyCalculator()
		singlePointEnergyCalculator.setConformationFactory(appContext.getBean("conformationFactory"))
		singlePointEnergyCalculator.setModelFactory(singlePointModel())
		return singlePointEnergyCalculator
	}
	
	/**
	 * @return This bean determines which external program is used for
	 * single point calculations. Currently working are interfaces to
	 * the programs {@link #mopac()} and {@link #dftbPlus()}. {@link #orca()}
	 * and {@link #gaussian()} should be tested before using them.
	 * 
	 * For subsequent optimizations a different code might be
	 * wanted (e.g. start with a force field and do final optimizations
	 * with a cheap HF code), to be chosen in the {@link #optimizedEnergyCalculator()}.
	 * 
	 * @NeededProperties
	 * <b>swy.service.singlePoint</b> - The external code to be used to
	 * calculate a single point energy.<br>
	 * <b>possible choices:</b> <i>{@link #mopac() mopac}</i> |
	 * {@link #dftbPlus() dftbPlus} |
	 * {@link #orca() orca} |
	 * {@link #gaussian() gaussian}<br><br>
	 */
	@Bean
	public ModellingInterface singlePointModel() {
		BeanReferenceFactoryBean factory = new BeanReferenceFactoryBean()
		factory.setTargetBeanName(swyConfig.safeGetProperty("swy.service.singlePoint"))
		factory.setBeanFactory(appContext)
		return factory.getObject()
	}
	
	// TODO not yet correct (compare with above)
	/**
	 * Should not be used.
	 *
	 * @NeededProperties
	 * <b>swy.service.optimizer</b> - The external code to be used to
	 * calculate an energy by locally optimizing coordinates.
	 * <b>possible choices</b>
	 */
	@Bean
	public StructureOptimizer optimizedEnergyCalculator() {
		BeanReferenceFactoryBean factory = new BeanReferenceFactoryBean()
		factory.setTargetBeanName(swyConfig.safeGetProperty("swy.service.optimizer"))
		factory.setBeanFactory(appContext)
		return factory.getObject()
	}
	
	/*	@Bean
	public OptimizationInterface optimizationInterface(){
		BeanReferenceFactoryBean factory = new BeanReferenceFactoryBean()
		factory.setTargetBeanName(env.getProperty("swy.service.optimization"))
//		factory.setTargetBeanName("${swy_service_optimization}")
		return factory
	}
*/
	
	/*
	 * The external codes
	 */
	/**
	 * @NeededBeans
	 * {@link org.wallerlab.swy.configuration.SwyConfig#molecularSystem()}
	 * 
	 * @see org.wallerlab.swy.service.energy.wrappers.Mopac
	 */
	@Bean
	public Mopac mopac() {
		Mopac mopac = new Mopac()
		mopac.setMolecularSystem(appContext.getBean("molecularSystem"))
		return mopac
	}
	
	/**
	 * @see org.wallerlab.swy.service.energy.wrappers.Orca
	 * 
	 * @NeededBeans
	 * {@link org.wallerlab.swy.configuration.SwyConfig#molecularSystem()}
	 */
	@Bean
	public Orca orca() {
		Orca orca = new Orca()
		orca.setMolecularSystem(appContext.getBean("molecularSystem"))
		return orca
	}
	
	/**
	 * @see org.wallerlab.swy.service.energy.wrappers.DftbPlus
	 * 
	 * @NeededBeans
	 * {@link org.wallerlab.swy.configuration.SwyConfig#molecularSystem()}
	 */
	@Bean
	public DftbPlus dftbPlus() {
		DftbPlus dftbPlus = new DftbPlus()
		dftbPlus.setMolecularSystem(appContext.getBean("molecularSystem"))
		return dftbPlus
	}
	
	/**
	 * @see org.wallerlab.swy.service.energy.wrappers.Gaussian
	 * 
	 * @NeededBeans
	 * {@link org.wallerlab.swy.configuration.SwyConfig#molecularSystem()}
	 */
	@Bean
	public Gaussian gaussian() {
		Gaussian gaussian = new Gaussian()
		gaussian.setMolecularSystem(appContext.getBean("molecularSystem"))
		return gaussian
	}

	@Override
	@Required
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.appContext = applicationContext
	}
}
