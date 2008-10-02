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

import org.seasar.dao.IllegalProcedureParameterIndexRuntimeException;
import org.seasar.dao.IllegalSignatureRuntimeException;
import org.seasar.dao.ProcedureMetaData;
import org.seasar.dao.ProcedureParameterIndexDiscreteRuntimeException;
import org.seasar.dao.ProcedureParameterIndexDuplicatedRuntimeException;
import org.seasar.dao.ProcedureParameterType;
import org.seasar.extension.jdbc.types.ValueTypes;
import org.seasar.extension.unit.S2TestCase;

/**
 * @author taedium
 * 
 */
public class ProcedureMetaDataFactoryImplTest extends S2TestCase {

    protected void setUp() throws Exception {
        super.setUp();
        include("j2ee-derby.dicon");
    }

    public void testCreateProcedureMetaData() throws Exception {
        String name = "PROCEDURE_TEST_CCC2";
        ProcedureMetaDataFactoryImpl factory = new ProcedureMetaDataFactoryImpl();
        factory.setValueTypeFactory(new ValueTypeFactoryImpl());
        factory.setAnnotationReaderFactory(new FieldAnnotationReaderFactory());
        factory.initialize();
        ProcedureMetaData metaData = factory.createProcedureMetaData(name,
                Dao.class.getMethod("execute", new Class[] { Hoge.class }));

        assertNotNull(metaData);
        assertEquals(3, metaData.getParameterTypeSize());

        ProcedureParameterType ppt = metaData.getParameterType("ccc");
        assertTrue(ppt.isOutType());
        assertEquals(ValueTypes.STRING, ppt.getValueType());

        ppt = metaData.getParameterType("ddd");
        assertTrue(ppt.isInType());
        assertEquals(ValueTypes.INTEGER, ppt.getValueType());

        ppt = metaData.getParameterType("eee");
        assertTrue(ppt.isOutType());
        assertEquals(ValueTypes.STRING, ppt.getValueType());
    }

    public void testCreateProcedureMetaData_annotated() throws Exception {
        String name = "PROCEDURE_TEST_CCC2";
        ProcedureMetaDataFactoryImpl factory = new ProcedureMetaDataFactoryImpl();
        factory.setValueTypeFactory(new ValueTypeFactoryImpl());
        factory.setAnnotationReaderFactory(new FieldAnnotationReaderFactory());
        factory.initialize();
        ProcedureMetaData metaData = factory.createProcedureMetaData(name,
                Dao2.class.getMethod("execute", new Class[] { Hoge2.class }));

        assertNotNull(metaData);
        assertEquals(3, metaData.getParameterTypeSize());

        ProcedureParameterType ppt = metaData.getParameterType("ccc");
        assertTrue(ppt.isOutType());
        assertEquals(ValueTypes.STRING, ppt.getValueType());

        ppt = metaData.getParameterType("ddd");
        assertTrue(ppt.isInType());
        assertEquals(ValueTypes.INTEGER, ppt.getValueType());

        ppt = metaData.getParameterType("eee");
        assertTrue(ppt.isOutType());
        assertEquals(ValueTypes.STRING, ppt.getValueType());
    }

    public void testCreateProcedureMetaData_annotated_illegal()
            throws Exception {
        String name = "PROCEDURE_TEST_CCC2";
        ProcedureMetaDataFactoryImpl factory = new ProcedureMetaDataFactoryImpl();
        factory.setValueTypeFactory(new ValueTypeFactoryImpl());
        factory.setAnnotationReaderFactory(new FieldAnnotationReaderFactory());
        factory.initialize();
        try {
            factory.createProcedureMetaData(name, Dao2.class.getMethod(
                    "executeIllegal", new Class[] { Hoge3.class }));
            fail();
        } catch (IllegalProcedureParameterIndexRuntimeException expected) {
        }
    }

