package org.seasar.dao;

import org.seasar.framework.exception.SRuntimeException;

/**
 * @author higa
 *  
 */
public class UpdateFailureRuntimeException extends SRuntimeException {

	private Object bean_;
	private int rows_;

	public UpdateFailureRuntimeException(Object bean, int rows) {
		super("EDAO0005", new Object[] { bean.toString(), String.valueOf(rows) });
		bean_ = bean;
		rows_ = rows;
	}

	public Object getBean() {
		return bean_;
	}
	
	public int getRows() {
		return rows_;
	}
}