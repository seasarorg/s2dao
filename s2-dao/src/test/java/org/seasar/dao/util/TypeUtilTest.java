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
package org.seasar.dao.util;

import java.lang.reflect.Field;
import java.util.Map;

import junit.framework.TestCase;

/**
 * @author taedium
 * 
 */
public class TypeUtilTest extends TestCase {

    public void testIsSimpleType() throws Exception {
        assertFalse(TypeUtil.isSimpleType(Map.class));
        assertTrue(TypeUtil.isSimpleType(int.class));
    }

    public void testGetDeclaredFields() throws Exception {
        Field[] fields = TypeUtil.getDeclaredFields(TestClass.class);
        assertEquals(5, fields.length);
        assertEquals("aaa", fields[0].getName());
        assertEquals("bbb", fields[1].getName());
        assertEquals("ccc", fields[2].getName());
        assertEquals("ddd", fields[3].getName());
        assertEquals("eee", fields[4].getName());
    }

    public static class TestClass {
        int aaa;

        int bbb;

        int ccc;

        int ddd;

        int eee;
    }
}
