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
package org.seasar.dao.node;

import org.seasar.dao.CommandContext;
import org.seasar.dao.IllegalBoolExpressionRuntimeException;
import org.seasar.framework.util.OgnlUtil;

/**
 * @author higa
 * 
 */
public class IfNode extends ContainerNode {

    private String expression;

    private Object parsedExpression;

    private ElseNode elseNode;

    public IfNode(String expression) {
        this.expression = expression;
        this.parsedExpression = OgnlUtil.parseExpression(expression);
    }

    public String getExpression() {
        return expression;
    }

    public ElseNode getElseNode() {
        return elseNode;
    }

    public void setElseNode(ElseNode elseNode) {
        this.elseNode = elseNode;
    }

    /**
     * @see org.seasar.dao.Node#accept(org.seasar.dao.QueryContext)
     */
    public void accept(CommandContext ctx) {
        Object result = OgnlUtil.getValue(parsedExpression, ctx);
        if (result instanceof Boolean) {
            if (((Boolean) result).booleanValue()) {
                super.accept(ctx);
                ctx.setEnabled(true);
            } else if (elseNode != null) {
                elseNode.accept(ctx);
                ctx.setEnabled(true);
            }
        } else {
            throw new IllegalBoolExpressionRuntimeException(expression);
        }
    }

}
