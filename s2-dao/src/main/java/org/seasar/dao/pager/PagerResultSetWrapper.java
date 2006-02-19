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

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.extension.jdbc.impl.ResultSetWrapper;
import org.seasar.framework.exception.SQLRuntimeException;

/**
 * ページャ用のResultSetラッパー。<p>
 * 検索条件オブジェクトのoffset位置から、limitまでの範囲の結果を
 * nextメソッドで返します。<p>
 * limitが-1の場合、全ての結果をnextメソッドで返します。
 * 
 * @author Toshitaka Agata(Nulab,inc.)
 */
class PagerResultSetWrapper extends ResultSetWrapper {

    /** ログ */
    private static final Log log = LogFactory
            .getLog(PagerResultSetWrapper.class);

    /** カウント */
    private int counter = 0;

    /** オリジナルのResultSet */
    private ResultSet original;

    /** 検索条件オブジェクト */
    private PagerCondition condition;

    /** absoluteメソッドを使用するかどうかのフラグ */
    private boolean useAbsolute = true;

    public void setUseAbsolute(boolean useAbsolute) {
        this.useAbsolute = useAbsolute;
    }

    /**
     * コンストラクタ
     * @param original オリジナルのResultSet 
     * @param condition 検索条件オブジェクト
     * @param useAbsolute
     * @throws SQLException
     */
    public PagerResultSetWrapper(ResultSet original, PagerCondition condition,
            boolean useAbsolute) {
        super(original);
        this.original = original;
        this.condition = condition;
        this.useAbsolute = useAbsolute;
        moveOffset();
    }

    /**
     * 開始位置までカーソルを進めます。
     * @throws SQLException
     */
    private void moveOffset() {
        if (isUseCursor()) {
            if (log.isDebugEnabled()) {
                log.debug("[S2Pager]Use scroll cursor.");
            }
            try {
                original.absolute(condition.getOffset());
                counter = original.getRow();
            } catch (SQLException e) {
                throw new SQLRuntimeException(e);
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("[S2Pager]Not use scroll cursor.");
            }
            try {
                while (original.getRow() < condition.getOffset()
                        && original.next()) {
                    counter++;
                }
            } catch (SQLException e) {
                throw new SQLRuntimeException(e);
            }
        }
    }

    public boolean next() throws SQLException {
        boolean next = super.next();
        if ((condition.getLimit() == PagerCondition.NONE_LIMIT || counter < condition
                .getOffset()
                + condition.getLimit())
                && next) {
            counter += 1;
            return true;
        } else {
            if (isUseCursor()) {
                original.last();
                int count = original.getRow();
                condition.setCount(count);
            } else {
                if (next) {
                    counter++; // 調整
                    while (original.next()) {
                        counter++;
                    }
                }
                condition.setCount(counter);
            }
            return false;
        }
    }

    private boolean isUseCursor() {
        return useAbsolute && ResultSetUtil.isCursorSupport(original);
    }

}
