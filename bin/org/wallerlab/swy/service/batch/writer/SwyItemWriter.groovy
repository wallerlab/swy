package org.wallerlab.swy.service.batch.writer

//import org.wallerlab.swy.dao.ItemDao

import org.springframework.batch.item.ItemWriter
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component

/**
 * The Item Writer
 * 
 * @param results to write out to dao.
 */
//@Component('swyItemWriter')
@Profile(["unstable"])
class SwyItemWriter implements ItemWriter<Object> {

//	@Value("#{jobParameters['Dao']}")
//	ItemDao itemDao
	
	void write(List<? extends Object> items) {

		for(Object object: items){
			itemDao.save(object);
		}
	}
}

