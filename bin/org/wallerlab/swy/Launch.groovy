package org.wallerlab.swy

import org.wallerlab.swy.Swy

import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext

/**
 * The launcher for Swy is required to gather the Job Parameters
 * before the 'real' Application environment can be created. 
 */
class Launch{

	/* swyId is the unique id for running the calculations, i.e. unique primary key */	
	private static String inputLocation 
	
	/** 
	 *  The main entry into Swy. Welcome.
	 *  
	 *  @param args[0] is the file id. e.g. 101 would load in
	 *  data/Inputs/101.properties.
	 */
	static void main(String [] args){	

		/* Set the swyId from the command line, if possible.  */
		if(args[0]){ inputLocation = args[0]}
		
		/* Create the Launch Spring Container  */
		// TODO: the listener should be only loaded when it is supposed to be used.
		ApplicationContext appContext = new ClassPathXmlApplicationContext("launch-context.xml");	
		
		/* Entry Bean */
		Swy swy = appContext.getBean("swy")
		swy.startSwy(inputLocation)
				
	return
	}
}
