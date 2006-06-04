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

import javax.servlet.http.HttpServletRequest;

import org.seasar.framework.util.ClassUtil;

/**
 * ページャ管理ユーティリティクラス。
 * <p>
 * セッション中のページャ検索条件オブジェクトを管理します。
 * <p>
 * <p>
 * 使用方法は以下のようになります。
 * 
 * <pre>
 * public class XXXXAction extends Action {
 *   private PagerSupport pager = new Pager(20, MyPagerCondition.class, "myPagerCondition");
 *   private MyLogic logic;
 *   public void setMyLogic(MyLogic logic) {
 *     this.logic = logic;
 *   }
 * 
 *   public ActionForward doExecute(ActionMapping mapping, ActionForm _form,
 *       HttpServletRequest request, HttpServletResponse response) throws Exception {
 * 
 *      // パラメータoffsetを元にページャのoffset位置を更新
 *      pager.updateOffset(request);
 *      // 検索
 *  	MyPagerCondition dto = (MyPagerCondition) pager.getPagerCondition(request);
 *       if (form.getCode() != null) {
 *           // 条件が存在すれば、条件をセットする。
 *           dto.setCode(form.getCode());
 *       }
 *       List items = logic.getItems(dto);
 *       request.setAttribute("items", items);
 *   }
 * }
 * </pre>
 * 
 * @author Toshitaka Agata(Nulab,inc.)
 */
public class PagerSupport {

    /** 最大取得件数の初期値 */
    public static final int DEFAULT_LIMIT = PagerCondition.NONE_LIMIT;

    /** 最大取得件数 */
    private int limit = DEFAULT_LIMIT;

    /** ページャ検索条件クラス */
    private Class pagerConditionClass;

    /** 検索条件オブジェクトのセッション中の名前 */
    private String pagerConditionName;

    /**
     * コンストラクタ
     * <p>
     * 最大取得件数は無制限(-1)に設定されます。
     * 
     * @param pagerConditionClass
     *            ページャ検索条件クラス
     * @param pagerConditionName
     *            検索条件オブジェクトのセッション中の名前
     */
    public PagerSupport(Class pagerConditionClass, String pagerConditionName) {
        this(DEFAULT_LIMIT, pagerConditionClass, pagerConditionName);
    }

    /**
     * コンストラクタ
     * 
     * @param limit
     *            最大取得件数
     * @param pagerConditionClass
     *            ページャ検索条件クラス
     * @param pagerConditionName
     *            検索条件オブジェクトのセッション中の名前
     */
    public PagerSupport(int limit, Class pagerConditionClass,
            String pagerConditionName) {
        this.limit = limit;
        this.pagerConditionClass = pagerConditionClass;
        this.pagerConditionName = pagerConditionName;
    }

    /**
     * リクエストパラメータ名を指定して、セッション中の検索条件オブジェクトの現在位置を更新します。
     * <p>
     * 検索条件オブジェクトが存在しない場合、新規に検索条件オブジェクトを生成します。
     * 
     * @param request
     *            HttpServletRequest
     * @param offsetParamName
     *            現在位置を表すリクエストパラメータ名
     */
    public void updateOffset(HttpServletRequest request, String offsetParamName) {
        int offset = getOffset(request, offsetParamName);
        PagerCondition pagerCondition = getPagerCondition(request);
        pagerCondition.setOffset(offset);
    }

    /**
     * リクエストパラメータ名"offset"でセッション中の検索条件オブジェクトの現在位置を更新します。
     * <p>
     * 検索条件オブジェクトが存在しない場合、新規に検索条件オブジェクトを生成します。
     * 
     * @param request
     *            HttpServletRequest
     */
    public void updateOffset(HttpServletRequest request) {
        updateOffset(request, "offset");
    }

    /**
     * リクエストパラメータ"offset"から現在位置を取得します。
     * 
     * @param request
     *            HttpServletRequest
     * @param offsetParamName
     *            現在位置を表すリクエストパラメータ名
     * @return 現在位置
     */
    private int getOffset(HttpServletRequest request, String offsetParamName) {
        String value = request.getParameter(offsetParamName);
        if (value == null || value.length() == 0) {
            return 0;
        } else {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
    }

    /**
     * セッション中の検索条件オブジェクトを取得します。
     * <p>
     * 検索条件オブジェクトが存在しない場合、新規に検索条件オブジェクトを生成します。
     * 
     * @param request
     *            HttpServletRequest
     * @return 検索条件オブジェクト
     */
    public PagerCondition getPagerCondition(HttpServletRequest request) {
        PagerCondition dto = (PagerCondition) request.getSession()
                .getAttribute(pagerConditionName);
        if (dto == null) {
            dto = (PagerCondition) ClassUtil.newInstance(pagerConditionClass);
            dto.setLimit(limit);
            request.getSession().setAttribute(pagerConditionName, dto);
        }
        return dto;
    }

}