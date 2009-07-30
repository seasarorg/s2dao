/*
 * Copyright 2004-2008 the Seasar Foundation and the Others.
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * MySQL用のLimitOffsetPagingSqlRewriterです。
 * 
 * 
 * @author azusa
 * 
 */
public class MySQLLimitOffsetPagingSqlRewriter extends
        LimitOffsetPagingSqlRewriter {

    private static final Pattern baseSqlPattern = Pattern.compile(
            "^.*?(select)", Pattern.CASE_INSENSITIVE);

    protected String makeLimitOffsetSql(String baseSQL, int limit, int offset) {
        return super.makeLimitOffsetSql(makeCalcFoundRowsSQL(baseSQL), limit,
                offset);
    }

    public MySQLLimitOffsetPagingSqlRewriter() {
        this.countSqlCompatibility = false;
    }

    public String makeCountSql(String baseSQL) {
        return "SELECT FOUND_ROWS()";
    }

    public String makeCalcFoundRowsSQL(String baseSQL) {
        Matcher matcher = baseSqlPattern.matcher(baseSQL);
        if (matcher.find()) {
            baseSQL = matcher.replaceFirst(matcher.group(1)
                    + " SQL_CALC_FOUND_ROWS");
        }
        return baseSQL;
    }

    protected boolean isOriginalArgsRequiredForCounting() {
        return false;
    }

}
