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

import java.util.List;

import org.seasar.extension.unit.S2TestCase;
import org.seasar.framework.exception.SRuntimeException;

/**
 * @author azusa
 *
 */
public class QuestionInCommentTest extends S2TestCase {

    private QuestionInCommentDao dao;

    protected void setUp() throws Exception {
        include("QuestionInCommentTest.dicon");
    }

    //[DAO-72]
    public void testInsertByManualSql2Tx() throws Exception {
        Employee emp = new Employee();
        emp.setEmpno(2222);
        emp.setEname("aaaaaaaaaaaa");
        dao.insertBySql(emp);
        assertTrue(true);
    }

    // [DAO-72]
    // Bind変数と埋め込み変数コメントが混在する状態で後者の値に「?(はてな)」を含めると
    // SQL文のDebug文字列生成時にArrayIndexOutOfBoundsExceptionが発生する。
    // 埋め込み変数コメントに含められた「?(はてな)」をBind変数の一部として認識してしまうようである。
    public void testCauseArrayIndexOutOfBoundsByVariableCommentAtDebugSqlTx() {
        try {
            dao.causeArrayIndexOutOfBounds(new Integer(2), "x?xx?");
            fail();
        } catch(SRuntimeException e){
            System.out.println(e);
            assertEquals("EDAO0023", e.getMessageCode());
        }
    }

    public static interface QuestionInCommentDao {

        public Class BEAN = Employee.class;

        public String causeArrayIndexOutOfBounds_ARGS = "id, comment";// [DAO-72]

        public List causeArrayIndexOutOfBounds(Integer id, String comment);// [DAO-72]

        public void insertBySql(Employee emp);

    }

}