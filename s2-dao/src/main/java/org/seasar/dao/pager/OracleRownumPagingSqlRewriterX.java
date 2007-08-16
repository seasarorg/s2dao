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

/**
 * @author jundu
 *
 */
public class OracleRownumPagingSqlRewriterX extends AbstractPagingSqlRewriterX {

    /* (non-Javadoc)
     * @see org.seasar.dao.pager.AbstractSqlRewriteStatementFactory#makeCountSql(java.lang.String)
     */
    String makeCountSql(String baseSQL) {
        StringBuffer sqlBuf = new StringBuffer("SELECT count(*) FROM (");
        if (isChopOrderBy()) {
            sqlBuf.append(chopOrderBy(baseSQL));
        } else {
            sqlBuf.append(baseSQL);
        }
        sqlBuf.append(")");
        return sqlBuf.toString();
    }

    /* (non-Javadoc)
     * @see org.seasar.dao.pager.AbstractSqlRewriteStatementFactory#makeLimitOffsetSql(java.lang.String, int, int)
     */
    String makeLimitOffsetSql(String baseSQL, int limit, int offset) {
        if (offset < 0) {
            throw new IllegalArgumentException(
                    "The offset must be greater than or equal to zero.("
                            + offset + ")");
        }
        StringBuffer sqlBuf = new StringBuffer(baseSQL);
        sqlBuf
                .insert(0,
                        "SELECT * FROM (SELECT ROWNUM AS S2DAO_ROWNUMBER, S2DAO_ORIGINAL_DATA.* FROM (");
        sqlBuf.append(") S2DAO_ORIGINAL_DATA) WHERE S2DAO_ROWNUMBER BETWEEN ");
        sqlBuf.append(offset + 1);
        sqlBuf.append(" AND ");
        sqlBuf.append(offset + limit);
        sqlBuf.append(" AND ROWNUM <= ");
        sqlBuf.append(limit);
        sqlBuf.append(" ORDER BY S2DAO_ROWNUMBER");
        return sqlBuf.toString();
    }

}
