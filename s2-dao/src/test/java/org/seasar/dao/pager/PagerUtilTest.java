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
