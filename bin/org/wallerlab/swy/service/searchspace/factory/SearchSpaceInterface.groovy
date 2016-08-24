package org.wallerlab.swy.service.searchspace.factory

import org.wallerlab.swy.model.SearchSpace

/**
 * The implementing classes can set up a proper searchSpace.
 */
public interface SearchSpaceInterface {
	
	 SearchSpace generateSearchSpace();
	 	
}
