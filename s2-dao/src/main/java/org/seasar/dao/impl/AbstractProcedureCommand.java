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
package org.seasar.dao.impl;

import org.seasar.dao.ProcedureMetaData;
import org.seasar.dao.SqlCommand;

/**
 * プロシージャのコマンドの抽象クラスです。
 * 
 * @author taedium
 */
public abstract class AbstractProcedureCommand implements SqlCommand {

    /**
     * プロシージャのメタデータからSQLを作成します。
     * 
     * @param procedureMetaData プロシージャのメタデータ
     * @return SQL
     */
    protected String createSql(final ProcedureMetaData procedureMetaData) {
        final StringBuffer buf = new StringBuffer();
        buf.append("{");
        int size = procedureMetaData.getParameterTypeSize();
        if (procedureMetaData.hasReturnParameterType()) {
            buf.append("? = ");
            size--;
        }
        buf.append("call ");
        buf.append(procedureMetaData.getProcedureName());
        buf.append(" (");
        for (int i = 0; i < size; i++) {
            buf.append("?, ");
        }
        if (size > 0) {
            buf.setLength(buf.length() - 2);
        }
        buf.append(")}");
        return buf.toString();
    }

}
