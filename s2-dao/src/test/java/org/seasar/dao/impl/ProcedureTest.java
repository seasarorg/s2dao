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

import java.math.BigDecimal;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.seasar.extension.jdbc.impl.MapListResultSetHandler;
import org.seasar.extension.unit.S2TestCase;

/**
 * @author manhole
 */
public class ProcedureTest extends S2TestCase {

    public static class DummyBean {
    }

    public static interface ProcedureDao {

        public static Class BEAN = DummyBean.class;

        public String aaa1_PROCEDURE = "PROCEDURE_TEST_AAA1";

        public String aaa2_PROCEDURE = "PROCEDURE_TEST_AAA2";

        public String aaa3_PROCEDURE = "PROCEDURE_TEST_AAA3";

        public String bbb1_PROCEDURE = "PROCEDURE_TEST_BBB1";

        public String bbb2_PROCEDURE = "PROCEDURE_TEST_BBB2";

        public String ccc1_PROCEDURE = "PROCEDURE_TEST_CCC1";

        public String ccc2_PROCEDURE = "PROCEDURE_TEST_CCC2";

        public String ddd1_PROCEDURE = "PROCEDURE_TEST_DDD1";

        public String aaa1();

        public Map aaa2();

        public Map aaa3();

        public void bbb1(String ccc);

        public void bbb2(String ccc, Integer ddd, Timestamp eee);

        public String ccc1(String ccc, Integer ddd);

        public Map ccc2(Integer ddd);

        public String ddd1(String ccc);

    }

    private static boolean isAaa3Invoked;

    private static Map procedureParam;

    public static void procedureAaa1(String[] s) {
        s[0] = "aaaaa";
    }

    public static void procedureAaa2(String[] s, Timestamp[] t) {
        s[0] = "aaaaa2";
        t[0] = new Timestamp(System.currentTimeMillis());
    }

    public static void procedureAaa3() {
        isAaa3Invoked = true;
    }

    public static void procedureBbb1(String ccc) {
        procedureParam.put("ccc", ccc);
    }

    public static void procedureBbb2(String ccc, BigDecimal ddd, Timestamp eee) {
        procedureParam.put("ccc", ccc);
        procedureParam.put("ddd", ddd);
        procedureParam.put("eee", eee);
    }

    public static void procedureCcc1(String ccc, BigDecimal ddd, String[] eee) {
        procedureParam.put("ccc", ccc);
        procedureParam.put("ddd", ddd);
        procedureParam.put("eee", eee);
        eee[0] = ccc + ddd;
    }

    public static void procedureCcc2(String[] ccc, BigDecimal ddd, String[] eee) {
        procedureParam.put("ccc", ccc);
        procedureParam.put("ddd", ddd);
        procedureParam.put("eee", eee);
        ccc[0] = ddd.toString();
        eee[0] = ddd.multiply(ddd).toString();
    }

    public static void procedureDdd1(String[] ccc) {
        procedureParam.put("ccc", ccc);
        ccc[0] = ccc[0] + "cd";
    }

    private ProcedureDao procedureDao;

    protected void setUp() throws Exception {
        super.setUp();
        PathResolverImpl.setSuffix("-derby");
        include("ProcedureTest.dicon");
        procedureParam = new HashMap();
    }

    protected void tearDown() throws Exception {
        PathResolverImpl.setSuffix(null);
        super.tearDown();
    }

    public void testAaa1Tx() throws Exception {
        assertNotNull(procedureDao);
        String aaa = procedureDao.aaa1();
        assertEquals("aaaaa", aaa);
    }

    public void testAaa2() throws Exception {
        assertNotNull(procedureDao);
        final long t1 = System.currentTimeMillis();
        Map aaa = procedureDao.aaa2();
        final long t2 = System.currentTimeMillis();
        System.out.println(aaa);

        assertEquals("aaaaa2", aaa.get("BBB"));
        final Timestamp timestamp = (Timestamp) aaa.get("CCC");
        assertEquals(true, t1 <= timestamp.getTime());
        assertEquals(true, timestamp.getTime() <= t2);
    }

    public void testAaa3() throws Exception {
        assertNotNull(procedureDao);
        procedureDao.aaa3();
        assertTrue(isAaa3Invoked);
    }

    public void testBbb1() throws Exception {
        assertNotNull(procedureDao);
        procedureDao.bbb1("abcde");
        assertEquals("abcde", procedureParam.get("ccc"));
    }

    public void testBbb2() throws Exception {
        assertNotNull(procedureDao);
        final long current = System.currentTimeMillis();
        procedureDao.bbb2("abcde", new Integer(111), new Timestamp(current));
        assertEquals("abcde", procedureParam.get("ccc"));
        assertEquals(111, ((BigDecimal) procedureParam.get("ddd")).intValue());
        assertEquals(current, ((Timestamp) procedureParam.get("eee")).getTime());
    }

    public void testCcc1() throws Exception {
        assertNotNull(procedureDao);
        final String ret = procedureDao.ccc1("foo", new Integer(112));
        assertEquals("foo112", ret);
        assertEquals("foo", procedureParam.get("ccc"));
        assertEquals(112, ((BigDecimal) procedureParam.get("ddd")).intValue());
        String[] eee = (String[]) procedureParam.get("eee");
        assertEquals(1, eee.length);
    }

    public void testCcc2() throws Exception {
        assertNotNull(procedureDao);
        final Map ret = procedureDao.ccc2(new Integer(25));
        System.out.println(ret);
        assertEquals("25", ret.get("CCC"));
        assertEquals("625", ret.get("EEE"));

        String[] ccc = (String[]) procedureParam.get("ccc");
        assertEquals(1, ccc.length);
        assertEquals(25, ((BigDecimal) procedureParam.get("ddd")).intValue());
        String[] eee = (String[]) procedureParam.get("eee");
        assertEquals(1, eee.length);
    }

    public void testDdd1() throws Exception {
        assertNotNull(procedureDao);
        final String ret = procedureDao.ddd1("ab");
        assertEquals("abcd", ret);
        String[] ccc = (String[]) procedureParam.get("ccc");
        assertEquals(1, ccc.length);
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

}
