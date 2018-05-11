package com.glue.http;

import io.netty.handler.codec.http.HttpMethod;
import lombok.*;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xushipeng
 * @create 2018-05-04 17:06
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Route {

    /**
     * HTTP Request Method
     */
    private HttpMethod httpMethod;

    /**
     * Route path
     */
    private String path;

    /**
     * Logical controller object
     */
    private Object target;

    /**
     * PathKit Class EventType
     */
    private Class<?> targetType;

    /**
     * Implementation logic controller method
     */
    private Method action;

    private int sort = Integer.MAX_VALUE;

    /**
     * Url path params
     */
    private Map<String, String> pathParams = new HashMap<>(8);
}
