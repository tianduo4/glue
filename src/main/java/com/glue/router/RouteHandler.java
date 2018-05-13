package com.glue.router;

import com.glue.http.Route;
import com.glue.ioc.annotation.Path;
import com.glue.router.annotation.DeleteRoute;
import com.glue.router.annotation.GetRoute;
import com.glue.router.annotation.PostRoute;
import com.glue.router.annotation.PutRoute;
import com.glue.utils.*;
import io.netty.handler.codec.http.HttpMethod;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * @author xushipeng
 * @create 2018-05-11 17:20
 */
@Slf4j
public class RouteHandler {

    private static final Pattern PATH_VARIABLE_PATTERN = Pattern.compile("/([^:/]*):([^/]+)");

    // Storage URL and route
    private Map<String, Route> routes = new HashMap<>();
    private Map<String, List<Route>> hooks = new HashMap<>();

    private Map<HttpMethod, Map<Integer, FastRouteMappingInfo>> regexRoutes = new HashMap<>();
    private Map<String, Route> staticRoutes = new HashMap<>();
    private Map<HttpMethod, Pattern> regexRoutePatterns = new HashMap<>();
    private Map<HttpMethod, Integer> indexes = new HashMap<>();
    private Map<HttpMethod, StringBuilder> patternBuilders = new HashMap<>();


    public static RouteHandler empty() {
        return new RouteHandler();
    }

    /**
     * Parse all routing in a controller
     *
     * @param routeType resolve the routing class,
     *                  e.g RouteHandler.class or some controller class
     */
    public void addRouter(final Class<?> routeType, Object controller) {

        Method[] methods = routeType.getDeclaredMethods();
        if (CollectionUtils.isEmpty(methods)) {
            return;
        }

        String nameSpace = null, suffix = null;
        if (null != routeType.getAnnotation(Path.class)) {
            nameSpace = routeType.getAnnotation(Path.class).value();
            suffix = routeType.getAnnotation(Path.class).suffix();
        }

        if (null == nameSpace) {
            log.warn("Route [{}] not path annotation", routeType.getName());
            return;
        }

        for (Method method : methods) {

            com.glue.router.annotation.Route mapping = method.getAnnotation(com.glue.router.annotation.Route.class);
            GetRoute getRoute = method.getAnnotation(GetRoute.class);
            PostRoute postRoute = method.getAnnotation(PostRoute.class);
            PutRoute putRoute = method.getAnnotation(PutRoute.class);
            DeleteRoute deleteRoute = method.getAnnotation(DeleteRoute.class);

            this.parseRoute(RouteStruct.builder().mapping(mapping)
                    .getRoute(getRoute).postRoute(postRoute)
                    .putRoute(putRoute).deleteRoute(deleteRoute)
                    .nameSpace(nameSpace)
                    .suffix(suffix).routeType(routeType)
                    .controller(controller).method(method)
                    .build());
        }
    }

    Route addRoute(Route route) {
        String     path           = route.getPath();
        HttpMethod httpMethod     = route.getHttpMethod();
        Object     controller     = route.getTarget();
        Class<?>   controllerType = route.getTargetType();
        Method     method         = route.getAction();
        return addRoute(httpMethod, path, controller, controllerType, method);
    }

    private Route addRoute(HttpMethod httpMethod, String path, Object controller, Class<?> controllerType, Method method) {

        // [/** | /*]
        path = "*".equals(path) ? "/.*" : path;
        path = path.replace("/**", "/.*").replace("/*", "/.*");

        String key = path + "#" + httpMethod.toString();

        // exist
        if (this.routes.containsKey(key)) {
            log.warn("\tRoute {} -> {} has exist", path, httpMethod.toString());
        }

        Route route = new Route(httpMethod, path, controller, controllerType, method);
//        if (BladeKit.isWebHook(httpMethod)) {
//            Order order = controllerType.getAnnotation(Order.class);
//            if (null != order) {
//                route.setSort(order.value());
//            }
//            if (this.hooks.containsKey(key)) {
//                this.hooks.get(key).add(route);
//            } else {
//                List<Route> empty = new ArrayList<>();
//                empty.add(route);
//                this.hooks.put(key, empty);
//            }
//        } else {
            this.routes.put(key, route);
//        }
        return route;
    }

    private void parseRoute(RouteStruct routeStruct) {
        // build multiple route
        HttpMethod methodType = routeStruct.getMethod();
        String[]   paths      = routeStruct.getPaths();
        if (paths.length > 0) {
            for (String path : paths) {
                String pathV = getRoutePath(path, routeStruct.nameSpace, routeStruct.suffix);

                this.addRoute(Route.builder()
                        .target(routeStruct.controller)
                        .targetType(routeStruct.routeType)
                        .action(routeStruct.method)
                        .path(pathV)
                        .httpMethod(methodType)
                        .build());
            }
        }
    }

