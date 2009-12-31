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
package org.seasar.dao.pager;

import junit.framework.TestCase;

/**
 * @author jundu
 * 
 */
public class OracleRownumPagingSqlRewriterTest extends TestCase {

    OracleRownumPagingSqlRewriter rewriter;

    protected void setUp() throws Exception {
        super.setUp();
        rewriter = new OracleRownumPagingSqlRewriter();
    }

    public void testMakeCountSql() {
        assertEquals("count(*)で全件数を取得するSQLを生成",
                "SELECT count(*) FROM (SELECT * FROM DEPARTMENT)", rewriter
                        .makeCountSql("SELECT * FROM DEPARTMENT"));
        assertEquals("count(*)で全件数を取得するSQLを生成(order by 除去)",
                "SELECT count(*) FROM (SELECT * FROM DEPARTMENT )", rewriter
                        .makeCountSql("SELECT * FROM DEPARTMENT order by id"));
        assertEquals("count(*)で全件数を取得するSQLを生成(ORDER BY 除去)",
                "SELECT count(*) FROM (SELECT * FROM DEPARTMENT )", rewriter
                        .makeCountSql("SELECT * FROM DEPARTMENT ORDER BY id"));
        assertEquals(
                "count(*)で全件数を取得するSQLを生成(whitespace付きorder by 除去)",
                "SELECT count(*) FROM (SELECT * FROM DEPARTMENT\n)",
                rewriter
                        .makeCountSql("SELECT * FROM DEPARTMENT\norder by\n    id"));
        assertEquals(
                "count(*)で全件数を取得するSQLを生成(途中のorder byは除去しない)",
                "SELECT count(*) FROM (SELECT * FROM DEPARTMENT WHERE name like '%order by%' )",
                rewriter
                        .makeCountSql("SELECT * FROM DEPARTMENT WHERE name like '%order by%' order by id"));
        assertEquals(
                "count(*)で全件数を取得するSQLを生成(途中のorder byは除去しない)",
                "SELECT count(*) FROM (SELECT * FROM DEPARTMENT WHERE name='aaa'/*order by*/)",
                rewriter
                        .makeCountSql("SELECT * FROM DEPARTMENT WHERE name='aaa'/*order by*/order by id"));
        assertEquals(
                "count(*)で全件数を取得するSQLを生成(途中のorder byは除去しない)",
                "SELECT count(*) FROM (SELECT * FROM DEPARTMENT WHERE\n--order by\nname=1\n)",
                rewriter
                        .makeCountSql("SELECT * FROM DEPARTMENT WHERE\n--order by\nname=1\norder by id"));
        assertEquals("count(*)で全件数を取得するSQLを生成(order by除去 UNICODE)",
                "SELECT count(*) FROM (SELECT * FROM DEPARTMENT )", rewriter
                        .makeCountSql("SELECT * FROM DEPARTMENT order by ＮＯ"));
        assertEquals(
                "count(*)で全件数を取得するSQLを生成(order by除去 UNICODE)",
                "SELECT count(*) FROM (SELECT * FROM DEPARTMENT )",
                rewriter
                        .makeCountSql("SELECT * FROM DEPARTMENT order by 名前, 組織_ID"));
        assertEquals(
                "count(*)で全件数を取得するSQLを生成(order by除去 ASC,DESC)",
                "SELECT count(*) FROM (SELECT * FROM DEPARTMENT )",
                rewriter
                        .makeCountSql("SELECT * FROM DEPARTMENT order by 名前 ASC\n, 組織_ID DESC"));
        assertEquals(
                "count(*)で全件数を取得するSQLを生成(order by除去 ASC,DESC+空行)",
                "SELECT count(*) FROM (SELECT * FROM DEPARTMENT )",
                rewriter
                        .makeCountSql("SELECT * FROM DEPARTMENT order\n\tby\n\n 名前 \n\tASC \n\n\n, 組織_ID \n\tDESC \n"));
        assertEquals(
                "count(*)で全件数を取得するSQLを生成(order by除去 Order byのカンマの前に空白)",
                "SELECT count(*) FROM (SELECT * FROM DEPARTMENT )",
                rewriter
                        .makeCountSql("SELECT * FROM DEPARTMENT order by aaa , bbb , ccc"));
    }

    public void testSetChopOrderByAndMakeCountSql() throws Exception {
        assertEquals("count(*)で全件数を取得するSQLを生成(chopOrderBy=true, order by 除去)",
                "SELECT count(*) FROM (SELECT * FROM DEPARTMENT )", rewriter
                        .makeCountSql("SELECT * FROM DEPARTMENT order by id"));
        rewriter.setChopOrderBy(false);
        assertEquals("count(*)で全件数を取得するSQLを生成(chopOrderBy=false, order by 除去)",
                "SELECT count(*) FROM (SELECT * FROM DEPARTMENT order by id)",
                rewriter.makeCountSql("SELECT * FROM DEPARTMENT order by id"));
    }

    public void testMakeCountSql_orderbyClauseInSubquery() throws Exception {
        String actual = rewriter
                .makeCountSql("SELECT * FROM (SELECT * FROM EMPLOYEE E ORDER BY E.EMPLOYEE_NAME ASC, E.EMPLOYEE_ID DESC, E.EMPLOYEE_HIREDATE ASC)");
        assertEquals(
                "SELECT count(*) FROM (SELECT * FROM (SELECT * FROM EMPLOYEE E ORDER BY E.EMPLOYEE_NAME ASC, E.EMPLOYEE_ID DESC, E.EMPLOYEE_HIREDATE ASC))",
                actual);
    }

    /*
     * public void testMakeBaseSql() throws Exception { try {
     * PagerContext.getContext().pushArgs(createNormalArgs()); assertEquals(
     * "SELECTの前のネイティブSQLを除去", "SELECT * FROM DEPARTMENT",
     * wrapper.makeBaseSql("native sql ... SELECT * FROM DEPARTMENT"));
     * assertEquals( "ネイティブSQLが存在しない場合、元のSQLも変化なし", "SELECT * FROM DEPARTMENT",
     * wrapper.makeBaseSql("SELECT * FROM DEPARTMENT")); } finally {
     * PagerContext.getContext().popArgs(); } }
     */
    public void testLimitOffsetSql() throws Exception {
        assertEquals(
                "指定されたlimit offsetが付加されたSQLを生成",
                "SELECT * FROM (SELECT S2DAO_ORIGINAL_DATA.*, ROWNUM AS S2DAO_ROWNUMBER FROM (SELECT * FROM DEPARTMENT) S2DAO_ORIGINAL_DATA) WHERE S2DAO_ROWNUMBER BETWEEN 56 AND 65 AND ROWNUM <= 10 ORDER BY S2DAO_ROWNUMBER",
                rewriter.makeLimitOffsetSql("SELECT * FROM DEPARTMENT", 10, 55));
    }

}
