/*
 * Copyright 2004-2011 the Seasar Foundation and the Others.
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

import java.util.Stack;
import java.util.regex.Pattern;

import org.seasar.dao.EndCommentNotFoundRuntimeException;
import org.seasar.dao.IfConditionNotFoundRuntimeException;
import org.seasar.dao.Node;
import org.seasar.dao.SqlParser;
import org.seasar.dao.SqlTokenizer;
import org.seasar.dao.node.BeginNode;
import org.seasar.dao.node.BindVariableNode;
import org.seasar.dao.node.ContainerNode;
import org.seasar.dao.node.ElseNode;
import org.seasar.dao.node.EmbeddedValueNode;
import org.seasar.dao.node.IfNode;
import org.seasar.dao.node.ParenBindVariableNode;
import org.seasar.dao.node.PrefixSqlNode;
import org.seasar.dao.node.SqlNode;
import org.seasar.framework.util.StringUtil;

/**
 * @author higa
 * 
 */
public class SqlParserImpl implements SqlParser {

    private static final Pattern lineBreak = Pattern.compile("(\\r(\\n)?|\\n)");

    private SqlTokenizer tokenizer;

    private Stack nodeStack = new Stack();

    public SqlParserImpl(String sql) {
        sql = sql.trim();
        if (sql.endsWith(";")) {
            sql = sql.substring(0, sql.length() - 1);
        }
        sql = deleteQuestionInLineComment(sql);
        tokenizer = new SqlTokenizerImpl(sql);
    }

    public Node parse() {
        push(new ContainerNode());
        while (SqlTokenizer.EOF != tokenizer.next()) {
            parseToken();
        }
        return pop();
    }

    protected String deleteQuestionInLineComment(String sql) {
        String[] sqlParts = lineBreak.split(sql);
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < sqlParts.length; i++) {
            int pos = sqlParts[i].indexOf("--");
            if (pos != -1 && (sqlParts[i].indexOf("ELSE") == -1)) {
                buf.append(sqlParts[i].substring(0, pos));
                buf.append(sqlParts[i].substring(pos).replaceAll("\\?", ""));
            } else {
                buf.append(sqlParts[i]);
            }
            if (i + 1 != sqlParts.length) {
                buf.append("\n");
            }
        }
        return buf.toString();
    }

    protected void parseToken() {
        switch (tokenizer.getTokenType()) {
        case SqlTokenizer.SQL:
            parseSql();
            break;
        case SqlTokenizer.COMMENT:
            parseComment();
            break;
        case SqlTokenizer.ELSE:
            parseElse();
            break;
        case SqlTokenizer.BIND_VARIABLE:
            parseBindVariable();
            break;
        }
    }

    protected void parseSql() {
        String sql = tokenizer.getToken();
        if (isElseMode()) {
            sql = StringUtil.replace(sql, "--", "");
        }
        Node node = peek();
        if ((node instanceof IfNode || node instanceof ElseNode)
                && node.getChildSize() == 0) {

            SqlTokenizer st = new SqlTokenizerImpl(sql);
            st.skipWhitespace();
            String token = st.skipToken();
            st.skipWhitespace();
            if (sql.startsWith(",")) {
                if (sql.startsWith(", ")) {
                    node.addChild(new PrefixSqlNode(", ", sql.substring(2)));
                } else {
                    node.addChild(new PrefixSqlNode(",", sql.substring(1)));
                }
            } else if ("AND".equalsIgnoreCase(token)
                    || "OR".equalsIgnoreCase(token)) {
                node.addChild(new PrefixSqlNode(st.getBefore(), st.getAfter()));
            } else {
                node.addChild(new SqlNode(sql));
            }
        } else {
            node.addChild(new SqlNode(sql));
        }
    }

    protected void parseComment() {
        String comment = tokenizer.getToken();
        if (isTargetComment(comment)) {
            if (isIfComment(comment)) {
                parseIf();
            } else if (isBeginComment(comment)) {
                parseBegin();
            } else if (isEndComment(comment)) {
                return;
            } else {
                parseCommentBindVariable();
            }
        } else if (comment != null && 0 < comment.length()) {
            String before = tokenizer.getBefore();
            peek().addChild(
                    new SqlNode(before.substring(before.lastIndexOf("/*"))
                            .replaceAll("\\?", "")));
        }
    }

    protected void parseIf() {
        String condition = tokenizer.getToken().substring(2).trim();
        if (StringUtil.isEmpty(condition)) {
            throw new IfConditionNotFoundRuntimeException();
        }
        IfNode ifNode = new IfNode(condition);
        peek().addChild(ifNode);
        push(ifNode);
        parseEnd();
    }

    protected void parseBegin() {
        BeginNode beginNode = new BeginNode();
        peek().addChild(beginNode);
        push(beginNode);
        parseEnd();
    }

    protected void parseEnd() {
        while (SqlTokenizer.EOF != tokenizer.next()) {
            if (tokenizer.getTokenType() == SqlTokenizer.COMMENT
                    && isEndComment(tokenizer.getToken())) {

                pop();
                return;
            }
            parseToken();
        }
        throw new EndCommentNotFoundRuntimeException();
    }

    protected void parseElse() {
        Node parent = peek();
        if (!(parent instanceof IfNode)) {
            return;
        }
        IfNode ifNode = (IfNode) pop();
        ElseNode elseNode = new ElseNode();
        ifNode.setElseNode(elseNode);
        push(elseNode);
        tokenizer.skipWhitespace();
    }

    protected void parseCommentBindVariable() {
        String expr = tokenizer.getToken();
        String s = tokenizer.skipToken();
        if (s.startsWith("(") && s.endsWith(")")) {
            peek().addChild(new ParenBindVariableNode(expr));
        } else if (expr.startsWith("$")) {
            peek().addChild(new EmbeddedValueNode(expr.substring(1)));
        } else {
            peek().addChild(new BindVariableNode(expr));
        }
    }

    protected void parseBindVariable() {
        String expr = tokenizer.getToken();
        peek().addChild(new BindVariableNode(expr));
    }

    protected Node pop() {
        return (Node) nodeStack.pop();
    }

    protected Node peek() {
        return (Node) nodeStack.peek();
    }

    protected void push(Node node) {
        nodeStack.push(node);
    }

    protected boolean isElseMode() {
        for (int i = 0; i < nodeStack.size(); ++i) {
            if (nodeStack.get(i) instanceof ElseNode) {
                return true;
            }
        }
        return false;
    }

    private static boolean isTargetComment(String comment) {
        return comment != null && comment.length() > 0
                && Character.isJavaIdentifierStart(comment.charAt(0));
    }

    private static boolean isIfComment(String comment) {
        return comment.startsWith("IF");
    }

    private static boolean isBeginComment(String content) {
        return content != null && "BEGIN".equals(content);
    }

    private static boolean isEndComment(String content) {
        return content != null && "END".equals(content);
    }
}
