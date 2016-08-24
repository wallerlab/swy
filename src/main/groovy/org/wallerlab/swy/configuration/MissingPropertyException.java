package org.wallerlab.swy.configuration;

import org.springframework.context.annotation.Profile;

@Profile({ "unstable" })
public class MissingPropertyException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5549009218059452666L;
	
	public MissingPropertyException(String propertyName) {
		super("Property "+propertyName+" missing. A default is intentionally not provided.");
	}
	
}
