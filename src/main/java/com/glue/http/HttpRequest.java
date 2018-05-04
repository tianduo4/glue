package com.glue.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xushipeng
 * @create 2018-05-04 16:46
 */
@Slf4j
public class HttpRequest {
    private static final HttpDataFactory HTTP_DATA_FACTORY = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE); // Disk if size exceed

    private String  remoteAddress;
    private String  uri;
    private String  url;
    private String  protocol;
    private String  method;
    private boolean keepAlive;

    private ByteBuf body = Unpooled.copiedBuffer("", CharsetUtil.UTF_8);
    private Map<String, String> headers    = null;
    private Map<String, Object>       attributes = null;
    private Map<String, List<String>> parameters = new HashMap<>();
    private Map<String, String>       pathParams = null;
//    private Map<String, Cookie>       cookies    = new HashMap<>();
//    private Map<String, FileItem>     fileItems  = new HashMap<>();


    public HttpRequest(FullHttpRequest fullHttpRequest) {
        this.keepAlive = HttpUtil.isKeepAlive(fullHttpRequest);
//        String remoteAddress = ctx.channel().remoteAddress().toString();
//        httpRequest.remoteAddress = remoteAddress;
        this.url = fullHttpRequest.uri();
        int pathEndPos = this.url.indexOf('?');
        this.uri = pathEndPos < 0 ? this.url : this.url.substring(0, pathEndPos);
        this.protocol = fullHttpRequest.protocolVersion().text();
        this.method = fullHttpRequest.method().name();

        // headers
        HttpHeaders httpHeaders = fullHttpRequest.headers();
        if (httpHeaders.size() > 0) {
            this.headers = new HashMap<>(httpHeaders.size());
            httpHeaders.forEach((header) -> headers.put(header.getKey(), header.getValue()));
        } else {
            this.headers = new HashMap<>();
        }

        // body content
        this.body = fullHttpRequest.content().copy();

        // request query parameters
        Map<String, List<String>> parameters = new QueryStringDecoder(fullHttpRequest.uri(), CharsetUtil.UTF_8).parameters();
        if (null != parameters) {
            this.parameters = new HashMap<>();
            this.parameters.putAll(parameters);
        }
//
//        if (!HttpConstant.METHOD_GET.equals(fullHttpRequest.method().name())) {
//            HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(HTTP_DATA_FACTORY, fullHttpRequest);
//            decoder.getBodyHttpDatas().forEach(this::parseData);
//        }
//
//        // cookies
//        String cookie = header(HttpConstant.COOKIE_STRING);
//        cookie = cookie.length() > 0 ? cookie : header(HttpConstant.COOKIE_STRING.toLowerCase());
//        if (StringKit.isNotBlank(cookie)) {
//            ServerCookieDecoder.LAX.decode(cookie).forEach(this::parseCookie);
//        }

    }


//    public HttpRequest build(FullHttpRequest fullHttpRequest) {
//        HttpRequest httpRequest = new HttpRequest();
//        httpRequest.keepAlive = HttpUtil.isKeepAlive(fullHttpRequest);
////        String remoteAddress = ctx.channel().remoteAddress().toString();
////        httpRequest.remoteAddress = remoteAddress;
//        httpRequest.url = fullHttpRequest.uri();
//        int pathEndPos = httpRequest.url.indexOf('?');
//        httpRequest.uri = pathEndPos < 0 ? httpRequest.url : httpRequest.url.substring(0, pathEndPos);
//        httpRequest.protocol = fullHttpRequest.protocolVersion().text();
//        httpRequest.method = fullHttpRequest.method().name();
//
//        // headers
//        HttpHeaders httpHeaders = fullHttpRequest.headers();
//        if (httpHeaders.size() > 0) {
//            this.headers = new HashMap<>(httpHeaders.size());
//            httpHeaders.forEach((header) -> headers.put(header.getKey(), header.getValue()));
//        } else {
//            this.headers = new HashMap<>();
//        }
//
//        // body content
//        this.body = fullHttpRequest.content().copy();
//
//        // request query parameters
//        Map<String, List<String>> parameters = new QueryStringDecoder(fullHttpRequest.uri(), CharsetUtil.UTF_8).parameters();
//        if (null != parameters) {
//            this.parameters = new HashMap<>();
//            this.parameters.putAll(parameters);
//        }
////
////        if (!HttpConstant.METHOD_GET.equals(fullHttpRequest.method().name())) {
////            HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(HTTP_DATA_FACTORY, fullHttpRequest);
////            decoder.getBodyHttpDatas().forEach(this::parseData);
////        }
////
////        // cookies
////        String cookie = header(HttpConstant.COOKIE_STRING);
////        cookie = cookie.length() > 0 ? cookie : header(HttpConstant.COOKIE_STRING.toLowerCase());
////        if (StringKit.isNotBlank(cookie)) {
////            ServerCookieDecoder.LAX.decode(cookie).forEach(this::parseCookie);
////        }
//
//        return httpRequest;
//    }


    public String getUri() {
        return uri;
    }

    public String getUrl() {
        return url;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getMethod() {
        return method;
    }

    public boolean isKeepAlive() {
        return keepAlive;
    }

    public ByteBuf getBody() {
        return body;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public Map<String, List<String>> getParameters() {
        return parameters;
    }

    public Map<String, String> getPathParams() {
        return pathParams;
    }
}
