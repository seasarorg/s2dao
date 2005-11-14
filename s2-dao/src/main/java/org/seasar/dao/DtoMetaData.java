package org.seasar.dao;

import org.seasar.extension.jdbc.PropertyType;
import org.seasar.framework.beans.PropertyNotFoundRuntimeException;

/**
 * @author higa
 *  
 */
public interface DtoMetaData {

	public String COLUMN_SUFFIX = "_COLUMN";

	public Class getBeanClass();

	public int getPropertyTypeSize();

	public PropertyType getPropertyType(int index);

	public PropertyType getPropertyType(String propertyName)
			throws PropertyNotFoundRuntimeException;

	public boolean hasPropertyType(String propertyName);
}