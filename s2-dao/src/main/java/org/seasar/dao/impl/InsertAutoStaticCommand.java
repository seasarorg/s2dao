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
package org.seasar.dao.impl;

import javax.sql.DataSource;

import org.seasar.dao.BeanMetaData;
import org.seasar.extension.jdbc.StatementFactory;

/**
 * @author higa
 * 
 * @deprecated
 */
public class InsertAutoStaticCommand extends AbstractAutoStaticCommand {

    public InsertAutoStaticCommand(DataSource dataSource,
            StatementFactory statementFactory, BeanMetaData beanMetaData,
            String[] propertyNames) {

        super(dataSource, statementFactory, beanMetaData, propertyNames);
    }

    protected AbstractAutoHandler createAutoHandler() {
        return new InsertAutoHandler(getDataSource(), getStatementFactory(),
                getBeanMetaData(), getPropertyTypes(), isCheckSingleRowUpdate());
    }

    protected void setupSql() {
        setupInsertSql();
    }

    protected void setupPropertyTypes(String[] propertyNames) {
        setupInsertPropertyTypes(propertyNames);

    }
}