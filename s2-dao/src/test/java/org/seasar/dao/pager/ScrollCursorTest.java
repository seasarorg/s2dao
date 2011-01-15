/*
 * Copyright 2004-2011 the Seasar Foundation and the Others.
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

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.seasar.extension.jdbc.impl.BasicResultSetFactory;
import org.seasar.extension.unit.S2TestCase;
import org.seasar.framework.util.ResultSetUtil;
import org.seasar.framework.util.StatementUtil;

/**
 * @author manhole
 */
public class ScrollCursorTest extends S2TestCase {

    private PagerResultSetFactoryWrapper pagerResultSetFactoryWrapper;

    private PagerStatementFactory pagerStatementFactory = new PagerStatementFactory();

    protected void setUp() throws Exception {
        super.setUp();
        include("j2ee.dicon");
        PagerContext.start();
    }

    protected void setUpAfterBindFields() throws Throwable {
        super.setUpAfterBindFields();
        pagerResultSetFactoryWrapper = new PagerResultSetFactoryWrapper(
                BasicResultSetFactory.INSTANCE);
        pagerResultSetFactoryWrapper.setUseScrollCursor(isScrollCursor());
    }

    protected boolean isScrollCursor() {
        return true;
    }

    protected void tearDown() throws Exception {
        PagerContext.end();
        super.tearDown();
    }

    public void testPageLimitTx() throws Exception {
        // ## Arrange ##
        DefaultPagerCondition condition = new DefaultPagerCondition();
        condition.setLimit(2);
        assertEquals(0, condition.getOffset());
        assertEquals(0, condition.getCount());
        PagerContext.getContext().pushArgs(new Object[] { condition });

        // ## Act ##
        List employees = getEmployees();

        // ## Assert ##
        assertEquals(14, condition.getCount());
        assertEquals(2, employees.size());
        assertEquals(new BigDecimal("7369"), (BigDecimal) employees.get(0));
        assertEquals(new BigDecimal("7499"), (BigDecimal) employees.get(1));
    }

    public void testOffsetTx() throws Exception {
        // ## Arrange ##
        DefaultPagerCondition condition = new DefaultPagerCondition();
        condition.setLimit(2);
        condition.setOffset(1);
        PagerContext.getContext().pushArgs(new Object[] { condition });

        // ## Act ##
        List employees = getEmployees();

        // ## Assert ##
        assertEquals(14, condition.getCount());
        assertEquals(2, employees.size());
        assertEquals(new BigDecimal("7499"), (BigDecimal) employees.get(0));
        assertEquals(new BigDecimal("7521"), (BigDecimal) employees.get(1));
    }

    public void testLastPageTx() throws Exception {
        // ## Arrange ##
        DefaultPagerCondition condition = new DefaultPagerCondition();
        condition.setLimit(5);
        condition.setOffset(10);
        PagerContext.getContext().pushArgs(new Object[] { condition });

        // ## Act ##
        List employees = getEmployees();

        // ## Assert ##
        assertEquals(14, condition.getCount());
        assertEquals(4, employees.size());
        assertEquals(new BigDecimal("7876"), (BigDecimal) employees.get(0));
        assertEquals(new BigDecimal("7900"), (BigDecimal) employees.get(1));
        assertEquals(new BigDecimal("7902"), (BigDecimal) employees.get(2));
        assertEquals(new BigDecimal("7934"), (BigDecimal) employees.get(3));
    }

    private List getEmployees() throws SQLException {
        List result = new ArrayList();
        PreparedStatement ps = pagerStatementFactory.createPreparedStatement(
                getConnection(), "SELECT EMPNO FROM EMP ORDER BY EMPNO");
        try {
            ResultSet rs = pagerResultSetFactoryWrapper.createResultSet(ps);
            try {
                while (rs.next()) {
                    result.add(rs.getObject(1));
                }
            } finally {
                ResultSetUtil.close(rs);
            }
        } finally {
            StatementUtil.close(ps);
        }
        return result;
    }

}
