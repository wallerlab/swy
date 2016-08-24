package org.wallerlab.swy

import org.wallerlab.swy.annotations.Cite
import org.wallerlab.swy.configuration.SwyConfig;
import org.wallerlab.swy.dao.*
import org.wallerlab.swy.model.main.Run
import org.wallerlab.swy.service.batch.BatchService
import org.wallerlab.swy.service.meta.MetaInterface;

import org.springframework.beans.factory.annotation.Required
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer
import org.springframework.core.env.Environment
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.io.support.ResourcePropertySource;

/**
 * <b>This is Swy:</b><br>
 * A black-box non-local optimizer.<br>
 * Swy is a multi-tiered large-scale modular optimizer for many body systems.
 * Swy was written as an (agile) software packages for performing such calculations.
 * The programming methodology herein is convention over configuration.
 * We interface with industry standard software for the heavy lifting,
 * and the relatively light business logic is left to two-up.<br><br>
 *
 * <b>Scope:</b><br>
 * Supramolecular Chemistry is the primary intended area of interest.
 * Counter-ion placement for charged complexes.
 * Solvation using a QM/MM type approach.<br><br>
 *
 * <b>Setup:</b><br>
 * To see how a swy calculation needs to and can be set up as well as for
 * all the internal dependencies look at the configuration classes, starting
 * with {@link org.wallerlab.swy.configuration.SwyConfig}.
 */
class Swy{
	
	/* Spring Container */
	private static AnnotationConfigApplicationContext appContext
	
	/* SwyId interface to get the Id. */
	private static SwyInputLocationDao swyIdInterface 
	
	/* Read in Swy Job Parameters */
	private static SwyPropertyDao swyPropertyInterface
	private static SwyMoleculeDao swyMoleculeInterface
	
	/** the environment the main job runs in. Contains profiles and properties. */
	private static ConfigurableEnvironment swyEnvironment
		
	/* For Command line runner using xml/flatfile input. */
	static void startSwy(String inputLocation){		
	
		/* If swyId is null, get it from swyIdInterface  */
		if(inputLocation  == null){
			 inputLocation = swyIdInterface.getInputLocation()
		}
		
		swyEnvironment.getPropertySources().addFirst(\
			new ResourcePropertySource("standard.properties"))
		/* Read in the parameters for the current job Id */
		swyEnvironment.getPropertySources().addFirst(\
			swyPropertyInterface.getPropertySource(inputLocation))
		
		/* Now that the parameters are read in, load up the SwyConfig.class */
		appContext = new AnnotationConfigApplicationContext()		
		appContext.register(SwyConfig.class)
		appContext.setEnvironment(swyEnvironment)
		PropertySourcesPlaceholderConfigurer pspc = new PropertySourcesPlaceholderConfigurer()
		pspc.setEnvironment(swyEnvironment)
		appContext.addBeanFactoryPostProcessor(pspc)
		appContext.refresh()
		
		Environment env = SwyConfig.getEnv()
		BatchService<Run> runBatchService = appContext.getBean("runBatchService")
		Run[] runs = new Run[env.getProperty("swy.run.cycles").toInteger()]
		for (int runIndex in 0..(env.getProperty("swy.run.cycles")).toInteger()-1) {
			runs[runIndex] = new Run()
		}
		runBatchService.populateAllModels(runs)
		
		System.out.println("Shutting down");
		
		printCitations()
		
		appContext.close()
		
		return
	}
	
	private static void printCitations() {
		ArrayList<Cite> usedReferences = new ArrayList<Cite>()
		Map citationAnnotatedBeans = appContext.getBeansWithAnnotation(Cite.class)
		println citationAnnotatedBeans
		citationAnnotatedBeans.each() {thisBeanType ->
			usedReferences << appContext.findAnnotationOnBean(thisBeanType.key, Cite.class)
		}
		usedReferences.unique()
		for (Cite citation: usedReferences) {
			printCitation(citation)
		}
	}
	private static void printCitation(Cite citation) {
		for (String author: citation.authors()){
			print (author+"; ")
		}
		print (citation.journal()+", ")
		print (citation.year()+", ")
		print (citation.pages()+".\n")
	}
	
	@Required
	public static void setSwyPropertyInterface (SwyPropertyDao swyPropertyInterface) {
		this.swyPropertyInterface = swyPropertyInterface
	}
	
	@Required
	public static void setSwyMoleculeInterface (SwyMoleculeDao swyMoleculeInterface) {
		this.swyMoleculeInterface = swyMoleculeInterface
	}
	
	@Required
	public static void setSwyEnvironment (ConfigurableEnvironment swyEnvironment) {
		this.swyEnvironment = swyEnvironment
	}
}