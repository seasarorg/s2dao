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
package org.seasar.dao.pager;

import java.sql.PreparedStatement;

import org.aopalliance.intercept.MethodInvocation;
import org.seasar.dao.interceptors.S2DaoInterceptor;
import org.seasar.framework.aop.interceptors.AbstractInterceptor;

/**
 * ページャ用のS2DaoInterceptorのラッパー。 PagerContextに引数をセットして、 S2DaoInterceptorを呼び出します。
 * <p>
 * 
 * 次のシーケンスにより、ページング処理が実行されます。
 * <ol>
 * <li>PagerS2DaoInterceptorWrapperが引数をPagerContextにセットします。</li>
 * <li>PagerResultSetFactoryWrapperが引数からPagerConditionを取得してPagerResultSetWrapperにセットします。</li>
 * <li>PagerResultSetWrapperはPagerConditionを元に指定された範囲の結果セットを返します。</li>
 * <li>PagerResultSetWrapperはResultSetの総件数をPagerConditionにセットします。</li>
 * </ol>
 * 
 * @author Toshitaka Agata(Nulab,inc.)
 * @see PagerContext#pushArgs(Object[])
 * @see PagerResultSetFactoryWrapper#createResultSet(PreparedStatement)
 * @see PagerResultSetWrapper#next()
 */
public class PagerS2DaoInterceptorWrapper extends AbstractInterceptor {

    private static final long serialVersionUID = 1L;

    /** オリジナルのS2DaoInterceptor */
    private S2DaoInterceptor interceptor;

    /**
     * コンストラクタ
     * 
     * @param interceptor
     *            オリジナルのS2DaoInterceptor
     */
    public PagerS2DaoInterceptorWrapper(S2DaoInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    /**
     * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
     */
    public Object invoke(MethodInvocation invocation) throws Throwable {
        try {
            PagerContext.getContext().pushArgs(invocation.getArguments());
            return interceptor.invoke(invocation);
        } finally {
            PagerContext.getContext().popArgs();
        }
    }
}