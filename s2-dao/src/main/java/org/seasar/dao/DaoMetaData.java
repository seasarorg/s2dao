package org.seasar.dao;

import org.seasar.framework.beans.MethodNotFoundRuntimeException;

/**
 * @author higa
 *  
 */
public interface DaoMetaData {

	public Class getBeanClass();

	public BeanMetaData getBeanMetaData();

	public boolean hasSqlCommand(String methodName);

	public SqlCommand getSqlCommand(String methodName)
			throws MethodNotFoundRuntimeException;

	public SqlCommand createFindCommand(String query);

	public SqlCommand createFindArrayCommand(String query);

	public SqlCommand createFindBeanCommand(String query);

	public SqlCommand createFindObjectCommand(String query);
}