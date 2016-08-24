package org.wallerlab.swy.service.meta;

import java.util.List;

/**
 * A controllable meta can be controlled from the outside, meaning that it is possible
 * to manipulate its parameters while it is running. Although every meta should have
 * this functionality it must be ensured that such a manipulation cannot break a running
 * meta.
 * 
 * @author t_dres03
 *
 */
public interface ControllableMeta extends MetaInterface {

	public List<String> getParameterNames();
	
}
