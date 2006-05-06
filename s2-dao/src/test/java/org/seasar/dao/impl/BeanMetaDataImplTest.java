/*
 * Copyright 2004-2006 the Seasar Foundation and the Others.
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

import org.seasar.dao.AnnotationReaderFactory;
import org.seasar.dao.BeanMetaData;
import org.seasar.dao.RelationPropertyType;
import org.seasar.dao.unit.S2DaoTestCase;
import org.seasar.extension.jdbc.PropertyType;

/**
 * @author higa
 * 
 */
public abstract class BeanMetaDataImplTest extends S2DaoTestCase {

    protected AnnotationReaderFactory readerFactory;

    protected void setUp() throws Exception {
        include("j2ee.dicon");
    }

    protected void setUpAfterBindFields() throws Throwable {
        super.setUpAfterBindFields();
        setAnnotationReaderFactory(readerFactory);
    }

    protected abstract Class getBeanClass(String className);

    public void testSetup() throws Exception {
        BeanMetaData bmd = createBeanMetaData(getBeanClass("MyBean"));
        assertEquals("1", "MyBean", bmd.getTableName());
        assertEquals("2", 3, bmd.getPropertyTypeSize());
        PropertyType aaa = bmd.getPropertyType("aaa");
        assertEquals("3", "aaa", aaa.getColumnName());
        PropertyType bbb = bmd.getPropertyType("bbb");
        assertEquals("4", "myBbb", bbb.getColumnName());
        assertEquals("5", 1, bmd.getRelationPropertyTypeSize());
        RelationPropertyType rpt = bmd.getRelationPropertyType(0);
        assertEquals("6", 1, rpt.getKeySize());
        assertEquals("7", "ddd", rpt.getMyKey(0));
        assertEquals("8", "id", rpt.getYourKey(0));
        assertNotNull("9", bmd.getIdentifierGenerator());
        assertEquals("10", 1, bmd.getPrimaryKeySize());
        assertEquals("11", "aaa", bmd.getPrimaryKey(0));
    }

    public void testSetupDatabaseMetaData() throws Exception {
        BeanMetaData bmd = createBeanMetaData(getBeanClass("Employee"));
        PropertyType empno = bmd.getPropertyType("empno");
        assertEquals("1", true, empno.isPrimaryKey());
        assertEquals("2", true, empno.isPersistent());
        PropertyType ename = bmd.getPropertyType("ename");
        assertEquals("3", false, ename.isPrimaryKey());
        PropertyType dummy = bmd.getPropertyType("dummy");
        assertEquals("4", false, dummy.isPersistent());
    }

    public void testSetupAutoSelectList() throws Exception {
        BeanMetaData bmd = createBeanMetaData(getBeanClass("Department"));
        BeanMetaData bmd2 = createBeanMetaData(getBeanClass("Employee"));
        String sql = bmd.getAutoSelectList();
        String sql2 = bmd2.getAutoSelectList();
        System.out.println(sql);
        System.out.println(sql2);

        assertTrue("1", sql2.indexOf("EMP.deptno") > 0);
        assertTrue("2", sql2.indexOf("department.deptno AS deptno_0") > 0);
        assertTrue("3", sql2.indexOf("dummy_0") < 0);
    }

    public void testConvertFullColumnName() throws Exception {
        BeanMetaData bmd = createBeanMetaData(getBeanClass("Employee"));
        assertEquals("1", "EMP.empno", bmd.convertFullColumnName("empno"));
        assertEquals("2", "department.dname", bmd
                .convertFullColumnName("dname_0"));
    }

    public void testHasPropertyTypeByAliasName() throws Exception {
        BeanMetaData bmd = createBeanMetaData(getBeanClass("Employee"));
        assertEquals("1", true, bmd.hasPropertyTypeByAliasName("empno"));
        assertEquals("2", true, bmd.hasPropertyTypeByAliasName("dname_0"));
        assertEquals("3", false, bmd.hasPropertyTypeByAliasName("xxx"));
        assertEquals("4", false, bmd.hasPropertyTypeByAliasName("xxx_10"));
        assertEquals("5", false, bmd.hasPropertyTypeByAliasName("xxx_0"));
    }

    public void testGetPropertyTypeByAliasName() throws Exception {
        BeanMetaData bmd = createBeanMetaData(getBeanClass("Employee"));
        assertNotNull("1", bmd.getPropertyTypeByAliasName("empno"));
        assertNotNull("2", bmd.getPropertyTypeByAliasName("dname_0"));
    }

    public void testSelfReference() throws Exception {
        BeanMetaData bmd = createBeanMetaData(getBeanClass("Employee4"));
        RelationPropertyType rpt = bmd.getRelationPropertyType("parent");
        assertEquals("1", getBeanClass("Employee4"), rpt.getBeanMetaData()
                .getBeanClass());
    }

    public void testNoPersistentPropsEmpty() throws Exception {
        BeanMetaData bmd = createBeanMetaData(getBeanClass("Ddd"));
        PropertyType pt = bmd.getPropertyType("name");
        assertEquals("1", false, pt.isPersistent());
    }

    public void testNoPersistentPropsDefined() throws Exception {
        BeanMetaData bmd = createBeanMetaData(getBeanClass("Eee"));
        PropertyType pt = bmd.getPropertyType("name");
        assertEquals("1", false, pt.isPersistent());
    }

    public void testPrimaryKeyForIdentifier() throws Exception {
        BeanMetaData bmd = createBeanMetaData(getBeanClass("IdentityTable"));
        assertEquals("1", "id", bmd.getPrimaryKey(0));
    }

    public void testGetVersionNoPropertyName() throws Exception {
        BeanMetaData bmd = createBeanMetaData(getBeanClass("Fff"));
        assertEquals("1", "version", bmd.getVersionNoPropertyName());
    }

    public void testGetTimestampPropertyName() throws Exception {
        BeanMetaData bmd = createBeanMetaData(getBeanClass("Fff"));
        assertEquals("1", "updated", bmd.getTimestampPropertyName());
    }

}
