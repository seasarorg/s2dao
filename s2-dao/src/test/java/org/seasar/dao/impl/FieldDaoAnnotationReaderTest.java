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

import java.util.List;

import org.seasar.dao.DaoMetaDataFactory;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.factory.BeanDescFactory;

/**
 * @author manhole
 */
public class FieldDaoAnnotationReaderTest extends
        AbstractDaoAnnotationReaderImplTest {

    protected void setUp() throws Exception {
        super.setUp();
        clazz = AbstractAaaDaoImpl.class;
        BeanDesc daoDesc = BeanDescFactory
                .getBeanDesc(AbstractAaaDaoImpl.class);
        annotationReader = new FieldDaoAnnotationReader(daoDesc);
        aaaClazz = Aaa.class;
        daoClazz = AaaDao.class;
    }

    public void testIsSingleValueType() throws Exception {
        assertTrue(((FieldDaoAnnotationReader) annotationReader)
                .isSingleValueType(void.class));
    }

    public static interface AaaDao {

        public Class BEAN = Aaa.class;

        public boolean CHECK_SINGLE_ROW_UPDATE = false;

        public String getAaaById1_ARGS = "aaa1, aaa2";

        public Aaa getAaaById1(int id);

        public String getAaaById2_QUERY = "A > B";

        public Aaa getAaaById2(int id);

        public String getAaaById3_SQL = "SELECT * FROM AAA";

        public Aaa getAaaById3(int id);

        public Class findAll_BEAN = Aaa.class;

        public List findAll();

        public Aaa[] findArray();

        public Aaa find(int id);

        public String createAaa1_NO_PERSISTENT_PROPS = "abc";

        public Aaa createAaa1(Aaa aaa);

        public String createAaa2_PERSISTENT_PROPS = "def";

        public Aaa createAaa2(Aaa aaa);

        public boolean createAaa3_CHECK_SINGLE_ROW_UPDATE = false;

        public int createAaa3(Aaa aaa);

        public String selectB_SQL = "SELECT * FROM DDD";

        public String selectB_oracle_SQL = "SELECT * FROM BBB";

        public Aaa selectB(int id);

        public String selectC_oracle_SQL = "SELECT * FROM CCC";

        public Aaa selectC(int id);

        public String findUsingSqlFile_SQL_FILE = null;

        public Aaa findUsingSqlFile(int id);

        public String execute_PROCEDURE_CALL = "hoge";

        public void execute();
    }

    public static interface Aaa2Dao extends AaaDao {
    }

    public static abstract class AbstractAaaDaoImpl extends AbstractDao
            implements Aaa2Dao {

        public AbstractAaaDaoImpl(DaoMetaDataFactory daoMetaDataFactory) {
            super(daoMetaDataFactory);
        }

    }

    public static class Aaa {
    }

}
