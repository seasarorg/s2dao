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
package org.seasar.dao.handler;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;

import javax.sql.DataSource;

import org.seasar.dao.ProcedureMetaData;
import org.seasar.dao.ProcedureMetaDataFactory;
import org.seasar.dao.SqlCommand;
import org.seasar.dao.ValueTypeFactory;
import org.seasar.dao.impl.ArgumentDtoProcedureCommand;
import org.seasar.dao.impl.FieldArgumentDtoAnnotationReader;
import org.seasar.dao.impl.ProcedureMetaDataFactoryImpl;
import org.seasar.dao.impl.Procedures;
import org.seasar.dao.impl.ValueTypeFactoryImpl;
import org.seasar.extension.jdbc.ResultSetFactory;
import org.seasar.extension.jdbc.ResultSetHandler;
import org.seasar.extension.jdbc.StatementFactory;
import org.seasar.extension.jdbc.impl.ObjectResultSetHandler;
import org.seasar.extension.unit.S2TestCase;
import org.seasar.framework.exception.SIllegalArgumentException;

/**
 * @author taedium
 *
 */
public class ArgumentDtoProcedureCommandTest extends S2TestCase {

    private DataSource dataSource;

    private ResultSetHandler resultSetHandler = new ObjectResultSetHandler();

    private ResultSetFactory resultSetFactory;

    private StatementFactory statementFactory;

    protected void setUp() throws Exception {
        super.setUp();
        include("j2ee-derby.dicon");
        Procedures.params = new HashMap();
    }

    protected void tearDown() throws Exception {
        Procedures.params = null;
        super.tearDown();
    }

    public void testAaa1Tx() throws Exception {
        Aaa1 dto = new Aaa1();
        SqlCommand command = createSqlCommand("PROCEDURE_TEST_AAA1", Dao.class
                .getMethod("executeAaa1", new Class[] { Aaa1.class }));
        command.execute(new Object[] { dto });
        assertNotNull(dto.getFoo());
    }

    public void testAaa2Tx() throws Exception {
        Aaa2 dto = new Aaa2();
        SqlCommand command = createSqlCommand("PROCEDURE_TEST_AAA2", Dao.class
                .getMethod("executeAaa2", new Class[] { Aaa2.class }));
        command.execute(new Object[] { dto });
        assertNotNull(dto.getBbb());
        assertNotNull(dto.getCcc());
    }

    public void testAaa3Tx() throws Exception {
        Aaa3 dto = new Aaa3();
        SqlCommand command = createSqlCommand("PROCEDURE_TEST_AAA3", Dao.class
                .getMethod("executeAaa3", new Class[] { Aaa3.class }));
        command.execute(new Object[] { dto });
        assertTrue(Procedures.isAaa3Invoked);
    }

    public void testAaa3_nullTx() throws Exception {
        SqlCommand command = createSqlCommand("PROCEDURE_TEST_AAA3", Dao.class
                .getMethod("executeAaa3", new Class[] { Aaa3.class }));
        try {
            command.execute(new Object[] { null });
            fail();
        } catch (SIllegalArgumentException e) {
            assertEquals("EDAO0029", e.getMessageCode());
            System.out.println(e.getMessage());
        }
    }

    public void testBbb1Tx() throws Exception {
        Bbb1 dto = new Bbb1();
        SqlCommand command = createSqlCommand("PROCEDURE_TEST_BBB1", Dao.class
                .getMethod("executeBbb1", new Class[] { Bbb1.class }));
        dto.setCcc("hoge");

        command.execute(new Object[] { dto });
        assertEquals("hoge", Procedures.params.get("ccc"));
    }

