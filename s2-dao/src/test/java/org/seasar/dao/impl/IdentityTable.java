package org.seasar.dao.impl;

public class IdentityTable {

	public static final String myid_ID = "identity";
	
	public static final String myid_COLUMN = "id";

	private int myid;
	
	private String idName;
	
	public int getMyid() {
		return myid;
	}
	
	public void setMyid(int myid) {
		this.myid = myid;
	}
	
	public String getIdName() {
		return idName;
	}
	
	public void setIdName(String idName) {
		this.idName = idName;
	}
}
