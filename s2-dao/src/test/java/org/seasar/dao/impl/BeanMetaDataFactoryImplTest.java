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

import org.seasar.dao.BeanMetaData;
import org.seasar.dao.BeanMetaDataFactory;
import org.seasar.dao.NullBean;
import org.seasar.dao.unit.S2DaoTestCase;

/**
 * @author jflute
 * @author manhole
 */
public class BeanMetaDataFactoryImplTest extends S2DaoTestCase {

    protected void setUp() throws Exception {
        super.setUp();
        include("dao.dicon");
    }

    public void test_Accessor() {
        // ## Arrange ##
        final String invokeMark = "test_Accessor()";
        final BeanMetaDataFactoryImpl beanMetaDataFactoryImpl = new BeanMetaDataFactoryImpl() {
            public String toString() {
                assertNotNull(this.annotationReaderFactory);
                return invokeMark;
            }
        };

        // ## Act ##
        beanMetaDataFactoryImpl
                .setAnnotationReaderFactory(getAnnotationReaderFactory());

        // ## Assert ##
        assertEquals(invokeMark, beanMetaDataFactoryImpl.toString());
    }

    public void test_createBeanMetaData_Tx() {
        final BeanMetaDataFactory bmdFactory = getBeanMetaDataFactory();
        final Class beanClass = Employee.class; // This should have a relation property.
        final BeanMetaData bmd = bmdFactory.createBeanMetaData(beanClass);
        assertNotNull(bmd);
        assertNotNull(bmd.getBeanClass());
        assertEquals(Employee.TABLE, bmd.getTableName());
        final int relationPropertyTypeSize = bmd.getRelationPropertyTypeSize();
        assertNotSame(new Integer(0), new Integer(relationPropertyTypeSize));
        for (int i = 0; i < relationPropertyTypeSize; i++) {
            assertNotNull(bmd.getRelationPropertyType(i));
        }
    }

    public void test_createBeanMetaData_NestLevelOne_Tx() {
        final BeanMetaDataFactory bmdFactory = getBeanMetaDataFactory();
        final Class beanClass = Employee.class;// This should have a relation property.
        final BeanMetaData bmd = bmdFactory.createBeanMetaData(beanClass, 1);
        assertNotNull(bmd);
        assertNotNull(bmd.getBeanClass());
        assertEquals(Employee.TABLE, bmd.getTableName());
        final int relationPropertyTypeSize = bmd.getRelationPropertyTypeSize();
        assertEquals(new Integer(0), new Integer(relationPropertyTypeSize));
    }

    public void test_newBeanMetaDataImpl() {
        // ## Arrange ##
        final String invokeMark = "test_newBeanMetaDataImpl()";
        final BeanMetaDataFactoryImpl beanMetaDataFactoryImpl = new BeanMetaDataFactoryImpl() {
            public String toString() {
                final BeanMetaDataImpl beanMetaDataImpl = super
                        .createBeanMetaDataImpl();
                assertNotNull(beanMetaDataImpl);
                return invokeMark;
            }
        };

        // ## Act & Assert ##
        assertEquals(invokeMark, beanMetaDataFactoryImpl.toString());
    }

    public void test_isRelationNestLevel() {
        // ## Arrange ##
        final String invokeMark = "test_isRelationNestLevel()";
        final BeanMetaDataFactoryImpl beanMetaDataFactoryImpl = new BeanMetaDataFactoryImpl() {
            public String toString() {
                {
                    final int relationNestLevel = 0;
                    final boolean isRelationNestLevel = super
                            .isLimitRelationNestLevel(relationNestLevel);
                    assertEquals((relationNestLevel == super
                            .getLimitRelationNestLevel()), isRelationNestLevel);
                }
                {
                    final int relationNestLevel = 1;
                    final boolean isRelationNestLevel = super
                            .isLimitRelationNestLevel(relationNestLevel);
                    assertEquals((relationNestLevel == super
                            .getLimitRelationNestLevel()), isRelationNestLevel);
                }
                {
                    final int relationNestLevel = 2;
                    final boolean isRelationNestLevel = super
                            .isLimitRelationNestLevel(relationNestLevel);
                    assertEquals((relationNestLevel == super
                            .getLimitRelationNestLevel()), isRelationNestLevel);
                }
                return invokeMark;
            }
        };

        // ## Act & Assert ##
        assertEquals(invokeMark, beanMetaDataFactoryImpl.toString());
    }

    public void test_getLimitRelationNestLevel() {
        // ## Arrange ##
        final String invokeMark = "test_getLimitRelationNestLevel()";
        final BeanMetaDataFactoryImpl beanMetaDataFactoryImpl = new BeanMetaDataFactoryImpl() {
            public String toString() {
                final int level = super.getLimitRelationNestLevel();
                assertEquals(1, level);
                return invokeMark;
            }
        };

        // ## Act & Assert ##
        assertEquals(invokeMark, beanMetaDataFactoryImpl.toString());
    }

    public void testCreateBeanMetaData_byNullClass() throws Exception {
        // ## Arrange ##
        final BeanMetaDataFactoryImpl factory = new BeanMetaDataFactoryImpl();
        try {
            // ## Act ##
            // ## Assert ##
            factory.createBeanMetaData((Class) null);
            fail();
        } catch (final NullPointerException e) {
            assertTrue((e.getMessage() != null && 0 < e.getMessage().length()));
        }
    }

    public void testCreateBeanMetaData_byNullBean() throws Exception {
        BeanMetaDataFactoryImpl factory = new BeanMetaDataFactoryImpl();
        BeanMetaData metaData = factory.createBeanMetaData(HogeDao.class,
                NullBean.class);
        assertEquals(NullBeanMetaData.class, metaData.getClass());
    }

    public static class HogeDao {
    }
}
