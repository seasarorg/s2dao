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
package org.seasar.dao.pager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.seasar.extension.jdbc.ResultSetFactory;

public class MockResultSetFactory implements ResultSetFactory {

    private List createdResultSets = new ArrayList();

    public ResultSet getResultSet(Statement statement) {
        MockResultSet resultSet = new MockResultSet();
        createdResultSets.add(resultSet);
        return resultSet;
    }

    public ResultSet createResultSet(PreparedStatement arg0) {
        MockResultSet resultSet = new MockResultSet();
        createdResultSets.add(resultSet);
        return resultSet;
    }

    public int getCreatedResultSetCount() {
        return createdResultSets.size();
    }

    public ResultSet getCreatedResultSet(int index) {
        return (ResultSet) createdResultSets.get(index);
    }

}
