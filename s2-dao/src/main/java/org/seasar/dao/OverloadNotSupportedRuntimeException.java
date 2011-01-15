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
package org.seasar.dao;

import org.seasar.framework.exception.SRuntimeException;

/**
 * Daoインタフェースのメソッドがオーバーロードされている場合にスローされる例外です。
 * 
 * @author taedium
 */
public class OverloadNotSupportedRuntimeException extends SRuntimeException {

    private static final long serialVersionUID = 1L;

    private String className;

    private String methodName;

    /**
     * {@link OverloadNotSupportedRuntimeException}を生成します。
     * 
     * @param className
     *            クラス名
     * @param methodName
     *            メソッド名
     */
    public OverloadNotSupportedRuntimeException(String className,
            String methodName) {
        super("EDAO0033", new Object[] { className, methodName });
        this.className = className;
        this.methodName = methodName;
    }

    /**
     * クラス名を返します。
     * 
     * @return クラス名
     */
    public String getClassName() {
        return className;
    }

    /**
     * メソッド名を返します。
     * 
     * @return メソッド名
     */
    public String getMethodName() {
        return methodName;
    }

}
