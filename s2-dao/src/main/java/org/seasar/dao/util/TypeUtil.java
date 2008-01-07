/*
 * Copyright 2004-2008 the Seasar Foundation and the Others.
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

import java.util.Calendar;
import java.util.Date;

/**
 * 型に関するユーティリティです。
 * 
 * @author taedium
 */
public class TypeUtil {

    private TypeUtil() {
    }

    /**
     * 単純な型の場合に<code>true</code>を返します。
     * 
     * @param clazz
     *            クラス
     * @return 単純な型の場合<code>true</code>、そうでない場合<code>false</code>
     */
    public static boolean isSimpleType(Class clazz) {
        if (clazz == null) {
            throw new NullPointerException("clazz");
        }
        return clazz == String.class || clazz.isPrimitive()
                || clazz == Boolean.class || clazz == Character.class
                || Number.class.isAssignableFrom(clazz)
                || Date.class.isAssignableFrom(clazz)
                || Calendar.class.isAssignableFrom(clazz)
                || clazz == byte[].class;
    }

}
