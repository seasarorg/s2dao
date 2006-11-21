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
package org.seasar.dao.dbms;

import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.seasar.dao.BeanMetaData;
import org.seasar.dao.Dbms;
import org.seasar.dao.RelationPropertyType;
import org.seasar.framework.util.Disposable;
import org.seasar.framework.util.DisposableUtil;

/**
 * @author higa
 * 
 */
public class Standard implements Dbms, Disposable {

    private static final Pattern baseSqlPattern = Pattern.compile(
            "^.*?(select)", Pattern.CASE_INSENSITIVE);

    final Map autoSelectFromClauseCache = new HashMap();

    boolean initialized;

    /**
     * @see org.seasar.dao.Dbms#getSuffix()
     */
    public String getSuffix() {
        return "";
    }

    /**
     * @see org.seasar.dao.Dbms#getAutoSelectSql(org.seasar.dao.BeanMetaData)
     */
    public String getAutoSelectSql(BeanMetaData beanMetaData) {
        if (!initialized) {
            DisposableUtil.add(this);
            initialized = true;
        }
        StringBuffer buf = new StringBuffer(100);
        buf.append(beanMetaData.getAutoSelectList());
        buf.append(" ");
        String beanName = beanMetaData.getBeanClass().getName();
        synchronized (autoSelectFromClauseCache) {
            String fromClause = (String) autoSelectFromClauseCache
                    .get(beanName);
            if (fromClause == null) {
                fromClause = createAutoSelectFromClause(beanMetaData);
                autoSelectFromClauseCache.put(beanName, fromClause);
            }
            buf.append(fromClause);
        }
        return buf.toString();
    }

    protected String createAutoSelectFromClause(BeanMetaData beanMetaData) {
        StringBuffer buf = new StringBuffer(100);
        buf.append("FROM ");
        String myTableName = beanMetaData.getTableName();
        buf.append(myTableName);
        for (int i = 0; i < beanMetaData.getRelationPropertyTypeSize(); ++i) {
            RelationPropertyType rpt = beanMetaData.getRelationPropertyType(i);
            BeanMetaData bmd = rpt.getBeanMetaData();
            buf.append(" LEFT OUTER JOIN ");
            buf.append(bmd.getTableName());
            buf.append(" ");
            String yourAliasName = rpt.getPropertyName();
            buf.append(yourAliasName);
            buf.append(" ON ");
            for (int j = 0; j < rpt.getKeySize(); ++j) {
                buf.append(myTableName);
                buf.append(".");
                buf.append(rpt.getMyKey(j));
                buf.append(" = ");
                buf.append(yourAliasName);
                buf.append(".");
                buf.append(rpt.getYourKey(j));
                buf.append(" AND ");
            }
            buf.setLength(buf.length() - 5);

        }
        return buf.toString();
    }

    public String getIdentitySelectString() {
        return null;
    }

    public String getSequenceNextValString(String sequenceName) {
        return null;
    }

    public boolean isSelfGenerate() {
        return true;
    }

    public String getBaseSql(Statement st) {
        String sql = st.toString();
        Matcher matcher = baseSqlPattern.matcher(sql);
        if (matcher.find()) {
            return matcher.replaceFirst(matcher.group(1));
        } else {
            return sql;
        }
    }

    public synchronized void dispose() {
        autoSelectFromClauseCache.clear();
        initialized = false;
    }

}
