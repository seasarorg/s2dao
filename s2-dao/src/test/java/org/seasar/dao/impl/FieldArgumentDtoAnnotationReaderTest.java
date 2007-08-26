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

import junit.framework.TestCase;

import org.seasar.dao.ProcedureParameterType;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.PropertyDesc;
import org.seasar.framework.beans.factory.BeanDescFactory;

/**
 * @author taedium
 *
 */
public class FieldArgumentDtoAnnotationReaderTest extends TestCase {

    private FieldArgumentDtoAnnotationReader reader = new FieldArgumentDtoAnnotationReader();

    private BeanDesc beanDesc = BeanDescFactory.getBeanDesc(Hoge.class);

    public void testIsProcedureParameters() throws Exception {
        assertTrue(reader.isProcedureParameters(Hoge.class));
    }

    public void testGetProcedureParameter() throws Exception {
        PropertyDesc pd = beanDesc.getPropertyDesc("aaa");
        ProcedureParameterType ppt = reader.getProcedureParameter(beanDesc, pd);
        assertEquals("hoge", ppt.getParameterName());
        assertTrue(ppt.isOutType());
        assertEquals(new Integer(1), ppt.getIndex());
    }

    public void testGetProcedureParameter_default() throws Exception {
        PropertyDesc pd = beanDesc.getPropertyDesc("bbb");
        ProcedureParameterType ppt = reader.getProcedureParameter(beanDesc, pd);
        assertEquals("bbb", ppt.getParameterName());
        assertTrue(ppt.isInType());
        assertEquals(null, ppt.getIndex());
    }

    public void testGetProcedureParameter_invalidType() throws Exception {
        PropertyDesc pd = beanDesc.getPropertyDesc("ccc");
        try {
            reader.getProcedureParameter(beanDesc, pd);
            fail();
        } catch (RuntimeException ignore) {
        }
    }

    public void testGetProcedureParameter_invalidIndex() throws Exception {
        PropertyDesc pd = beanDesc.getPropertyDesc("ddd");
        try {
            reader.getProcedureParameter(beanDesc, pd);
            fail();
        } catch (RuntimeException ignore) {
        }
    }

    public void testGetValueType() throws Exception {
        PropertyDesc pd = beanDesc.getPropertyDesc("aaa");
        assertEquals("hogeValueType", reader.getValueType(beanDesc, pd));
    }

    public static class Hoge {

        public static final String PROCEDURE_PARAMETERS = null;

        public static final String aaa_PROCEDURE_PARAMETER = "name=hoge, type=out, index=1";

        public static final String ccc_PROCEDURE_PARAMETER = "type=x";

        public static final String ddd_PROCEDURE_PARAMETER = "index=x";

        public static final String aaa_VALUE_TYPE = "hogeValueType";

        private String aaa;

        private String bbb;

        private String ccc;

        private String ddd;

        public String getAaa() {
            return aaa;
        }

        public void setAaa(String aaa) {
            this.aaa = aaa;
        }

        public String getBbb() {
            return bbb;
        }

        public void setBbb(String bbb) {
            this.bbb = bbb;
        }

        public String getCcc() {
            return ccc;
        }

        public void setCcc(String ccc) {
            this.ccc = ccc;
        }

        public String getDdd() {
            return ddd;
        }

        public void setDdd(String ddd) {
            this.ddd = ddd;
        }

    }
}
