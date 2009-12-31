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
package org.seasar.dao.dbms;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.seasar.dao.types.BytesType;
import org.seasar.dao.types.BytesType.Trait;

/**
 * @author higa
 * 
 */
public class PostgreSQL extends Standard {

    public static Trait POSTGRE_TRAIT = new PostgreTrait();

    public String getSuffix() {
        return "_postgre";
    }

    public String getSequenceNextValString(String sequenceName) {
        return "select nextval ('" + sequenceName + "')";
    }

    public static class PostgreTrait implements Trait {

        public int getSqlType() {
            return Types.BLOB;
        }

        public void set(final PreparedStatement ps, final int parameterIndex,
                final byte[] bytes) throws SQLException {
            ps.setBlob(parameterIndex, new BlobImpl(bytes));
        }

        public void set(final CallableStatement cs, final String parameterName,
                final byte[] bytes) throws SQLException {
            cs.setBytes(parameterName, bytes);
        }

        public byte[] get(final ResultSet rs, final int columnIndex)
                throws SQLException {
            return BytesType.toBytes(rs.getBlob(columnIndex));
        }

        public byte[] get(final ResultSet rs, final String columnName)
                throws SQLException {
            return BytesType.toBytes(rs.getBlob(columnName));
        }

        public byte[] get(final CallableStatement cs, final int columnIndex)
                throws SQLException {
            return BytesType.toBytes(cs.getBlob(columnIndex));
        }

        public byte[] get(final CallableStatement cs, final String columnName)
                throws SQLException {
            return BytesType.toBytes(cs.getBlob(columnName));
        }

    }

    public static class BlobImpl implements Blob {

        /** バイト列 */
        protected byte[] bytes;

        /**
         * インスタンスを構築します。
         * 
         * @param bytes
         *            バイト列
         */
        public BlobImpl(final byte[] bytes) {
            this.bytes = bytes;
        }

        public InputStream getBinaryStream() throws SQLException {
            return new ByteArrayInputStream(bytes);
        }

        public byte[] getBytes(final long pos, final int length)
                throws SQLException {
            if (length == bytes.length) {
                return bytes;
            }
            final byte[] result = new byte[length];
            System.arraycopy(bytes, 0, result, 0, length);
            return result;
        }

        public long length() throws SQLException {
            return bytes.length;
        }

        public long position(final Blob pattern, final long start)
                throws SQLException {
            throw new UnsupportedOperationException("position");
        }

        public long position(final byte[] pattern, final long start)
                throws SQLException {
            throw new UnsupportedOperationException("position");
        }

        public OutputStream setBinaryStream(final long pos) throws SQLException {
            throw new UnsupportedOperationException("setBinaryStream");
        }

        public int setBytes(final long pos, final byte[] bytes,
                final int offset, final int len) throws SQLException {
            throw new UnsupportedOperationException("setBytes");
        }

        public int setBytes(final long pos, final byte[] bytes)
                throws SQLException {
            throw new UnsupportedOperationException("setBytes");
        }

        public void truncate(final long len) throws SQLException {
            throw new UnsupportedOperationException("truncate");
        }

    }
}