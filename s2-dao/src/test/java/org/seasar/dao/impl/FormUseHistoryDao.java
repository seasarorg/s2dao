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

