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

import java.util.LinkedList;

/**
 * @author manhole
 */
public class ImplementInterfaceWalker {

    public static interface Handler {
        public Status accept(Class ifs);
    }

    public static class Status {
    }

    public static final Status CONTINUE = new Status();

    public static final Status BREAK = new Status();

    public static void walk(Class implClass,
            ImplementInterfaceWalker.Handler handler) {
        LinkedList list = new LinkedList();
        addInterfaces(list, implClass);
        while (!list.isEmpty()) {
            Class ifs = (Class) list.removeFirst();
            if (BREAK == handler.accept(ifs)) {
                break;
            }
            addInterfaces(list, ifs);
        }
    }

    private static void addInterfaces(LinkedList list, Class clazz) {
        final Class[] ifs = clazz.getInterfaces();
        for (int i = 0; i < ifs.length; i++) {
            list.addLast(ifs[i]);
        }
    }

}
