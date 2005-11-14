package org.seasar.dao;

import org.seasar.framework.exception.SRuntimeException;


/**
 * @author higa
 *
 */
public class IllegalBoolExpressionRuntimeException extends SRuntimeException {

	private String expression_;
	
	public IllegalBoolExpressionRuntimeException(String expression) {
		super("EDAO0003", new Object[]{expression});
		expression_ = expression;
	}
	
	public String getExpression() {
		return expression_;
	}
}
