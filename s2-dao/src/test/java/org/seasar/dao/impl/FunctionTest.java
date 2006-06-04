package org.seasar.dao.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;

import javax.sql.DataSource;

import junit.framework.TestCase;

import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.factory.S2ContainerFactory;

public class FunctionTest extends TestCase {

    public void testCallFunctionDirectory() throws Exception {
        final S2Container container = S2ContainerFactory
                .create("dao-derby.dicon");

        DataSource dataSource = (DataSource) container
                .getComponent(DataSource.class);
        Connection conn = dataSource.getConnection();

        CallableStatement cstmt = conn
                .prepareCall("{? = call FUNCTION_TEST_MAX(?, ?)}");
        cstmt.registerOutParameter(1, Types.DOUBLE);
        cstmt.setDouble(2, 2.5);
        cstmt.setDouble(3, 10.3);

        cstmt.execute();

        double result = cstmt.getDouble(1);
        assertEquals(10.3d, result, 0);
    }

    // Apache Derby does'nt returns FUNCTION from
    // DatabaseMetaData#getProcedures.
    public void pending_testCallFunctionViaDao() throws Exception {
        final S2Container container = S2ContainerFactory.create(getClass()
                .getName().replace('.', '/')
                + ".dicon");

        FunctionDao dao = (FunctionDao) container
                .getComponent(FunctionDao.class);

        double result = dao.max(10.0, 20.0);
        assertEquals(20.0, result, 0);
    }

    public static interface FunctionDao {

        public static Class BEAN = DummyBean.class;

        public String max_PROCEDURE = "FUNCTION_TEST_MAX";

        public double max(double a, double b);

    }

    public static class DummyBean {
    }

}
