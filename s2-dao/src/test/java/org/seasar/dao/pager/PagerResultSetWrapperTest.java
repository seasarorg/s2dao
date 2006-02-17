package org.seasar.dao.pager;

import junit.framework.TestCase;

/**
 * 
 * @author Toshitaka Agata
 */
public class PagerResultSetWrapperTest extends TestCase {

    public void testNext() throws Exception {

//        assertPaging(50, 20, 10, 11, 50);
//        assertPaging(50, 45, 10, 6, 50);
//        assertPaging(5 ,  0, 10, 6, 5);
        //assertPaging(1 ,  0, 10, 1, 1);
    }

    public void assertPaging(int total, int offset, int limit,
            int expectedNextCount, int expectedCount) throws Exception {
        MockResultSet original = new MockResultSet(total);
        PagerCondition condition = new DefaultPagerCondition();
        condition.setOffset(offset);
        condition.setLimit(limit);
        PagerResultSetWrapper wrapper = new PagerResultSetWrapper(original,
                condition, true);
        while (wrapper.next()) {
        }
        assertEquals(expectedNextCount, original.getCallNextCount());
        assertEquals(expectedCount, condition.getCount());
    }

}