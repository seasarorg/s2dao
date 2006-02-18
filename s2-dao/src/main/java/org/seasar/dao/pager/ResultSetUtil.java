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
package org.seasar.dao.pager;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.seasar.framework.exception.SQLRuntimeException;

/**
 * ResultSetユーティリティです。
 * 
 * @author Toshitaka Agata(Nulab,inc.)
 */
class ResultSetUtil {

    /**
     * ResultSetを指定された位置まで進めます。
     * @param resultSet ResultSet
     * @param offset 位置
     * @return ResultSet#nextを呼び出した回数
     * @throws SQLException
     */
    public static int autoAbsolute(ResultSet resultSet, int offset)
            throws SQLException {
        if (isCursorSupport(resultSet)) {
            try {
                if (offset != resultSet.getRow()) {
                    resultSet.absolute(offset);
                    return resultSet.getRow();
                }
            } catch (SQLException e) {
                return manualAbsolute(resultSet, offset);
            }
        } else {
            return manualAbsolute(resultSet, offset);
        }
        return 0;
    }

    /**
     * ResultSetを指定された位置まで進めます。
     * @param resultSet ResultSet
     * @param offset 位置
     * @return ResultSet#nextを呼び出した回数
     * @throws SQLException
     */
    private static int manualAbsolute(ResultSet resultSet, int offset)
            throws SQLException {
        int count = 0;
        while (resultSet.getRow() < offset && resultSet.next()) {
            count++;
        }
        return count;
    }

    /**
     * ResultSetを最後の位置まで進めます。
     * @param resultSet ResultSet
     * @throws SQLException
     */
    public static boolean autoLast(ResultSet resultSet) throws SQLException {
        if (isCursorSupport(resultSet)) {
            try {
                resultSet.last();
                return true;
            } catch (SQLException e) {
                manualLast(resultSet);
                return false;
            }
        } else {
            manualLast(resultSet);
            return false;
        }
    }

    /**
     * ResultSetがカーソルをサポートしているかどうかを判定します。
     * @param resultSet ResultSet
     * @return カーソルをサポートしていればtrue、それ以外はfalse
     * @throws SQLException
     */
    public static boolean isCursorSupport(ResultSet resultSet) {
        try {
            return !(resultSet.getType() == ResultSet.TYPE_FORWARD_ONLY);
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    /**
     * ResultSetを最後の位置まで進めます。
     * @param resultSet ResultSet
     * @throws SQLException
     */
    private static void manualLast(ResultSet resultSet) throws SQLException {
        while (resultSet.next()) {
        }
    }
}