    public void testCreateProcedureMetaData_annotated_descrete()
            throws Exception {
        String name = "PROCEDURE_TEST_CCC2";
        ProcedureMetaDataFactoryImpl factory = new ProcedureMetaDataFactoryImpl();
        factory.setValueTypeFactory(new ValueTypeFactoryImpl());
        factory.setAnnotationReaderFactory(new FieldAnnotationReaderFactory());
        factory.initialize();
        try {
            factory.createProcedureMetaData(name, Dao2.class.getMethod(
                    "executeDiscrete", new Class[] { Hoge4.class }));
            fail();
        } catch (ProcedureParameterIndexDiscreteRuntimeException expected) {
        }
    }

    public void testCreateProcedureMetaData_annotated_duplicated()
            throws Exception {
        String name = "PROCEDURE_TEST_CCC2";
        ProcedureMetaDataFactoryImpl factory = new ProcedureMetaDataFactoryImpl();
        factory.setValueTypeFactory(new ValueTypeFactoryImpl());
        factory.setAnnotationReaderFactory(new FieldAnnotationReaderFactory());
        factory.initialize();
        try {
            factory.createProcedureMetaData(name, Dao2.class.getMethod(
                    "executeDuplicated", new Class[] { Hoge5.class }));
            fail();
        } catch (ProcedureParameterIndexDuplicatedRuntimeException expected) {
        }
    }

    public void testCreateProcedureMetaData_noParameter() throws Exception {
        String name = "PROCEDURE_TEST_CCC2";
        ProcedureMetaDataFactoryImpl factory = new ProcedureMetaDataFactoryImpl();
        factory.setValueTypeFactory(new ValueTypeFactoryImpl());
        factory.setAnnotationReaderFactory(new FieldAnnotationReaderFactory());
        factory.initialize();
        ProcedureMetaData metaData = factory.createProcedureMetaData(name,
                Dao.class.getMethod("executeWithNoParameter", new Class[] {}));
        assertNotNull(metaData);
        assertEquals(0, metaData.getParameterTypeSize());
    }

