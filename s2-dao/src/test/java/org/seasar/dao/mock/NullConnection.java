/*
 * Copyright 2004-2010 the Seasar Foundation and the Others.
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
package org.seasar.dao.mock;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.Map;

import junit.framework.AssertionFailedError;

/**
 * @author manhole
 */
public class NullConnection implements Connection {

    public Statement createStatement() throws SQLException {
        throw new AssertionFailedError();
    }

    public PreparedStatement prepareStatement(final String sql)
            throws SQLException {
        throw new AssertionFailedError();
    }

    public CallableStatement prepareCall(final String sql) throws SQLException {
        throw new AssertionFailedError();
    }

    public String nativeSQL(final String sql) throws SQLException {
        throw new AssertionFailedError();
    }

    public void setAutoCommit(final boolean autoCommit) throws SQLException {
        throw new AssertionFailedError();
    }

    public boolean getAutoCommit() throws SQLException {
        throw new AssertionFailedError();
    }

    public void commit() throws SQLException {
        throw new AssertionFailedError();
    }

    public void rollback() throws SQLException {
        throw new AssertionFailedError();
    }

    public void close() throws SQLException {
        throw new AssertionFailedError();
    }

    public boolean isClosed() throws SQLException {
        throw new AssertionFailedError();
    }

    public DatabaseMetaData getMetaData() throws SQLException {
        throw new AssertionFailedError();
    }

    public void setReadOnly(final boolean readOnly) throws SQLException {
        throw new AssertionFailedError();
    }

    public boolean isReadOnly() throws SQLException {
        throw new AssertionFailedError();
    }

    public void setCatalog(final String catalog) throws SQLException {
        throw new AssertionFailedError();
    }

    public String getCatalog() throws SQLException {
        throw new AssertionFailedError();
    }

    public void setTransactionIsolation(final int level) throws SQLException {
        throw new AssertionFailedError();
    }

    public int getTransactionIsolation() throws SQLException {
        throw new AssertionFailedError();
    }

    public SQLWarning getWarnings() throws SQLException {
        throw new AssertionFailedError();
    }

    public void clearWarnings() throws SQLException {
        throw new AssertionFailedError();
    }

    public Statement createStatement(final int resultSetType,
            final int resultSetConcurrency) throws SQLException {
        throw new AssertionFailedError();
    }

    public PreparedStatement prepareStatement(final String sql,
            final int resultSetType, final int resultSetConcurrency)
            throws SQLException {
        throw new AssertionFailedError();
    }

    public CallableStatement prepareCall(final String sql,
            final int resultSetType, final int resultSetConcurrency)
            throws SQLException {
        throw new AssertionFailedError();
    }

    public Map getTypeMap() throws SQLException {
        throw new AssertionFailedError();
    }

    public void setTypeMap(final Map arg0) throws SQLException {
        throw new AssertionFailedError();
    }

    public void setHoldability(final int holdability) throws SQLException {
        throw new AssertionFailedError();
    }

    public int getHoldability() throws SQLException {
        throw new AssertionFailedError();
    }

    public Savepoint setSavepoint() throws SQLException {
        throw new AssertionFailedError();
    }

    public Savepoint setSavepoint(final String name) throws SQLException {
        throw new AssertionFailedError();
    }

    public void rollback(final Savepoint savepoint) throws SQLException {
        throw new AssertionFailedError();
    }

    public void releaseSavepoint(final Savepoint savepoint) throws SQLException {
        throw new AssertionFailedError();
    }

    public Statement createStatement(final int resultSetType,
            final int resultSetConcurrency, final int resultSetHoldability)
            throws SQLException {
        throw new AssertionFailedError();
    }

    public PreparedStatement prepareStatement(final String sql,
            final int resultSetType, final int resultSetConcurrency,
            final int resultSetHoldability) throws SQLException {
        throw new AssertionFailedError();
    }

    public CallableStatement prepareCall(final String sql,
            final int resultSetType, final int resultSetConcurrency,
            final int resultSetHoldability) throws SQLException {
        throw new AssertionFailedError();
    }

    public PreparedStatement prepareStatement(final String sql,
            final int autoGeneratedKeys) throws SQLException {
        throw new AssertionFailedError();
    }

    public PreparedStatement prepareStatement(final String sql,
            final int[] columnIndexes) throws SQLException {
        throw new AssertionFailedError();
    }

    public PreparedStatement prepareStatement(final String sql,
            final String[] columnNames) throws SQLException {
        throw new AssertionFailedError();
    }

}
