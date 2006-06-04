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
     * 
     * @param ture/false
     */
    public boolean isPrev() {
        return PagerUtil.isPrev(condition);
    }

    /**
     * 次へのリンクが表示できるかどうかを判定します。
     * 
     * @param ture/false
     */
    public boolean isNext() {
        return PagerUtil.isNext(condition);
    }

    /**
     * 現在表示中の一覧の最後のoffsetを取得します。
     * 
     * @param 現在表示中の一覧の最後のoffset
     */
    public int getCurrentLastOffset() {
        return PagerUtil.getCurrentLastOffset(condition);
    }

    /**
     * 次へリンクのoffsetを返します。
     * 
     * @return 次へリンクのoffset
     */
    public int getNextOffset() {
        return PagerUtil.getNextOffset(condition);
    }

    /**
     * 前へリンクのoffsetを返します。
     * 
     * @return 前へリンクのoffset
     */
    public int getPrevOffset() {
        return PagerUtil.getPrevOffset(condition);
    }

    /**
     * 現在ページのインデックスを返します。
     * 
     * @return 現在ページのインデックス
     */
    public int getPageIndex() {
        return PagerUtil.getPageIndex(condition);
    }

    /**
     * 現在ページのカウント(インデックス+1)を返します。
     * 
     * @return 現在ページのカウント(インデックス+1)
     */
    public int getPageCount() {
        return PagerUtil.getPageCount(condition);
    }

    /**
     * 最終ページのインデックスを返します。
     * 
     * @return 最終ページのインデックス
     */
    public int getLastPageIndex() {
        return PagerUtil.getLastPageIndex(condition);
    }

    /**
     * ページリンクの表示上限を元に、ページ番号リンクの表示開始位置を返します。
     * 
     * @return ページ番号リンクの表示開始位置
     */
    public int getDisplayPageIndexBegin() {
        return PagerUtil.getDisplayPageIndexBegin(condition, displayPageMax);
    }

    /**
     * ページリンクの表示上限を元に、ページ番号リンクの表示終了位置を返します。
     * 
     * @return ページ番号リンクの表示終了位置
     */
    public int getDisplayPageIndexEnd() {
        return PagerUtil.getDisplayPageIndexEnd(condition, displayPageMax);
    }
}