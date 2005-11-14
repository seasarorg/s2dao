package org.seasar.dao;

import org.seasar.framework.exception.SRuntimeException;


/**
 * @author higa
 *
 */
public class PrimaryKeyNotFoundRuntimeException extends SRuntimeException {
	
	private Class targetClass_;
	
	public PrimaryKeyNotFoundRuntimeException(Class targetClass) {
		super("EDAO0009", new Object[]{targetClass.getName()});
		targetClass_ = targetClass;
	}
	
	public Class getTargetClass() {
		return targetClass_;
	}
}