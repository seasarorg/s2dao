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

import junit.framework.TestCase;

/**
 * @author jundu
 *
 */
public class NullPagingSqlRewriterTest extends TestCase {

    NullPagingSqlRewriter rewriter = new NullPagingSqlRewriter();

    public void testRewrite() {
        String sql = "SELECT * FROM EMP WHERE ENAME = 'SCOTT'";
        String actual = rewriter.rewrite(sql, null, null);
        assertEquals(sql, actual);
    }

}
