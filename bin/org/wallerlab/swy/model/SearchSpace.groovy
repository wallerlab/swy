package org.wallerlab.swy.model;

import org.springframework.context.annotation.Profile;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Domain model for the search space. 
 */
@Entity
@Table(name = "SOLUTION")
@Profile(["untested"])
public class SearchSpace {

	@Id
	@Column(name = "ID")
	int id
	
	/** dimensionality of the searchspace */
	public int numberOfDimensions;
	
	/** min limit of the Search space  */
	public double[] startValues;
	/** max limit of the Search space  */
	public double[] endValues;

	/** This flag is needed because angles are "periodic", while cartesians are not  */
	public boolean[] periodic;

	/** This is a list which's entries are generated via use of the granularity. */
	// -> each dimension can have a somewhat own step size/granularity/..., constraints could be realized through this list, .....
	public double[] stepSize;
	
	/* TODO ideas how to get that in?? */
	public def constraints;
	
	/** Every dimension has a name explaining what it is. Important if automatic parameter control is used. */
	public List<String> caption;
}
