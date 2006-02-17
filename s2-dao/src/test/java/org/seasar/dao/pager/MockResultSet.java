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
    
    /* (non-Javadoc)
     * @see org.seasar.dao.pager.MockResultSetBase#next()
     */
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
