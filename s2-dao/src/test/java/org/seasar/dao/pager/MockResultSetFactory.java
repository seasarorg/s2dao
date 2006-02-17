package org.seasar.dao.pager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.seasar.extension.jdbc.ResultSetFactory;

public class MockResultSetFactory implements ResultSetFactory {

    private List createdResultSets = new ArrayList();
    
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
