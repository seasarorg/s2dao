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

import java.io.Serializable;

/**
 * WEB 画面利用履歴クラス<br>
 * <br>
 * File Information :<br>
 * 	$Header: /cvsroot/seasar/s2-dao/src/test/org/seasar/dao/impl/FormUseHistory.java,v 1.1 2005/01/18 10:42:18 higa Exp $<br>
 *
 * @author ARGO21
 * @version 1.0
 */
public class FormUseHistory implements Serializable {
	//
	// 定数
	//
	
	/** TABLEアノテーション */
	public static final String TABLE = "CWEB_FORM_HIST";
	
	/** COLUMNアノテーション WEBユーザコード */
	public static final String webUserCode_COLUMN = "W_USER_CD";
	
	/** COLUMNアノテーション WEB画面ID */
	public static final String webFormId_COLUMN = "W_FORM_ID";
	
	/** COLUMNアノテーション 参照タイムスタンプ */
	public static final String referenceTimestamp_COLUMN = "REF_TIMESTAMP";
	
	/** COLUMNアノテーション 参照ホストIP */
	public static final String referenceHostIp_COLUMN = "REF_HOST_IP";
	
	//
	// インスタンスフィールド
	//
	
	/** WEBユーザコード */
	private String webUserCode;
	
	/** WEB画面ID */
	private String webFormId;
	
	/** 参照タイムスタンプ */
	private java.sql.Timestamp referenceTimestamp;
	
	/** 参照ホストIP */
	private String referenceHostIp;
	
	//
	// インスタンスメソッド
	//
	
	/**
	 * WEBユーザコード取得
	 * @return String
	 */
	public String getWebUserCode() {
		return this.webUserCode;
	}
	/**
	 * WEBユーザコード設定
	 * @param webUserCode
	 */
	public void setWebUserCode(String webUserCode) {
		this.webUserCode = webUserCode;
	}
	/**
	 * WEB画面ID取得
	 * @return String
	 */
	public String getWebFormId() {
		return this.webFormId;
	}
	/**
	 * WEB画面ID設定
	 * @param webFormId
	 */
	public void setWebFormId(String webFormId) {
		this.webFormId = webFormId;
	}
	/**
	 * 参照タイムスタンプ取得
	 * @return java.sql.Timestamp
	 */
	public java.sql.Timestamp getReferenceTimestamp() {
		return this.referenceTimestamp;
	}
	/**
	 * 参照タイムスタンプ設定
	 * @param referenceTimestamp
	 */
	public void setReferenceTimestamp(java.sql.Timestamp referenceTimestamp) {
		this.referenceTimestamp = referenceTimestamp;
	}
	/**
	 * 参照ホストIP取得
	 * @return String
	 */
	public String getReferenceHostIp() {
		return this.referenceHostIp;
	}
	/**
	 * 参照ホストIP設定
	 * @param referenceHostIp
	 */
	public void setReferenceHostIp(String referenceHostIp) {
		this.referenceHostIp = referenceHostIp;
	}
	/**
	 * 文字列化
	 * @return 文字列
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("webUserCode[").append(this.webUserCode).append("]");
		buffer.append("webFormId[").append(this.webFormId).append("]");
		buffer.append("referenceTimestamp[").append(this.referenceTimestamp).append("]");
		buffer.append("referenceHostIp[").append(this.referenceHostIp).append("]");
		return buffer.toString();
	}
}

