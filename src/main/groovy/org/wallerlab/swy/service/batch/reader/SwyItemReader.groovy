package org.wallerlab.swy.service.batch.reader

import org.wallerlab.swy.model.main.Run;

import org.springframework.batch.item.ItemReader
import org.springframework.beans.factory.annotation.Value
import org.springframework.beans.factory.config.BeanReferenceFactoryBean
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component

/**
 * Creates then Reads: The Run Batch
 * 
 * @return an array of beans to process
 */

//@Component('swyItemReader')
@Profile(["unstable"])
class SwyItemReader implements ItemReader<Object> {
	
	static  items =[]
	static int readIndex = -1;
	
	/* Create items (List) */
	static {
		
		/* Wire in the correct bean name from jobParams */
		@Value("#{jobParameters['Stage']}")
		 String stage
		
		/* Get the number of cycles from the properties file */
		@Value("#{jobParameters['Cycles']}")
		int cycles
		
		BeanReferenceFactoryBean factory = new BeanReferenceFactoryBean()
		
		for (i in 0..cycles){	
			// Either run, hyper, meta, or agent. - all must have .startSearch  method enforced via interface	
			 items[i] = factory.setTargetBeanName('$stage')
		}
	}

	/**
	 * Reads next record from a list of items
	 */
	Run read()  {
		readIndex++;
		if (readIndex>=items.size()) {
			readIndex=-1;
			return null;
		}
		items[readIndex];
	}
}
