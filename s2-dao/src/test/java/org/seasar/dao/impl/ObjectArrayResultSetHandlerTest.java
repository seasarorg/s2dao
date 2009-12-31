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
package org.seasar.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.seasar.extension.jdbc.ResultSetHandler;
import org.seasar.extension.unit.S2TestCase;

/**
 * @author taedium
 * 
 */
public class ObjectArrayResultSetHandlerTest extends S2TestCase {

    public void setUp() {
        include("j2ee.dicon");
    }

    /**
     * @throws Exception
     */
    public void testHandle() throws Exception {
        ResultSetHandler handler = new ObjectArrayResultSetHandler(
                Integer.class);
        String sql = "select empno from emp";
        Connection con = getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        Integer[] ret = null;
        try {
            ResultSet rs = ps.executeQuery();
            try {
                ret = (Integer[]) handler.handle(rs);
            } finally {
                rs.close();
            }
        } finally {
            ps.close();
        }
        assertEquals(14, ret.length);
    }
}
