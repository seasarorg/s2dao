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
import java.sql.Timestamp;
import java.util.HashMap;

import org.seasar.dao.DaoMetaData;
import org.seasar.dao.SqlCommand;
import org.seasar.dao.unit.S2DaoTestCase;
import org.seasar.framework.exception.SIllegalArgumentException;

/**
 * @author taedium
 *
 */
public class ArgumentDtoProcedureCommandTest extends S2DaoTestCase {

    protected void setUp() throws Exception {
        super.setUp();
        include("j2ee-derby.dicon");
        Procedures.params = new HashMap();
    }

    protected void tearDown() throws Exception {
        Procedures.params = null;
        super.tearDown();
    }

    public void testOutParameterTx() throws Exception {
        DaoMetaData dmd = createDaoMetaData(Dao.class);
        SqlCommand command = dmd.getSqlCommand("executeAaa1");
        Aaa1 dto = new Aaa1();
        command.execute(new Object[] { dto });
        assertNotNull(dto.getFoo());
    }

    public void testMultiOutParametersTx() throws Exception {
        DaoMetaData dmd = createDaoMetaData(Dao.class);
        SqlCommand command = dmd.getSqlCommand("executeAaa2");
        Aaa2 dto = new Aaa2();
        command.execute(new Object[] { dto });
        assertNotNull(dto.getBbb());
        assertNotNull(dto.getCcc());
    }

    public void testEmptyArgumentTx() throws Exception {
        DaoMetaData dmd = createDaoMetaData(Dao.class);
        SqlCommand command = dmd.getSqlCommand("executeAaa3");
        command.execute(new Object[] {});
        assertTrue(Procedures.isAaa3Invoked);
    }

    public void testInParameterTx() throws Exception {
        DaoMetaData dmd = createDaoMetaData(Dao.class);
        SqlCommand command = dmd.getSqlCommand("executeBbb1");
        Bbb1 dto = new Bbb1();
        dto.setCcc("hoge");
        command.execute(new Object[] { dto });
        assertEquals("hoge", Procedures.params.get("ccc"));
    }

    public void testMultiInParametersTx() throws Exception {
        DaoMetaData dmd = createDaoMetaData(Dao.class);
        SqlCommand command = dmd.getSqlCommand("executeBbb2");
        Bbb2 dto = new Bbb2();
        dto.setCcc("hoge");
        dto.setDdd(new BigDecimal("10"));
        dto.setXxx(Timestamp.valueOf("2007-08-26 14:30:00"));
        command.execute(new Object[] { dto });
        assertEquals("hoge", Procedures.params.get("ccc"));
        assertEquals(new BigDecimal("10"), (BigDecimal) Procedures.params
                .get("ddd"));
        assertEquals(Timestamp.valueOf("2007-08-26 14:30:00"),
                (Timestamp) Procedures.params.get("eee"));
    }

    public void testInOutMixedParametersTx() throws Exception {
        DaoMetaData dmd = createDaoMetaData(Dao.class);
        SqlCommand command = dmd.getSqlCommand("executeCcc1");
        Ccc1 dto = new Ccc1();
        dto.setCcc("hoge");
        dto.setDdd(new BigDecimal("10"));
        command.execute(new Object[] { dto });
        assertEquals("hoge", Procedures.params.get("ccc"));
        assertEquals(new BigDecimal("10"), (BigDecimal) Procedures.params
                .get("ddd"));
        assertNotNull(dto.getEee());
    }

    public void testNullArgumentTx() throws Exception {
        DaoMetaData dmd = createDaoMetaData(Dao.class);
        SqlCommand command = dmd.getSqlCommand("executeCcc1");
        try {
            command.execute(new Object[] { null });
            fail();
        } catch (SIllegalArgumentException e) {
            assertEquals("EDAO0029", e.getMessageCode());
            System.out.println(e.getMessage());
        }
    }

    public void testInOutMixedParameters2Tx() throws Exception {
        DaoMetaData dmd = createDaoMetaData(Dao.class);
        SqlCommand command = dmd.getSqlCommand("executeCcc2");
        Ccc2 dto = new Ccc2();
        dto.setDdd(new BigDecimal("10"));
        command.execute(new Object[] { dto });
        assertNotNull(dto.getCcc());
        assertEquals(new BigDecimal("10"), (BigDecimal) Procedures.params
                .get("ddd"));
        assertNotNull(dto.getEee());
    }

    public void testInOutParameterTx() throws Exception {
        DaoMetaData dmd = createDaoMetaData(Dao.class);
        SqlCommand command = dmd.getSqlCommand("executeDdd1");
        Ddd1 dto = new Ddd1();
        dto.setCcc("ab");
        command.execute(new Object[] { dto });
        assertEquals("abcd", dto.getCcc());
    }

    public void testReturnParameterTx() throws Exception {
        DaoMetaData dmd = createDaoMetaData(Dao.class);
        SqlCommand command = dmd.getSqlCommand("max");
        MaxDto dto = new MaxDto();
        dto.setBbb(5d);
        dto.setCcc(10d);
        command.execute(new Object[] { dto });
        assertEquals(10d, dto.getAaa(), 0);
    }

    public static interface Dao {

        public static String executeAaa1_PROCEDURE_CALL = "PROCEDURE_TEST_AAA1";

