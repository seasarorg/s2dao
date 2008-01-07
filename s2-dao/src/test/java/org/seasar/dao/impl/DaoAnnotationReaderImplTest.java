/*
 * Copyright 2004-2008 the Seasar Foundation and the Others.
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

import java.lang.reflect.Method;

import org.seasar.dao.AnnotationReaderFactory;
import org.seasar.dao.DaoAnnotationReader;
import org.seasar.dao.DaoMetaDataFactory;
import org.seasar.dao.NullBean;
import org.seasar.extension.unit.S2TestCase;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.factory.BeanDescFactory;

/**
 * @author higa
 * 
 */
public class DaoAnnotationReaderImplTest extends S2TestCase {
    protected AnnotationReaderFactory readerFactory;

    /**
     * Constructor for InvocationImplTest.
     * 
     * @param arg0
     */
    public DaoAnnotationReaderImplTest(String arg0) {
        super(arg0);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(DaoAnnotationReaderImplTest.class);
    }

    public void setUp() {
        include("FieldDaoMetaDataImplTest.dicon");
    }

    protected Class getDaoClass(String className) {
        if (className.equals("AnnotationTestDaoImpl")) {
            return AnnotationTestDaoImpl.class;
        } else if (className.equals("DummyDao")) {
            return DummyDao.class;
        }
        throw new RuntimeException("unkown dao class " + className);
    }

    public void testGetBean() {
        BeanDesc beanDesc1 = BeanDescFactory
                .getBeanDesc(getDaoClass("AnnotationTestDaoImpl"));
        DaoAnnotationReader reader1 = readerFactory
                .createDaoAnnotationReader(beanDesc1);
        assertEquals(Employee.class, reader1.getBeanClass());
    }

    public void testGetNullBean() {
        BeanDesc beanDesc = BeanDescFactory
                .getBeanDesc(getDaoClass("DummyDao"));
        DaoAnnotationReader reader = readerFactory
                .createDaoAnnotationReader(beanDesc);
        assertEquals(NullBean.class, reader.getBeanClass());
    }

    public void testGetArgNames() throws Exception {
        BeanDesc beanDesc = BeanDescFactory
                .getBeanDesc(getDaoClass("AnnotationTestDaoImpl"));
        DaoAnnotationReader reader = readerFactory
                .createDaoAnnotationReader(beanDesc);
        assertEquals("1", Employee.class, reader.getBeanClass());
        Method method = beanDesc.getMethods("withArgumentAnnotaion")[0];
        String[] names = reader.getArgNames(method);
        assertEquals("2", 2, names.length);
        assertEquals("2", "arg1", names[0]);
        assertEquals("2", "arg2", names[1]);
        // getArgNames return 0 length array if args annotation is not
        // specified.
        Method method2 = beanDesc.getMethods("withNoAnnotaion")[0];
        String[] names2 = reader.getArgNames(method2);
        assertEquals("3", 0, names2.length);
        // annotationReader must read subclass annotation
        Method method3 = beanDesc.getMethods("subclassMethod")[0];
        String[] names3 = reader.getArgNames(method3);
        assertEquals("3", 1, names3.length);
    }

    public void testGetQuery() throws Exception {
        BeanDesc beanDesc = BeanDescFactory
                .getBeanDesc(getDaoClass("AnnotationTestDaoImpl"));
        DaoAnnotationReader reader = readerFactory
                .createDaoAnnotationReader(beanDesc);
        Method method1 = beanDesc.getMethods("withQueryAnnotaion")[0];
        String queryq = reader.getQuery(method1);
        assertEquals("1", "arg1 = /*arg1*/'dummy'", queryq);
        // return null if QUERY annotation not found
        Method method2 = beanDesc.getMethods("withNoAnnotaion")[0];
        String query2 = reader.getQuery(method2);
        assertNull("1", query2);
        // annotationReader must read subclass annotation
        Method method3 = beanDesc.getMethods("subclassMethod")[0];
        String[] names3 = reader.getArgNames(method3);
        assertEquals("3", 1, names3.length);

    }

