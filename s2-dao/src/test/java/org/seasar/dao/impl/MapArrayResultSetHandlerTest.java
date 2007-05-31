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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

import org.seasar.dao.unit.S2DaoTestCase;
import org.seasar.extension.jdbc.ResultSetHandler;

public class MapArrayResultSetHandlerTest extends S2DaoTestCase {

    public void testHandle() throws Exception {
        ResultSetHandler handler = new MapArrayResultSetHandler();
        String sql = "select employee_id, employee_name from emp4 where employee_id = 7369";
        Connection con = getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        Map[] ret = null;
        try {
            ResultSet rs = ps.executeQuery();
            try {
                ret = (Map[]) handler.handle(rs);
            } finally {
                rs.close();
            }
        } finally {
            ps.close();
        }
        assertNotNull(ret);
        assertEquals(1, ret.length);
        Map m = ret[0];
        assertEquals(new Integer(7369), m.get("employeeId"));
        assertEquals("SMITH", m.get("employeeName"));
    }

    public void setUp() {
        include("j2ee.dicon");
    }
}