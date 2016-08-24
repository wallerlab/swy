package org.wallerlab.swy.dao.jms

import org.wallerlab.swy.dao.SwyInputLocationDao;

import java.util.Map;
import org.springframework.context.annotation.Profile;

/**
 * The JmsListener is needed at initialization, 
 * i.e. before app-context is loaded. It gets the 
 * Job Id from the grails front-end.
 * 
 */
@Profile(["unstable"])
public class SwyIdListener implements SwyInputLocationDao{

	private String inputLocation
	
	public int swyReceived(Map<String, Object> message ) throws Exception {
		inputLocation = ${message['JobId']}
		return
	}

	@Override
	public String getInputLocation() {
		while(inputLocation=null) {
			sleep(500)
		}
		return inputLocation;
	}
	
	@Override
        public void putSwyId(String inputLocation) {
			this.inputLocation = inputLocation
        }

}
