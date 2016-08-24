package org.wallerlab.swy.dao.flat;

import org.wallerlab.swy.dao.SwyPropertyDao;

import org.springframework.context.annotation.Profile;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.ResourcePropertySource;
import org.springframework.stereotype.Repository;

import java.io.IOException;

/**
 * Obtain environment parameters from a flat properties file.
 * Uses standard spring properties file format.
 */
@Repository
@Profile(["untested"])
public class PropertyFileLinker implements SwyPropertyDao {
	
	@Override
	public PropertySource<Map<String,Object>> getPropertySource(String inputLocation) throws IOException {
		/* 
		 * In case a file is not found it is searched for on the classpath before
		 * throwing the exception.
		 */
		try{
			return new ResourcePropertySource("swyProperties", "file:${inputLocation}");
		} catch (FileNotFoundException fileError) {
			try{
				return new ResourcePropertySource("swyProperties", "classpath:${inputLocation}")
			} catch (FileNotFoundException classpathError) {
				throw fileError
			}
		}
	}

}
