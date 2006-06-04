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
package org.seasar.dao;

import java.lang.reflect.Method;

/**
 * @author uehara keizou
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public interface DaoAnnotationReader {

    /**
     * @param name
     * @return
     */
    String getQuery(Method method);

    String getStoredProcedureName(Method method);

    /**
     * @param method
     * @return
     */
    String[] getArgNames(Method method);

    /**
     * @return
     */
    Class getBeanClass();

    /**
     * @param methodName
     * @return
     */
    String[] getNoPersistentProps(Method method);

    /**
     * @param methodName
     * @return
     */
    String[] getPersistentProps(Method method);

    /**
     * @param name
     * @param suffix
     * @return
     */
    String getSQL(Method method, String suffix);

}
