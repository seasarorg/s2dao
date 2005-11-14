package org.seasar.dao.impl;

import org.seasar.dao.BeanAnnotationReader;
import org.seasar.dao.DtoMetaData;
import org.seasar.extension.jdbc.PropertyType;
import org.seasar.extension.jdbc.ValueType;
import org.seasar.extension.jdbc.impl.PropertyTypeImpl;
import org.seasar.extension.jdbc.types.ValueTypes;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.PropertyDesc;
import org.seasar.framework.beans.PropertyNotFoundRuntimeException;
import org.seasar.framework.beans.factory.BeanDescFactory;
import org.seasar.framework.util.CaseInsensitiveMap;

/**
 * @author higa
 *  
 */
public class DtoMetaDataImpl implements DtoMetaData {

	private Class beanClass_;

	private CaseInsensitiveMap propertyTypes_ = new CaseInsensitiveMap();
	
	protected BeanAnnotationReader beanAnnotationReader_;

	protected DtoMetaDataImpl() {
	}

	public DtoMetaDataImpl(Class beanClass,
			BeanAnnotationReader beanAnnotationReader) {
		beanClass_ = beanClass;
		beanAnnotationReader_ = beanAnnotationReader;
		BeanDesc beanDesc = BeanDescFactory.getBeanDesc(beanClass);
		setupPropertyType(beanDesc);
	}

	/**
	 * @see org.seasar.dao.DtoMetaData#getBeanClass()
	 */
	public Class getBeanClass() {
		return beanClass_;
	}
	
	protected void setBeanClass(Class beanClass) {
		beanClass_ = beanClass;
	}

	/**
	 * @see org.seasar.dao.DtoMetaData#getPropertyTypeSize()
	 */
	public int getPropertyTypeSize() {
		return propertyTypes_.size();
	}

	/**
	 * @see org.seasar.dao.DtoMetaData#getPropertyType(int)
	 */
	public PropertyType getPropertyType(int index) {
		return (PropertyType) propertyTypes_.get(index);
	}

	/**
	 * @see org.seasar.dao.DtoMetaData#getPropertyType(java.lang.String)
	 */
	public PropertyType getPropertyType(String propertyName)
			throws PropertyNotFoundRuntimeException {

		PropertyType propertyType = (PropertyType) propertyTypes_
				.get(propertyName);
		if (propertyType == null) {
			throw new PropertyNotFoundRuntimeException(beanClass_, propertyName);
		}
		return propertyType;
	}

	/**
	 * @see org.seasar.dao.DtoMetaData#hasPropertyType(java.lang.String)
	 */
	public boolean hasPropertyType(String propertyName) {
		return propertyTypes_.get(propertyName) != null;
	}

	protected void setupPropertyType(BeanDesc beanDesc) {
		for (int i = 0; i < beanDesc.getPropertyDescSize(); ++i) {
			PropertyDesc pd = beanDesc.getPropertyDesc(i);
			PropertyType pt = createPropertyType(beanDesc, pd);
			addPropertyType(pt);
		}
	}

	protected PropertyType createPropertyType(BeanDesc beanDesc,
			PropertyDesc propertyDesc) {

		String columnName = propertyDesc.getPropertyName();
		String ca = beanAnnotationReader_.getColumnAnnotation(propertyDesc);
		if(ca != null){
			columnName = ca;
		}
		ValueType valueType = ValueTypes.getValueType(propertyDesc
				.getPropertyType());
		PropertyType pt = new PropertyTypeImpl(propertyDesc, valueType,
				columnName);
		return pt;
	}

	protected void addPropertyType(PropertyType propertyType) {
		propertyTypes_.put(propertyType.getPropertyName(), propertyType);
	}
}