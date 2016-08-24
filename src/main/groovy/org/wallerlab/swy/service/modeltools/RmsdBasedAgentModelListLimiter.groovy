package org.wallerlab.swy.service.modeltools;

import org.wallerlab.swy.model.main.Agent;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;

import java.util.ArrayList;

/**
 * This class can resort a list of agent models by its energy
 * and simply throws away the ones with the worst energies.
 * 
 * @author ??? who wants to have his/her name here?
 *
 * @NeededProperties
 * <b><i><font color=purple>int</font></i> swy.model.maximumNumberOfAgentModelsPerMetaModel</b> -
 * This number determines how many of the agent models should be stored. If more than one
 * meta model is created each one of it has this limit.<br>
 * <b>limits:</b> 0 to infinity. 0 is not recommended unless doing test calculations. When
 * having a lot of runs or metas this number should be chosen low.<br>
 * <b>default:</b> <i>50</i>
 */
@Profile([ "unstable" ])
public class RmsdBasedAgentModelListLimiter implements AgentModelListLimiter {

	@Value('${swy.model.maximumNumberOfAgentModelsPerMetaModel}')
	private int maximumNumberOfAgentModelsPerMetaModel;
	
	@Override
	public void shortenAgentModelList(ArrayList<Agent> agentList) {
		// TODO not implemented
	}

}
