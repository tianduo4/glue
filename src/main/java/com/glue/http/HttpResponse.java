package com.glue.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.cookie.Cookie;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/**
 * @author xushipeng
 * @create 2018-05-04 18:04
 */
@NoArgsConstructor
public class HttpResponse {
    private HttpHeaders headers     = new DefaultHttpHeaders(false);
    private Set<Cookie> cookies     = new HashSet<>(4);
    private int                   statusCode  = 200;
    private boolean               isCommit    = false;
    private ChannelHandlerContext ctx         = null;
    private CharSequence          contentType = null;
    private CharSequence          dateString  = null;

    public static HttpResponse build(ChannelHandlerContext ctx) {
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.ctx = ctx;
        return httpResponse;
    }

}
