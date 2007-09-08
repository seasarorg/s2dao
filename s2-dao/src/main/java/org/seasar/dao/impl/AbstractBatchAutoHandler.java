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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.sql.DataSource;

import org.seasar.dao.BeanMetaData;
import org.seasar.extension.jdbc.PropertyType;
import org.seasar.extension.jdbc.ReturningRowsBatchHandler;
import org.seasar.extension.jdbc.StatementFactory;
import org.seasar.extension.jdbc.util.ConnectionUtil;
import org.seasar.framework.exception.SQLRuntimeException;
import org.seasar.framework.util.PreparedStatementUtil;
import org.seasar.framework.util.StatementUtil;

/**
 * @author higa
 * 
 */
public abstract class AbstractBatchAutoHandler extends AbstractAutoHandler
        implements ReturningRowsBatchHandler {

    public AbstractBatchAutoHandler(DataSource dataSource,
            StatementFactory statementFactory, BeanMetaData beanMetaData,
            PropertyType[] propertyTypes) {

        super(dataSource, statementFactory, beanMetaData, propertyTypes);
    }

    public int[] execute(List list, Class[] argTypes)
            throws SQLRuntimeException {
        return execute(list);
    }

    public int[] execute(List list) throws SQLRuntimeException {
        if (list == null) {
            throw new IllegalArgumentException("list");
        }
        Connection connection = getConnection();
        try {
            PreparedStatement ps = prepareStatement(connection);
            try {
                for (Iterator iter = list.iterator(); iter.hasNext();) {
                    Object bean = (Object) iter.next();
                    execute(ps, bean);
                }
                return PreparedStatementUtil.executeBatch(ps);
            } finally {
                StatementUtil.close(ps);
            }
        } finally {
            ConnectionUtil.close(connection);
        }
    }

    public int execute(Object[] args) throws SQLRuntimeException {
        List list = null;
        if (args[0] instanceof Object[]) {
            list = Arrays.asList((Object[]) args[0]);
        } else if (args[0] instanceof List) {
            list = (List) args[0];
        }
        if (list == null) {
            throw new IllegalArgumentException("args[0]");
        }
        int[] ret = execute(list);
        int updatedRow = 0;
        for (int i = 0; i < ret.length; i++) {
            if (ret[i] > 0) {
                updatedRow += ret[i];
            }
        }
        return updatedRow;
    }

    public int[] executeBatch(Object[] args) throws SQLRuntimeException {
        List list = null;
        if (args[0] instanceof Object[]) {
            list = Arrays.asList((Object[]) args[0]);
        } else if (args[0] instanceof List) {
            list = (List) args[0];
        }
        if (list == null) {
            throw new IllegalArgumentException("args[0]");
        }
        return execute(list);
    }

    protected void execute(PreparedStatement ps, Object bean) {
        setupBindVariables(bean);
        logSql(getBindVariables(), getArgTypes(getBindVariables()));
        bindArgs(ps, getBindVariables(), getBindVariableValueTypes());
        PreparedStatementUtil.addBatch(ps);
    }
}