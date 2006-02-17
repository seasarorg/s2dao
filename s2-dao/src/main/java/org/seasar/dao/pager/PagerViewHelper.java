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

/**
 * ページャのビューヘルパークラスです。
 * 
 * @author Toshitaka Agata(Nulab,inc.)
 */
public class PagerViewHelper implements PagerCondition {

    private static final int DEFAULT_DISPLAY_PAGE_MAX = 9;

    /** 検索条件オブジェクト */
    private final PagerCondition condition;
    
    /** 画面上でのページの最大表示数 */
    private final int displayPageMax;
    
    
    public PagerViewHelper(PagerCondition condition) {
        this(condition, DEFAULT_DISPLAY_PAGE_MAX);
    }

    public PagerViewHelper(PagerCondition condition, int displayPageMax) {
        this.condition = condition;
        this.displayPageMax = displayPageMax;
    }

    public int getCount() {
        return condition.getCount();
    }

    public void setCount(int count) {
        condition.setCount(count);
    }

    public int getLimit() {
        return condition.getLimit();
    }

    public void setLimit(int limit) {
        condition.setLimit(limit);
    }

    public int getOffset() {
        return condition.getOffset();
    }

    public void setOffset(int offset) {
        condition.setOffset(offset);
    }

    /**
     * 前へのリンクが表示できるかどうかを判定します。
     * @param ture/false
     */
    public boolean isPrev() {
        return PagerUtil.isPrev(condition);
    }

    /**
     * 次へのリンクが表示できるかどうかを判定します。
     * @param ture/false
     */
    public boolean isNext() {
        return PagerUtil.isNext(condition);
    }

    /**
     * 現在表示中の一覧の最後のoffsetを取得します。
     * @param 現在表示中の一覧の最後のoffset
     */
    public int getCurrentLastOffset() {
        return PagerUtil.getCurrentLastOffset(condition);
    }
    
    /**
     * 次へリンクのoffsetを返します。
     * @return 次へリンクのoffset
     */
    public int getNextOffset() {
        return PagerUtil.getNextOffset(condition);
    }
    
    /**
     * 前へリンクのoffsetを返します。
     * @return 前へリンクのoffset
     */
    public int getPrevOffset() {
        return PagerUtil.getPrevOffset(condition);
    }
    
    /**
     * 現在ページのインデックスを返します。
     * @return 現在ページのインデックス
     */
    public int getPageIndex() {
        return PagerUtil.getPageIndex(condition);
    }
    
    /**
     * 現在ページのカウント(インデックス+1)を返します。
     * @return 現在ページのカウント(インデックス+1)
     */
    public int getPageCount() {
        return PagerUtil.getPageCount(condition);
    }
    
    /**
     * 最終ページのインデックスを返します。
     * @return 最終ページのインデックス
     */
    public int getLastPageIndex() {
        return PagerUtil.getLastPageIndex(condition);
    }
    
    /**
     * ページリンクの表示上限を元に、ページ番号リンクの表示開始位置を返します。
     * @return ページ番号リンクの表示開始位置
     */
    public int getDisplayPageIndexBegin() {
        return PagerUtil.getDisplayPageIndexBegin(condition, displayPageMax);
    }

    /**
     * ページリンクの表示上限を元に、ページ番号リンクの表示終了位置を返します。
     * @return ページ番号リンクの表示終了位置
     */
    public int getDisplayPageIndexEnd() {
        return PagerUtil.getDisplayPageIndexEnd(condition, displayPageMax);
    }
}