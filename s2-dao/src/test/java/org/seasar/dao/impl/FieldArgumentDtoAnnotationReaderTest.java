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

import java.lang.reflect.Field;

import junit.framework.TestCase;

import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.factory.BeanDescFactory;

/**
 * @author taedium
 *
 */
public class FieldArgumentDtoAnnotationReaderTest extends TestCase {

    private FieldArgumentDtoAnnotationReader reader = new FieldArgumentDtoAnnotationReader();

    private BeanDesc beanDesc = BeanDescFactory.getBeanDesc(Hoge.class);

    public void testGetProcedureParameter() throws Exception {
        Field field = beanDesc.getField("aaa");
        String value = reader.getProcedureParameter(beanDesc, field);
        assertEquals("in", value);
    }

    public void testGetProcedureParameter_none() throws Exception {
        Field field = beanDesc.getField("bbb");
        String value = reader.getProcedureParameter(beanDesc, field);
        assertNull(value);
    }

    public void testGetProcedureParameter_public() throws Exception {
        Field field = beanDesc.getField("ccc");
        String value = reader.getProcedureParameter(beanDesc, field);
        assertEquals("out", value);
    }

    public void testGetValueType() throws Exception {
        Field field = beanDesc.getField("aaa");
        assertEquals("hogeValueType", reader.getValueType(beanDesc, field));
    }

    public static class Hoge {

        public static final String PROCEDURE_PARAMETERS = null;

        public static final String aaa_VALUE_TYPE = "hogeValueType";

        public static final String aaa_PROCEDURE_PARAMETER = "in";

        public static final String ccc_PROCEDURE_PARAMETER = "out";

        private String aaa;

        private String bbb;

        public String ccc;
    }
}
