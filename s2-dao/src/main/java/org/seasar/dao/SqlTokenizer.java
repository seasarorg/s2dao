package org.seasar.dao;

/**
 * @author higa
 *
 */
public interface SqlTokenizer {
	
	public int SQL = 1;
	public int COMMENT = 2;
	public int ELSE = 3;
	public int BIND_VARIABLE = 4;
	public int EOF = 99;
	
	public String getToken();
	
	public String getBefore();

	public String getAfter();
	
	public int getPosition();
	
	public int getTokenType();
	
	public int getNextTokenType();
	
	public int next();
	
	public String skipToken();
	
	public String skipWhitespace();
}
