package org.seasar.dao;

import org.seasar.framework.exception.SRuntimeException;


/**
 * @author higa
 *
 */
public class IfConditionNotFoundRuntimeException extends SRuntimeException {
	
	public IfConditionNotFoundRuntimeException() {
		super("EDAO0004");
	}
}