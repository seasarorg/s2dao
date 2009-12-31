/*
 * Copyright 2004-2010 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.dao.impl;

import junit.framework.TestCase;

import org.seasar.dao.BeanAnnotationReader;
import org.seasar.dao.DaoAnnotationReader;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.factory.BeanDescFactory;

/**
 * @author manhole
 */
public class AnnotationReaderFactoryImplTest extends TestCase {

    private FieldAnnotationReaderFactory fieldAnnotationReaderFactory;

    private AnnotationReaderFactoryImpl annotationReaderFactory;

    protected void setUp() throws Exception {
        super.setUp();
        fieldAnnotationReaderFactory = new FieldAnnotationReaderFactory();
        annotationReaderFactory = new AnnotationReaderFactoryImpl();
    }

    public void testBeanAnnotationReader() throws Exception {
        // ## Arrange ##
        final Class beanClass = Employee.class;

        // ## Act ##
        final BeanAnnotationReader beanAnnotationReader = annotationReaderFactory
                .createBeanAnnotationReader(beanClass);

        // ## Assert ##
        assertEquals(fieldAnnotationReaderFactory.createBeanAnnotationReader(
                beanClass).getClass(), beanAnnotationReader.getClass());
    }

    public void testDaoAnnotationReader() throws Exception {
        // ## Arrange ##
        final BeanDesc daoBeanDesc = BeanDescFactory
                .getBeanDesc(EmployeeDao.class);

        // ## Act ##
        final DaoAnnotationReader daoAnnotationReader = annotationReaderFactory
                .createDaoAnnotationReader(daoBeanDesc);

        // ## Assert ##
        assertEquals(fieldAnnotationReaderFactory.createDaoAnnotationReader(
                daoBeanDesc).getClass(), daoAnnotationReader.getClass());
    }

}