    private String getRoutePath(String value, String nameSpace, String suffix) {
        String path = value.startsWith("/") ? value : "/" + value;
        nameSpace = nameSpace.startsWith("/") ? nameSpace : "/" + nameSpace;
        path = nameSpace + path;
        path = path.replaceAll("[/]+", "/");
        path = path.length() > 1 && path.endsWith("/") ? path.substring(0, path.length() - 1) : path;
        path = path + suffix;
        return path;
    }

    /**
     * register route to container
     */
    public void register() {
        routes.values().forEach(route -> log.info("Add route => {}", route));
        hooks.values().forEach(route -> log.info("Add hook  => {}", route));

        Stream.of(routes.values(), hooks.values().stream().findAny().orElse(new ArrayList<>()))
                .flatMap(Collection::stream).forEach(this::registerRoute);

        patternBuilders.keySet().stream()
//                .filter(BladeKit::notIsWebHook)
                .forEach(httpMethod -> {
                    StringBuilder patternBuilder = patternBuilders.get(httpMethod);
                    if (patternBuilder.length() > 1) {
                        patternBuilder.setCharAt(patternBuilder.length() - 1, '$');
                    }
                    log.debug("Fast Route Method: {}, regex: {}", httpMethod, patternBuilder);
                    regexRoutePatterns.put(httpMethod, Pattern.compile(patternBuilder.toString()));
                });
    }


    private void registerRoute(Route route) {
        String path = parsePath(route.getPath());
        Matcher matcher = null;
        if (path != null) {
            matcher = PATH_VARIABLE_PATTERN.matcher(path);
        }
        boolean find = false;
        List<String> uriVariableNames = new ArrayList<>();
        while (matcher != null && matcher.find()) {
            if (!find) {
                find = true;
            }
            String regexName = matcher.group(1);
            String regexValue = matcher.group(2);

            // just a simple path param
            if (StringUtils.isBlank(regexName)) {
                uriVariableNames.add(regexValue);
            } else {
                //regex path param
                uriVariableNames.add(regexName);
            }
        }
        HttpMethod httpMethod = route.getHttpMethod();
//        if (find || BladeKit.isWebHook(httpMethod)) {
        if (find) {
            if (regexRoutes.get(httpMethod) == null) {
                regexRoutes.put(httpMethod, new HashMap<>());
                patternBuilders.put(httpMethod, new StringBuilder("^"));
                indexes.put(httpMethod, 1);
            }
            int i = indexes.get(httpMethod);
            regexRoutes.get(httpMethod).put(i, new FastRouteMappingInfo(route, uriVariableNames));
            indexes.put(httpMethod, i + uriVariableNames.size() + 1);
            patternBuilders.get(httpMethod).append(new PathRegexUtils().parsePath(path));
        } else {
            String routeKey = path + '#' + httpMethod.toString();
            staticRoutes.putIfAbsent(routeKey, route);
        }
    }

    private String parsePath(String path) {
        path = PathUtils.fixPath(path);
        try {
            URI uri = new URI(path);
            return uri.getPath();
        } catch (URISyntaxException e) {
            log.warn("parse [" + path + "] error", e);
            return path;
        }
    }

    public Route lookupRoute(String httpMethod, String path) throws Exception {
        path = parsePath(path);
        String routeKey = path + '#' + httpMethod.toUpperCase();
        Route  route    = staticRoutes.get(routeKey);
        if (null != route) {
            return route;
        }
        route = staticRoutes.get(path + "#ALL");
        if (null != route) {
            return route;
        }

        route=routes.get(routeKey);

//        Map<String, String> uriVariables  = new LinkedHashMap<>();
//        HttpMethod          requestMethod = HttpMethod.valueOf(httpMethod);
//        try {
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
////            if (!matched) {
////                requestMethod = HttpMethod.ALL;
////                pattern = regexRoutePatterns.get(requestMethod);
////                if (null == pattern) {
////                    return null;
////                }
////                if (path != null) {
////                    matcher = pattern.matcher(path);
////                }
////                matched = matcher != null && matcher.matches();
////            }
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
            return route;
//        } catch (Exception e) {
//            throw e;
//        }
    }

    private class FastRouteMappingInfo {
        Route route;
        List<String> variableNames;

        FastRouteMappingInfo(Route route, List<String> variableNames) {
            this.route = route;
            this.variableNames = variableNames;
        }

        public Route getRoute() {
            return route;
        }

        List<String> getVariableNames() {
            return variableNames;
        }
    }
}
