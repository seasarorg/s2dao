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
package org.seasar.dao.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Map;

/**
 * @author taedium
 *
 */
public class Procedures {

    public static Map params;

    public static boolean isAaa3Invoked;

    public static void procedureAaa1(String[] s) {
        s[0] = "aaaaa";
    }

    public static void procedureAaa2(String[] s, Timestamp[] t) {
        s[0] = "aaaaa2";
        t[0] = new Timestamp(System.currentTimeMillis());
    }

    public static void procedureAaa3() {
        isAaa3Invoked = true;
    }

    public static void procedureBbb1(String ccc) {
        params.put("ccc", ccc);
    }

    public static void procedureBbb2(String ccc, BigDecimal ddd, Timestamp eee) {
        params.put("ccc", ccc);
        params.put("ddd", ddd);
        params.put("eee", eee);
    }

    public static void procedureCcc1(String ccc, BigDecimal ddd, String[] eee) {
        params.put("ccc", ccc);
        params.put("ddd", ddd);
        params.put("eee", eee);
        eee[0] = ccc + ddd;
    }

    public static void procedureCcc2(String[] ccc, BigDecimal ddd, String[] eee) {
        params.put("ccc", ccc);
        params.put("ddd", ddd);
        params.put("eee", eee);
        ccc[0] = ddd.toString();
        eee[0] = ddd.multiply(ddd).toString();
    }

    public static void procedureDdd1(String[] ccc) {
        params.put("ccc", ccc);
        ccc[0] = ccc[0] + "cd";
    }

}
