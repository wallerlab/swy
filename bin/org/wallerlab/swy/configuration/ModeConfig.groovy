package org.wallerlab.swy.configuration;

import org.wallerlab.swy.dao.SwyMoleculeDao;
import org.wallerlab.swy.dao.xml.XmlCoordinateReader;

import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * Configuration of mode-specific settings. Here settings that differ
 * in development/production mode are configured.
 * 
 * @author t_dres03
 */
@Configuration
public class ModeConfig implements EnvironmentAware{

	Environment environment;
	
	private String getMode() {
		if(environment.acceptsProfiles("prod")) {
			return "prod";
		} else {
			return "dev";
		}
	}
	
	@Bean
	SwyMoleculeDao swyMoleculeDao() throws BadConfigurationException {
		switch(getMode()){
		case "dev":
			return new XmlCoordinateReader();
		default:
			throw new BadConfigurationException("SwyMoleculeDao only available for profiles 'dev'.");
		}
	}
	
	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}
}
