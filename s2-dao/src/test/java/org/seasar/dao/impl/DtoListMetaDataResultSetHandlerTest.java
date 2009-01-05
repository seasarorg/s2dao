/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
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
import java.util.List;

import org.seasar.dao.RowCreator;
import org.seasar.dao.unit.S2DaoTestCase;
import org.seasar.extension.jdbc.ResultSetHandler;

/**
 * @author jflute
 */
public class DtoListMetaDataResultSetHandlerTest extends S2DaoTestCase {

    public void testHandle() throws Exception {
        ResultSetHandler handler = new DtoListMetaDataResultSetHandler(
                createDtoMetaData(EmployeeDto.class), createRowCreator());
        String sql = "select empno, ename, dname from emp, dept where empno = 7788 and emp.deptno = dept.deptno";
        Connection con = getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        List ret = null;
        try {
            ResultSet rs = ps.executeQuery();
            try {
                ret = (List) handler.handle(rs);
            } finally {
                rs.close();
            }
        } finally {
            ps.close();
        }
        assertNotNull(ret);
        assertEquals(1, ret.size());
        EmployeeDto dto = (EmployeeDto) ret.get(0);
        assertEquals(7788, dto.getEmpno());
        assertEquals("SCOTT", dto.getEname());
        assertEquals("RESEARCH", dto.getDname());
    }

    public void testHandle2() throws Exception {
        ResultSetHandler handler = new DtoListMetaDataResultSetHandler(
                createDtoMetaData(EmployeeDto3.class), createRowCreator());
        String sql = "select employee_id, employee_name from emp4 where employee_id = 7369";
        Connection con = getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        List ret = null;
        try {
            ResultSet rs = ps.executeQuery();
            try {
                ret = (List) handler.handle(rs);
            } finally {
                rs.close();
            }
        } finally {
            ps.close();
        }
        assertNotNull(ret);
        assertEquals(1, ret.size());
        EmployeeDto3 dto = (EmployeeDto3) ret.get(0);
        assertEquals(7369, dto.getEmployeeId());
        assertEquals("SMITH", dto.getEmployeeName());
    }

    protected RowCreator createRowCreator() {// [DAO-118] (2007/08/25)
        return new RowCreatorImpl();
    }
    
    public void setUp() {
        include("j2ee.dicon");
    }
}