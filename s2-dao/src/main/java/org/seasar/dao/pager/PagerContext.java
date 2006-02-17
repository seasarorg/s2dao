/*
 * 
 * The Seasar Software License, Version 1.1
 *
 * Copyright (c) 2003-2004 The Seasar Project. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or 
 * without modification, are permitted provided that the following 
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above 
 *    copyright notice, this list of conditions and the following 
 *    disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above 
 *    copyright notice, this list of conditions and the following 
 *    disclaimer in the documentation and/or other materials provided 
 *    with the distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgement:  
 *    "This product includes software developed by the 
 *    Seasar Project (http://www.seasar.org/)."
 *    Alternately, this acknowledgement may appear in the software
 *    itself, if and wherever such third-party acknowledgements 
 *    normally appear.
 *
 * 4. Neither the name "The Seasar Project" nor the names of its
 *    contributors may be used to endour or promote products derived 
 *    from this software without specific prior written permission of 
 *    the Seasar Project.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESSED OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE SEASAR PROJECT 
 * OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, 
 * INCIDENTAL,SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS 
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY,OR TORT (INCLUDING 
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF 
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.seasar.dao.pager;

import java.util.Stack;

/**
 * ページャの情報をスレッドローカルに保持します。
 * 
 * @author Toshitaka Agata(Nulab,inc.)
 */
class PagerContext {
    
    private static final Object[] EMPTY_ARGS = new Object[0];
    
    /**　スレッドローカル */
    private static ThreadLocal threadLocal = new ThreadLocal() {
		protected Object initialValue() {
		    return new PagerContext();
		}
    };
    
    /** Stack */
    private Stack argsStack = new Stack();
    
    /**
     * コンストラクタ
     */
    private PagerContext(){};
    
    /**
     * 現在のスレッドに結びついたPagerContextを取得します。
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
	 * @param args 引数
     * @return true/false
     */
    public static boolean isPagerCondition(Object[] args) {
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (arg instanceof PagerCondition) {
                return true;
            }
        }
        return false;
    }

    /**
	 * メソッドの引数からPagerConditionを取得します。
     * @param args 引数
     * @return PagerCondition
     */
    public static PagerCondition getPagerCondition(Object[] args) {
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (arg instanceof PagerCondition) {
                return (PagerCondition)arg;
            }
        }
        return null;
    }
}
