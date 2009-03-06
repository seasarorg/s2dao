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
import java.util.List;

/**
 * @author jundu
 * 
 */
public class NullPagingSqlRewriter implements PagingSqlRewriter {

    private boolean countSqlCompatibility = true;

    public String rewrite(String sql, Object[] args, Class[] argTypes) {
        return sql;
    }

    public int getCount(String baseSQL, Object[] args, Class[] argTypes,
            Object ret) throws SQLException {
        if (List.class.isAssignableFrom(ret.getClass())) {
            return ((List) ret).size();
        } else if (ret.getClass().isArray()) {
            return ((Object[]) ret).length;
        } else if (ret == null) {
            return 0;
        } else {
            return 1;
        }
    }

    public boolean isCountSqlCompatibility() {
        return countSqlCompatibility;
    }

}
