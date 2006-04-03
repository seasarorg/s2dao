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

import java.io.Serializable;

/**
 * ページャ条件保持オブジェクトのベースクラス。
 * 
 * @author Toshitaka Agata(Nulab,inc.)
 */
public class DefaultPagerCondition implements PagerCondition, Serializable {

    private static final long serialVersionUID = 1L;

    /** 現在の位置 */
    private int offset;

    /** 表示の最大値 */
    private int limit = NONE_LIMIT;

    /** 取得した総数 */
    private int count;

    /**
     * コンストラクタ
     */
    public DefaultPagerCondition() {
    }

    /**
     * @return Returns the total.
     */
    public int getCount() {
        return count;
    }

    /**
     * @param total The total to set.
     */
    public void setCount(int total) {
        this.count = total;
    }

    /**
     * @return Returns the limit.
     */
    public int getLimit() {
        return limit;
    }

    /**
     * @param limit The limit to set.
     */
    public void setLimit(int limit) {
        this.limit = limit;
    }

    /**
     * @return Returns the offset.
     */
    public int getOffset() {
        return offset;
    }

    /**
     * @param offset The offset to set.
     */
    public void setOffset(int offset) {
        this.offset = offset;
    }

}
