package com.glue.router;

import com.glue.http.Route;
import com.glue.utils.PathRegexUtils;
import com.glue.utils.PathUtils;
import com.glue.utils.StringUtils;
import io.netty.handler.codec.http.HttpMethod;
import lombok.extern.slf4j.Slf4j;

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
    private Map<String, Route> routes          = new HashMap<>();
    private Map<String, List<Route>> hooks           = new HashMap<>();

    private Map<HttpMethod, Map<Integer, FastRouteMappingInfo>> regexRoutes        = new HashMap<>();
    private Map<String, Route>                                  staticRoutes       = new HashMap<>();
    private Map<HttpMethod, Pattern>                            regexRoutePatterns = new HashMap<>();
    private Map<HttpMethod, Integer>                            indexes            = new HashMap<>();
    private Map<HttpMethod, StringBuilder>                      patternBuilders    = new HashMap<>();


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

    private class FastRouteMappingInfo {
        Route        route;
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
