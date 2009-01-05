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

import java.util.List;

import org.seasar.extension.unit.S2TestCase;

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

    public void testQuestionInCommentTx(){
        dao.questionInQuote("'te?st'");
        assertTrue(true);
    }

    public static interface QuestionInCommentDao {

        public Class BEAN = Employee.class;

        public void insertBySql(Employee emp);

        List questionInQuote(String arg);
    }

}