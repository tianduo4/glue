package com.glue.constant;

import io.netty.util.AsciiString;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xushipeng
 * @create 2018-05-03 16:58
 */

public interface HttpConstant {

    String IF_MODIFIED_SINCE   = "IF_MODIFIED_SINCE";
    String USER_AGENT          = "User-Agent";
    String CONTENT_TYPE_STRING = "Content-Type";
    String COOKIE_STRING       = "Cookie";
    String METHOD_GET          = "GET";
    String IE_UA               = "MSIE";
    String DEFAULT_SESSION_KEY = "SESSION";
    String SLASH               = "/";
    char   CHAR_SLASH          = '/';
    char   CHAR_POINT          = '.';

    CharSequence CONNECTION     = AsciiString.cached("Connection");
    CharSequence CONTENT_LENGTH = AsciiString.cached("Content-Length");
    CharSequence CONTENT_TYPE   = AsciiString.cached("Content-Type");
    CharSequence DATE           = AsciiString.cached("Date");
    CharSequence LOCATION       = AsciiString.cached("Location");
    CharSequence X_POWER_BY     = AsciiString.cached("X-Powered-By");
    CharSequence EXPIRES        = AsciiString.cached("Expires");
    CharSequence CACHE_CONTROL  = AsciiString.cached("Cache-Control");
    CharSequence LAST_MODIFIED  = AsciiString.cached("Last-Modified");
    CharSequence SERVER         = AsciiString.cached("Server");
    CharSequence SET_COOKIE     = AsciiString.cached("Set-Cookie");
    CharSequence KEEP_ALIVE     = AsciiString.cached("Keep-Alive");

    String CONTENT_TYPE_HTML = "text/html; charset=UTF-8";

//    Map<CharSequence, CharSequence> contentTypes = new ConcurrentHashMap<>(8);
//
//    static CharSequence getContentType(CharSequence contentType) {
//        if (null == contentType) {
//            contentType = CONTENT_TYPE_HTML;
//        }
//        if (contentTypes.containsKey(contentType)) {
//            return contentTypes.get(contentType);
//        }
//        contentTypes.put(contentType, AsciiString.cached(String.valueOf(contentType)));
//        return contentTypes.get(contentType);
//    }
}
