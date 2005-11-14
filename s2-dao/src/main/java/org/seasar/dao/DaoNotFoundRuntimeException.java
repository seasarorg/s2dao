package org.seasar.dao;

import org.seasar.framework.exception.SRuntimeException;


/**
 * @author higa
 *
 */
public class DaoNotFoundRuntimeException extends SRuntimeException {
	
	private Class targetClass_;
	
	public DaoNotFoundRuntimeException(Class targetClass) {
		super("EDAO0008", new Object[]{targetClass.getName()});
		targetClass_ = targetClass;
	}
	
	public Class getTargetClass() {
		return targetClass_;
	}
}