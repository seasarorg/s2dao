/*
 * Copyright 2004-2006 the Seasar Foundation and the Others.
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

import java.sql.Timestamp;
import java.util.HashSet;

import org.seasar.dao.SqlCommand;
import org.seasar.dao.unit.S2DaoTestCase;
import org.seasar.extension.jdbc.PropertyType;
import org.seasar.framework.util.ClassUtil;

/**
 * @author manhole
 */
public class UpdateModifiedOnlyCommandTest extends S2DaoTestCase {

    /*
     * TODO testing...
     * 
     * - Entityがinterface PropertyModifiedSupportをimplementsしている場合(余計なエンハンスをしないこと)
     * - Entityのsetterがfinalだった場合
     * - SELECTのSqlCommandが、Entity単体を返却する場合と、複数Entityを返す場合をtestする
     * - relation先のentityをupdateした場合(RelationPropertyTypeの先もエンハンスされていること)
     * - "ModifiedOnly"サフィックスが変更された場合にも動くこと
     * 
     */

    private EmpDao empDao;

    protected void setUp() throws Exception {
        super.setUp();
        include(ClassUtil.getSimpleClassName(
                UpdateModifiedOnlyCommandTest.class).replace('.', '/')
                + ".dicon");
    }

    /*
     * 更新されたプロパティとtimestampだけをUPDATE文に含むこと。
     */
    public void testCreateModifiedPropertiesTx() throws Exception {
        // ## Arrange ##
        final DaoMetaDataImpl dmd = createDaoMetaData(EmpDao.class);

        final SqlCommand findById = dmd.getSqlCommand("findById");
        final Emp emp = (Emp) findById.execute(new Object[] { new Long(7499) });
        System.out.println(emp);
        assertEquals(7499, emp.getEmpno());

        /*
         * ここで更新した2カラムと、必ず追加されるtimestampの、
         * あわせて3カラムがUPDATE文に含まれるべき。
         */
        emp.setEname("hoge");
        emp.setJob("hoge2");

        // ## Act ##
        final UpdateModifiedOnlyCommand updateModifiedOnly = (UpdateModifiedOnlyCommand) dmd
                .getSqlCommand("updateModifiedOnly");
        final PropertyType[] propertyTypes = updateModifiedOnly
                .createUpdatePropertyTypes(dmd.getBeanMetaData(), emp,
                        updateModifiedOnly.getPropertyNames());

        // ## Assert ##
        final HashSet set = new HashSet();
        for (int i = 0; i < propertyTypes.length; i++) {
            final PropertyType type = propertyTypes[i];
            set.add(type.getPropertyName());
            System.out.println(type.getPropertyName() + ", "
                    + type.getColumnName());
        }
        assertEquals(3, set.size());
        assertEquals(true, set.contains("ename"));
        assertEquals(true, set.contains("job"));
        assertEquals(true, set.contains("timestamp"));
        updateModifiedOnly.execute(new Object[] { emp });
    }

    public void testByDaoTx() throws Exception {
        // ## Arrange ##
        final Emp emp = empDao.findById(7499);
        System.out.println(emp);
        emp.setJob("MANAGER");

        // ## Act ##
        empDao.updateModifiedOnly(emp);

        // ## Assert ##

    }

    public static interface EmpDao {

        Class BEAN = Emp.class;

        public String findById_ARGS = "empno";

        Emp findById(long empno);

        int updateModifiedOnly(Emp emp);

    }

    public static class Emp {

        public static final String TABLE = "EMP";

        // public static final int dept_RELNO = 0;

        public static final String timestamp_COLUMN = "tstamp";

        private long empno;

        private String ename;

        private String job;

        private Float sal;

        private Float comm;

        private Timestamp timestamp;

        private Dept dept;

        public long getEmpno() {
            return this.empno;
        }

        public void setEmpno(long empno) {
            this.empno = empno;
        }

        public java.lang.String getEname() {
            return this.ename;
        }

        public void setEname(java.lang.String ename) {
            this.ename = ename;
        }

        public java.lang.String getJob() {
            return this.job;
        }

        public void setJob(java.lang.String job) {
            this.job = job;
        }

        public Float getSal() {
            return this.sal;
        }

        public void setSal(Float sal) {
            this.sal = sal;
        }

        public Float getComm() {
            return this.comm;
        }

        public void setComm(Float comm) {
            this.comm = comm;
        }

        public Timestamp getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Timestamp timestamp) {
            this.timestamp = timestamp;
        }

        public String toString() {
            StringBuffer buf = new StringBuffer();
            buf.append(empno).append(", ");
            buf.append(ename).append(", ");
            buf.append(job).append(", ");
            buf.append(sal).append(", ");
            buf.append(comm).append(", ");
            buf.append(timestamp);
            return buf.toString();
        }

        public Dept getDept() {
            return dept;
        }

        public void setDept(Dept dept) {
            this.dept = dept;
        }
    }

    public static class Dept {

        public static final String TABLE = "DEPT";

        private long deptno;

        private String dname;

        public long getDeptno() {
            return this.deptno;
        }

        public void setDeptno(long deptno) {
            this.deptno = deptno;
        }

        public String getDname() {
            return this.dname;
        }

        public void setDname(String dname) {
            this.dname = dname;
        }

        public String toString() {
            StringBuffer buf = new StringBuffer();
            buf.append(deptno).append(", ");
            buf.append(dname);
            return buf.toString();
        }

    }

}
