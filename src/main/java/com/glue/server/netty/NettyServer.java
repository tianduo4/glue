package com.glue.server.netty;

import com.glue.server.ILifeCycle;
import com.glue.utils.NamedThreadFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import static com.glue.constant.SystemConstant.CLASSPATH;

/**
 * @author xushipeng
 * @create 2018-05-03 16:20
 */
@Slf4j
public class NettyServer implements ILifeCycle {
    @Override
    public void startup() {
        long startTime = System.currentTimeMillis();
        String appName="Glue";
//        String appName = environment.get(ENV_KEY_APP_NAME, "Blade");

        log.info("Environment: jdk.version    => {}", System.getProperty("java.version"));
        log.info("Environment: user.dir       => {}", System.getProperty("user.dir"));
        log.info("Environment: java.io.tmpdir => {}", System.getProperty("java.io.tmpdir"));
        log.info("Environment: user.timezone  => {}", System.getProperty("user.timezone"));
        log.info("Environment: file.encoding  => {}", System.getProperty("file.encoding"));
        log.info("Environment: classpath      => {}", CLASSPATH);

        this.initConfig();
//        WebContext.init(blade, "/");

        this.initIoc();

//        this.shutdownHook();

//        this.watchEnv();

        try {
            this.startServer();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        log.info("⬢ {} initialize successfully, Time elapsed: {} ms", appName, (System.currentTimeMillis() - startTime));

    }



    @Override
    public void shutdown() {

    }

    private void initConfig() {
    }

    private void initIoc() {
    }

    private void startServer() throws InterruptedException {
        // Configure the server.
//        int backlog = environment.getInt(ENV_KEY_NETTY_SO_BACKLOG, 8192);
        int backlog = 8192;

        ServerBootstrap b = new ServerBootstrap();
        b.option(ChannelOption.SO_BACKLOG, backlog);
        b.option(ChannelOption.SO_REUSEADDR, true);
        b.childOption(ChannelOption.SO_REUSEADDR, true);

        //        int acceptThreadCount = environment.getInt(ENC_KEY_NETTY_ACCEPT_THREAD_COUNT, 1);
//        int ioThreadCount     = environment.getInt(ENV_KEY_NETTY_IO_THREAD_COUNT, 0);
        int acceptThreadCount=1;
        int ioThreadCount=0;

        EventLoopGroup bossGroup = new NioEventLoopGroup(acceptThreadCount, new NamedThreadFactory("nio-boss@"));
        EventLoopGroup workerGroup = new NioEventLoopGroup(ioThreadCount, new NamedThreadFactory("nio-worker@"));
        b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class);

        b.handler(new LoggingHandler(LogLevel.DEBUG))
//                .childHandler(new HttpServerInitializer(sslCtx, blade, bossGroup.next()));
                  .childHandler(new ServerInitializer());

//        String address = environment.get(ENV_KEY_SERVER_ADDRESS, DEFAULT_SERVER_ADDRESS);
//        int    port    = environment.getInt(ENV_KEY_SERVER_PORT, DEFAULT_SERVER_PORT);
        String address="127.0.0.1";
        int    port =4000;
        Channel  channel = b.bind(address, port).sync().channel();
        log.info("⬢ Glue start with {}:{}", address, port);

        channel.closeFuture().sync();

    }

    public static void main(String[] args) {
        NettyServer server=new NettyServer();
        server.startup();
    }
}
