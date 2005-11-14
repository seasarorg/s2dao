package org.seasar.dao.impl;

import org.seasar.dao.AnnotationReaderFactory;
import org.seasar.dao.BeanAnnotationReader;
import org.seasar.dao.DaoAnnotationReader;
import org.seasar.framework.beans.BeanDesc;

public class FieldAnnotationReaderFactory implements AnnotationReaderFactory {

	public DaoAnnotationReader createDaoAnnotationReader(BeanDesc daoBeanDesc) {
		return new FieldDaoAnnotationReader(daoBeanDesc);
	}
	public BeanAnnotationReader createBeanAnnotationReader(Class beanClass_) {
		return new FieldBeanAnnotationReader(beanClass_);
	}
}
