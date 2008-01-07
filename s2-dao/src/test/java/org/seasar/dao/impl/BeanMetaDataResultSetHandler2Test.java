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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.seasar.dao.BeanMetaData;
import org.seasar.dao.RelationRowCreator;
import org.seasar.dao.RowCreator;
import org.seasar.dao.unit.S2DaoTestCase;
import org.seasar.extension.jdbc.ResultSetHandler;

public class BeanMetaDataResultSetHandler2Test extends S2DaoTestCase {

    private BeanMetaData beanMetaData;

    public void testHandle() throws Exception {
        ResultSetHandler handler = new BeanMetaDataResultSetHandler(
                beanMetaData, createRowCreator(), createRelationRowCreator());
        String sql = "select empno, dept.dname as d_name from emp, dept where empno = 7788 and emp.deptno = dept.deptno";
        Connection con = getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        MyEmp ret = null;
        try {
            ResultSet rs = ps.executeQuery();
            try {
                ret = (MyEmp) handler.handle(rs);
            } finally {
                rs.close();
            }
        } finally {
            ps.close();
        }
        assertNotNull("1", ret);
        System.out.println(ret.getEmpno());
        assertEquals("2", "RESEARCH", ret.getDname());
    }

    protected RowCreator createRowCreator() {// [DAO-118] (2007/08/25)
        return new RowCreatorImpl();
    }

    protected RelationRowCreator createRelationRowCreator() {
        return new RelationRowCreatorImpl();
    }

    public void setUp() {
        include("j2ee.dicon");
    }

    protected void setUpAfterBindFields() throws Throwable {
        super.setUpAfterBindFields();
        beanMetaData = createBeanMetaData(MyEmp.class);
    }

    public static class MyEmp {
        private int empno;

        private String dname;

        /**
         * @return Returns the dname.
         */
        public String getDname() {
            return dname;
        }

        /**
         * @param dname
         *            The dname to set.
         */
        public void setDname(String dname) {
            this.dname = dname;
        }

        /**
         * @return Returns the empno.
         */
        public int getEmpno() {
            return empno;
        }

        /**
         * @param empno
         *            The empno to set.
         */
        public void setEmpno(int empno) {
            this.empno = empno;
        }
    }
}