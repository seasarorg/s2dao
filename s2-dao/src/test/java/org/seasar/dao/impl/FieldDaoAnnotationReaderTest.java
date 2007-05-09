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

import java.lang.reflect.Method;
import java.util.List;

import junit.framework.TestCase;

import org.seasar.dao.DaoMetaDataFactory;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.factory.BeanDescFactory;

/**
 * @author manhole
 */
public class FieldDaoAnnotationReaderTest extends TestCase {

    private FieldDaoAnnotationReader annotationReader;

    public void testBasic() throws Exception {
        final BeanDesc daoDesc = BeanDescFactory.getBeanDesc(AaaDao.class);
        FieldDaoAnnotationReader reader = new FieldDaoAnnotationReader(daoDesc);
        assertEquals(Aaa.class, reader.getBeanClass());

        String query = reader.getQuery(AaaDao.class.getMethod("getAaaById2",
                new Class[] { int.class }));
        assertEquals("A > B", query);
    }

    protected void setUp() throws Exception {
        super.setUp();
        BeanDesc daoDesc = BeanDescFactory
                .getBeanDesc(AbstractAaaDaoImpl.class);
        annotationReader = new FieldDaoAnnotationReader(daoDesc);
    }

    public void testBeanClass() throws Exception {
        assertEquals(Aaa.class, annotationReader.getBeanClass());
    }

    public void testBeanClassByMethod() throws Exception {
        Method method = AaaDao.class.getMethod("findAll", null);
        assertEquals(Aaa.class, annotationReader.getBeanClass(method));

        method = AaaDao.class.getMethod("findArray", null);
        assertEquals(Aaa.class, annotationReader.getBeanClass(method));

        method = AaaDao.class.getMethod("find", new Class[] { int.class });
        assertEquals(Aaa.class, annotationReader.getBeanClass(method));
    }

    public void testIsSingleValueType() throws Exception {
        assertTrue(FieldDaoAnnotationReader.isSingleValueType(void.class));
    }

    public void testQuery() throws Exception {
        String query = annotationReader.getQuery(AbstractAaaDaoImpl.class
                .getMethod("getAaaById2", new Class[] { int.class }));
        assertEquals("A > B", query);
    }

    public void testSql() throws Exception {
        String sql = annotationReader.getSQL(AbstractAaaDaoImpl.class
                .getMethod("getAaaById3", new Class[] { int.class }), null);
        assertEquals("SELECT * FROM AAA", sql);
    }

    public void testArgNames() throws Exception {
        String[] argNames = annotationReader
                .getArgNames(AbstractAaaDaoImpl.class.getMethod("getAaaById1",
                        new Class[] { int.class }));
        assertEquals(2, argNames.length);
        assertEquals("aaa1", argNames[0]);
        assertEquals("aaa2", argNames[1]);
    }

    public void testNoPersistentProps() throws Exception {
        final String[] noPersistentProps = annotationReader
                .getNoPersistentProps(AbstractAaaDaoImpl.class.getMethod(
                        "createAaa1", new Class[] { Aaa.class }));
        assertEquals(1, noPersistentProps.length);
        assertEquals("abc", noPersistentProps[0]);
    }

    public void testPersistentProps() throws Exception {
        final String[] persistentProps = annotationReader
                .getPersistentProps(AbstractAaaDaoImpl.class.getMethod(
                        "createAaa2", new Class[] { Aaa.class }));
        assertEquals(1, persistentProps.length);
        assertEquals("def", persistentProps[0]);
    }

    public static interface AaaDao {

        public Class BEAN = Aaa.class;

        public Class findAll_BEAN = Aaa.class;

        public String getAaaById1_ARGS = "aaa1, aaa2";

        public Aaa getAaaById1(int id);

        public String getAaaById2_QUERY = "A > B";

        public Aaa getAaaById2(int id);

        public String getAaaById3_SQL = "SELECT * FROM AAA";

        public Aaa getAaaById3(int id);

        public List findAll();

        public Aaa[] findArray();

        public Aaa find(int id);

        public String createAaa1_NO_PERSISTENT_PROPS = "abc";

        public Aaa createAaa1(Aaa aaa);

        public String createAaa2_PERSISTENT_PROPS = "def";

        public Aaa createAaa2(Aaa aaa);

    }

    public static interface Aaa2Dao extends AaaDao {
    }

    public static abstract class AbstractAaaDaoImpl extends AbstractDao
            implements Aaa2Dao {

        public AbstractAaaDaoImpl(DaoMetaDataFactory daoMetaDataFactory) {
            super(daoMetaDataFactory);
        }

    }

    public static class Aaa {
    }

}