    public void testGetPersistentProps() throws Exception {
        BeanDesc beanDesc = BeanDescFactory
                .getBeanDesc(getDaoClass("AnnotationTestDaoImpl"));
        DaoAnnotationReader reader = readerFactory
                .createDaoAnnotationReader(beanDesc);
        Method method1 = beanDesc.getMethods("withPersistentProps")[0];
        String[] props1 = reader.getPersistentProps(method1);
        assertEquals("1", 2, props1.length);
        assertEquals("1", "prop1", props1[0]);
        assertEquals("1", "prop2", props1[1]);
        // return null if QUERY annotation not found
        Method method2 = beanDesc.getMethods("withNoAnnotaion")[0];
        String[] props2 = reader.getPersistentProps(method2);
        assertNull("2", props2);
        // annotationReader must read subclass annotation
        Method method3 = beanDesc.getMethods("subclassMethod")[0];
        String[] props3 = reader.getPersistentProps(method3);
        assertEquals("1", 2, props3.length);
        assertEquals("1", "prop1", props3[0]);
        assertEquals("1", "prop2", props3[1]);

    }

    public void testGetNoPersistentProps() throws Exception {
        BeanDesc beanDesc = BeanDescFactory
                .getBeanDesc(getDaoClass("AnnotationTestDaoImpl"));
        DaoAnnotationReader reader = readerFactory
                .createDaoAnnotationReader(beanDesc);
        Method method1 = beanDesc.getMethods("withNoPersistentProps")[0];
        String[] props1 = reader.getNoPersistentProps(method1);
        assertEquals("1", 2, props1.length);
        assertEquals("1", "prop1", props1[0]);
        assertEquals("1", "prop2", props1[1]);
        // return null if QUERY annotation not found
        Method method2 = beanDesc.getMethods("withNoAnnotaion")[0];
        String[] props2 = reader.getNoPersistentProps(method2);
        assertNull("2", props2);
        // annotationReader must read subclass annotation
        Method method3 = beanDesc.getMethods("subclassMethod2")[0];
        String[] props3 = reader.getNoPersistentProps(method3);
        assertEquals("1", 2, props3.length);
        assertEquals("1", "prop1", props3[0]);
        assertEquals("1", "prop2", props3[1]);
    }

    public void testGetSql() throws Exception {
        BeanDesc beanDesc = BeanDescFactory
                .getBeanDesc(getDaoClass("AnnotationTestDaoImpl"));
        DaoAnnotationReader reader = readerFactory
                .createDaoAnnotationReader(beanDesc);
        Method method1 = beanDesc.getMethods("subclassMethod2")[0];
        String sql = reader.getSQL(method1, "mysql");
        assertEquals("1", "SELECT * FROM emp", sql);
    }

    public static interface AnnotationTestDao {

        public Class BEAN = Employee.class;

        public String withArgumentAnnotaion_ARGS = "arg1 , arg2 ";

        public Employee withArgumentAnnotaion(int arg1, String arg2);

        public String withQueryAnnotaion_QUERY = "arg1 = /*arg1*/'dummy'";

        public Employee withQueryAnnotaion(int arg1);

        public String withPersistentProps_PERSISTENT_PROPS = "prop1 , prop2";

        public Employee withPersistentProps(int arg1);

        public String withNoPersistentProps_NO_PERSISTENT_PROPS = "prop1 , prop2";

        public Employee withNoPersistentProps(int arg1);

        public String withSQLAnnotaion_mysql_SQL = "SELECT * FROM emp1";

        public String withSQLAnnotaion_SQL = "SELECT * FROM emp2";

        public Employee withSQLAnnotaion();

        public Employee withNoAnnotaion(int arg1);
    }

    public static interface DummyDao {

    }

    public static abstract class AnnotationTestDaoImpl extends AbstractDao
            implements AnnotationTestDao {

        public AnnotationTestDaoImpl(DaoMetaDataFactory factory) {
            super(factory);
        }

        public Employee[] getEmployeesByDeptno(int deptno) {
            return (Employee[]) getEntityManager().findArray("deptno = ?",
                    new Integer(deptno));
        }

        public static String subclassMethod_ARGS = "arg1";

        public static String subclassMethod_QUERY = "arg1 = /*arg1*/'dummy'";

        public static String subclassMethod_PERSISTENT_PROPS = "prop1 , prop2";

        public abstract Employee subclassMethod(String arg1);

        public static String subclassMethod2_NO_PERSISTENT_PROPS = "prop1 , prop2";

        public static String subclassMethod2_SQL = "SELECT * FROM emp";

        public abstract Employee subclassMethod2(String arg1);

    }

}
