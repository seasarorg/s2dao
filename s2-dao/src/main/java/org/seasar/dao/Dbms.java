package org.seasar.dao;

/**
 * @author higa
 *
 */
public interface Dbms {

	String getAutoSelectSql(BeanMetaData beanMetaData);
	
	String getSuffix();
	
	String getIdentitySelectString();
	
	String getSequenceNextValString(String sequenceName);
    
    boolean isSelfGenerate();
}
