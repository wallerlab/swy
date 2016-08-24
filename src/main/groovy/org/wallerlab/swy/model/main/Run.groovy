package org.wallerlab.swy.model.main

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
* Domain model for runs.
*/
@Entity
@Table(name = "RUN")
class Run {
	
	/* The table id of the run */
	@Id
	@Column(name = "ID")
	int id
		
	/** The hyper domain models belonging to that run */
	@Column(name = "METAS")
	ArrayList<Meta> metas
	
	/** A status flag for when calculation has completed */
	@Column(name = "FINISHED")
	boolean finished
}
