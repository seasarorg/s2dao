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
package org.seasar.dao.impl;

import java.util.List;

import org.seasar.dao.BeanMetaData;
import org.seasar.extension.jdbc.PropertyType;

/**
 * @author manhole
 */
public class InsertBatchAutoDynamicCommand extends InsertAutoDynamicCommand {

    public Object execute(Object[] args) {
        Object[] beans = null;
        if (args[0] instanceof Object[]) {
            beans = (Object[]) args[0];
        } else if (args[0] instanceof List) {
            beans = ((List) args[0]).toArray();
        }
        if (beans == null) {
            throw new IllegalArgumentException("args[0]");
        }

        final BeanMetaData bmd = getBeanMetaData();
        final PropertyType[] propertyTypes = createInsertPropertyTypes(bmd,
                beans[0], getPropertyNames());
        final String sql = createInsertSql(bmd, propertyTypes);

        AbstractAutoHandler handler = new InsertBatchAutoHandler(
                getDataSource(), getStatementFactory(), bmd, propertyTypes);
        handler.setSql(sql);
        int rows = handler.execute(args);
        return new Integer(rows);
    }

}
