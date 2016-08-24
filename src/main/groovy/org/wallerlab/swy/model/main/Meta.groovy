package org.wallerlab.swy.model.main

import org.springframework.context.annotation.Profile;

import java.util.ArrayList;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
* Domain model for the Meta.
*/
@Entity
@Table(name = "META")
@Profile(["unstable"])
class Meta {
	
	@Id
	@Column(name = "ID")
	int id
	
	/** The agent domain models belonging to that meta */
	@Column(name = "AGENTS")
	ArrayList<Agent> agents
	
	/** A status flag for when calculation has completed */
	@Column(name = "FINISHED")
	boolean finished
}
