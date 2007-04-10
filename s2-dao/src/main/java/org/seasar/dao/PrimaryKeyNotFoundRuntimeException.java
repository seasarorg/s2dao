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
package org.seasar.dao;

import org.seasar.framework.exception.SRuntimeException;

/**
 * @author higa
 * 
 */
public class PrimaryKeyNotFoundRuntimeException extends SRuntimeException {

    private static final long serialVersionUID = -714079806115499952L;

    private Class targetClass;

    public PrimaryKeyNotFoundRuntimeException(Class targetClass) {
        super("EDAO0009", new Object[] { targetClass.getName() });
        this.targetClass = targetClass;
    }

    public Class getTargetClass() {
        return targetClass;
    }
}