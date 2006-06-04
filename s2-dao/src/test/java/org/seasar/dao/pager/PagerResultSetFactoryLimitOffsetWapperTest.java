package org.seasar.dao.pager;

import junit.framework.TestCase;

/**
 * @author Toshitaka Agata
 */
public class PagerResultSetFactoryLimitOffsetWapperTest extends TestCase {

    MockResultSetFactory original;

    PagerResultSetFactoryLimitOffsetWrapper wrapper;

    protected void setUp() throws Exception {
        original = new MockResultSetFactory();
        wrapper = new PagerResultSetFactoryLimitOffsetWrapper(original, "MySQL");
    }

    /*
     * public void testMakeBaseSql() throws Exception { try {
     * PagerContext.getContext().pushArgs(createNormalArgs()); assertEquals(
     * "SELECTの前のネイティブSQLを除去", "SELECT * FROM DEPARTMENT",
     * wrapper.makeBaseSql("native sql ... SELECT * FROM DEPARTMENT"));
     * assertEquals( "ネイティブSQLが存在しない場合、元のSQLも変化なし", "SELECT * FROM DEPARTMENT",
     * wrapper.makeBaseSql("SELECT * FROM DEPARTMENT")); } finally {
     * PagerContext.getContext().popArgs(); }
     *  }
     */
    public void testLimitOffsetSql() throws Exception {
        try {
            PagerContext.getContext().pushArgs(createNormalArgs());
            assertEquals("指定されたlimit offsetが付加されたSQLを生成",
                    "SELECT * FROM DEPARTMENT LIMIT 10 OFFSET 55", wrapper
                            .makeLimitOffsetSql("SELECT * FROM DEPARTMENT", 10,
                                    55));
        } finally {
            PagerContext.getContext().popArgs();
        }

    }

    private Object[] createNormalArgs() {
        return new Object[] {};
    }
}
