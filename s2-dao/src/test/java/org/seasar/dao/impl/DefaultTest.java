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

import java.io.Serializable;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.seasar.extension.jdbc.impl.MapListResultSetHandler;
import org.seasar.extension.jdbc.util.DatabaseMetaDataUtil;
import org.seasar.extension.unit.S2TestCase;
import org.seasar.framework.exception.SRuntimeException;

/**
 * @author manhole
 */
public class DefaultTest extends S2TestCase {

    private DefaultTableDao defaultTableDao;

    private DefaultTable2Dao defaultTable2Dao;

    protected void setUp() throws Exception {
        super.setUp();
        include("DefaultTest.dicon");
    }

    public void testMetaDataForColumnsTx() throws Exception {
        final DatabaseMetaData metaData = getConnection().getMetaData();
        String userName = metaData.getUserName();
        userName = DatabaseMetaDataUtil.convertIdentifier(metaData, userName);
        final ResultSet rset = metaData.getColumns(null, userName,
                "DEFAULT_TABLE", null);
        final ResultSetMetaData rMeta = rset.getMetaData();
        final int columnCount = rMeta.getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
            final String columnName = rMeta.getColumnName(i);
            System.out.println("[" + i + "] " + columnName);
        }
        MapListResultSetHandler handler = new MapListResultSetHandler();
        List l = (List) handler.handle(rset);
        for (Iterator it = l.iterator(); it.hasNext();) {
            Map m = (Map) it.next();
            System.out.println(m);
        }
    }

    public void testLearningGetDefaultValueTx() throws Exception {
        final DatabaseMetaData metaData = getConnection().getMetaData();
        String userName = DatabaseMetaDataUtil.convertIdentifier(metaData,
                metaData.getUserName());
        final ResultSet rset = metaData.getColumns(null, userName,
                "DEFAULT_TABLE", null);

        int[] columns = { 0, 0, 0 };
        while (rset.next()) {
            final String columnName = rset.getString("COLUMN_NAME");
            final String columnDef = rset.getString("COLUMN_DEF");
            if ("ID".equals(columnName)) {
                columns[0]++;
                assertEquals((String) null, columnDef);
            } else if ("DEFAULT_COLUMN".equals(columnName)) {
                columns[1]++;
                //assertEquals("'ABC'", columnDef);
                assertEquals(columnDef, true, columnDef.indexOf("ABC") > -1);
            } else if ("VERSIONNO".equals(columnName)) {
                columns[2]++;
                assertEquals((String) null, columnDef);
            } else {
                fail(columnName);
            }
        }
        assertEquals(1, columns[0]);
        assertEquals(1, columns[1]);
        assertEquals(1, columns[2]);
    }

    public void testInsertByAutoSqlTx() throws Exception {
        {
            DefaultTable bean = new DefaultTable();
            bean.setId(new Integer(345));
            bean.setDefaultColumn("1234567");
            defaultTableDao.insert(bean);
        }
        {
            final DefaultTable bean = defaultTableDao.getDefaultTable(345);
            assertEquals("1234567", bean.getDefaultColumn());
            assertEquals(new Integer(0), bean.getVersionNo());
        }
    }

    public void testInsertDefaultByAutoSqlTx() throws Exception {
        {
            DefaultTable bean = new DefaultTable();
            bean.setId(new Integer(334));
            defaultTableDao.insert(bean);
        }
        {
            final DefaultTable bean = defaultTableDao.getDefaultTable(334);
            assertEquals("ABC", bean.getDefaultColumn());
            assertEquals(new Integer(0), bean.getVersionNo());
        }
    }

    public void testInsertByAutoSql2Tx() throws Exception {
        Integer id;
        {
            DefaultTable2 bean = new DefaultTable2();
            bean.setDefaultColumn("bar");
            defaultTable2Dao.insert(bean);
            id = bean.getId();
        }
        System.out.println("id=" + id);
        {
            final DefaultTable2 bean = defaultTable2Dao.getDefaultTable(id);
            assertEquals("bar", bean.getDefaultColumn());
            assertEquals(new Integer(0), bean.getVersionNo());
        }
    }

    public void testThrownExceptionWhenOnlyNullDataTx() throws Exception {
        DefaultTable bean = new DefaultTable();
        try {
            defaultTableDao.insert(bean);
            fail();
        } catch (SRuntimeException e) {
            e.printStackTrace();
            assertEquals("EDAO0014", e.getMessageCode());
        }
    }

    public void testThrownExceptionWhenOnlyNullData2Tx() throws Exception {
        DefaultTable2 bean = new DefaultTable2();
        try {
            defaultTable2Dao.insert(bean);
            fail();
        } catch (SRuntimeException e) {
            e.printStackTrace();
            assertEquals("EDAO0014", e.getMessageCode());
        }
    }

    public void testInsertByManualSqlTx() throws Exception {
        {
            DefaultTable bean = new DefaultTable();
            bean.setId(new Integer(101));
            bean.setDefaultColumn("foooo");
            defaultTableDao.insertBySql(bean);
        }
        {
            final DefaultTable bean = defaultTableDao.getDefaultTable(101);
            assertEquals("foooo", bean.getDefaultColumn());
            //assertEquals(new Integer(0), bean.getVersionNo());
        }
    }

    public void testInsertDefaultByManualSqlTx() throws Exception {
        {
            DefaultTable bean = new DefaultTable();
            bean.setId(new Integer(7710));
            defaultTableDao.insertBySql(bean);
        }
        {
            final DefaultTable bean = defaultTableDao.getDefaultTable(7710);
            assertEquals("ABC", bean.getDefaultColumn());
            //assertEquals(new Integer(0), bean.getVersionNo());
        }
    }

    public static interface DefaultTableDao {

        public Class BEAN = DefaultTable.class;

        public String getDefaultTable_ARGS = "id";

        public DefaultTable getDefaultTable(int id);

        public void insert(DefaultTable largeBinary);

        public void insertBySql(DefaultTable largeBinary);

        public void update(DefaultTable largeBinary);

    }

    public static interface DefaultTable2Dao {

        public Class BEAN = DefaultTable2.class;

        public String getDefaultTable_ARGS = "id";

        public DefaultTable2 getDefaultTable(Integer id);

        public void insert(DefaultTable2 largeBinary);

        public void insertBySql(DefaultTable2 largeBinary);

        public void update(DefaultTable2 largeBinary);

    }

    public static class DefaultTable implements Serializable {

        private static final long serialVersionUID = 1L;

        public static final String TABLE = "DEFAULT_TABLE";

        private Integer id;

        private String defaultColumn;

        private Integer versionNo;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getDefaultColumn() {
            return defaultColumn;
        }

        public void setDefaultColumn(String defaultColumn) {
            this.defaultColumn = defaultColumn;
        }

        public Integer getVersionNo() {
            return versionNo;
        }

        public void setVersionNo(Integer versionNo) {
            this.versionNo = versionNo;
        }
    }

    public static class DefaultTable2 implements Serializable {

        private static final long serialVersionUID = 1L;

        public static final String TABLE = "DEFAULT_TABLE2";

        public static final String id_ID = "identity";

        private Integer id;

        private String defaultColumn;

        private Integer versionNo;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getDefaultColumn() {
            return defaultColumn;
        }

        public void setDefaultColumn(String defaultColumn) {
            this.defaultColumn = defaultColumn;
        }

        public Integer getVersionNo() {
            return versionNo;
        }

        public void setVersionNo(Integer versionNo) {
            this.versionNo = versionNo;
        }
    }

}
