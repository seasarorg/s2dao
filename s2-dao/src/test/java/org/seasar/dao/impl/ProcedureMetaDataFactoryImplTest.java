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

import org.seasar.dao.IllegalSignatureRuntimeException;
import org.seasar.dao.ProcedureMetaData;
import org.seasar.dao.ProcedureMetaDataFactory;
import org.seasar.dao.ProcedureParameterType;
import org.seasar.dao.ValueTypeFactory;
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
        ValueTypeFactory valueTypeFactory = new ValueTypeFactoryImpl();
        FieldArgumentDtoAnnotationReader reader = new FieldArgumentDtoAnnotationReader();
        ProcedureMetaDataFactory factory = new ProcedureMetaDataFactoryImpl(
                name, valueTypeFactory, reader, Dao.class.getMethod("execute",
                        new Class[] { Hoge.class }));
        ProcedureMetaData metaData = factory.createProcedureMetaData();

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

    public void testCreateProcedureMetaData_noParameter() throws Exception {
        String name = "PROCEDURE_TEST_CCC2";
        ValueTypeFactory valueTypeFactory = new ValueTypeFactoryImpl();
        FieldArgumentDtoAnnotationReader reader = new FieldArgumentDtoAnnotationReader();
        ProcedureMetaDataFactory factory = new ProcedureMetaDataFactoryImpl(
                name, valueTypeFactory, reader, Dao.class.getMethod(
                        "executeWithNoParameter", null));
        ProcedureMetaData metaData = factory.createProcedureMetaData();
        assertNotNull(metaData);
        assertEquals(0, metaData.getParameterTypeSize());
    }

    public void testCreateProcedureMetaData_simpleParameter() throws Exception {
        String name = "PROCEDURE_TEST_CCC2";
        ValueTypeFactory valueTypeFactory = new ValueTypeFactoryImpl();
        FieldArgumentDtoAnnotationReader reader = new FieldArgumentDtoAnnotationReader();
        try {
            new ProcedureMetaDataFactoryImpl(name, valueTypeFactory, reader,
                    Dao.class.getMethod("executeWithSimpleParameter",
                            new Class[] { int.class }));
            fail();
        } catch (IllegalSignatureRuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    public void testCreateProcedureMetaData_multiParameters() throws Exception {
        String name = "PROCEDURE_TEST_CCC2";
        ValueTypeFactory valueTypeFactory = new ValueTypeFactoryImpl();
        FieldArgumentDtoAnnotationReader reader = new FieldArgumentDtoAnnotationReader();
        try {
            new ProcedureMetaDataFactoryImpl(name, valueTypeFactory, reader,
                    Dao.class.getMethod("executeWithMultiParameters",
                            new Class[] { Hoge.class, int.class }));
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
}
