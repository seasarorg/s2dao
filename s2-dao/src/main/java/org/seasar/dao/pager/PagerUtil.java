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

import java.util.ArrayList;
import java.util.List;

/**
 * ページャユーティリティ
 * 
 * @author Toshitaka Agata(Nulab,inc.)
 */
public class PagerUtil {

    public static boolean isPrev(PagerCondition condition) {
        boolean prev = condition.getOffset() > 0;
        return prev;
    }
    
    public static boolean isNext(PagerCondition condition) {
        boolean next = condition.getCount() > 0 && condition.getOffset() + condition.getLimit() < condition.getCount();
        return next;
    }

    public static int getCurrentLastOffset(PagerCondition condition) {
        int nextOffset = getNextOffset(condition);
        return nextOffset < condition.getCount() - 1 ? nextOffset : condition.getCount();
    }
    
    public static int getNextOffset(PagerCondition condition) {
        return condition.getOffset() + condition.getLimit();
    }
    
    public static int getPrevOffset(PagerCondition condition) {
        int prevOffset = condition.getOffset() - condition.getLimit();
        return prevOffset < 0 ? 0 : prevOffset;
    }
    
    public static int getPageIndex(PagerCondition condition) {
        if (condition.getLimit() == 0) {
            return 1;
        } else {
            return condition.getOffset() / condition.getLimit();
        }
    }
    
    public static int getPageCount(PagerCondition condition) {
        return getPageIndex(condition) + 1;
    }
    
    public static int getLastPageIndex(PagerCondition condition) {
        if (condition.getLimit() == 0) {
            return 0;
        } else {
            return (condition.getCount() - 1) / condition.getLimit();
        }
    }
    
    public static int getDisplayPageIndexBegin(PagerCondition condition, int displayPageMax) {
        int lastPageIndex = getLastPageIndex(condition);
        if (lastPageIndex < displayPageMax) {
            return 0;
        } else {
            int currentPageIndex = getPageIndex(condition);
            int displayPageIndexBegin = currentPageIndex - ((int)Math.floor(displayPageMax / 2));
            return displayPageIndexBegin < 0 ? 0 : displayPageIndexBegin;
        }
    }

    public static int getDisplayPageIndexEnd(PagerCondition condition, int displayPageMax) {
        int lastPageIndex = getLastPageIndex(condition);
        int displayPageIndexBegin = getDisplayPageIndexBegin(condition, displayPageMax);
        int displayPageRange = lastPageIndex - displayPageIndexBegin;
        if (displayPageRange < displayPageMax) {
            return lastPageIndex;
        } else {
            return displayPageIndexBegin + displayPageMax - 1;
        }
    }
    
    /**
     * Listの内容をPagerConditionの条件でフィルタリングします。
     * @param list List
     * @param condition 条件
     * @return フィルタリング後のList
     */
    public static List filter(List list, PagerCondition condition) {
        condition.setCount(list.size());
        if (condition.getLimit() == PagerCondition.NONE_LIMIT) {
            return list;
        } else {
            List result = new ArrayList();
            for (int i = 0; i < list.size(); i++) {
                if (i >= condition.getOffset() 
                        && i < condition.getOffset() + condition.getLimit()) {
                    result.add(list.get(i));
                }
            }
            return result;
        }
    }
}
