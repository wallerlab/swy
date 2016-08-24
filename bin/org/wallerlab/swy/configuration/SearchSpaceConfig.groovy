package org.wallerlab.swy.configuration;

import org.wallerlab.swy.service.conformation.factory.*;
import org.wallerlab.swy.service.modeltools.SearchSpaceDiscretiser
import org.wallerlab.swy.service.searchspace.factory.*;
import org.wallerlab.swy.service.searchspace.factory.supramolecular.*

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.config.BeanReferenceFactoryBean
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Lazy;

/**
 * This class configures the way the search space is set up,
 * as well as the configuration that follows upon the choice
 * of the search space. So the way conformations are generated
 * is also triggered here.
 */
@Configuration
@Lazy
public class SearchSpaceConfig implements ApplicationContextAware {
	private static ApplicationContext appContext
	
	@Autowired
	private SwyConfig swyConfig
	
	/**
	 * @return A bean containing the rules in which way the search space is supposed to
	 * be set up. Currently working possibilities to create a search space are {@link #bondRotation()}
	 * and {@link #supramolecular()} (and the related ones). The {@link #zMatrixStyle()}
	 * should NOT be used. The {@link org.wallerlab.swy.configuration.MetaConfig#parameterControlSearchSpaceInterface()}
	 * is configured elsewhere.
	 * 
	 * @NeededProperties
	 * <b>swy.service.searchspace</b> - Determines the way the search space is created.<br>
	 * <b>possible choices:</b> {@link #bondRotation() bondRotation} |
	 * {@link #supramolecular() supramolecular} |
	 * {@link #supramolecularPosition() supramolecularPosition} |
	 * {@link #supramolecularAngles() supramolecularAngles} |
	 * {@link #zMatrixStyle() zMatrixStyle}<br><br>
	 */
	@Bean
	@Primary
	@Lazy(false)
	public SearchSpaceInterface searchSpaceInterface(){
		BeanReferenceFactoryBean factory = new BeanReferenceFactoryBean()
		factory.setTargetBeanName(swyConfig.safeGetProperty("swy.service.searchspace"))
		factory.setBeanFactory(appContext)
		return factory.getObject()
	}
	
	/**
	 * @see org.wallerlab.swy.service.searchspace.factory.BondRotation
	 * 
	 * @NeededBeans
	 * {@link org.wallerlab.swy.configuration.SwyConfig#molecularSystem()}
	 */
	@Bean
	public BondRotation bondRotation() {
		BondRotation bondRotation = new BondRotation()
		bondRotation.setMolecularSystem(appContext.getBean("molecularSystem"))
		return bondRotation
	}
	
	/**
	 * @see org.wallerlab.swy.service.searchspace.factory.supramolecular.PositionAndAngles
	 * 
	 * @NeededBeans
	 * {@link org.wallerlab.swy.configuration.SwyConfig#molecularSystem()}
	 */
	@Bean
	public PositionAndAngles supramolecular() {
		PositionAndAngles positionAndAngles = new PositionAndAngles()
		positionAndAngles.setMolecularSystem(appContext.getBean("molecularSystem"))
		return positionAndAngles
	}
	
	/**
	 * Should NOT be used.
	 * 
	 * @see org.wallerlab.swy.service.searchspace.factory.ZMatrixStyle
	 * 
	 * @NeededBeans
	 * {@link org.wallerlab.swy.configuration.SwyConfig#molecularSystem()}
	 */
	@Bean
	public ZMatrixStyle zMatrixStyle() {
		ZMatrixStyle zMatrixStyle = new ZMatrixStyle()
		zMatrixStyle.setMolecularSystem(appContext.getBean("molecularSystem"))
		return zMatrixStyle
	}
	
	/**
	 * @see org.wallerlab.swy.service.searchspace.factory.supramolecular.Angles
	 * 
	 * @NeededBeans
	 * {@link org.wallerlab.swy.configuration.SwyConfig#molecularSystem()}
	 */
	@Bean
	public Angles supramolecularAngles() {
		Angles angles = new Angles()
		angles.setMolecularSystem(appContext.getBean("molecularSystem"))
		return angles
	}
	
	/**
	 * @see org.wallerlab.swy.service.searchspace.factory.supramolecular.Position
	 * 
	 * @NeededBeans
	 * {@link org.wallerlab.swy.configuration.SwyConfig#molecularSystem()}
	 */
	@Bean
	public Position supramolecularPosition() {
		Position position = new Position()
		position.setMolecularSystem(appContext.getBean("molecularSystem"))
		return position
	}

	/**
	 * @return A bean containing a {@link org.wallerlab.swy.service.conformation.factory.ConformationInterface
	 * conformation factory}, corresponding to the requested {@link #searchSpaceInterface()
	 * search space generator}. The coupling of a certain search space to a certain conformation
	 * factory is hard-coded.
	 * 
	 * @NeededBeans
	 * {@link org.wallerlab.swy.configuration.SwyConfig#molecularSystem()}
	 * 
	 * @NeededProperties
	 * <b>swy.service.searchspace</b> - Determines the way the search space is created.<br>
	 * <b>possible choices:</b> {@link #bondRotation() bondRotation} |
	 * {@link #supramolecular() supramolecular} |
	 * {@link #supramolecularPosition() supramolecularPosition} |
	 * {@link #supramolecularAngles() supramolecularAngles} |
	 * {@link #zMatrixStyle() zMatrixStyle}<br><br>
	 */
	@Bean
	public ConformationInterface conformationFactory() {
		ConformationInterface conformationFactory
		switch(swyConfig.safeGetProperty("swy.service.searchspace")) {
			case "bondRotation":
				conformationFactory = new DihedralConformation()
				break
			
			case ["supramolecular", "supramolecularAngles", "supramolecularPosition"]:
				conformationFactory = new SupramolecularConformation()
				if (["supramolecular", "supramolecularAngles"].contains\
						(swyConfig.safeGetProperty("swy.service.searchspace"))) {
					conformationFactory.adjustAngles = true
				}
				if (["supramolecular", "supramolecularPosition"].contains\
						(swyConfig.safeGetProperty("swy.service.searchspace"))) {
					conformationFactory.adjustPositions = true
				}
				break
				
			case "zMatrixStyle":
				conformationFactory = new CompleteMoleculeManipulation()
				break
		}
		conformationFactory.setMolecularSystem(appContext.getBean("molecularSystem"))
		return conformationFactory
	}
	
	@Bean
	public SearchSpaceDiscretiser searchSpaceDiscretiser() {
		SearchSpaceDiscretiser searchSpaceDiscretiser = \
			new SearchSpaceDiscretiser(appContext.getBean("searchSpace"))
		return searchSpaceDiscretiser
	}

	@Override
	@Required
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.appContext = applicationContext
	}
}
