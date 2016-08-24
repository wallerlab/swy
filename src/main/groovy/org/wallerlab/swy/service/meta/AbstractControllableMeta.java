package org.wallerlab.swy.service.meta;

import org.springframework.context.annotation.Profile;

import java.util.List;

@Profile({ "untested" })
public abstract class AbstractControllableMeta extends AbstractMeta implements ControllableMeta {

	/**
	 * Every MetaInterface must be controllable from the outside (e.g. by a hyperHeuristic)
	 * This array contains the names of all parameters that can be changed while the meta is
	 * running without causing trouble.
	 */
	protected final List<String> parameterNames;


	/**
	 * It is crucial that the parameters that CAN be modified during the run of a meta
	 * are already known after construction. So THIS superconstructor should be called by
	 * the children of this class (default constructor set to private to ensure that).
	 * 
	 * @param parameterNames is supposed to be an unmodifiableList, but no idea how to ensure that.
	 */
	protected AbstractControllableMeta(List<String> parameterNames) {
		this.parameterNames = parameterNames;
	}
	@SuppressWarnings("unused")
	private AbstractControllableMeta() {
		parameterNames=null;
	}
	
	/* Final is not necessary. */
	@Override
	public final List<String> getParameterNames() {
		return parameterNames;
	};
}
