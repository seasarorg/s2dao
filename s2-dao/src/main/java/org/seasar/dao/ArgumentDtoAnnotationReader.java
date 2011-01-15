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
package org.seasar.dao;

import java.lang.reflect.Field;

import org.seasar.framework.beans.BeanDesc;

/**
 * メソッドの引数に使用されるDTOのアノテーションを読み取るインタフェースです。
 * 
 * @author taedium
 */
public interface ArgumentDtoAnnotationReader {

    /**
     * <code>PROCEDURE_PARAMETER</code>アノテーションの文字列を返します。
     * 
     * @param dtoDesc DTOのクラス記述
     * @param field フィールド
     * @return <code>PROCEDURE_PARAMETER</code>アノテーションが存在する場合はそのアノテーションの文字列、存在しない場合は<code>null</code>
     */
    String getProcedureParameter(BeanDesc dtoDesc, Field field);

    /**
     * <code>VALUE_TYPE</code>アノテーションの文字列を返します。
     * 
     * @param dtoDesc DTOのクラス記述
     * @param field フィールド
     * @return <code>VALUE_TYPE</code>アノテーションが存在する場合はそのアノテーションの文字列、存在しない場合は<code>null</code>
     */
    String getValueType(BeanDesc dtoDesc, Field field);
}