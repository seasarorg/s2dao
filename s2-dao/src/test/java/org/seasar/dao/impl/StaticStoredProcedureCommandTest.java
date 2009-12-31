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

import java.math.BigDecimal;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.seasar.dao.DaoMetaData;
import org.seasar.dao.SqlCommand;
import org.seasar.dao.unit.S2DaoTestCase;
import org.seasar.extension.jdbc.impl.MapListResultSetHandler;
import org.seasar.framework.exception.SIllegalArgumentException;

/**
 * @author manhole
 */
public class StaticStoredProcedureCommandTest extends S2DaoTestCase {

    protected void setUp() throws Exception {
        super.setUp();
        include("dao-derby.dicon");
        Procedures.params = new HashMap();
    }

    protected void tearDown() throws Exception {
        Procedures.params = null;
        super.tearDown();
    }

    public void testOutParameterTx() throws Exception {
        DaoMetaData dmd = createDaoMetaData(ProcedureDao.class);
        SqlCommand command = dmd.getSqlCommand("aaa1");
        String aaa = (String) command.execute(new Object[] {});
        assertEquals("aaaaa", aaa);
    }

    public void testMultiOutParametersTx() throws Exception {
        DaoMetaData dmd = createDaoMetaData(ProcedureDao.class);
        SqlCommand command = dmd.getSqlCommand("aaa2");
        final long t1 = System.currentTimeMillis();
        Map aaa = (Map) command.execute(new Object[] {});
        final long t2 = System.currentTimeMillis();
        System.out.println(aaa);

        assertEquals("aaaaa2", aaa.get("BBB"));
        final Timestamp timestamp = (Timestamp) aaa.get("CCC");
        assertEquals(true, t1 <= timestamp.getTime());
        assertEquals(true, timestamp.getTime() <= t2);
    }

    public void testNoParameterTx() throws Exception {
        DaoMetaData dmd = createDaoMetaData(ProcedureDao.class);
        SqlCommand command = dmd.getSqlCommand("aaa3");
        command.execute(new Object[] {});
        assertTrue(Procedures.isAaa3Invoked);
    }

    public void testInParameterTx() throws Exception {
        DaoMetaData dmd = createDaoMetaData(ProcedureDao.class);
        SqlCommand command = dmd.getSqlCommand("bbb1");
        command.execute(new Object[] { "abcde" });
        assertEquals("abcde", Procedures.params.get("ccc"));
    }

    public void testMultiInParametersTx() throws Exception {
        DaoMetaData dmd = createDaoMetaData(ProcedureDao.class);
        SqlCommand command = dmd.getSqlCommand("bbb2");
        final long current = System.currentTimeMillis();
        command.execute(new Object[] { "abcde", new Integer(111),
                new Timestamp(current) });
        assertEquals("abcde", Procedures.params.get("ccc"));
        assertEquals(111, ((BigDecimal) Procedures.params.get("ddd"))
                .intValue());
        assertEquals(current, ((Timestamp) Procedures.params.get("eee"))
                .getTime());
    }

    public void testInOutMixedParameters1Tx() throws Exception {
        DaoMetaData dmd = createDaoMetaData(ProcedureDao.class);
        SqlCommand command = dmd.getSqlCommand("ccc1");
        final String ret = (String) command.execute(new Object[] { "foo",
                new Integer(112) });
        assertEquals("foo112", ret);
        assertEquals("foo", Procedures.params.get("ccc"));
        assertEquals(112, ((BigDecimal) Procedures.params.get("ddd"))
                .intValue());
        String[] eee = (String[]) Procedures.params.get("eee");
        assertEquals(1, eee.length);
    }

