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
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private static final Pattern patternOrderBy = Pattern
            .compile(
                    "order\\s+by\\s+("
                            + // order by
                            "[\\w\\p{L}.`\\[\\]]+(\\s+(asc|desc))?"
                            + // 並び替え条件1個の場合
                            "|([\\w\\p{L}.`\\[\\]]+(\\s+(asc|desc))?\\s*,\\s*)+[\\w\\p{L}.`\\[\\]])+(\\s+(asc|desc))?"
                            + // 並び替え条件2個以上の場合
                            "\\s*$", Pattern.CASE_INSENSITIVE
                            | Pattern.UNICODE_CASE);

    /** オリジナルのResultSetFactory */
    private ResultSetFactory resultSetFactory;

    private Dbms dbms;

    /**
     * 全件数取得時のSQLからorder by句を除去するかどうかのフラグです。
     * trueならorder by句を除去します、falseなら除去しません
     */
    private boolean chopOrderBy = true;

    /**
     * コンストラクタ(test only)
     * 
     * @param resultSetFactory
     *            オリジナルのResultSetFactory
     */
    PagerResultSetFactoryLimitOffsetWrapper(ResultSetFactory resultSetFactory,
            String productName) {
        this.resultSetFactory = resultSetFactory;
        this.dbms = DbmsManager.getDbms(productName);
    }

    /**
     * コンストラクタ
     * 
     * @param resultSetFactory
     *            オリジナルのResultSetFactory
     */
    public PagerResultSetFactoryLimitOffsetWrapper(
            ResultSetFactory resultSetFactory, DataSource dataSource) {
        this.resultSetFactory = resultSetFactory;
        this.dbms = DbmsManager.getDbms(dataSource);
    }

    /**
     * 全件数取得時のSQLからorder by句を除去するフラグをセットします
     * @param chopOrderBy trueならorder by句を除去します、falseなら除去しません
     */
    public void setChopOrderBy(boolean chopOrderBy) {
        this.chopOrderBy = chopOrderBy;
    }

    public ResultSet getResultSet(Statement statement) {
        return resultSetFactory.getResultSet(statement);
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

                String baseSQL = dbms.getBaseSql(ps);
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
                    return resultSetFactory.createResultSet(ps.getConnection()
                            .prepareStatement(limitOffsetSql));
                } else {
                    return resultSetFactory.createResultSet(ps);
                }
            } catch (SQLException e) {
                throw new SQLRuntimeException(e);
            }
        } else {
            return resultSetFactory.createResultSet(ps);
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
        String countSQL = makeCountSql(baseSQL);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("S2Pager execute SQL : " + countSQL);
        }

        PreparedStatement psCount = null;
        ResultSet rs = null;
        try {
            psCount = ps.getConnection().prepareStatement(countSQL);
            rs = resultSetFactory.createResultSet(psCount);
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

    /**
     * count(*)で全件数を取得するSQLを生成します。<br/> パフォーマンス向上のためorder by句を除去したSQLを発行します
     * 
     * @param baseSQL
     *            元のSQL
     * @return count(*)が付加されたSQL
     */
    String makeCountSql(String baseSQL) {
        StringBuffer sqlBuf = new StringBuffer("SELECT count(*) FROM (");
        if (chopOrderBy) {
            sqlBuf.append(chopOrderBy(baseSQL));
        } else {
            sqlBuf.append(baseSQL);
        }
        sqlBuf.append(") AS total");
        return sqlBuf.toString();
    }

    /**
     * order by句を除去したSQLを作成します。
     * 
     * @param baseSQL
     *            元のSQL
     * @return order by句が除去されたSQL
     */
    private String chopOrderBy(String baseSQL) {
        Matcher matcher = patternOrderBy.matcher(baseSQL);
        if (matcher.find()) {
            return matcher.replaceAll("");
        } else {
            return baseSQL;
        }
    }
}
