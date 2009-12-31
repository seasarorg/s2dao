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

import java.io.Serializable;
import java.util.List;

import org.seasar.dao.DaoMetaData;
import org.seasar.dao.RelationRowCreator;
import org.seasar.dao.RowCreator;
import org.seasar.dao.SqlCommand;
import org.seasar.dao.pager.NullPagingSqlRewriter;
import org.seasar.dao.unit.S2DaoTestCase;
import org.seasar.extension.jdbc.impl.BasicResultSetFactory;
import org.seasar.extension.jdbc.impl.BasicStatementFactory;

public class SelectDynamicCommandTest extends S2DaoTestCase {

    public SelectDynamicCommandTest(String arg0) {
        super(arg0);
    }

    public void testExecute() throws Exception {
        SelectDynamicCommand cmd = new SelectDynamicCommand(getDataSource(),
                BasicStatementFactory.INSTANCE,
                new BeanMetaDataResultSetHandler(
                        createBeanMetaData(Employee.class), createRowCreator(),
                        createRelationRowCreator()),
                BasicResultSetFactory.INSTANCE, new NullPagingSqlRewriter());
        cmd.setSql("SELECT * FROM emp WHERE empno = /*empno*/1234");
        Employee emp = (Employee) cmd
                .execute(new Object[] { new Integer(7788) });
        System.out.println(emp);
        assertNotNull("1", emp);
    }

    protected RowCreator createRowCreator() {// [DAO-118] (2007/08/25)
        return new RowCreatorImpl();
    }

    protected RelationRowCreator createRelationRowCreator() {
        return new RelationRowCreatorImpl();
    }

    public void testSelectDynamic() throws Exception {
        DaoMetaData dmd = createDaoMetaData(DynamicDao.class);
        SqlCommand cmd = dmd.getSqlCommand("getEmployeesBySearchCondition");
        assertTrue(cmd instanceof SelectDynamicCommand);
        Employee cond = new Employee();
        cond.setJob("CLERK");
        cond.setEmpno(7369);
        cond.setDeptno(20);
        List result = (List) cmd.execute(new Object[] { cond });
        assertEquals(1, result.size());
        assertTrue(result.get(0) instanceof Employee);

        cmd = dmd.getSqlCommand("getEmployeeBySearchCondition");
        assertTrue(cmd instanceof SelectDynamicCommand);
        Object obj = cmd.execute(new Object[] { cond });
        assertTrue(obj instanceof Employee);
    }

    public interface DynamicDao {
        public Class BEAN = Employee.class;

        List getEmployeesBySearchCondition(Serializable dto);

        Object getEmployeeBySearchCondition(Serializable dto);

        int update(Object dto);

        List getEmployeeByDto(String s);
    }

    public void testSelectByDtoTx() throws Exception {
        DaoMetaData dmd = createDaoMetaData(Emp3Dao.class);
        SqlCommand cmd = dmd.getSqlCommand("insert");
        Object[] param = new Object[1];
        for (int i = 0; i < 3; i++) {
            Emp3 e = new Emp3();
            e.employeeId = new Integer(i);
            e.departmentId = new Integer((i + 1) * 100);
            e.employeeName = "NAME" + String.valueOf(i);
            param[0] = e;
            cmd.execute(param);
        }

        cmd = dmd.getSqlCommand("selectByDto");
        assertTrue(cmd instanceof SelectDynamicCommand);
        Emp3Dto dto = new Emp3Dto();
        dto.employeeName = "NAME1";
        List l = (List) cmd.execute(new Object[] { dto });
        assertEquals(1, l.size());

        cmd = dmd.getSqlCommand("selectByDto2");
        assertTrue(cmd instanceof SelectDynamicCommand);
        Emp3ExDto ex = new Emp3ExDto();
        ex.department_Id = new Integer(200);
        l = (List) cmd.execute(new Object[] { ex });
        assertEquals(1, l.size());
    }

    public static class Emp3Dto {
        private String employeeName;

        public String getEmployeeName() {
            return employeeName;
        }

        public void setEmployeeName(String employeeName) {
            this.employeeName = employeeName;
        }

    }

    public static class Emp3ExDto {
        private Integer department_Id;

        public Integer getDepartment_Id() {
            return department_Id;
        }

        public void setDepartment_Id(Integer department_Id) {
            this.department_Id = department_Id;
        }
    }

    public static class Emp3 {
        public static String TABLE = "EMP3";

        private Integer employeeId;

        private String employeeName;

        private Integer departmentId;

        public Integer getDepartmentId() {
            return departmentId;
        }

        public void setDepartmentId(Integer departmentId) {
            this.departmentId = departmentId;
        }

        public String getEmployeeName() {
            return employeeName;
        }

        public void setEmployeeName(String employeeName) {
            this.employeeName = employeeName;
        }

        public Integer getEmployeeId() {
            return employeeId;
        }

        public void setEmployeeId(Integer employeeId) {
            this.employeeId = employeeId;
        }
    }

    public interface Emp3Dao {
        Class BEAN = Emp3.class;

        List selectByDto(Emp3Dto dto);

        List selectByDto2(Emp3ExDto dto);

        List select(Emp3 dto);

        void insert(Emp3 emp3);
    }

    public void setUp() {
        include("j2ee.dicon");
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(SelectDynamicCommandTest.class);
    }

}