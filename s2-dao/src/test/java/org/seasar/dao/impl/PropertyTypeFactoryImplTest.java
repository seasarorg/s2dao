/*
 * Copyright 2004-2007 the Seasar Foundation and the Others.
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

import java.sql.DatabaseMetaData;

import org.seasar.dao.BeanAnnotationReader;
import org.seasar.dao.Dbms;
import org.seasar.dao.PropertyTypeFactory;
import org.seasar.dao.PropertyTypeFactoryBuilder;
import org.seasar.dao.dbms.DbmsManager;
import org.seasar.extension.jdbc.PropertyType;
import org.seasar.extension.jdbc.types.ValueTypes;
import org.seasar.extension.unit.S2TestCase;

/**
 * @author taedium
 *
 */
public class PropertyTypeFactoryImplTest extends S2TestCase {

    private Class beanClass = Employee20.class;

    private PropertyTypeFactoryBuilder builder;

    private boolean empnoInvoked;

    private boolean managerInvoked;

    private boolean deptnoInvoked;

    private boolean dummyInvoked;

    protected void setUp() throws Exception {
        super.setUp();
        include(getClass().getName().replace('.', '/') + ".dicon");
    }

    public void testDto() throws Exception {
        PropertyTypeFactory factory = createDtoPropertyTypeFactory();
        PropertyType[] propertyTypes = factory.createDtoPropertyTypes();
        assertNotNull(propertyTypes);
        assertEquals(5, propertyTypes.length);
    }

    public void testBean() throws Exception {
        PropertyTypeFactory factory = createBeanPropertyTypeFactory();
        PropertyType[] propertyTypes = factory.createBeanPropertyTypes("EMP");
        assertNotNull(propertyTypes);
        assertEquals(4, propertyTypes.length);
        for (int i = 0; i < propertyTypes.length; i++) {
            PropertyType pt = propertyTypes[i];
            if (pt.getPropertyName().equals("empno")) {
                empno(pt);
            } else if (pt.getPropertyName().equals("manager")) {
                manager(pt);
            } else if (pt.getPropertyName().equals("deptno")) {
                deptno(pt);
            } else if (pt.getPropertyName().equals("dummy")) {
                dummy(pt);
            } else {
                fail();
            }
        }
        assertTrue(empnoInvoked);
        assertTrue(managerInvoked);
        assertTrue(deptnoInvoked);
        assertTrue(dummyInvoked);
    }

    private void empno(PropertyType pt) throws Exception {
        assertEquals("empno", pt.getColumnName());
        assertTrue(pt.isPrimaryKey());
        assertTrue(pt.isPersistent());
        assertEquals(ValueTypes.LONG, pt.getValueType());
        empnoInvoked = true;
    }

    private void manager(PropertyType pt) throws Exception {
        assertEquals("mgr", pt.getColumnName());
        assertFalse(pt.isPrimaryKey());
        assertTrue(pt.isPersistent());
        assertEquals(ValueTypes.SHORT, pt.getValueType());
        managerInvoked = true;
    }

    private void deptno(PropertyType pt) throws Exception {
        assertEquals("deptno", pt.getColumnName());
        assertFalse(pt.isPrimaryKey());
        assertTrue(pt.isPersistent());
        assertEquals(ValueTypes.INTEGER, pt.getValueType());
        deptnoInvoked = true;
    }

    private void dummy(PropertyType pt) throws Exception {
        assertEquals("dummy", pt.getColumnName());
        assertFalse(pt.isPrimaryKey());
        assertFalse(pt.isPersistent());
        assertEquals(ValueTypes.STRING, pt.getValueType());
        dummyInvoked = true;
    }

    private PropertyTypeFactory createDtoPropertyTypeFactory() {
        BeanAnnotationReader beanAnnotationReader = new FieldBeanAnnotationReader(
                beanClass);
        return builder.build(beanClass, beanAnnotationReader);
    }

    private PropertyTypeFactory createBeanPropertyTypeFactory() {
        BeanAnnotationReader beanAnnotationReader = new FieldBeanAnnotationReader(
                beanClass);
        DatabaseMetaData databaseMetaData = getDatabaseMetaData();
        Dbms dbms = DbmsManager.getDbms(databaseMetaData);
        return builder.build(beanClass, beanAnnotationReader, dbms,
                databaseMetaData);
    }
}
