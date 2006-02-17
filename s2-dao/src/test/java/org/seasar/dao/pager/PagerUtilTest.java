package org.seasar.dao.pager;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

/**
 * @author agata
 */
public class PagerUtilTest extends TestCase {
    
    private List list;
    DefaultPagerCondition condition;
    protected void setUp() throws Exception {
        list = new ArrayList();
        for (int i = 0; i < 21; i++) {
            list.add(String.valueOf(i));
        }
        condition = new DefaultPagerCondition();
    }
    
    public void testFilter1() {
        condition.setLimit(10);
        condition.setOffset(0);
        List result = PagerUtil.filter(list, condition);
        assertEquals(21, condition.getCount());
        assertEquals(10, result.size());
        for (int i = 0; i < result.size(); i++) {
            assertEquals(String.valueOf(i), result.get(i));
        }
    }

    public void testFilter2() {
        condition.setLimit(10);
        condition.setOffset(10);
        List result = PagerUtil.filter(list, condition);
        assertEquals(21, condition.getCount());
        assertEquals(10, result.size());
        for (int i = 0; i < result.size(); i++) {
            assertEquals(String.valueOf(i + 10), result.get(i));
        }
    }
    
    public void testFilter3() {
        condition.setLimit(10);
        condition.setOffset(20);
        List result = PagerUtil.filter(list, condition);
        assertEquals(21, condition.getCount());
        assertEquals(1, result.size());
        for (int i = 0; i < result.size(); i++) {
            assertEquals(String.valueOf(i + 20), result.get(i));
        }
    }
    
    public void testFilter4() {
        condition.setLimit(PagerCondition.NONE_LIMIT);
        condition.setOffset(20);
        List result = PagerUtil.filter(list, condition);
        assertEquals(21, condition.getCount());
        assertEquals(21, result.size());
        for (int i = 0; i < result.size(); i++) {
            assertEquals(String.valueOf(i), result.get(i));
        }
    }
}
