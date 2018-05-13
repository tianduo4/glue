package com.glue.constant;

import java.io.File;

/**
 * 系统常量
 *
 * @author xushipeng
 * @create 2018-05-03 16:24
 */
public interface SystemConstant {

    String CLASSPATH = new File(SystemConstant.class.getResource("/").getPath()).getPath();
    String HTTP_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";
    String       CONTENT_TYPE_JSON          = "application/json; charset=UTF-8";

    String ENV_KEY_BOOT_CONF = "boot_conf";
    String ENV_KEY_SERVER_ADDRESS = "server.address";
    String ENV_KEY_SERVER_PORT = "server.port";
    String ENC_KEY_NETTY_ACCEPT_THREAD_COUNT = "server.netty.accept-thread-count";
    String ENV_KEY_NETTY_IO_THREAD_COUNT = "server.netty.io-thread-count";
    String ENV_KEY_NETTY_SO_BACKLOG = "server.netty.so-backlog";
    String ENV_KEY_SSL_ENABLE = "http.ssl.enable";
    String ENV_KEY_GZIP_ENABLE = "http.gzip.enable";
    String ENV_KEY_CORS_ENABLE = "http.cors.enable";
    String ENV_KEY_SCAN_PACKAGE = "scan.package";


    int DEFAULT_SERVER_PORT = 9000;
    String DEFAULT_SERVER_ADDRESS = "0.0.0.0";
    int DEFAULT_NETTY_ACCEPT_THREAD_COUNT = 1;
    int DEFAULT_NETTY_IO_THREAD_COUNT = 0;
    int DEFAULT_KEY_NETTY_SO_BACKLOG = 8192;
    String LOCAL_IP_ADDRESS = "127.0.0.1";


}
