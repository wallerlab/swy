package org.wallerlab.swy

import org.springframework.beans.BeansException
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component

/**
 * A useful way to get the Application Context. But except
 * for configuration classes nothing should normally need
 * this.
 */
@Component
class ApplicationContextProvider implements ApplicationContextAware{
	 
	private static ApplicationContext ctx = null
	
	 static ApplicationContext getApplicationContext() {
		return ctx
	}
	 void setApplicationContext(ApplicationContext ctx) throws BeansException {
		this.ctx = ctx
	}
}
