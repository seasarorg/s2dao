package org.seasar.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.seasar.dao.BeanMetaData;
import org.seasar.dao.unit.S2DaoTestCase;
import org.seasar.extension.jdbc.ResultSetHandler;

public class BeanArrayMetaDataResultSetHandlerTest extends S2DaoTestCase {

    private BeanMetaData beanMetaData_;

    public BeanArrayMetaDataResultSetHandlerTest(String arg0) {
        super(arg0);
    }

    public void testHandle() throws Exception {
        ResultSetHandler handler = new BeanArrayMetaDataResultSetHandler(
                beanMetaData_);
        String sql = "select * from emp";
        Connection con = getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        Employee[] ret = null;
        try {
            ResultSet rs = ps.executeQuery();
            try {
                ret = (Employee[]) handler.handle(rs);
            } finally {
                rs.close();
            }
        } finally {
            ps.close();
        }
        assertNotNull("1", ret);
        for (int i = 0; i < ret.length; ++i) {
            Employee emp = ret[i];
            System.out.println(emp.getEmpno() + "," + emp.getEname());
        }
    }

    public void setUp() {
        include("j2ee.dicon");
    }

    protected void setUpAfterContainerInit() throws Throwable {
        super.setUpAfterContainerInit();
        beanMetaData_ = new BeanMetaDataImpl(Employee.class,
                getDatabaseMetaData(), getDbms());
    }

    public static void main(String[] args) {
        junit.textui.TestRunner
                .run(BeanArrayMetaDataResultSetHandlerTest.class);
    }

}