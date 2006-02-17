package org.seasar.dao.pager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.seasar.extension.jdbc.ResultSetFactory;
import org.seasar.framework.exception.SQLRuntimeException;
import org.seasar.framework.log.Logger;

/**
 * @author yamamoto
 */
public class PagerResultSetFactoryLimitOffsetWrapper implements
        ResultSetFactory {

    private static final Logger LOGGER = Logger
            .getLogger(PagerResultSetFactoryLimitOffsetWrapper.class);

    /** オリジナルのResultSetFactory */
    private ResultSetFactory resultSetFactory_;

    /**
     * コンストラクタ
     * 
     * @param resultSetFactory
     *            オリジナルのResultSetFactory
     */
    public PagerResultSetFactoryLimitOffsetWrapper(
            ResultSetFactory resultSetFactory) {
        resultSetFactory_ = resultSetFactory;
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
                String baseSQL = ps.toString().replaceFirst("^.*SELECT",
                        "SELECT");
                StringBuffer sqlBuf = new StringBuffer("SELECT count(*) FROM (");
                sqlBuf.append(baseSQL);
                sqlBuf.append(") AS total");

                LOGGER.debug("S2Pager execute SQL : " + sqlBuf.toString());

                PreparedStatement psCount = ps.getConnection()
                        .prepareStatement(sqlBuf.toString());
                ResultSet rs = resultSetFactory_.createResultSet(psCount);

                if (rs.next()) {
                    PagerCondition dto = PagerContext.getPagerCondition(args);
                    dto.setCount(rs.getInt(1));

                    if (dto.getLimit() > 0 && dto.getOffset() > -1) {
                        sqlBuf = new StringBuffer(baseSQL);
                        sqlBuf.append(" LIMIT ");
                        sqlBuf.append(dto.getLimit());
                        sqlBuf.append(" OFFSET ");
                        sqlBuf.append(dto.getOffset());

                        LOGGER.debug("S2Pager execute SQL : "
                                + sqlBuf.toString());

                        rs = resultSetFactory_.createResultSet(ps
                                .getConnection().prepareStatement(
                                        sqlBuf.toString()));

                    } else {
                        rs = resultSetFactory_.createResultSet(ps);
                    }
                    return rs;
                } else {
                    throw new SQLException();
                }

            } catch (SQLException e) {
                throw new SQLRuntimeException(e);
            }

        } else {
            return resultSetFactory_.createResultSet(ps);
        }
    }

}
