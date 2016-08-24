package org.wallerlab.swy.service.batch.processor

import org.wallerlab.swy.model.ProblemRepresentation
import org.wallerlab.swy.model.main.Run;

import org.springframework.batch.item.ItemProcessor
import org.springframework.context.annotation.Lazy
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service

/**
 * The Item Processor.
 */
@Lazy
//@Service('swyItemProcessor')
@Profile(["unstable"])
class SwyItemProcessor implements ItemProcessor<Object, Object> {			
	
	/**
	 * Generic batch 
	 * 
	 * @param run
	 * @return
	 */
	public Object process(Object object){			
		object.startSearch()
		return  
	}	
}
