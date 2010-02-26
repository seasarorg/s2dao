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

import org.seasar.dao.BeanMetaData;
import org.seasar.dao.NotSingleResultRuntimeException;
import org.seasar.dao.RelationRowCreator;
import org.seasar.dao.RowCreator;
import org.seasar.dao.impl.BeanMetaDataResultSetHandler.RestrictBeanMetaDataResultSetHandler;
import org.seasar.dao.unit.S2DaoTestCase;
import org.seasar.extension.jdbc.ResultSetHandler;

public class BeanMetaDataResultSetHandlerTest extends S2DaoTestCase {

    public void testHandle() throws Exception {
        BeanMetaData beanMetaData = createBeanMetaData(Employee.class);
        ResultSetHandler handler = new BeanMetaDataResultSetHandler(
                beanMetaData, createRowCreator(), createRelationRowCreator());
        String sql = "select emp.*, dept.deptno as deptno_0, dept.dname as dname_0 from emp, dept where empno = 7788 and emp.deptno = dept.deptno";
        Connection con = getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        Employee ret = null;
        try {
            ResultSet rs = ps.executeQuery();
            try {
                ret = (Employee) handler.handle(rs);
            } finally {
                rs.close();
            }
        } finally {
            ps.close();
        }
        assertNotNull("1", ret);
        System.out.println(ret.getEmpno() + "," + ret.getEname());
        Department dept = ret.getDepartment();
        assertNotNull("2", dept);
        assertEquals("3", 20, dept.getDeptno());
        assertEquals("4", "RESEARCH", dept.getDname());
    }

    public void testHandle2() throws Exception {
        BeanMetaData beanMetaData = createBeanMetaData(Employee.class);
        ResultSetHandler handler = new BeanMetaDataResultSetHandler(
                beanMetaData, createRowCreator(), createRelationRowCreator());
        String sql = "select ename, job from emp where empno = 7788";
        Connection con = getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        Employee ret = null;
        try {
            ResultSet rs = ps.executeQuery();
            try {
                ret = (Employee) handler.handle(rs);
            } finally {
                rs.close();
            }
        } finally {
            ps.close();
        }
        assertNotNull("1", ret);
        System.out.println(ret.getEmpno() + "," + ret.getEname());
        Department dept = ret.getDepartment();
        assertNull("2", dept);
    }

    public void testHandle_relationshipIsNull() throws Exception {
        BeanMetaData beanMetaData = createBeanMetaData(Employee23.class);
        ResultSetHandler handler = new BeanMetaDataResultSetHandler(
                beanMetaData, createRowCreator(), createRelationRowCreator());
        String sql = "select emp.empno, emp.ename, emp.deptno, department.deptno as deptno_0, department.dname as dname_0 from EMP5 emp LEFT OUTER JOIN DEPT department on emp.deptno = department.deptno where emp.empno = 7788";
        Connection con = getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        Employee23 ret = null;
        try {
            ResultSet rs = ps.executeQuery();
            try {
                ret = (Employee23) handler.handle(rs);
            } finally {
                rs.close();
            }
        } finally {
            ps.close();
        }
        assertNotNull(ret);
        assertEquals(7788, ret.getEmpno());
        assertEquals("SCOTT", ret.getEname());
        assertNull(ret.getDeptno());
        assertNull(ret.getDepartment());
    }

    public void testHandle_relationshipIsNotNull() throws Exception {
        BeanMetaData beanMetaData = createBeanMetaData(Employee23.class);
        ResultSetHandler handler = new BeanMetaDataResultSetHandler(
                beanMetaData, createRowCreator(), createRelationRowCreator());
        String sql = "select emp.empno, emp.ename, emp.deptno, department.deptno as deptno_0, department.dname as dname_0 from EMP5 emp LEFT OUTER JOIN DEPT department on emp.deptno = department.deptno where emp.empno = 7839";
        Connection con = getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        Employee23 ret = null;
        try {
            ResultSet rs = ps.executeQuery();
            try {
                ret = (Employee23) handler.handle(rs);
            } finally {
                rs.close();
            }
        } finally {
            ps.close();
        }
        assertNotNull(ret);
        assertEquals(7839, ret.getEmpno());
        assertEquals("KING", ret.getEname());
        assertEquals(new Integer(10), ret.getDeptno());
        Department dept = ret.getDepartment();
        assertNotNull(dept);
        assertEquals(10, dept.getDeptno());
        assertEquals("ACCOUNTING", dept.getDname());
    }

    public void testHandle_notSingleResult() throws Exception {
        BeanMetaData beanMetaData = createBeanMetaData(Employee23.class);
        ResultSetHandler handler = new RestrictBeanMetaDataResultSetHandler(
                beanMetaData, createRowCreator(), createRelationRowCreator());
        String sql = "select emp.empno, emp.ename, emp.deptno, department.deptno as deptno_0, department.dname as dname_0 from EMP5 emp LEFT OUTER JOIN DEPT department on emp.deptno = department.deptno";
        Connection con = getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        Employee23 ret = null;
        try {
            ResultSet rs = ps.executeQuery();
            try {
                try {
                    ret = (Employee23) handler.handle(rs);
                    fail();
                } catch (NotSingleResultRuntimeException e){
                    assertTrue(true);
                }
            } finally {
                rs.close();
            }
        } finally {
            ps.close();
        }
        assertNull(ret);
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

}
