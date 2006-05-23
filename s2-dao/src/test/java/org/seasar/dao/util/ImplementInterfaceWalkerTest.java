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
package org.seasar.dao.util;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

/**
 * @author manhole
 */
public class ImplementInterfaceWalkerTest extends TestCase {

    public void testWalkAll() throws Exception {
        final List list = new ArrayList();
        ImplementInterfaceWalker.walk(Base.class,
                new ImplementInterfaceWalker.Handler() {
                    public ImplementInterfaceWalker.Status accept(Class ifs) {
                        list.add(ifs);
                        return ImplementInterfaceWalker.CONTINUE;
                    }
                });
        assertEquals(6, list.size());
        list.contains(I1.class);
        list.contains(I11.class);
        list.contains(I12.class);
        list.contains(I2.class);
        list.contains(I21.class);
        list.contains(I22.class);
    }

    public void testWalkBreak() throws Exception {
        final List list = new ArrayList();
        ImplementInterfaceWalker.walk(Base.class,
                new ImplementInterfaceWalker.Handler() {
                    public ImplementInterfaceWalker.Status accept(Class ifs) {
                        if (ifs == I12.class || ifs == I11.class
                                || ifs == I21.class || ifs == I21.class) {
                            return ImplementInterfaceWalker.BREAK;
                        }
                        list.add(ifs);
                        return ImplementInterfaceWalker.CONTINUE;
                    }
                });
        assertEquals(2, list.size());
        list.contains(I1.class);
        list.contains(I2.class);
    }

    static class Base implements I1, I2 {
    }

    static interface I1 extends I11, I12 {
    }

    static interface I11 {
    }

    static interface I12 {
    }

    static interface I2 extends I21, I22 {
    }

    static interface I21 {
    }

    static interface I22 {
    }

}
