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

import org.seasar.dao.DaoMetaData;
import org.seasar.dao.NoUpdatePropertyTypeRuntimeException;
import org.seasar.dao.SqlCommand;
import org.seasar.dao.unit.S2DaoTestCase;

/**
 * @author li1770
 * 
 */
public class UpdateAutoDynamicCommandTest extends S2DaoTestCase {

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        include("j2ee.dicon");
    }

    public void testExecuteTx() throws Exception {
        DaoMetaData dmd = createDaoMetaData(EmployeeAutoDao.class);
        SqlCommand select = dmd.getSqlCommand("getEmployee");
        Employee before = (Employee) select.execute(new Object[] { new Integer(
                7369) });

        Employee e = new Employee();
        e.setEmpno(7369);
        e.setDeptno(20);
        e.setEname("HOGE");
        e.setHiredate(null);
        e.setMgr(null);
        e.setTimestamp(before.getTimestamp());
        SqlCommand unlessNull = dmd.getSqlCommand("updateUnlessNull");
        assertTrue(unlessNull instanceof UpdateAutoDynamicCommand);
        unlessNull.execute(new Object[] { e });

        Employee after = (Employee) select.execute(new Object[] { new Integer(
                7369) });
        assertEquals(e.getEname(), after.getEname());
        assertEquals(before.getHiredate(), after.getHiredate());
        assertEquals(before.getMgr(), after.getMgr());
    }

    /*
     * https://www.seasar.org/issues/browse/DAO-39
     */
    public void testExecuteOnePropertyTx() throws Exception {
        DaoMetaData dmd = createDaoMetaData(EmployeeAutoDao.class);
        SqlCommand select = dmd.getSqlCommand("getEmployee");
        Employee before = (Employee) select.execute(new Object[] { new Integer(
                7369) });

        Employee e = new Employee();
        e.setEmpno(7369);
        e.setDeptno(20);
        SqlCommand unlessNull = dmd.getSqlCommand("updateUnlessNull");
        assertTrue(unlessNull instanceof UpdateAutoDynamicCommand);
        unlessNull.execute(new Object[] { e });

        Employee after = (Employee) select.execute(new Object[] { new Integer(
                7369) });
        assertEquals(before.getHiredate(), after.getHiredate());
        assertEquals(before.getMgr(), after.getMgr());
    }

    /*
     * https://www.seasar.org/issues/browse/DAO-39
     */
    public void testExecuteAllNullTx() throws Exception {
        DaoMetaData dmd = createDaoMetaData(CharTableDao.class);
        SqlCommand unlessNull = dmd.getSqlCommand("updateUnlessNull");
        CharTable data = new CharTable();
        data.setId(1);
        try {
            unlessNull.execute(new Object[] { data });
            fail();
        } catch (NoUpdatePropertyTypeRuntimeException e) {
            assertTrue(true);
        }
    }

    public class CharTable {
        public static final String TABLE = "CHAR_TABLE";

        int id = 0;

        Character aaa = null;

        public Character getAaa() {
            return aaa;
        }

        public void setAaa(Character aaa) {
            this.aaa = aaa;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

    public interface CharTableDao {
        Class BEAN = CharTable.class;

        int updateUnlessNull(CharTable data);
    }
}
