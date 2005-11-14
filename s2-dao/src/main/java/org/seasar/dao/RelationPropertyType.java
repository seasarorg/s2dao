package org.seasar.dao;

import org.seasar.extension.jdbc.PropertyType;


/**
 * @author higa
 *
 */
public interface RelationPropertyType extends PropertyType {

	public int getRelationNo();

	public int getKeySize();
	
	public String getMyKey(int index);
	
	public String getYourKey(int index);
	
	public boolean isYourKey(String columnName);
	
	public BeanMetaData getBeanMetaData();
}
