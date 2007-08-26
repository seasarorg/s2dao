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
package org.seasar.dao;

import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.PropertyDesc;

/**
 * メソッドの引数に使用されるDTOのアノテーションを読み取るインタフェースです。
 * 
 * @author taedium
 */
public interface ArgumentDtoAnnotationReader {

    /**
     * プロシージャのパラメータを表すDTOの場合に<code>true</code>を返します。
     * 
     * @param argClass
     * @return プロシージャのパラメータを表すDTOの場合<code>true</code>、そうでない場合<code>false</code>
     */
    boolean isProcedureParameters(Class argClass);

    /**
     * プロシージャのパラメータのタイプを返します。
     * 
     * @param dtoDesc DTOのクラス記述
     * @param propertyDesc プロパティ記述
     * @return ProcedureParameterアノテーションが存在する場合はそのアノテーションが示す
     * {@link ProcedureParameterType}、存在しない場合はデフォルトの{@link ProcedureParameterType}
     */
    ProcedureParameterType getProcedureParameter(BeanDesc dtoDesc,
            PropertyDesc propertyDesc);

    /**
     * ValueTypeアノテーションの文字列を返します。
     * 
     * @param dtoDesc DTOのクラス記述
     * @param propertyDesc プロパティ記述
     * @return ValueTypeアノテーションが存在する場合はそのアノテーションの文字列、存在しない場合は<code>null</code>
     */
    String getValueType(BeanDesc dtoDesc, PropertyDesc propertyDesc);
}