    public void testBbb2Tx() throws Exception {
        Bbb2 dto = new Bbb2();
        SqlCommand command = createSqlCommand("PROCEDURE_TEST_BBB2", Dao.class
                .getMethod("executeBbb2", new Class[] { Bbb2.class }));
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

    public void testCcc1Tx() throws Exception {
        Ccc1 dto = new Ccc1();
        SqlCommand command = createSqlCommand("PROCEDURE_TEST_CCC1", Dao.class
                .getMethod("executeCcc1", new Class[] { Ccc1.class }));
        dto.setCcc("hoge");
        dto.setDdd(new BigDecimal("10"));
        command.execute(new Object[] { dto });

        assertEquals("hoge", Procedures.params.get("ccc"));
        assertEquals(new BigDecimal("10"), (BigDecimal) Procedures.params
                .get("ddd"));
        assertNotNull(dto.getEee());
    }

    public void testCcc1_nullTx() throws Exception {
        SqlCommand command = createSqlCommand("PROCEDURE_TEST_CCC1", Dao.class
                .getMethod("executeCcc1", new Class[] { Ccc1.class }));
        try {
            command.execute(new Object[] { null });
            fail();
        } catch (SIllegalArgumentException e) {
            assertEquals("EDAO0029", e.getMessageCode());
            System.out.println(e.getMessage());
        }
    }

    public void testCcc2Tx() throws Exception {
        Ccc2 dto = new Ccc2();
        SqlCommand command = createSqlCommand("PROCEDURE_TEST_CCC2", Dao.class
                .getMethod("executeCcc2", new Class[] { Ccc2.class }));
        dto.setDdd(new BigDecimal("10"));
        command.execute(new Object[] { dto });

        assertNotNull(dto.getCcc());
        assertEquals(new BigDecimal("10"), (BigDecimal) Procedures.params
                .get("ddd"));
        assertNotNull(dto.getEee());
    }

    public void testDdd1Tx() throws Exception {
        Ddd1 dto = new Ddd1();
        SqlCommand command = createSqlCommand("PROCEDURE_TEST_DDD1", Dao.class
                .getMethod("executeDdd1", new Class[] { Ddd1.class }));
        dto.setCcc("ab");
        command.execute(new Object[] { dto });

        assertEquals("abcd", dto.getCcc());
    }

    private SqlCommand createSqlCommand(String procedureName, Method method) {
        ValueTypeFactory valueTypeFactory = new ValueTypeFactoryImpl();
        FieldArgumentDtoAnnotationReader reader = new FieldArgumentDtoAnnotationReader();
        ProcedureMetaDataFactory factory = new ProcedureMetaDataFactoryImpl(
                procedureName, valueTypeFactory, reader, method);
        ProcedureMetaData metaData = factory.createProcedureMetaData();
        return new ArgumentDtoProcedureCommand(dataSource, resultSetHandler,
                statementFactory, resultSetFactory, metaData);
    }

    public static interface Dao {
        void executeAaa1(Aaa1 aaa1);

        void executeAaa2(Aaa2 aaa2);

        void executeAaa3(Aaa3 aaa3);

        void executeBbb1(Bbb1 bbb1);

        void executeBbb2(Bbb2 bbb2);

        void executeCcc1(Ccc1 ccc1);

        void executeCcc2(Ccc2 ccc2);

        void executeDdd1(Ddd1 ddd1);
    }

    public static class Aaa1 {

        public static String PROCEDURE_PARAMETERS = null;

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

        public static String PROCEDURE_PARAMETERS = null;

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

    public static class Aaa3 {
        public static String PROCEDURE_PARAMETERS = null;
    }

    public static class Bbb1 {

        public static String PROCEDURE_PARAMETERS = null;

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

        public static String PROCEDURE_PARAMETERS = null;

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

        public static String PROCEDURE_PARAMETERS = null;

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

        public static String PROCEDURE_PARAMETERS = null;

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

        public static String PROCEDURE_PARAMETERS = null;

        public static String ccc_PROCEDURE_PARAMETER = "inout";

        private String ccc;

        public String getCcc() {
            return ccc;
        }

        public void setCcc(String ccc) {
            this.ccc = ccc;
        }
    }
}
