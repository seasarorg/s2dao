/*
 * Copyright 2004-2011 the Seasar Foundation and the Others.
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

import java.sql.ResultSet;

import junit.framework.TestCase;

/**
 * @author Toshitaka Agata
 * @author azusa
 */
public class PagerResultSetFactoryWapperTest extends TestCase {

    MockResultSetFactory original;

    PagerResultSetFactoryWrapper wrapper;

    protected void setUp() throws Exception {
        super.setUp();
        original = new MockResultSetFactory();
        wrapper = new PagerResultSetFactoryWrapper(original);
        PagerContext.start();
    }

    protected void tearDown() throws Exception {
        PagerContext.end();
        super.tearDown();
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

    public void testCreateResultSetPagerConditionNoneLimit() throws Exception {
        try {
            PagerContext.getContext().pushArgs(
                    createPagerConditionArgsNoneLimit());
            ResultSet resultSet = wrapper.createResultSet(null);
            assertEquals(1, original.getCreatedResultSetCount());
            assertEquals(original.getCreatedResultSet(0), resultSet);
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
        pagerConditionBase.setLimit(10);
        return new Object[] { pagerConditionBase };
    }

    private Object[] createPagerConditionArgsNoneLimit() {
        DefaultPagerCondition pagerConditionBase = new DefaultPagerCondition();
        pagerConditionBase.setLimit(PagerCondition.NONE_LIMIT);
        return new Object[] { pagerConditionBase };
    }

}
