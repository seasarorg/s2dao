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

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Date;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;

import org.seasar.framework.exception.NoSuchFieldRuntimeException;
import org.seasar.framework.util.ClassPoolUtil;

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

    /**
     * クラスに定義された{@link Field フィールド}をクラスファイルに定義された順番で返します。
     * 
     * @param clazz
     *            対象のクラス
     * @return このクラスに定義されたフィールドの配列
     */
    public static Field[] getDeclaredFields(final Class clazz) {
        final ClassPool pool = ClassPoolUtil.getClassPool(clazz);
        final CtClass ctClass = ClassPoolUtil.toCtClass(pool, clazz);
        final CtField[] ctFields = ctClass.getDeclaredFields();
        final int size = ctFields.length;
        final Field[] fields = new Field[size];
        for (int i = 0; i < size; ++i) {
            fields[i] = TypeUtil.getDeclaredField(clazz, ctFields[i].getName());
        }
        return fields;
    }

    /**
     * クラスに宣言されている {@link Field}を返します。
     * 
     * @param clazz
     * @param fieldName
     * @return {@link Field}
     * @throws NoSuchFieldRuntimeException
     *             {@link NoSuchFieldException}がおきた場合
     * @see Class#getDeclaredField(String)
     */
    public static Field getDeclaredField(Class clazz, String fieldName)
            throws NoSuchFieldRuntimeException {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException ex) {
            throw new NoSuchFieldRuntimeException(clazz, fieldName, ex);
        }
    }

}
