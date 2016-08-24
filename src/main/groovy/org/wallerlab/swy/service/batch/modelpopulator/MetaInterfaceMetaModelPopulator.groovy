package org.wallerlab.swy.service.batch.modelpopulator

import org.wallerlab.swy.model.main.Agent
import org.wallerlab.swy.model.main.Meta
import org.wallerlab.swy.service.meta.MetaInterface
import org.wallerlab.swy.service.modeltools.AgentModelListLimiter

import org.springframework.beans.factory.annotation.Required
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

/**
 * This class populates a meta model using a metaInterface.
 * 
 * 
 * No person understands this.....we need a name change.
 */
@Service
@Profile(["untested"])
public class MetaInterfaceMetaModelPopulator implements ModelPopulator<Meta> {

	private MetaInterface metaInterface;

	public MetaInterfaceMetaModelPopulator() {
	}

	private ArrayList<Agent> modelAgents;
	
	private Agent[] newAgents;
	
	private AgentModelListLimiter agentModelListLimiter;
	
	@Override
	public void populateModel(Meta model) {
		model.setFinished(false);
		modelAgents = new ArrayList<Agent>();
		model.setAgents(modelAgents);
		while(true) {
			newAgents = metaInterface.getBetterAgents();
			if (newAgents==null) {
				model.setFinished(true);
				break;
			} else {
				updateAgentsOfModel();
			}
		}
	}
	
	private void updateAgentsOfModel() {
		modelAgents.addAll(Arrays.asList(newAgents));
		modelAgents.unique();
		agentModelListLimiter.shortenAgentModelList(modelAgents)
	}
	
	@Required
	public void setMetaInterface(MetaInterface metaInterface) {
		this.metaInterface = metaInterface
	}
	
	@Required
	public void setAgentModelListLimiter(AgentModelListLimiter agentModelListLimiter) {
		this.agentModelListLimiter = agentModelListLimiter
	}
}
