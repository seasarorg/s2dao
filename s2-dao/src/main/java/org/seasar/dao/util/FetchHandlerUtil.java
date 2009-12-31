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
package org.seasar.dao.util;

import org.seasar.framework.exception.ClassNotFoundRuntimeException;
import org.seasar.framework.util.ClassUtil;

/**
 * @author jundu
 * 
 */
public class FetchHandlerUtil {

    private static final Class fetchHandlerClass;

    static {
        Class clazz = null;
        try {
            clazz = ClassUtil.forName("org.seasar.dao.tiger.FetchHandler");
        } catch (ClassNotFoundRuntimeException ignore) {
        }
        fetchHandlerClass = clazz;
    }

    public static boolean isFetchHandlingEnable() {
        return fetchHandlerClass != null;
    }

    public static boolean isFetchHandler(Class clazz) {
        if (!isFetchHandlingEnable()) {
            return false;
        }
        if (clazz == null) {
            return false;
        }
        if (fetchHandlerClass.isAssignableFrom(clazz)) {
            return true;
        }
        return false;
    }
}
