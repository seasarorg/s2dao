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
package org.seasar.dao.util;

import junit.framework.TestCase;

/**
 * @author higa
 *
 */
public class DaoNamingConventionUtilTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * Test method for {@link org.seasar.dao.util.DaoNamingConventionUtil#decamelize(java.lang.String)}.
     */
    public void testDecamelize() {
        assertNull(DaoNamingConventionUtil.decamelize(null));
        assertEquals("EMP", DaoNamingConventionUtil.decamelize("Emp"));
        assertEquals("AAA_BBB", DaoNamingConventionUtil.decamelize("aaaBbb"));
        assertEquals("AAA_BBB", DaoNamingConventionUtil.decamelize("AaaBbb"));
        assertEquals("AAA_BBB_C", DaoNamingConventionUtil.decamelize("aaaBbbC"));
    }

}
