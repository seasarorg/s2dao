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
package org.seasar.dao.parser;

import org.seasar.dao.SqlTokenizer;
import org.seasar.dao.TokenNotClosedRuntimeException;

/**
 * @author higa
 *  
 */
public class SqlTokenizerImpl implements SqlTokenizer {

	private String sql_;

	private int position_ = 0;

	private String token_;

	private int tokenType_ = SQL;

	private int nextTokenType_ = SQL;

	private int bindVariableNum = 0;

	public SqlTokenizerImpl(String sql) {
		sql_ = sql;
	}

	public int getPosition() {
		return position_;
	}

	public String getToken() {
		return token_;
	}

	public String getBefore() {
		return sql_.substring(0, position_);
	}

	public String getAfter() {
		return sql_.substring(position_);
	}

	public int getTokenType() {
		return tokenType_;
	}

	public int getNextTokenType() {
		return nextTokenType_;
	}

	public int next() {
		if (position_ >= sql_.length()) {
			token_ = null;
			tokenType_ = EOF;
			nextTokenType_ = EOF;
			return tokenType_;
		}
		switch (nextTokenType_) {
		case SQL:
			parseSql();
			break;
		case COMMENT:
			parseComment();
			break;
		case ELSE:
			parseElse();
			break;
		case BIND_VARIABLE:
			parseBindVariable();
			break;
		default:
			parseEof();
			break;
		}
		return tokenType_;
	}

	protected void parseSql() {
		int commentStartPos = sql_.indexOf("/*", position_);
		int commentStartPos2 = sql_.indexOf("#*", position_);
		if( 0 < commentStartPos2 &&
				commentStartPos2 < commentStartPos){
			commentStartPos = commentStartPos2;
		}
		int lineCommentStartPos = sql_.indexOf("--", position_);
		int bindVariableStartPos = sql_.indexOf("?", position_);
		int elseCommentStartPos = -1;
		int elseCommentLength = -1;
		if (lineCommentStartPos >= 0) {
			int skipPos = skipWhitespace(lineCommentStartPos + 2);
			if (skipPos + 4 < sql_.length()
					&& "ELSE".equals(sql_.substring(skipPos, skipPos + 4))) {
				elseCommentStartPos = lineCommentStartPos;
				elseCommentLength = skipPos + 4 - lineCommentStartPos;
			}
		}
		int nextStartPos = getNextStartPos(commentStartPos,
				elseCommentStartPos, bindVariableStartPos);
		if (nextStartPos < 0) {
			token_ = sql_.substring(position_);
			nextTokenType_ = EOF;
			position_ = sql_.length();
			tokenType_ = SQL;
		} else {
			token_ = sql_.substring(position_, nextStartPos);
			tokenType_ = SQL;
			boolean needNext = nextStartPos == position_;
			if (nextStartPos == commentStartPos) {
				nextTokenType_ = COMMENT;
				position_ = commentStartPos + 2;
			} else if (nextStartPos == elseCommentStartPos) {
				nextTokenType_ = ELSE;
				position_ = elseCommentStartPos + elseCommentLength;
			} else if (nextStartPos == bindVariableStartPos) {
				nextTokenType_ = BIND_VARIABLE;
				position_ = bindVariableStartPos;
			}
			if (needNext) {
				next();
			}
		}
	}

	protected int getNextStartPos(int commentStartPos, int elseCommentStartPos,
			int bindVariableStartPos) {

		int nextStartPos = -1;
		if (commentStartPos >= 0) {
			nextStartPos = commentStartPos;
		}
		if (elseCommentStartPos >= 0
				&& (nextStartPos < 0 || elseCommentStartPos < nextStartPos)) {
			nextStartPos = elseCommentStartPos;
		}
		if (bindVariableStartPos >= 0
				&& (nextStartPos < 0 || bindVariableStartPos < nextStartPos)) {
			nextStartPos = bindVariableStartPos;
		}
		return nextStartPos;
	}

	protected String nextBindVariableName() {
		return "$" + ++bindVariableNum;
	}

	protected void parseComment() {
		int commentEndPos = sql_.indexOf("*/", position_);
		int commentEndPos2 = sql_.indexOf("*#", position_);
		if( 0 < commentEndPos2 &&
				commentEndPos2 < commentEndPos){
			commentEndPos = commentEndPos2;
		}
		if (commentEndPos < 0) {
			throw new TokenNotClosedRuntimeException("*/", sql_
					.substring(position_));
		}
		token_ = sql_.substring(position_, commentEndPos);
		nextTokenType_ = SQL;
		position_ = commentEndPos + 2;
		tokenType_ = COMMENT;
	}

	protected void parseBindVariable() {
		token_ = nextBindVariableName();
		nextTokenType_ = SQL;
		position_ += 1;
		tokenType_ = BIND_VARIABLE;
	}

	protected void parseElse() {
		token_ = null;
		nextTokenType_ = SQL;
		tokenType_ = ELSE;
	}

	protected void parseEof() {
		token_ = null;
		tokenType_ = EOF;
		nextTokenType_ = EOF;
	}

	public String skipToken() {
		int index = sql_.length();
		char quote = position_ < sql_.length() ? sql_.charAt(position_) : '\0';
		boolean quoting = quote == '\'' || quote == '(';
		if (quote == '(') {
			quote = ')';
		}
		for (int i = quoting ? position_ + 1 : position_; i < sql_.length(); ++i) {
			char c = sql_.charAt(i);
			if ((Character.isWhitespace(c) || c == ',' || c == ')' || c == '(')
					&& !quoting) {
				index = i;
				break;
			} else if (c == '/' && i + 1 < sql_.length()
					&& sql_.charAt(i + 1) == '*') {
				index = i;
				break;
			} else if (c == '-' && i + 1 < sql_.length()
					&& sql_.charAt(i + 1) == '-') {
				index = i;
				break;
			} else if (quoting && quote == '\'' && c == '\''
					&& (i + 1 >= sql_.length() || sql_.charAt(i + 1) != '\'')) {
				index = i + 1;
				break;
			} else if (quoting && c == quote) {
				index = i + 1;
				break;
			}
		}
		token_ = sql_.substring(position_, index);
		tokenType_ = SQL;
		nextTokenType_ = SQL;
		position_ = index;
		return token_;
	}

	public String skipWhitespace() {
		int index = skipWhitespace(position_);
		token_ = sql_.substring(position_, index);
		position_ = index;
		return token_;
	}

	private int skipWhitespace(int position) {
		int index = sql_.length();
		for (int i = position; i < sql_.length(); ++i) {
			char c = sql_.charAt(i);
			if (!Character.isWhitespace(c)) {
				index = i;
				break;
			}
		}
		return index;
	}
}