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

import java.util.Date;

import junit.framework.AssertionFailedError;

import org.seasar.dao.BeanAnnotationReader;

/**
 * publicフィールドのテスト
 */
public class FieldBeanAnnotationReaderPublicFieldTest extends
        AbstractBeanAnnotationReaderTest {

    protected BeanAnnotationReader createBeanAnnotationReader(Class clazz) {
        return new FieldBeanAnnotationReader(clazz);
    }

    protected Class getBeanClass(String className) {
        if ("AnnotationTestBean1".equals(className)) {
            return AnnotationTestBean1.class;
        } else if ("AnnotationTestBean2".equals(className)) {
            return AnnotationTestBean2.class;
        } else if ("AnnotationTestBean3".equals(className)) {
            return AnnotationTestBean3.class;
        } else if ("AnnotationTestBean4".equals(className)) {
            return AnnotationTestBean4.class;
        } else if ("AnnotationTestBean5".equals(className)) {
            return AnnotationTestBean5.class;
        } else if ("AnnotationTestBean6".equals(className)) {
            return AnnotationTestBean6.class;
        }
        throw new AssertionFailedError(className);
    }

    public static class AnnotationTestBean1 {

        public static String TABLE = "TABLE";

        public static final String NO_PERSISTENT_PROPS = "prop2";

        public static final String TIMESTAMP_PROPERTY = "myTimestamp";

        public static final String VERSION_NO_PROPERTY = "myVersionNo";

        public static final String prop1_ID = "sequence, sequenceName=myseq";

        public static String prop1_COLUMN = "Cprop1";

        public int prop1;

        public int prop2;

        public Date myTimestamp;

        public static final int department_RELNO = 0;

        public static final String department_RELKEYS = "DEPTNUM:DEPTNO";

        public Department department;

    }

    public static class AnnotationTestBean2 {

        public static String prop1_COLUMN = "Cprop1";

        public int prop1;

        public int prop2;

    }

    public static class AnnotationTestBean3 {

        public String aaa;

        public String bbb;

        public static String bbb_VALUE_TYPE = "fooType";

    }

    public static class AnnotationTestBean4 {

        public String aaa;

        public String bbb;

        public static final String aaa_oracle_ID = "identity";

        public static final String aaa_mysql_ID = "sequence, sequenceName=myseq";

        public static final String aaa_ID = "sequence, sequenceName=myseq_2";

    }

    public static class AnnotationTestBean5 {

        public String aaa;

        public String bbb;

        public static final String aaa_oracle_ID = "identity";

    }

    public static class AnnotationTestBean6 {

        public String aaa;

        public String bbb;

        public static final String aaa_ID = "identity";

    }
}
