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
package org.seasar.dao.dbms;

import org.seasar.dao.BeanMetaData;
import org.seasar.dao.RelationPropertyType;

/**
 * @author higa
 * 
 */
public class Oracle extends Standard {

    /**
     * @see org.seasar.dao.Dbms#getSuffix()
     */
    public String getSuffix() {
        return "_oracle";
    }

    /**
     * @see org.seasar.dao.dbms.Standard#createAutoSelectFromClause(org.seasar.dao.BeanMetaData)
     */
    protected String createAutoSelectFromClause(BeanMetaData beanMetaData) {
        StringBuffer buf = new StringBuffer(100);
        buf.append("FROM ");
        String myTableName = beanMetaData.getTableName();
        buf.append(myTableName);
        StringBuffer whereBuf = new StringBuffer(100);
        for (int i = 0; i < beanMetaData.getRelationPropertyTypeSize(); ++i) {
            RelationPropertyType rpt = beanMetaData.getRelationPropertyType(i);
            BeanMetaData bmd = rpt.getBeanMetaData();
            buf.append(", ");
            buf.append(bmd.getTableName());
            buf.append(" ");
            String yourAliasName = rpt.getPropertyName();
            buf.append(yourAliasName);
            for (int j = 0; j < rpt.getKeySize(); ++j) {
                whereBuf.append(myTableName);
                whereBuf.append(".");
                whereBuf.append(rpt.getMyKey(j));
                whereBuf.append(" = ");
                whereBuf.append(yourAliasName);
                whereBuf.append(".");
                whereBuf.append(rpt.getYourKey(j));
                whereBuf.append("(+)");
                whereBuf.append(" AND ");
            }
        }
        if (whereBuf.length() > 0) {
            whereBuf.setLength(whereBuf.length() - 5);
            buf.append(" WHERE ");
            buf.append(whereBuf);
        }
        return buf.toString();
    }

    public String getSequenceNextValString(String sequenceName) {
        return "select " + sequenceName + ".nextval from dual";
    }
}