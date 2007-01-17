/*
 * Copyright 2004-2006 the Seasar Foundation and the Others.
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

/**
 * @author manhole
 */
public class BeanEnhancerImplTest extends TestCase {

    public void testEquals() throws Exception {
        assertEquals(true, BeanEnhancerImpl.EqualsUtil.equals(null, null));
        assertEquals(true, BeanEnhancerImpl.EqualsUtil.equals("12", "12"));
        assertEquals(true, BeanEnhancerImpl.EqualsUtil.equals(
                new Integer("12"), new Integer("12")));

        assertEquals(false, BeanEnhancerImpl.EqualsUtil.equals(null, "1"));
        assertEquals(false, BeanEnhancerImpl.EqualsUtil.equals("1", null));
        assertEquals(false, BeanEnhancerImpl.EqualsUtil.equals(
                new Integer("12"), "12"));

        // long
        assertEquals(true, BeanEnhancerImpl.EqualsUtil.equals(123L, 123L));
        assertEquals(false, BeanEnhancerImpl.EqualsUtil.equals(123L, 124L));

        // int
        assertEquals(true, BeanEnhancerImpl.EqualsUtil.equals(12, 12));
        assertEquals(false, BeanEnhancerImpl.EqualsUtil.equals(13, 14));

        // short
        assertEquals(true, BeanEnhancerImpl.EqualsUtil.equals((short) 12,
                (short) 12));
        assertEquals(false, BeanEnhancerImpl.EqualsUtil.equals((short) 13,
                (short) -13));

        // char
        assertEquals(true, BeanEnhancerImpl.EqualsUtil.equals((char) 1243,
                (char) 1243));
        assertEquals(false, BeanEnhancerImpl.EqualsUtil.equals((char) 13,
                (char) -13));

        // byte
        assertEquals(true, BeanEnhancerImpl.EqualsUtil.equals((byte) 43,
                (byte) 43));
        assertEquals(false, BeanEnhancerImpl.EqualsUtil.equals((byte) 13,
                (byte) -13));

        // boolean
        assertEquals(true, BeanEnhancerImpl.EqualsUtil.equals(true, true));
        assertEquals(false, BeanEnhancerImpl.EqualsUtil.equals(true, false));

        // double
        assertEquals(true, BeanEnhancerImpl.EqualsUtil.equals(4300.55, 4300.55));
        assertEquals(false, BeanEnhancerImpl.EqualsUtil.equals(13.2, -13.2));

        // float
        assertEquals(true, BeanEnhancerImpl.EqualsUtil.equals(43.55F, 43.55F));
        assertEquals(false, BeanEnhancerImpl.EqualsUtil.equals(13.2F, -13.2F));
    }

}
