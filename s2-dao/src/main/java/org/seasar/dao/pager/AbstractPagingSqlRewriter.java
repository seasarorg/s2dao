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

import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.seasar.extension.jdbc.ResultSetFactory;
import org.seasar.extension.jdbc.StatementFactory;
import org.seasar.extension.jdbc.impl.BasicSelectHandler;
import org.seasar.extension.jdbc.impl.ObjectResultSetHandler;
import org.seasar.framework.exception.SQLRuntimeException;
import org.seasar.framework.util.IntegerConversionUtil;

/**
 * S2Dao用にSELECT文を書き換えるための骨格実装を提供するクラスです。
 * <p>
 * 元のSELECT文を編集して、 {@link org.seasar.dao.pager.PagingCondition}
 * として渡されたパラメータの値に従ってページング処理を含むSQLを生成した後、カウントを取るSQLを実行します。
 * </p>
 * 
 * @author jundu
 * @author azusa
 * 
 */
public abstract class AbstractPagingSqlRewriter implements PagingSqlRewriter {

    private static final Pattern patternOrderBy = Pattern
            .compile(
                    "order\\s+by\\s+([\\w\\p{L}.`\\[\\]]+(\\s+(asc|desc))?\\s*)(,\\s*[\\w\\p{L}.`\\[\\]]+(\\s+(asc|desc))?\\s*)*$",
                    Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

    /*
     * 全件数取得時のSQLからorder by句を除去するかどうかのフラグです。 trueならorder
     * by句を除去します、falseなら除去しません。
     */
    private boolean chopOrderBy = true;

    public static final String dataSource_BINDING = "bindingType=must";

    private DataSource dataSource;

    public static final String statementFactory_BINDING = "bindingType=must";

    private StatementFactory statementFactory;

    public static final String resultsetFactory_BINDING = "bindingType=must";

    private ResultSetFactory resultsetFactory;

    /**
     * カウントを取るタイミングについての互換性設定です。(デフォルト<code>true</code>>)
     */
    protected boolean countSqlCompatibility = true;

    public String rewrite(String sql, Object[] args, Class[] argTypes) {
        final Object[] pagingArgs = PagerContext.getContext().peekArgs();
        if (PagerContext.isPagerCondition(pagingArgs)) {
            try {
                PagerCondition dto = PagerContext.getPagerCondition(pagingArgs);
                if (countSqlCompatibility) {
                    dto.setCount(getCountLogic(sql, args, argTypes));
                }
                if (dto.getLimit() > 0 && dto.getOffset() > -1) {
                    String limitOffsetSql = makeLimitOffsetSql(sql, dto
                            .getLimit(), dto.getOffset());
                    return limitOffsetSql;
                }
            } catch (SQLException e) {
                throw new SQLRuntimeException(e);
            }
        }
        return sql;
    }

    /**
     * 全件数取得時のSQLからorder by句を除去するフラグをセットします
     * 
     * @param chopOrderBy
     *            trueならorder by句を除去します、falseなら除去しません
     */
    public void setChopOrderBy(boolean chopOrderBy) {
        this.chopOrderBy = chopOrderBy;
    }

    /**
     * 全件数取得時のSQLからorder by句を除去するかどうかを返します
     * 
     * @return order by句を除去するならtrue、それ以外ではfalse
     */
    public boolean isChopOrderBy() {
        return this.chopOrderBy;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public StatementFactory getStatementFactory() {
        return statementFactory;
    }

    public void setStatementFactory(StatementFactory statementFactory) {
        this.statementFactory = statementFactory;
    }

    public ResultSetFactory getResultsetFactory() {
        return resultsetFactory;
    }

    public void setResultsetFactory(ResultSetFactory resultsetFactory) {
        this.resultsetFactory = resultsetFactory;
    }

    public void setCount(String baseSQL, Object[] args, Object[] bindVariables,
            Class[] bindVariableTypes) {
        if (!countSqlCompatibility) {
            // trueの場合はrewrite()で設定済み
            if (PagerContext.isPagerCondition(args)) {
                PagerCondition condition = PagerContext.getPagerCondition(args);
                try {
                    condition.setCount(getCountLogic(baseSQL, bindVariables,
                            bindVariableTypes));
                } catch (SQLException e) {
                    throw new SQLRuntimeException(e);
                }
            }
        }
    }

    protected int getCountLogic(String baseSQL, Object[] args, Class[] argTypes)
            throws SQLException {
        String countSQL = makeCountSql(baseSQL);

        BasicSelectHandler handler = new BasicSelectHandler(dataSource,
                countSQL, new ObjectResultSetHandler(), statementFactory,
                resultsetFactory);
        // [DAO-139]
        handler.setFetchSize(-1);
        Object ret = handler.execute(args, (Class[]) argTypes);
        if (ret != null) {
            return IntegerConversionUtil.toPrimitiveInt(ret);
        }
        throw new SQLException("[S2Pager]Result not found.");
    }

    /**
     * order by句を除去したSQLを作成します。
     * 
     * @param baseSQL
     *            元のSQL
     * @return order by句が除去されたSQL
     */
    protected String chopOrderBy(String baseSQL) {
        Matcher matcher = patternOrderBy.matcher(baseSQL);
        if (matcher.find()) {
            return matcher.replaceAll("");
        } else {
            return baseSQL;
        }
    }

    /**
     * 指定したオフセットと件数で絞り込む条件を付加したSQLを作成します。
     * 
     * @param baseSQL
     *            変更前のSQL
     * @param limit
     *            取得する件数
     * @param offset
     *            何行目以降を取得するか（offset >= 0)
     * @return 条件を付加したSQL
     */
    abstract String makeLimitOffsetSql(String baseSQL, int limit, int offset);

    /**
     * count(*)で全件数を取得するSQLを生成します。<br/>
     * パフォーマンス向上のためorder by句を除去したSQLを発行します
     * 
     * @param baseSQL
     *            元のSQL
     * @return count(*)が付加されたSQL
     */
    abstract String makeCountSql(String baseSQL);

    public boolean isCountSqlCompatibility() {
        return countSqlCompatibility;
    }

    /**
     * カウントを取るSQLの実行タイミングの互換性設定を設定します。
     * 
     * @param countSqlCompatibility
     *            S2Dao1.0.49以前と同様のタイミングの場合<code>true</code>
     */
    public void setCountSqlCompatibility(boolean countSqlCompatibility) {
        this.countSqlCompatibility = countSqlCompatibility;
    }

}
