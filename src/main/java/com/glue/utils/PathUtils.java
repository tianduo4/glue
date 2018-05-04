package com.glue.utils;

import com.glue.constant.HttpConstant;

/**
 * @author xushipeng
 * @create 2018-05-04 17:35
 */
public class PathUtils {

    public static String fixPath(String path) {
        if (null == path) {
            return HttpConstant.SLASH;
        }
        if (path.charAt(0) != '/') {
            path = HttpConstant.SLASH + path;
        }
        if (path.length() > 1 && path.endsWith(HttpConstant.SLASH)) {
            path = path.substring(0, path.length() - 1);
        }
//        if (!path.contains("\\s")) {
//            return path;
//        }
//        return VAR_FIXPATH_PATTERN.matcher(path).replaceAll("%20");
        return path;
    }
}
