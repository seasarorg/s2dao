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
package org.seasar.dao.pager;

import java.util.Stack;

/**
 * ページャの情報をスレッドローカルに保持します。
 * 
 * @author Toshitaka Agata(Nulab,inc.)
 * @author azusa
 */
public class PagerContext {

    private static final PagerContext instance = new PagerContext();

    private static final Object[] EMPTY_ARGS = new Object[0];

    /** スレッドローカル */
    private static ThreadLocal threadLocal = createThreadLocal();

    /** Stack */
    private Stack argsStack = new Stack();

    /**
     * コンストラクタ
     */
    private PagerContext() {
    };

    /**
     * 現在のスレッドに結びついたPagerContextを取得します。
     * 
     * @return PagerContext
     */
    public static PagerContext getContext() {
        return (PagerContext) threadLocal.get();
    }

    public void pushArgs(Object[] args) {
        argsStack.push(args);
    }

    public Object[] popArgs() {
        return (Object[]) argsStack.pop();
    }

    public Object[] peekArgs() {
        if (argsStack.size() == 0) {
            return EMPTY_ARGS;
        } else {
            return (Object[]) argsStack.peek();
        }
    }

    /**
     * メソッドの引数にPagerConditionが含まれているかどうかを判定します。
     * <p>ただし、PagerConditon#getLimitがPagerConditon#NONE_LIMITの場合はfalseを返します。</p>
     * 
     * @param args
     *            引数
     * @return true/false
     */
    public static boolean isPagerCondition(Object[] args) {
        final PagerCondition condition = getPagerCondition(args);
        if (condition == null) {
            return false;
        }
        if (condition.getLimit() == PagerCondition.NONE_LIMIT
                && condition.getOffset() == 0) {
            return false;
        }
        return true;
    }

    /**
     * メソッドの引数からPagerConditionを取得します。
     * 
     * @param args
     *            引数
     * @return PagerCondition
     */
    public static PagerCondition getPagerCondition(Object[] args) {
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (arg instanceof PagerCondition) {
                return (PagerCondition) arg;
            }
        }
        return null;
    }

    public static void init() {
        threadLocal = createThreadLocal();
    }

    public static void destroy() {
        threadLocal.set(null);
        threadLocal = null;
    }

    private static ThreadLocal createThreadLocal() {
        return new ThreadLocal() {
            protected Object initialValue() {
                return new PagerContext();
            }
        };
    }

}
