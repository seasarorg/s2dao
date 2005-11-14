package org.seasar.dao.impl;

import org.seasar.dao.BeanMetaData;
import org.seasar.dao.RelationPropertyType;
import org.seasar.extension.jdbc.impl.PropertyTypeImpl;
import org.seasar.framework.beans.PropertyDesc;

/**
 * @author higa
 *  
 */
public class RelationPropertyTypeImpl extends PropertyTypeImpl
		implements
			RelationPropertyType {

	protected int relationNo_;
	protected String[] myKeys_;
	protected String[] yourKeys_;
	protected BeanMetaData beanMetaData_;

	public RelationPropertyTypeImpl(PropertyDesc propertyDesc){
		super(propertyDesc);		
	}
	public RelationPropertyTypeImpl(PropertyDesc propertyDesc, int relationNo,
			String[] myKeys, String[] yourKeys, BeanMetaData  beanMetaData
			) {

		super(propertyDesc);
		relationNo_ = relationNo;
		myKeys_ = myKeys;
		yourKeys_ = yourKeys;
		beanMetaData_ = beanMetaData;
	}

	public int getRelationNo() {
		return relationNo_;
	}

	/**
	 * @see org.seasar.dao.RelationPropertyType#getKeySize()
	 */
	public int getKeySize() {
		if (myKeys_.length > 0) {
			return myKeys_.length;
		} else {
			return beanMetaData_.getPrimaryKeySize();
		}
		
	}

	/**
	 * @see org.seasar.dao.RelationPropertyType#getMyKey(int)
	 */
	public String getMyKey(int index) {
		if (myKeys_.length > 0) {
			return myKeys_[index];
		} else {
			return beanMetaData_.getPrimaryKey(index);
		}
	}

	/**
	 * @see org.seasar.dao.RelationPropertyType#getYourKey(int)
	 */
	public String getYourKey(int index) {
		if (yourKeys_.length > 0) {
			return yourKeys_[index];
		} else {
			return beanMetaData_.getPrimaryKey(index);
		}
	}
	
	/**
	 * @see org.seasar.dao.RelationPropertyType#isYourKey(java.lang.String)
	 */
	public boolean isYourKey(String columnName) {
		for (int i = 0; i < getKeySize(); ++i) {
			if (columnName.equalsIgnoreCase(getYourKey(i))) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @see org.seasar.extension.jdbc.RelationPropertyType#getBeanMetaData()
	 */
	public BeanMetaData getBeanMetaData() {
		return beanMetaData_;
	}
}