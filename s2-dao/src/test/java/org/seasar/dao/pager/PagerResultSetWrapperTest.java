/*
 * Copyright 2004-2007 the Seasar Foundation and the Others.
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

import junit.framework.TestCase;

/**
 * 
 * @author Toshitaka Agata
 */
public class PagerResultSetWrapperTest extends TestCase {

    public void testNext() throws Exception {

        // assertPaging(50, 20, 10, 11, 50);
        // assertPaging(50, 45, 10, 6, 50);
        // assertPaging(5 , 0, 10, 6, 5);
        // assertPaging(1 , 0, 10, 1, 1);
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