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

import java.sql.SQLException;

/**
 * @author agata
 */
public class MockResultSet extends MockResultSetBase {

    int total;

    int counter;

    int callNextCount;

    public MockResultSet() {
    }

    public MockResultSet(int total) {
        this.total = total;
    }

    public boolean next() throws SQLException {
        callNextCount++;
        if (counter <= total) {
            counter++;
            return true;
        } else {
            return false;
        }
    }

    public boolean absolute(int row) throws SQLException {
        counter = row;
        return true;
    }

    public boolean last() throws SQLException {
        counter = total + 1;
        return true;
    }

    public int getRow() throws SQLException {
        return counter;
    }

    public int getCallNextCount() throws SQLException {
        return callNextCount;
    }
}
