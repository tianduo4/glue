package com.glue.http;

import com.glue.utils.PathUtils;
import io.netty.handler.codec.http.HttpMethod;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author xushipeng
 * @create 2018-05-04 17:10
 */
public class RouteHandler {

    public Route lookupRoute(String httpMethod, String path) throws Exception {
        path = PathUtils.fixPath(path);
        String routeKey = path + '#' + httpMethod.toUpperCase();
//        Route  route    = staticRoutes.get(routeKey);
//        if (null != route) {
//            return route;
//        }
//        route = staticRoutes.get(path + "#ALL");
//        if (null != route) {
//            return route;
//        }

        Map<String, String> uriVariables  = new LinkedHashMap<>();
        HttpMethod requestMethod = HttpMethod.valueOf(httpMethod);
        try {
//            Pattern pattern = regexRoutePatterns.get(requestMethod);
//            if (null == pattern) {
//                return null;
//            }
//            Matcher matcher = null;
//            if (path != null) {
//                matcher = pattern.matcher(path);
//            }
//            boolean matched = false;
//            if (matcher != null) {
//                matched = matcher.matches();
//            }
//            if (!matched) {
//                requestMethod = HttpMethod.ALL;
//                pattern = regexRoutePatterns.get(requestMethod);
//                if (null == pattern) {
//                    return null;
//                }
//                if (path != null) {
//                    matcher = pattern.matcher(path);
//                }
//                matched = matcher != null && matcher.matches();
//            }
//            if (matched) {
//                int i;
//                for (i = 1; matcher.group(i) == null; i++) ;
//                FastRouteMappingInfo mappingInfo = regexRoutes.get(requestMethod).get(i);
//                route = mappingInfo.getRoute();
//
//                // find path variable
//                String uriVariable;
//                int    j = 0;
//                while (++i <= matcher.groupCount() && (uriVariable = matcher.group(i)) != null) {
//                    uriVariables.put(mappingInfo.getVariableNames().get(j++), uriVariable);
//                }
//                route.setPathParams(uriVariables);
//                log.trace("lookup path: " + path + " uri variables: " + uriVariables);
//            }
//            return route;
        } catch (Exception e) {
            throw e;
        }
        return null;
    }

}
