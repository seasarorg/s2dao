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
package org.seasar.dao.pager;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.seasar.extension.jdbc.StatementFactory;
import org.seasar.extension.jdbc.impl.BooleanToIntPreparedStatement;
import org.seasar.extension.jdbc.util.ConnectionUtil;
import org.seasar.framework.exception.SQLRuntimeException;

/**
 * @author agata
 * @author manhole
 * @author azusa
 */
public class PagerStatementFactory implements StatementFactory {

    protected boolean booleanToInt = false;

    public PreparedStatement createPreparedStatement(Connection con, String sql) {
        /*
         * https://www.seasar.org/issues/browse/DAO-42
         */
        final Object[] args = PagerContext.getContext().peekArgs();
        PreparedStatement pstmt = null;
        if (PagerContext.isPagerCondition(args)) {
            try {
                pstmt = con.prepareStatement(sql,
                        ResultSet.TYPE_SCROLL_INSENSITIVE,
                        ResultSet.CONCUR_READ_ONLY);
            } catch (SQLException e) {
                throw new SQLRuntimeException(e);
            }
            return createPreparedStatement(pstmt, sql);
        }
        pstmt = ConnectionUtil.prepareStatement(con, sql);
        return createPreparedStatement(pstmt, sql);
    }

    private PreparedStatement createPreparedStatement(PreparedStatement pstmt, String sql){
        if (booleanToInt) {
            return new BooleanToIntPreparedStatement(pstmt, sql);
        } else {
            return pstmt;
        }
    }
    
    public CallableStatement createCallableStatement(Connection con, String sql) {
        return ConnectionUtil.prepareCall(con, sql);
    }

    public void setBooleanToInt(boolean b) {
        booleanToInt = b;
    }

}
