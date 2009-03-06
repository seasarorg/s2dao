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

/**
 * S2Pager用にSELECT文を実行直前に書き換えるためのインターフェースです。
 * 
 * @author jundu
 * 
 */
public interface PagingSqlRewriter {

    /**
     * 指定されたSQL文を書き換え、 ページング処理が含まれたSQLを返します。
     * 
     * @param sql
     *            書き換え対象のSQL
     * @param args
     *            対象のSQLにバインドされる予定の値
     * @param argTypes
     *            対象のSQLにバインドされる予定の値の型
     * @return 書き換え済みのSQL
     */
    public String rewrite(String sql, Object[] args, Class[] argTypes);

    /**
     * 元のSQLによる結果総件数を取得します
     * 
     * @param baseSQL
     *            元のSQL
     * @param args
     *            対象のSQLにバインドされる予定の値
     * @param argTypes
     *            対象のSQLにバインドされる予定の値の型
     * @param ret
     *            SQLの実行結果
     * @return 結果総件数
     * @throws SQLException
     *             SQLExceptionが発生した場合
     */
    public int getCount(String baseSQL, Object[] args, Class[] argTypes,
            Object ret) throws SQLException;

    /**
     * カウントを取るSQLの実行タイミングの互換性設定を返します。
     * 
     * @return S2Dao1.0.49以前と同様のタイミングの場合<code>true</code>
     */
    public boolean isCountSqlCompatibility();

}
