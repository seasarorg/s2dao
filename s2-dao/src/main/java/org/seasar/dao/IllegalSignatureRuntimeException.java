package org.seasar.dao;

import org.seasar.framework.exception.SRuntimeException;


/**
 * @author higa
 *
 */
public class IllegalSignatureRuntimeException extends SRuntimeException {

	private String signature_;
	
	public IllegalSignatureRuntimeException(String messageCode, String signature) {
		super(messageCode, new Object[]{signature});
		signature_ = signature;
	}
	
	public String getSignature() {
		return signature_;
	}
}
