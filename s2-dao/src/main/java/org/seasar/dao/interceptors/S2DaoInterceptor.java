/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
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
package org.seasar.dao.interceptors;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInvocation;
import org.seasar.dao.DaoMetaData;
import org.seasar.dao.DaoMetaDataFactory;
import org.seasar.dao.SqlCommand;
import org.seasar.framework.aop.interceptors.AbstractInterceptor;
import org.seasar.framework.util.MethodUtil;
import org.seasar.framework.util.NumberConversionUtil;

/**
 * @author higa
 * 
 */
public class S2DaoInterceptor extends AbstractInterceptor {

    private static final long serialVersionUID = 1L;

    private DaoMetaDataFactory daoMetaDataFactory;

    public S2DaoInterceptor(DaoMetaDataFactory daoMetaDataFactory) {
        this.daoMetaDataFactory = daoMetaDataFactory;
    }

    /**
     * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
     */
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        if (!MethodUtil.isAbstract(method)) {
            return invocation.proceed();
        }
        Class targetClass = getTargetClass(invocation);
        DaoMetaData dmd = daoMetaDataFactory.getDaoMetaData(targetClass);
        SqlCommand cmd = dmd.getSqlCommand(method.getName());
        Object ret = cmd.execute(invocation.getArguments());
        Class retType = method.getReturnType();
        if (retType.isPrimitive()) {
            return NumberConversionUtil.convertPrimitiveWrapper(retType, ret);
        } else if (Number.class.isAssignableFrom(retType)) {
            return NumberConversionUtil.convertNumber(retType, ret);
        }
        return ret;
    }

}
