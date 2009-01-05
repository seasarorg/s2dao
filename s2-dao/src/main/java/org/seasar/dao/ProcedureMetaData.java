/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
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
package org.seasar.dao;

/**
 * プロシージャのメタデータです。
 * 
 * @author taedium
 */
public interface ProcedureMetaData {

    /**
     * プロシージャ名を返します。
     * 
     * @return プロシージャ名
     */
    String getProcedureName();

    /**
     * プロシージャのパラメータのタイプを返します。
     * 
     * @param index 位置
     * @return プロシージャのパラメータのタイプ
     */
    ProcedureParameterType getParameterType(int index);

    /**
     * プロシージャのパラメータのタイプを返します。
     * 
     * @param parameterName パラメータ名
     * @return プロシージャのパラメータのタイプ
     */
    ProcedureParameterType getParameterType(String parameterName);

    /**
     * プロシージャのパラメータのサイズを返します。
     * 
     * @return　プロシージャのパラメータのサイズ
     */
    int getParameterTypeSize();

    /**
     * <code>RETURN</code>パラメータを持っている場合に<code>true</code>を返します。
     * 
     * @return <code>RETURN</code>パラメータを持っている場合に<code>true</code>、そうでない場合<code>false</code>
     */
    boolean hasReturnParameterType();

}
