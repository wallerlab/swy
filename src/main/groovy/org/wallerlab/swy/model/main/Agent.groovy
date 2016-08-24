package org.wallerlab.swy.model.main

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
* Domain model for the Agents. This is a precise form of
* what we want to get out of the whole program, that is
* how do we need to construct the coordinates to get a
* (hopefully good) enerfy/fitness.
*/
@Entity
@Table(name = "AGENT")
class Agent {

	@Id
	@Column(name = "ID")
	int id 
	
	@Column(name = "AGENT_ID")
	public int agentIndex

	/** The energy for the current agent */
	@Column(name = "ENERGY")
	public double lastReasonableEnergy
	
	/** The fitness for the current agent */
	@Column(name = "FITNESS")
	public double fitness
	
	/** The solution for the current agent */
	@Column(name = "SOLUTION")
	public double[] lastReasonableSolution
}
