package org.seasar.dao.pager;

import java.sql.ResultSet;

import junit.framework.TestCase;

/**
 * @author Toshitaka Agata
 */
public class PagerResultSetFactoryWapperTest extends TestCase {
    
    MockResultSetFactory original;
    PagerResultSetFactoryWrapper wrapper;
    
    protected void setUp() throws Exception {
        original = new MockResultSetFactory();
        wrapper = new PagerResultSetFactoryWrapper(original);
    }
    
    public void testCreateResultSetNotPagerCondition() throws Exception {
        try {
            PagerContext.getContext().pushArgs(createNormalArgs());
            ResultSet resultSet = wrapper.createResultSet(null);
            assertEquals(1, original.getCreatedResultSetCount());
            assertEquals(original.getCreatedResultSet(0), resultSet);
        } finally {
            PagerContext.getContext().popArgs();
        }

    }
    
    public void testCreateResultSetPagerCondition() throws Exception {
        try {
	        PagerContext.getContext().pushArgs(createPagerConditionArgs());
	        ResultSet resultSet = wrapper.createResultSet(null);
	        assertEquals(1, original.getCreatedResultSetCount());
	        assertEquals(PagerResultSetWrapper.class, resultSet.getClass());
        } finally {
            PagerContext.getContext().popArgs();
        }
    }

    public void testCreateResultSetSequence() throws Exception {
        try {
	        PagerContext.getContext().pushArgs(createPagerConditionArgs());
	        PagerContext.getContext().pushArgs(createNormalArgs());
	        ResultSet resultSet = wrapper.createResultSet(null);
	        assertEquals(1, original.getCreatedResultSetCount());
	        assertEquals(original.getCreatedResultSet(0), resultSet);
	    } finally {
	        PagerContext.getContext().popArgs();
	        try {
		        ResultSet resultSet = wrapper.createResultSet(null);
		        assertEquals(2, original.getCreatedResultSetCount());
		        assertEquals(PagerResultSetWrapper.class, resultSet.getClass());
	        } finally {
		        PagerContext.getContext().popArgs();
	        }
	    }
    }
    
    private Object[] createNormalArgs() {
        return new Object[] {};
    }

    private Object[] createPagerConditionArgs() {
        DefaultPagerCondition pagerConditionBase = new DefaultPagerCondition();
        return new Object[] {pagerConditionBase};
    }
}
