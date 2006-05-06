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
package org.seasar.dao.dbms;

import org.seasar.dao.BeanMetaData;
import org.seasar.dao.Dbms;
import org.seasar.dao.impl.BeanMetaDataImpl;
import org.seasar.dao.impl.FieldAnnotationReaderFactory;
import org.seasar.dao.impl.ValueTypeFactoryImpl;
import org.seasar.dao.unit.S2DaoTestCase;

/**
 * @author higa
 *  
 */
public class OracleTest extends S2DaoTestCase {

    /**
     * Constructor for InvocationImplTest.
     * 
     * @param arg0
     */
    public OracleTest(String arg0) {
        super(arg0);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(OracleTest.class);
    }

    protected void setUp() throws Exception {
        include("j2ee.dicon");
    }

    protected void tearDown() throws Exception {
    }

    public void testCreateAutoSelectList() throws Exception {
        Dbms dbms = new Oracle();
        BeanMetaData bmd = createBeanMetaData(Employee.class, dbms);
        String sql = dbms.getAutoSelectSql(bmd);
        System.out.println(sql);
    }

    public void testCreateAutoSelectList2() throws Exception {
        Dbms dbms = new Oracle();
        BeanMetaData bmd = createBeanMetaData(Department.class, dbms);
        String sql = dbms.getAutoSelectSql(bmd);
        System.out.println(sql);
        assertTrue("1", sql.endsWith("FROM DEPT"));
    }

    private BeanMetaData createBeanMetaData(Class beanClass, Dbms dbms) {
        BeanMetaDataImpl beanMetaData = new BeanMetaDataImpl();
        beanMetaData.setBeanClass(beanClass);
        beanMetaData.setDatabaseMetaData(getDatabaseMetaData());
        beanMetaData.setDbms(dbms);
        beanMetaData.setAnnotationReaderFactory(getAnnotationReaderFactory());
        beanMetaData.setValueTypeFactory(getValueTypeFactory());
        beanMetaData.initialize();
        return beanMetaData;
    }

}