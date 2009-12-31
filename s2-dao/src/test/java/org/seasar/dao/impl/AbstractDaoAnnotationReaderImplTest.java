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

import java.lang.reflect.Method;

import junit.framework.TestCase;

import org.seasar.dao.DaoAnnotationReader;
import org.seasar.dao.dbms.HSQL;
import org.seasar.dao.dbms.Oracle;

/**
 * @author manhole
 * @author azusa
 */
public abstract class AbstractDaoAnnotationReaderImplTest extends TestCase {

    protected DaoAnnotationReader annotationReader;

    protected Class clazz;

    protected Class aaaClazz;

    protected Class daoClazz;

    public void testBasic() throws Exception {
        assertEquals(aaaClazz, annotationReader.getBeanClass());

        String query = annotationReader.getQuery(daoClazz.getMethod(
                "getAaaById2", new Class[] { int.class }));
        assertEquals("A > B", query);
    }

    public void testBeanClassByMethod() throws Exception {
        Method method = daoClazz.getMethod("findAll", null);
        assertEquals(aaaClazz, annotationReader.getBeanClass(method));

        method = daoClazz.getMethod("findArray", null);
        assertEquals(aaaClazz, annotationReader.getBeanClass(method));

        method = daoClazz.getMethod("findSimpleTypeArray", null);
        assertEquals(int.class, annotationReader.getBeanClass(method));

        method = daoClazz.getMethod("find", new Class[] { int.class });
        assertEquals(aaaClazz, annotationReader.getBeanClass(method));
    }

    public void testBeanClass() throws Exception {
        assertEquals(aaaClazz, annotationReader.getBeanClass());
    }

    public void testQuery() throws Exception {
        String query = annotationReader.getQuery(clazz.getMethod("getAaaById2",
                new Class[] { int.class }));
        assertEquals("A > B", query);
    }

    public void testSql() throws Exception {
        String sql = annotationReader.getSQL(clazz.getMethod("getAaaById3",
                new Class[] { int.class }), null);
        assertEquals("SELECT * FROM AAA", sql);
    }

    public void testSql2() throws Exception {
        String sql = annotationReader.getSQL(clazz.getMethod("selectB",
                new Class[] { int.class }), new Oracle().getSuffix());
        assertEquals("1", "SELECT * FROM BBB", sql);
        sql = annotationReader.getSQL(clazz.getMethod("selectB",
                new Class[] { int.class }), new HSQL().getSuffix());
        assertEquals("2", "SELECT * FROM DDD", sql);
        sql = annotationReader.getSQL(clazz.getMethod("selectC",
                new Class[] { int.class }), new Oracle().getSuffix());
        assertEquals("3", "SELECT * FROM CCC", sql);
        sql = annotationReader.getSQL(clazz.getMethod("selectC",
                new Class[] { int.class }), new HSQL().getSuffix());
        assertNull("4", sql);

    }

    public void testArgNames() throws Exception {
        String[] argNames = annotationReader.getArgNames(clazz.getMethod(
                "getAaaById1", new Class[] { int.class }));
        assertEquals(2, argNames.length);
        assertEquals("aaa1", argNames[0]);
        assertEquals("aaa2", argNames[1]);
    }

    public void testNoPersistentProps() throws Exception {
        final String[] noPersistentProps = annotationReader
                .getNoPersistentProps(clazz.getMethod("createAaa1",
                        new Class[] { aaaClazz }));
        assertEquals(1, noPersistentProps.length);
        assertEquals("abc", noPersistentProps[0]);
    }

    public void testPersistentProps() throws Exception {
        final String[] persistentProps = annotationReader
                .getPersistentProps(clazz.getMethod("createAaa2",
                        new Class[] { aaaClazz }));
        assertEquals(1, persistentProps.length);
        assertEquals("def", persistentProps[0]);
    }

    public void testSqlFile() throws Exception {
        final Method method = daoClazz.getMethod("findUsingSqlFile",
                new Class[] { int.class });
        final boolean isSqlFile = annotationReader.isSqlFile(method);
        assertEquals(true, isSqlFile);
    }

    public void testNoSqlFile() throws Exception {
        final Method method = daoClazz.getMethod("getAaaById1",
                new Class[] { int.class });
        final boolean isSqlFile = annotationReader.isSqlFile(method);
        assertEquals(false, isSqlFile);
    }

    public void testSqlFileWithPath() throws Exception {
        final Method method = daoClazz.getMethod("findUsingSqlFile2",
                new Class[] { int.class });
        final String sqlFile = annotationReader.getSqlFilePath(method);
        assertEquals("org/seasar/dao/impl/sqlfile/testFile.sql", sqlFile);
    }

    public void testSqlFileWithoutPath() throws Exception {
        {
            final Method method = daoClazz.getMethod("findUsingSqlFile",
                    new Class[] { int.class });
            final String sqlFile = annotationReader.getSqlFilePath(method);
            assertEquals("1", "", sqlFile);
        }
        {
            final Method method = daoClazz.getMethod("getAaaById1",
                    new Class[] { int.class });
            final String sqlFile = annotationReader.getSqlFilePath(method);
            assertEquals("2", "", sqlFile);
        }
    }

    public void testProcedureCall() throws Exception {
        final Method method = daoClazz.getMethod("execute", new Class[] {});
        final String name = annotationReader.getProcedureCallName(method);
        assertEquals("hoge", name);
    }

    public void testNoCheckSingleRowUpdateOnMethod() throws Exception {
        final Method method = daoClazz.getMethod("createAaa3",
                new Class[] { aaaClazz });
        boolean resultCheck = annotationReader.isCheckSingleRowUpdate(method);
        assertFalse(resultCheck);
    }

    public void testCheckSingleRowUpdateOnMethod() throws Exception {
        final Method method = daoClazz.getMethod("createAaa2",
                new Class[] { aaaClazz });
        boolean resultCheck = annotationReader.isCheckSingleRowUpdate(method);
        assertTrue(resultCheck);
    }

    public void testNoCheckSingleRowUpdateOnDao() throws Exception {
        boolean resultCheck = annotationReader.isCheckSingleRowUpdate();
        assertFalse(resultCheck);
    }
}
