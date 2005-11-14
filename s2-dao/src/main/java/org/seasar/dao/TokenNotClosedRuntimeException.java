package org.seasar.dao;

import org.seasar.framework.exception.SRuntimeException;


/**
 * @author higa
 *
 */
public class TokenNotClosedRuntimeException extends SRuntimeException {

	private String token_;
	private String sql_;
	
	public TokenNotClosedRuntimeException(String token, String sql) {
		super("EDAO0002", new Object[]{token, sql});
		token_ = token;
		sql_ = sql;
	}
	
	public String getToken() {
			return token_;
	}

	public String getSql() {
		return sql_;
	}
}
