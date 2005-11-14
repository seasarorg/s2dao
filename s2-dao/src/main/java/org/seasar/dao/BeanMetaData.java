package org.seasar.dao;

import org.seasar.extension.jdbc.ColumnNotFoundRuntimeException;
import org.seasar.extension.jdbc.PropertyType;
import org.seasar.framework.beans.PropertyNotFoundRuntimeException;

/**
 * @author higa
 *  
 */
public interface BeanMetaData extends DtoMetaData {


	public String getTableName();

	public PropertyType getVersionNoPropertyType()
			throws PropertyNotFoundRuntimeException;

	public String getVersionNoPropertyName();
	
	public boolean hasVersionNoPropertyType();

	public PropertyType getTimestampPropertyType()
			throws PropertyNotFoundRuntimeException;

	public String getTimestampPropertyName();

	public boolean hasTimestampPropertyType();

	public String convertFullColumnName(String alias);

	public PropertyType getPropertyTypeByAliasName(String aliasName)
			throws ColumnNotFoundRuntimeException;

	public PropertyType getPropertyTypeByColumnName(String columnName)
			throws ColumnNotFoundRuntimeException;

	public boolean hasPropertyTypeByColumnName(String columnName);

	public boolean hasPropertyTypeByAliasName(String aliasName);

	public int getRelationPropertyTypeSize();

	public RelationPropertyType getRelationPropertyType(int index);

	public RelationPropertyType getRelationPropertyType(String propertyName)
			throws PropertyNotFoundRuntimeException;

	public int getPrimaryKeySize();

	public String getPrimaryKey(int index);
	
	public IdentifierGenerator getIdentifierGenerator();

	public String getAutoSelectList();

	public boolean isRelation();
}