package org.seasar.dao.pager;

import junit.framework.TestCase;

/**
 * PagerContextTest
 * @author agata
 */
public class PagerContextTest extends TestCase {

    public void testIsPagerCondition() {
        assertEquals(true, PagerContext.isPagerCondition(
                new Object[]{new DefaultPagerCondition()}));
        assertEquals(true, PagerContext.isPagerCondition(
                new Object[]{new DefaultPagerCondition(), "dummy"}));
        assertEquals(false, PagerContext.isPagerCondition(
                new Object[]{"dummy"}));
    }
    public void testGetPagerCondition() {
    	PagerCondition condition = new DefaultPagerCondition();
    	PagerCondition condition2 = PagerContext.getPagerCondition(
                new Object[]{"dummy", condition, "dummy"});
        assertEquals(true, condition == condition2);
    }
    
}
