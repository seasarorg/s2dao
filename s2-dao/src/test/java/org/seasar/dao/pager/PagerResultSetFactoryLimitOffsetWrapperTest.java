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
package org.seasar.dao.pager;

import junit.framework.TestCase;

/**
 * @author Toshitaka Agata
 */
public class PagerResultSetFactoryLimitOffsetWrapperTest extends TestCase {

    MockResultSetFactory original;

    PagerResultSetFactoryLimitOffsetWrapper wrapper;

    protected void setUp() throws Exception {
        super.setUp();
        original = new MockResultSetFactory();
        wrapper = new PagerResultSetFactoryLimitOffsetWrapper(original, "MySQL");
        PagerContext.start();
        PagerContext.getContext().pushArgs(createNormalArgs());
    }

    protected void tearDown() throws Exception {
        PagerContext.getContext().popArgs();
        PagerContext.end();
        super.tearDown();
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
        assertEquals("指定されたlimit offsetが付加されたSQLを生成",
                "SELECT * FROM DEPARTMENT LIMIT 10 OFFSET 55", wrapper
                        .makeLimitOffsetSql("SELECT * FROM DEPARTMENT", 10, 55));
    }

    public void testMakeCountSql() throws Exception {
        assertEquals("count(*)で全件数を取得するSQLを生成",
                "SELECT count(*) FROM (SELECT * FROM DEPARTMENT) AS total",
                wrapper.makeCountSql("SELECT * FROM DEPARTMENT"));
        assertEquals("count(*)で全件数を取得するSQLを生成(order by 除去)",
                "SELECT count(*) FROM (SELECT * FROM DEPARTMENT ) AS total",
                wrapper.makeCountSql("SELECT * FROM DEPARTMENT order by id"));
        assertEquals("count(*)で全件数を取得するSQLを生成(ORDER BY 除去)",
                "SELECT count(*) FROM (SELECT * FROM DEPARTMENT ) AS total",
                wrapper.makeCountSql("SELECT * FROM DEPARTMENT ORDER BY id"));
        assertEquals(
                "count(*)で全件数を取得するSQLを生成(whitespace付きorder by 除去)",
                "SELECT count(*) FROM (SELECT * FROM DEPARTMENT\n) AS total",
                wrapper
                        .makeCountSql("SELECT * FROM DEPARTMENT\norder by\n    id"));
        assertEquals(
                "count(*)で全件数を取得するSQLを生成(途中のorder byは除去しない)",
                "SELECT count(*) FROM (SELECT * FROM DEPARTMENT WHERE name like '%order by%' ) AS total",
                wrapper
                        .makeCountSql("SELECT * FROM DEPARTMENT WHERE name like '%order by%' order by id"));
        assertEquals(
                "count(*)で全件数を取得するSQLを生成(途中のorder byは除去しない)",
                "SELECT count(*) FROM (SELECT * FROM DEPARTMENT WHERE name='aaa'/*order by*/) AS total",
                wrapper
                        .makeCountSql("SELECT * FROM DEPARTMENT WHERE name='aaa'/*order by*/order by id"));
        assertEquals(
                "count(*)で全件数を取得するSQLを生成(途中のorder byは除去しない)",
                "SELECT count(*) FROM (SELECT * FROM DEPARTMENT WHERE\n--order by\nname=1\n) AS total",
                wrapper
                        .makeCountSql("SELECT * FROM DEPARTMENT WHERE\n--order by\nname=1\norder by id"));
        assertEquals("count(*)で全件数を取得するSQLを生成(order by除去 UNICODE)",
                "SELECT count(*) FROM (SELECT * FROM DEPARTMENT ) AS total",
                wrapper.makeCountSql("SELECT * FROM DEPARTMENT order by ＮＯ"));
        assertEquals(
                "count(*)で全件数を取得するSQLを生成(order by除去 UNICODE)",
                "SELECT count(*) FROM (SELECT * FROM DEPARTMENT ) AS total",
                wrapper
                        .makeCountSql("SELECT * FROM DEPARTMENT order by 名前, 組織_ID"));
        assertEquals(
                "count(*)で全件数を取得するSQLを生成(order by除去 ASC,DESC)",
                "SELECT count(*) FROM (SELECT * FROM DEPARTMENT ) AS total",
                wrapper
                        .makeCountSql("SELECT * FROM DEPARTMENT order by 名前 ASC\n, 組織_ID DESC"));
        assertEquals(
                "count(*)で全件数を取得するSQLを生成(order by除去 ASC,DESC+空行)",
                "SELECT count(*) FROM (SELECT * FROM DEPARTMENT ) AS total",
                wrapper
                        .makeCountSql("SELECT * FROM DEPARTMENT order\n\tby\n\n 名前 \n\tASC \n\n\n, 組織_ID \n\tDESC \n"));
    }

    public void testSetChopOrderByAndMakeCountSql() throws Exception {
        assertEquals("count(*)で全件数を取得するSQLを生成(chopOrderBy=true, order by 除去)",
                "SELECT count(*) FROM (SELECT * FROM DEPARTMENT ) AS total",
                wrapper.makeCountSql("SELECT * FROM DEPARTMENT order by id"));
        wrapper.setChopOrderBy(false);
        assertEquals(
                "count(*)で全件数を取得するSQLを生成(chopOrderBy=false, order by 除去)",
                "SELECT count(*) FROM (SELECT * FROM DEPARTMENT order by id) AS total",
                wrapper.makeCountSql("SELECT * FROM DEPARTMENT order by id"));
    }

    private Object[] createNormalArgs() {
        return new Object[] {};
    }

}
