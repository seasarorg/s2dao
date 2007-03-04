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

import org.seasar.framework.container.factory.PathResolver;

/**
 * @author koichik
 * @author azusa
 */
public class PathResolverImpl implements PathResolver {

    protected static boolean olderS23 = isOlderS23();

    protected static String suffix = "";

    public static void setSuffix(String suffix) {
        PathResolverImpl.suffix = suffix == null ? "" : suffix;
    }

    public String resolvePath(String context, String path) {
        if (olderS23 && "j2ee.dicon".equals(path)) {
            path = "j2ee_s23" + suffix + ".dicon";
        } else if ("jdbc.dicon".equals(path)) {
            path = "jdbc" + suffix + ".dicon";
        }
        return path;
    }

    protected static boolean isOlderS23() {
        try {
            Class.forName("org.seasar.framework.env.Env");
            return false;
        } catch (Throwable ignore) {
        }
        return true;
    }

}
