/*
 * Copyright 2004-2007 the Seasar Foundation and the Others.
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

/**
 * @author higa
 * 
 */
public class ContainerNode extends AbstractNode {

    public ContainerNode() {
    }

    /**
     * @see org.seasar.dao.Node#accept(org.seasar.dao.QueryContext)
     */
    public void accept(CommandContext ctx) {
        for (int i = 0; i < getChildSize(); ++i) {
            getChild(i).accept(ctx);
        }
    }
}