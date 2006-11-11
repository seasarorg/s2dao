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
package org.seasar.dao.pager;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import junit.framework.TestCase;

import org.seasar.dao.mock.NullConnection;

/**
 * @author manhole
 */
public class PagerStatementFactoryTest extends TestCase {

    /**
     * Pagerで無い場合は引数1つのprepareStatementを呼ぶこと。
     */
    public void testCreatePreparedStatement_NoPager() throws Exception {
        // ## Arrange ##
        final PagerContext pagerContext = PagerContext.getContext();
        pagerContext.pushArgs(new Object[] { new Integer(1) });
        final boolean[] calls = { false };
        final NullConnection con = new NullConnection() {
            public PreparedStatement prepareStatement(String sql)
                    throws SQLException {
                calls[0] = true;
                return null;
            }
        };
        try {
            // ## Act ##
            final PagerStatementFactory statementFactory = new PagerStatementFactory();
            // 例外にならなければOK
            statementFactory.createPreparedStatement(con, "aaaa");

            // ## Assert ##
        } finally {
            pagerContext.popArgs();
        }
        assertEquals(true, calls[0]);
    }

    /**
     * Pagerの場合は引数3つのprepareStatementを呼ぶこと。
     */
    public void testCreatePreparedStatement_Pager() throws Exception {
        // ## Arrange ##
        final PagerContext pagerContext = PagerContext.getContext();
        pagerContext.pushArgs(new Object[] { new DefaultPagerCondition() });
        final boolean[] calls = { false };
        final NullConnection con = new NullConnection() {
            public PreparedStatement prepareStatement(String sql,
                    int resultSetType, int resultSetConcurrency)
                    throws SQLException {
                calls[0] = true;
                return null;
            }
        };
        try {
            // ## Act ##
            final PagerStatementFactory statementFactory = new PagerStatementFactory();
            // 例外にならなければOK
            statementFactory.createPreparedStatement(con, "aaaa");

            // ## Assert ##
        } finally {
            pagerContext.popArgs();
        }
        assertEquals(true, calls[0]);
    }

}
