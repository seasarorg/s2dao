package org.seasar.dao;

import org.seasar.framework.exception.SRuntimeException;


/**
 * @author higa
 *
 */
public class EndCommentNotFoundRuntimeException extends SRuntimeException {
	
	public EndCommentNotFoundRuntimeException() {
		super("EDAO0007");
	}
}