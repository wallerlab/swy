package org.wallerlab.swy.model

import javax.persistence.Id
import javax.persistence.Table
import javax.persistence.Column
import javax.persistence.Entity

/**
 * Domain model for Molecular Coordinates
 */
@Entity
@Table(name = "COORDINATES")
class Coordinates {
	
	@Id
	@Column(name = "ID")
	int id
	
	/* Molecular Coordinates */
	@Column(name = "X_COORD")
	public ArrayList<double[]> xCoord
	
	@Column(name = "Y_COORD")
	public ArrayList<double[]> yCoord
	
	@Column(name = "Z_COORD")
	public ArrayList<double[]> zCoord

	public Coordinates() {
	}
}
