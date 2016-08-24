package org.wallerlab.swy.dao;

import java.io.IOException;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.PropertySource;

/**
 * Obtain job properties for the environment from a source 
 * such as jms or plain .properties file.
 *
 */
public interface SwyPropertyDao {

	public PropertySource<?> getPropertySource(String inputLocation) throws IOException;
	
}
