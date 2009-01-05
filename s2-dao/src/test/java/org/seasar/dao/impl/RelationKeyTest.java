/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
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
 * @author higa
 * 
 */
public class RelationKeyTest extends TestCase {

    protected void tearDown() throws Exception {
    }

    public void testEquals() throws Exception {
        Object[] values = new Object[] { "1", "2" };
        RelationKey pk = new RelationKey(values);
        assertEquals("1", pk, pk);
        assertEquals("2", pk, new RelationKey(values));
        assertEquals("3", false, new RelationKey(new Object[] { "1" })
                .equals(pk));
    }

    public void testHashCode() throws Exception {
        Object[] values = new Object[] { "1", "2" };
        RelationKey pk = new RelationKey(values);
        assertEquals("1", "1".hashCode() + "2".hashCode(), pk.hashCode());
    }

    public static class MyBean {
        public static final String TABLE = "MyBean";

        public static final String bbb_COLUMN = "myBbb";

        public static final int ccc_RELNO = 0;

        public static final String ccc_RELKEYS = "ddd:id";

        private Integer aaa;

        private String bbb;

        private Ccc ccc;

        private Integer ddd;

        public Integer getAaa() {
            return aaa;
        }

        public void setAaa(Integer aaa) {
            this.aaa = aaa;
        }

        public String getBbb() {
            return bbb;
        }

        public void setBbb(String bbb) {
            this.bbb = bbb;
        }

        public Ccc getCcc() {
            return ccc;
        }

        public void setCcc(Ccc ccc) {
            this.ccc = ccc;
        }

        public Integer getDdd() {
            return ddd;
        }

        public void setDdd(Integer ddd) {
            this.ddd = ddd;
        }
    }

    public static class Ccc {
        private Integer id;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }
    }
}