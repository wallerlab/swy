package org.wallerlab.swy.service.modeltools;

import org.wallerlab.swy.model.main.Agent;

import java.util.ArrayList;

/**
 * To prevent the database (and memory) from being overcrowded
 * with agents/solutions which might be pretty bad anyway the
 * implementations of this interface provide ways to limit the
 * number of agent models that e.g. a meta model holds.
 * 
 * @author t_dres03
 */
public interface AgentModelListLimiter {

	public void shortenAgentModelList(ArrayList<Agent> agentList);

}
