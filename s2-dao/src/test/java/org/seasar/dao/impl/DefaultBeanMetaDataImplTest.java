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

import java.sql.Timestamp;


/**
 * @author higa
 * 
 */
public class DefaultBeanMetaDataImplTest extends BeanMetaDataImplTest {

    public void setUp() {
        include("DefaultBeanMetaDataTest.dicon");
    }

    protected Class getBeanClass(String className) {
        if (className.equals("MyBean")) {
            return MyBean.class;
        } else if (className.equals("Employee")) {
            return Employee.class;
        } else if (className.equals("Department")) {
            return Department.class;
        } else if (className.equals("Employee4")) {
            return Employee4.class;
        } else if (className.equals("Ddd")) {
            return Ddd.class;
        } else if (className.equals("Eee")) {
            return Eee.class;
        } else if (className.equals("Fff")) {
            return Fff.class;
        } else if (className.equals("IdentityTable")) {
            return IdentityTable.class;
        }
        return null;
    }

    public static class MyBean {
        public static final String TABLE = "MyBean";

        public static final String aaa_ID = "assigned";

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
        public static final String id_ID = "assigned";

        private Integer id;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }
    }

    public static class Ddd extends Ccc {
        public static final String NO_PERSISTENT_PROPS = "";

        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class Eee extends Ccc {
        public static final String NO_PERSISTENT_PROPS = "name";

        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class Fff {
        public static final String VERSION_NO_PROPERTY = "version";

        public static final String TIMESTAMP_PROPERTY = "updated";

        private int version;

        private Integer id;

        private Timestamp updated;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public int getVersion() {
            return version;
        }

        public void setVersion(int version) {
            this.version = version;
        }

        public Timestamp getUpdated() {
            return updated;
        }

        public void setUpdated(Timestamp updated) {
            this.updated = updated;
        }
    }
}