        public static String executeAaa2_PROCEDURE_CALL = "PROCEDURE_TEST_AAA2";

        public static String executeAaa3_PROCEDURE_CALL = "PROCEDURE_TEST_AAA3";

        public static String executeBbb1_PROCEDURE_CALL = "PROCEDURE_TEST_BBB1";

        public static String executeBbb2_PROCEDURE_CALL = "PROCEDURE_TEST_BBB2";

        public static String executeCcc1_PROCEDURE_CALL = "PROCEDURE_TEST_CCC1";

        public static String executeCcc2_PROCEDURE_CALL = "PROCEDURE_TEST_CCC2";

        public static String executeDdd1_PROCEDURE_CALL = "PROCEDURE_TEST_DDD1";

        public static String max_PROCEDURE_CALL = "FUNCTION_TEST_MAX";

        void executeAaa1(Aaa1 aaa1);

        void executeAaa2(Aaa2 aaa2);

        void executeAaa3();

        void executeBbb1(Bbb1 bbb1);

        void executeBbb2(Bbb2 bbb2);

        void executeCcc1(Ccc1 ccc1);

        void executeCcc2(Ccc2 ccc2);

        void executeDdd1(Ddd1 ddd1);

        double max(MaxDto maxDto);
    }

    public static class Aaa1 {

        public static String foo_PROCEDURE_PARAMETER = "out";

        private String foo;

        public String getFoo() {
            return foo;
        }

        public void setFoo(String foo) {
            this.foo = foo;
        }
    }

    public static class Aaa2 {

        public static String bbb_PROCEDURE_PARAMETER = "out";

        public static String ccc_PROCEDURE_PARAMETER = "out";

        private String bbb;

        private Timestamp ccc;

        public String getBbb() {
            return bbb;
        }

        public void setBbb(String bbb) {
            this.bbb = bbb;
        }

        public Timestamp getCcc() {
            return ccc;
        }

        public void setCcc(Timestamp ccc) {
            this.ccc = ccc;
        }
    }

    public static class Bbb1 {

        public static String ccc_PROCEDURE_PARAMETER = "in";

        private String ccc;

        public String getCcc() {
            return ccc;
        }

        public void setCcc(String ccc) {
            this.ccc = ccc;
        }
    }

    public static class Bbb2 {

        public static String ccc_PROCEDURE_PARAMETER = "in";

        public static String ddd_PROCEDURE_PARAMETER = "in";

        public static String xxx_PROCEDURE_PARAMETER = "in";

        private String ccc;

        private BigDecimal ddd;

        private Timestamp xxx;

        public String getCcc() {
            return ccc;
        }

        public void setCcc(String ccc) {
            this.ccc = ccc;
        }

        public BigDecimal getDdd() {
            return ddd;
        }

        public void setDdd(BigDecimal ddd) {
            this.ddd = ddd;
        }

        public Timestamp getXxx() {
            return xxx;
        }

        public void setXxx(Timestamp xxx) {
            this.xxx = xxx;
        }
    }

    public static class Ccc1 {

        public static String ccc_PROCEDURE_PARAMETER = "in";

        public static String ddd_PROCEDURE_PARAMETER = "in";

        public static String eee_PROCEDURE_PARAMETER = "out";

        private String ccc;

        private BigDecimal ddd;

        private String eee;

        public String getCcc() {
            return ccc;
        }

        public void setCcc(String ccc) {
            this.ccc = ccc;
        }

        public BigDecimal getDdd() {
            return ddd;
        }

        public void setDdd(BigDecimal ddd) {
            this.ddd = ddd;
        }

        public String getEee() {
            return eee;
        }

        public void setEee(String eee) {
            this.eee = eee;
        }
    }

    public static class Ccc2 {

        public static String ccc_PROCEDURE_PARAMETER = "out";

        public static String ddd_PROCEDURE_PARAMETER = "in";

        public static String eee_PROCEDURE_PARAMETER = "out";

        private String ccc;

        private BigDecimal ddd;

        private String eee;

        public String getCcc() {
            return ccc;
        }

        public void setCcc(String ccc) {
            this.ccc = ccc;
        }

        public BigDecimal getDdd() {
            return ddd;
        }

        public void setDdd(BigDecimal ddd) {
            this.ddd = ddd;
        }

        public String getEee() {
            return eee;
        }

        public void setEee(String eee) {
            this.eee = eee;
        }
    }

    public static class Ddd1 {

        public static String ccc_PROCEDURE_PARAMETER = "inout";

        private String ccc;

        public String getCcc() {
            return ccc;
        }

        public void setCcc(String ccc) {
            this.ccc = ccc;
        }
    }

    public static class MaxDto {

        public static String aaa_PROCEDURE_PARAMETER = "return";

        public static String bbb_PROCEDURE_PARAMETER = "in";

        public static String ccc_PROCEDURE_PARAMETER = "in";

        private double aaa;

        private double bbb;

        private double ccc;

        public double getAaa() {
            return aaa;
        }

        public void setAaa(double aaa) {
            this.aaa = aaa;
        }

        public double getBbb() {
            return bbb;
        }

        public void setBbb(double bbb) {
            this.bbb = bbb;
        }

        public double getCcc() {
            return ccc;
        }

        public void setCcc(double ccc) {
            this.ccc = ccc;
        }

    }
}
