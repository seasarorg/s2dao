package org.seasar.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.seasar.dao.BeanMetaData;
import org.seasar.dao.unit.S2DaoTestCase;
import org.seasar.extension.jdbc.ResultSetHandler;

public class BeanMetaDataResultSetHandler2Test extends S2DaoTestCase {

    private BeanMetaData beanMetaData_;

    public BeanMetaDataResultSetHandler2Test(String arg0) {
        super(arg0);
    }

    public void testHandle() throws Exception {
        ResultSetHandler handler = new BeanMetaDataResultSetHandler(
                beanMetaData_);
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

    public void setUp() {
        include("j2ee.dicon");
    }

    protected void setUpAfterContainerInit() throws Throwable {
        super.setUpAfterContainerInit();
        beanMetaData_ = new BeanMetaDataImpl(MyEmp.class,
                getDatabaseMetaData(), getDbms());
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(BeanMetaDataResultSetHandler2Test.class);
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