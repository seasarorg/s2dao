/*
 * Copyright 2004-2010 the Seasar Foundation and the Others.
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
package org.seasar.dao.dbms;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.seasar.dao.Dbms;
import org.seasar.dao.unit.S2DaoTestCase;
import org.seasar.extension.jdbc.impl.BasicSelectHandler;
import org.seasar.extension.jdbc.impl.ObjectResultSetHandler;

/**
 * @author manhole
 */
public class H2Test extends S2DaoTestCase {

    protected void setUp() throws Exception {
        super.setUp();
        include("j2ee-h2.dicon");
    }

    public void test1() throws Exception {
        final Connection con = getConnection();

        final Statement stmt = con.createStatement();
        stmt.executeUpdate("DROP TABLE IF EXISTS H2TEST");
        stmt
                .executeUpdate("CREATE TABLE H2TEST (ID INT PRIMARY KEY, AAA VARCHAR(255))");
        stmt.executeUpdate("INSERT INTO H2TEST VALUES (1, 'z')");
        stmt.executeUpdate("INSERT INTO H2TEST VALUES (2, 'y')");
        stmt.executeUpdate("INSERT INTO H2TEST VALUES (3, 'x')");

        {
            final ResultSet rset = stmt
                    .executeQuery("SELECT COUNT(*) FROM H2TEST");
            assertEquals(true, rset.next());
            assertEquals(1, rset.getMetaData().getColumnCount());
            assertEquals(3L, rset.getLong(1));
            assertEquals(false, rset.next());
            rset.close();
        }
        {
            final ResultSet rset = stmt
                    .executeQuery("SELECT ID, AAA FROM H2TEST ORDER BY 2 ASC");
            assertEquals(2, rset.getMetaData().getColumnCount());

            assertEquals(true, rset.next());
            assertEquals("3", rset.getString(1));
            assertEquals("x", rset.getString(2));

            assertEquals(true, rset.next());
            assertEquals("2", rset.getString(1));
            assertEquals("y", rset.getString(2));

            assertEquals(true, rset.next());
            assertEquals("1", rset.getString(1));
            assertEquals("z", rset.getString(2));

            assertEquals(false, rset.next());
        }

        stmt.close();
        con.close();
    }

    public void testSequence() throws Exception {
        // ## Arrange ##
        final Connection con = getConnection();
        final Statement stmt = con.createStatement();
        stmt.executeUpdate("DROP SEQUENCE IF EXISTS H2TEST_SEQ");
        stmt
                .executeUpdate("CREATE SEQUENCE H2TEST_SEQ START WITH 7650 INCREMENT BY 1");
        stmt.close();
        final Dbms dbms = DbmsManager.getDbms(getDataSource());
        assertEquals(true, dbms instanceof H2);

        // ## Act ##
        // ## Assert ##
        final String sequenceNextValString = dbms
                .getSequenceNextValString("H2TEST_SEQ");
        final BasicSelectHandler nextvalHandler = new BasicSelectHandler(
                getDataSource(), sequenceNextValString,
                new ObjectResultSetHandler());
        {
            final Number nextval = (Number) nextvalHandler.execute(null);
            assertEquals(7650, nextval.intValue());
        }
        {
            final Number nextval = (Number) nextvalHandler.execute(null);
            assertEquals(7651, nextval.intValue());
        }
        {
            final Number nextval = (Number) nextvalHandler.execute(null);
            assertEquals(7652, nextval.intValue());
        }

        final String identitySelectString = dbms.getIdentitySelectString();
        final BasicSelectHandler identityHandler = new BasicSelectHandler(
                getDataSource(), identitySelectString,
                new ObjectResultSetHandler());
        {
            final Number currval = (Number) identityHandler.execute(null);
            assertEquals(7652, currval.intValue());
        }
        {
            final Number currval = (Number) identityHandler.execute(null);
            assertEquals(7652, currval.intValue());
        }
        {
            nextvalHandler.execute(null);
            final Number currval = (Number) identityHandler.execute(null);
            assertEquals(7653, currval.intValue());
        }

    }

}
