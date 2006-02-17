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
package org.seasar.dao.impl;

import java.util.List;

/**
 * WEB 画面利用履歴Daoクラス<br>
 * <br>
 * File Information :<br>
 * 	$Header: /cvsroot/seasar/s2-dao/src/test/org/seasar/dao/impl/FormUseHistoryDao.java,v 1.1 2005/01/18 10:42:18 higa Exp $<br>
 *
 * @author ARGO21
 * @version 1.0
 */
public interface FormUseHistoryDao {
	/** BEANアノテーション */
	static final Class BEAN = FormUseHistory.class;
	
	//
	// インスタンスメソッド
	//
	
	/**
	 * インサート 
	 * @param formUseHistory WEB 画面利用履歴
	 * @return 登録した数
	 */
	int insert(FormUseHistory formUseHistory);
	
	/** ARGSアノテーション getEntity() */
	static final String getEntity_ARGS = "W_USER_CD,W_FORM_ID";
	
	/**
	 * エンティティ取得
	 * @param webUserCode
	 * @param webFormId
	 * @return WEB 画面利用履歴
	 */
	FormUseHistory getEntity(String webUserCode,String webFormId);
	
	/**
	 * リスト取得
	 * @return WEB 画面利用履歴のリスト
	 */
	List getList();
	
	//
	// 追加メソッド
	//
	
}

