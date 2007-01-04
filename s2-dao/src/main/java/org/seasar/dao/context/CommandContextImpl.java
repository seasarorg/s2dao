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
package org.seasar.dao.context;

import java.util.ArrayList;
import java.util.List;

import ognl.OgnlRuntime;

import org.seasar.dao.CommandContext;
import org.seasar.framework.log.Logger;
import org.seasar.framework.util.CaseInsensitiveMap;

/**
 * @author higa
 * 
 */
public class CommandContextImpl implements CommandContext {

    private static Logger logger = Logger.getLogger(CommandContextImpl.class);

    private CaseInsensitiveMap args = new CaseInsensitiveMap();

    private CaseInsensitiveMap argTypes = new CaseInsensitiveMap();

    private StringBuffer sqlBuf = new StringBuffer(100);

    private List bindVariables = new ArrayList();

    private List bindVariableTypes = new ArrayList();

    private boolean enabled = true;

    private CommandContext parent;

    static {
        OgnlRuntime.setPropertyAccessor(CommandContext.class,
                new CommandContextPropertyAccessor());
    }

    public CommandContextImpl() {
    }

    public CommandContextImpl(CommandContext parent) {
        this.parent = parent;
        enabled = false;
    }

    /**
     * @see org.seasar.dao.CommandContext#getArg(java.lang.String)
     */
    public Object getArg(String name) {
        if (args.containsKey(name)) {
            return args.get(name);
        } else if (parent != null) {
            return parent.getArg(name);
        } else {
            if (args.size() == 1) {
                return args.get(0);
            }
            logger.log("WDAO0001", new Object[] { name });
            return null;
        }
    }

    /**
     * @see org.seasar.dao.CommandContext#getArgType(java.lang.String)
     */
    public Class getArgType(String name) {
        if (argTypes.containsKey(name)) {
            return (Class) argTypes.get(name);
        } else if (parent != null) {
            return parent.getArgType(name);
        } else {
            if (argTypes.size() == 1) {
                return (Class) argTypes.get(0);
            }
            logger.log("WDAO0001", new Object[] { name });
            return null;
        }
    }

    /**
     * @see org.seasar.dao.CommandContext#addArg(java.lang.String,
     *      java.lang.Object, java.lang.Class)
     */
    public void addArg(String name, Object arg, Class argType) {
        args.put(name, arg);
        argTypes.put(name, argType);
    }

    /**
     * @see org.seasar.dao.CommandContext#getSql()
     */
    public String getSql() {
        return sqlBuf.toString();
    }

    /**
     * @see org.seasar.dao.CommandContext#getBindVariables()
     */
    public Object[] getBindVariables() {
        return bindVariables.toArray(new Object[bindVariables.size()]);
    }

    /**
     * @see org.seasar.dao.CommandContext#getBindVariableTypes()
     */
    public Class[] getBindVariableTypes() {
        return (Class[]) bindVariableTypes.toArray(new Class[bindVariableTypes
                .size()]);
    }

    /**
     * @see org.seasar.dao.CommandContext#addSql(java.lang.String)
     */
    public CommandContext addSql(String sql) {
        sqlBuf.append(sql);
        return this;
    }

    /**
     * @see org.seasar.dao.CommandContext#addSql(java.lang.String,
     *      java.lang.Object, java.lang.Class)
     */
    public CommandContext addSql(String sql, Object bindVariable,
            Class bindVariableType) {

        sqlBuf.append(sql);
        bindVariables.add(bindVariable);
        bindVariableTypes.add(bindVariableType);
        return this;
    }

    public CommandContext addSql(String sql, Object[] bindVariables,
            Class[] bindVariableTypes) {

        sqlBuf.append(sql);
        for (int i = 0; i < bindVariables.length; ++i) {
            this.bindVariables.add(bindVariables[i]);
            this.bindVariableTypes.add(bindVariableTypes[i]);
        }
        return this;
    }

    /**
     * @see org.seasar.dao.CommandContext#isEnabled()
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @see org.seasar.dao.CommandContext#setEnabled(boolean)
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}