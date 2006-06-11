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
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.seasar.dao.Dbms;
import org.seasar.dao.dbms.DbmsManager;
import org.seasar.extension.jdbc.ResultSetFactory;
import org.seasar.framework.exception.SQLRuntimeException;
import org.seasar.framework.log.Logger;
import org.seasar.framework.util.ResultSetUtil;
import org.seasar.framework.util.StatementUtil;

/**
 * @author yamamoto
 * @author agata
 */
public class PagerResultSetFactoryLimitOffsetWrapper implements
        ResultSetFactory {

    private static final Logger LOGGER = Logger
            .getLogger(PagerResultSetFactoryLimitOffsetWrapper.class);

    /** オリジナルのResultSetFactory */
    private ResultSetFactory resultSetFactory_;

    private Dbms dbms_;

    /**
     * コンストラクタ(test only)
     * 
     * @param resultSetFactory
     *            オリジナルのResultSetFactory
     */
    PagerResultSetFactoryLimitOffsetWrapper(ResultSetFactory resultSetFactory,
            String productName) {
        resultSetFactory_ = resultSetFactory;
        dbms_ = DbmsManager.getDbms(productName);
    }

    /**
     * コンストラクタ
     * 
     * @param resultSetFactory
     *            オリジナルのResultSetFactory
     */
    public PagerResultSetFactoryLimitOffsetWrapper(
            ResultSetFactory resultSetFactory, DataSource dataSource) {
        resultSetFactory_ = resultSetFactory;
        dbms_ = DbmsManager.getDbms(dataSource);
    }

    /**
     * ResultSetを生成します。<br>
     * PagerContextにPagerConditionがセットされている場合、
     * <ul>
     * <li>検索結果件数を取得しPagerConditionにセットします。</li>
     * <li>LIMIT OFFSET 条件を付加したSQLを実行し、結果のResultSetを返します。</li>
     * </ul>
     * 
     * @param PreparedStatement
     * @return ResultSet
     */
    public ResultSet createResultSet(PreparedStatement ps) {

        Object[] args = PagerContext.getContext().peekArgs();

        if (PagerContext.isPagerCondition(args)) {
            try {
                if (LOGGER.isDebugEnabled()) {
                    String nativeSql = ps.toString();
                    LOGGER.debug("S2Pager native SQL : " + nativeSql);
                }

                String baseSQL = dbms_.getBaseSql(ps);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("S2Pager base SQL : " + baseSQL);
                }

                PagerCondition dto = PagerContext.getPagerCondition(args);
                dto.setCount(getCount(ps, baseSQL));
                if (dto.getLimit() > 0 && dto.getOffset() > -1) {
                    String limitOffsetSql = makeLimitOffsetSql(baseSQL, dto
                            .getLimit(), dto.getOffset());
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("S2Pager execute SQL : " + limitOffsetSql);
                    }
                    return resultSetFactory_.createResultSet(ps.getConnection()
                            .prepareStatement(limitOffsetSql));
                } else {
                    return resultSetFactory_.createResultSet(ps);
                }
            } catch (SQLException e) {
                throw new SQLRuntimeException(e);
            }
        } else {
            return resultSetFactory_.createResultSet(ps);
        }
    }

    /**
     * limit offsetを付加したSQLを作成します。
     * 
     * @param baseSQL
     * @param limit
     * @param offset
     * @return
     */
    String makeLimitOffsetSql(String baseSQL, int limit, int offset) {
        StringBuffer sqlBuf = new StringBuffer(baseSQL);
        sqlBuf.append(" LIMIT ");
        sqlBuf.append(limit);
        sqlBuf.append(" OFFSET ");
        sqlBuf.append(offset);
        return sqlBuf.toString();
    }

    /**
     * 元のSQLによる結果総件数を取得します
     * 
     * @param ps
     *            元のPreparedStatement
     * @param baseSQL
     *            元のSQL
     * @return 結果総件数
     * @throws SQLException
     */
    private int getCount(PreparedStatement ps, String baseSQL)
            throws SQLException {
        StringBuffer sqlBuf = new StringBuffer("SELECT count(*) FROM (");
        sqlBuf.append(baseSQL);
        sqlBuf.append(") AS total");
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("S2Pager execute SQL : " + sqlBuf.toString());
        }

        PreparedStatement psCount = null;
        ResultSet rs = null;
        try {
            psCount = ps.getConnection().prepareStatement(sqlBuf.toString());
            rs = resultSetFactory_.createResultSet(psCount);
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                throw new SQLException("[S2Pager]Result not found.");
            }
        } finally {
            ResultSetUtil.close(rs);
            StatementUtil.close(psCount);
        }
    }

}
