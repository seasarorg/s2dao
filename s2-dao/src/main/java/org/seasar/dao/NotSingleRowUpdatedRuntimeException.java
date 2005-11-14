package org.seasar.dao;

/**
 * @author higa
 *
 */
public class NotSingleRowUpdatedRuntimeException extends
		UpdateFailureRuntimeException {

	/**
	 * @param bean
	 * @param rows
	 */
	public NotSingleRowUpdatedRuntimeException(Object bean, int rows) {
		super(bean, rows);
	}

}
