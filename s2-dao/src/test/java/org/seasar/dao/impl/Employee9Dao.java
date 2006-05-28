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
package org.seasar.dao.impl;

/**
 * @author manhole
 */
public interface Employee9Dao {

    public Class BEAN = Employee9.class;

    public int insert(Employee9 employee);

    public int update(Employee9 employee);

    public int findBy(Employee9 employee);

    public String findByEname_ARGS = "eName";

    public int findByEname(String ename);

}
