package org.seasar.dao.handler;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.seasar.dao.impl.Employee;
import org.seasar.dao.unit.S2DaoTestCase;
import org.seasar.extension.jdbc.ResultSetHandler;
import org.seasar.extension.jdbc.impl.BeanListResultSetHandler;
import org.seasar.extension.jdbc.impl.MapListResultSetHandler;

/**
 * @author Satoshi Kimura
 * @author manhole
 */
public class ReturnsResultsetProcedureHandlerTest extends S2DaoTestCase {

    private DataSource dataSource;

    protected void setUp() {
        include("j2ee.dicon");
    }

    public void no_testExecuteConnectionObjectArray() {
        ResultSetHandler resultSetHandler = new BeanListResultSetHandler(
                Employee.class);
        ReturnsResultsetProcedureHandler handler = new ReturnsResultsetProcedureHandler(
                dataSource, "GET_EMP", resultSetHandler);

        Object[] args = { new Long(7499) };
        List list = (List) handler.execute(getConnection(), args);

        assertEquals("1", 1, list.size());
        Employee employee = (Employee) list.get(0);
        assertEquals("2", "ALLEN", employee.getEname());
    }

    public void testReturnResultsetProcedure() {
        ReturnsResultsetProcedureHandler handler = new ReturnsResultsetProcedureHandler(
                dataSource, "CURDATE", new ResultSetHandler() {

                    public Object handle(ResultSet rs) throws SQLException {
                        assertEquals(true, rs.next());
                        assertEquals(1, rs.getMetaData().getColumnCount());
                        Object ret = rs.getObject(1);
                        assertEquals(false, rs.next());
                        return ret;
                    }
                });
        handler.initialize();

        Object[] args = {};
        Object object = handler.execute(getConnection(), args);

        System.out.println(object);
        assertNotNull(object);
        assertEquals(true, object instanceof java.sql.Date);
    }

    public void testMetaDataForProcesures() throws Exception {
        final DatabaseMetaData metaData = getConnection().getMetaData();
        final ResultSet rset = metaData.getProcedures(null, null, null);
        MapListResultSetHandler handler = new MapListResultSetHandler();
        List l = (List) handler.handle(rset);
        for (Iterator it = l.iterator(); it.hasNext();) {
            Map m = (Map) it.next();
            System.out.println(m);
        }
    }

    public void testCallableStatement_CURDATE() throws Exception {
        // ## Arrange ##
        Connection conn = getConnection();
        CallableStatement cs = conn.prepareCall("{call CURDATE()}");

        // ## Act ##
        cs.execute();
        ResultSet rs = cs.getResultSet();

        // ## Assert ##
        assertEquals(true, rs.next());
        assertEquals(1, rs.getMetaData().getColumnCount());
        System.out.println(rs.getMetaData().getColumnLabel(1));
        System.out.println(rs.getMetaData().getColumnName(1));
        java.sql.Date object = (Date) rs.getObject(1);
        System.out.println(object);
        assertNotNull(object);
        assertEquals(false, rs.next());
    }

}
