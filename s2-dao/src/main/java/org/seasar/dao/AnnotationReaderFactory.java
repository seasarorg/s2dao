package org.seasar.dao;

import org.seasar.framework.beans.BeanDesc;

public interface AnnotationReaderFactory {
	public DaoAnnotationReader createDaoAnnotationReader(BeanDesc daoBeanDesc);

	public BeanAnnotationReader createBeanAnnotationReader(Class beanClass_);

}
