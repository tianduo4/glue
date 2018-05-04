package com.glue.server.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerExpectContinueHandler;
import io.netty.handler.codec.http.cors.CorsConfig;
import io.netty.handler.codec.http.cors.CorsConfigBuilder;
import io.netty.handler.codec.http.cors.CorsHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.AsciiString;

import java.time.LocalDateTime;
import java.util.concurrent.ScheduledExecutorService;

public class ServerInitializer extends ChannelInitializer<SocketChannel> {
    private boolean enableGzip = Boolean.TRUE;
    private boolean enableCors = Boolean.TRUE;
    private boolean enableSsl = Boolean.TRUE;

    public ServerInitializer(boolean enableSsl, boolean enableGzip, boolean enableCors) {
        this.enableSsl = enableSsl;
        this.enableGzip = enableGzip;
        this.enableCors = enableCors;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline p = ch.pipeline();
        if (enableSsl) {
//        boolean sslEnable = env.getBoolean(ENV_KEY_SSL, false);
//        SslContext sslCtx = null;
//        if (sslEnable) {
//            String certFilePath       = env.get(ENV_KEY_SSL_CERT, null);
//            String privateKeyPath     = env.get(ENE_KEY_SSL_PRIVATE_KEY, null);
//            String privateKeyPassword = env.get(ENE_KEY_SSL_PRIVATE_KEY_PASS, null);
//
//            log.info("⬢ SSL CertChainFile  Path: {}", certFilePath);
//            log.info("⬢ SSL PrivateKeyFile Path: {}", privateKeyPath);
//            sslCtx = SslContextBuilder.forServer(new File(certFilePath), new File(privateKeyPath), privateKeyPassword).build();
//        }
//            p.addLast(sslCtx.newHandler(ch.alloc()));
        }
        if (enableGzip) {
            p.addLast(new HttpContentCompressor());
        }
        p.addLast(new HttpServerCodec(36192 * 2, 36192 * 8, 36192 * 16, false));
        p.addLast(new HttpServerExpectContinueHandler());
        p.addLast(new HttpObjectAggregator(Integer.MAX_VALUE));
        p.addLast(new ChunkedWriteHandler());
//        p.addLast(new HttpFileServerHandler()); //文件上传处理

        if (enableCors) {
            CorsConfig corsConfig = CorsConfigBuilder.forAnyOrigin().allowNullOrigin().allowCredentials().build();
            p.addLast(new CorsHandler(corsConfig));
        }
//        if (null != blade.webSocketPath()) {
//            p.addLast(new WebSocketServerProtocolHandler(blade.webSocketPath(), null, true));
//            p.addLast(new WebSocketHandler(blade));
//        }
//        service.scheduleWithFixedDelay(() -> date = new AsciiString(DateKit.gmtDate(LocalDateTime.now())), 1000, 1000, TimeUnit.MILLISECONDS);
        p.addLast(new ServerHandler());
    }
}