    public void testInOutMixedParameters2Tx() throws Exception {
        DaoMetaData dmd = createDaoMetaData(ProcedureDao.class);
        SqlCommand command = dmd.getSqlCommand("ccc2");
        final Map ret = (Map) command.execute(new Object[] { new Integer(25) });
        System.out.println(ret);
        assertEquals("25", ret.get("CCC"));
        assertEquals("625", ret.get("EEE"));

        String[] ccc = (String[]) Procedures.params.get("ccc");
        assertEquals(1, ccc.length);
        assertEquals(25, ((BigDecimal) Procedures.params.get("ddd")).intValue());
        String[] eee = (String[]) Procedures.params.get("eee");
        assertEquals(1, eee.length);
    }

    public void testInOutParameterTx() throws Exception {
        DaoMetaData dmd = createDaoMetaData(ProcedureDao.class);
        SqlCommand command = dmd.getSqlCommand("ddd1");
        final String ret = (String) command.execute(new Object[] { "ab" });

        assertEquals("abcd", ret);
        String[] ccc = (String[]) Procedures.params.get("ccc");
        assertEquals(1, ccc.length);
    }

    public void testIllegalArgSize() throws Exception {
        DaoMetaData dmd = createDaoMetaData(ProcedureDao.class);
        SqlCommand command = dmd.getSqlCommand("illegalArgSizeBbb2");
        try {
            command.execute(new Object[] { "abcde", new Integer(111) });
        } catch (SIllegalArgumentException e) {
            System.out.println(e.getMessage());
            assertEquals("EDAO0032", e.getMessageCode());
        }
    }

    public void testMetaData() throws Exception {
        final DatabaseMetaData metaData = getConnection().getMetaData();
        System.out.println("DatabaseProductName="
                + metaData.getDatabaseProductName());
        System.out.println("DatabaseProductVersion="
                + metaData.getDatabaseProductVersion());
        System.out.println("DriverName=" + metaData.getDriverName());
        System.out.println("DriverVersion=" + metaData.getDriverVersion());
    }

    public void testMetaDataForProcesures() throws Exception {
        final DatabaseMetaData metaData = getConnection().getMetaData();
        final ResultSet rset = metaData.getProcedures(null, null, null);
        MapListResultSetHandler handler = new MapListResultSetHandler();
        List l = (List) handler.handle(rset);
        for (Iterator it = l.iterator(); it.hasNext();) {
            Map m = (Map) it.next();
            System.out.println(m);
        }
    }

    public void testMetaDataForTables() throws Exception {
        final DatabaseMetaData metaData = getConnection().getMetaData();
        final ResultSet rset = metaData.getTables(null, null, null, null);
        MapListResultSetHandler handler = new MapListResultSetHandler();
        List l = (List) handler.handle(rset);
        for (Iterator it = l.iterator(); it.hasNext();) {
            Map m = (Map) it.next();
            System.out.println(m);
        }
    }

    public static interface ProcedureDao {

        public String aaa1_PROCEDURE = "PROCEDURE_TEST_AAA1";

        public String aaa2_PROCEDURE = "PROCEDURE_TEST_AAA2";

        public String aaa3_PROCEDURE = "PROCEDURE_TEST_AAA3";

        public String bbb1_PROCEDURE = "PROCEDURE_TEST_BBB1";

        public String bbb2_PROCEDURE = "PROCEDURE_TEST_BBB2";

        public String illegalArgSizeBbb2_PROCEDURE = "PROCEDURE_TEST_BBB2";

        public String ccc1_PROCEDURE = "PROCEDURE_TEST_CCC1";

        public String ccc2_PROCEDURE = "PROCEDURE_TEST_CCC2";

        public String ddd1_PROCEDURE = "PROCEDURE_TEST_DDD1";

        public String aaa1();

        public Map aaa2();

        public Map aaa3();

        public void bbb1(String ccc);

        public void bbb2(String ccc, Integer ddd, Timestamp eee);

        public void illegalArgSizeBbb2(String ccc, Integer ddd);

        public String ccc1(String ccc, Integer ddd);

        public Map ccc2(Integer ddd);

        public String ddd1(String ccc);

    }

}