    public void testCreateProcedureMetaData_simpleParameter() throws Exception {
        String name = "PROCEDURE_TEST_CCC2";
        ProcedureMetaDataFactoryImpl factory = new ProcedureMetaDataFactoryImpl();
        factory.setValueTypeFactory(new ValueTypeFactoryImpl());
        factory.setAnnotationReaderFactory(new FieldAnnotationReaderFactory());
        factory.initialize();
        try {
            factory.createProcedureMetaData(name, Dao.class.getMethod(
                    "executeWithSimpleParameter", new Class[] { int.class }));
            fail();
        } catch (IllegalSignatureRuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    public void testCreateProcedureMetaData_multiParameters() throws Exception {
        String name = "PROCEDURE_TEST_CCC2";
        ProcedureMetaDataFactoryImpl factory = new ProcedureMetaDataFactoryImpl();
        factory.setValueTypeFactory(new ValueTypeFactoryImpl());
        factory.setAnnotationReaderFactory(new FieldAnnotationReaderFactory());
        factory.initialize();
        try {
            factory.createProcedureMetaData(name, Dao.class.getMethod(
                    "executeWithMultiParameters", new Class[] { Hoge.class,
                            int.class }));
            fail();
        } catch (IllegalSignatureRuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    public interface Dao {
        void execute(Hoge hoge);

        void executeWithNoParameter();

        void executeWithSimpleParameter(int aaa);

        void executeWithMultiParameters(Hoge hoge, int aaa);
    }

    public interface Dao2 {
        void execute(Hoge2 hoge);

        void executeIllegal(Hoge3 hoge);

        void executeDiscrete(Hoge4 hoge);

        void executeDuplicated(Hoge5 hoge);
    }

    public static class Hoge {

        public static String PROCEDURE_PARAMETERS = null;

        public static String ccc_PROCEDURE_PARAMETER = "out";

        public static String ddd_PROCEDURE_PARAMETER = "in";

        public static String eee_PROCEDURE_PARAMETER = "out";

        private String ccc;

        private int ddd;

        private String eee;

        public String getCcc() {
            return ccc;
        }

        public void setCcc(String ccc) {
            this.ccc = ccc;
        }

        public int getDdd() {
            return ddd;
        }

        public void setDdd(int ddd) {
            this.ddd = ddd;
        }

        public String getEee() {
            return eee;
        }

        public void setEee(String eee) {
            this.eee = eee;
        }
    }

    public static class Hoge2 {

        public static String ddd_PROCEDURE_PARAMETER = "in";

        public static int ddd_PROCEDURE_PARAMETER_INDEX = 2;

        public static String eee_PROCEDURE_PARAMETER = "out";

        public static int eee_PROCEDURE_PARAMETER_INDEX = 3;

        public static String ccc_PROCEDURE_PARAMETER = "out";

        public static int ccc_PROCEDURE_PARAMETER_INDEX = 1;

        private int ddd;

        private String eee;

        private String ccc;

        public String getCcc() {
            return ccc;
        }

        public void setCcc(String ccc) {
            this.ccc = ccc;
        }

        public int getDdd() {
            return ddd;
        }

        public void setDdd(int ddd) {
            this.ddd = ddd;
        }

        public String getEee() {
            return eee;
        }

        public void setEee(String eee) {
            this.eee = eee;
        }
    }

    public static class Hoge3 {

        public static String ccc_PROCEDURE_PARAMETER = "out";

        public static int ccc_PROCEDURE_PARAMETER_INDEX = 0;

        public static String ddd_PROCEDURE_PARAMETER = "in";

        public static int ddd_PROCEDURE_PARAMETER_INDEX = 1;

        public static String eee_PROCEDURE_PARAMETER = "out";

        public static int eee_PROCEDURE_PARAMETER_INDEX = 2;

        private String ccc;

        private int ddd;

        private String eee;

        public String getCcc() {
            return ccc;
        }

        public void setCcc(String ccc) {
            this.ccc = ccc;
        }

        public int getDdd() {
            return ddd;
        }

        public void setDdd(int ddd) {
            this.ddd = ddd;
        }

        public String getEee() {
            return eee;
        }

        public void setEee(String eee) {
            this.eee = eee;
        }
    }

    public static class Hoge4 {

        public static String ccc_PROCEDURE_PARAMETER = "out";

        public static int ccc_PROCEDURE_PARAMETER_INDEX = 1;

        public static String ddd_PROCEDURE_PARAMETER = "in";

        public static int ddd_PROCEDURE_PARAMETER_INDEX = 2;

        public static String eee_PROCEDURE_PARAMETER = "out";

        public static int eee_PROCEDURE_PARAMETER_INDEX = 4;

        private String ccc;

        private int ddd;

        private String eee;

        public String getCcc() {
            return ccc;
        }

        public void setCcc(String ccc) {
            this.ccc = ccc;
        }

        public int getDdd() {
            return ddd;
        }

        public void setDdd(int ddd) {
            this.ddd = ddd;
        }

        public String getEee() {
            return eee;
        }

        public void setEee(String eee) {
            this.eee = eee;
        }
    }

    public static class Hoge5 {

        public static String ccc_PROCEDURE_PARAMETER = "out";

        public static int ccc_PROCEDURE_PARAMETER_INDEX = 1;

        public static String ddd_PROCEDURE_PARAMETER = "in";

        public static int ddd_PROCEDURE_PARAMETER_INDEX = 2;

        public static String eee_PROCEDURE_PARAMETER = "out";

        public static int eee_PROCEDURE_PARAMETER_INDEX = 1;

        private String ccc;

        private int ddd;

        private String eee;

        public String getCcc() {
            return ccc;
        }

        public void setCcc(String ccc) {
            this.ccc = ccc;
        }

        public int getDdd() {
            return ddd;
        }

        public void setDdd(int ddd) {
            this.ddd = ddd;
        }

        public String getEee() {
            return eee;
        }

        public void setEee(String eee) {
            this.eee = eee;
        }
    }
}
