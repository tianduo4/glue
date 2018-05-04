package com.glue.server.netty;

import com.glue.constant.SystemConstant;
import com.glue.server.ILifeCycle;
import com.glue.utils.Environment;
import com.glue.utils.NamedThreadFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Optional;
import java.util.stream.Stream;

import static com.glue.constant.SystemConstant.CLASSPATH;

/**
 * @author xushipeng
 * @create 2018-05-03 16:20
 */
@Slf4j
public class NettyServer implements ILifeCycle {
    private Environment env = Environment.empty();

    @Override
    public void startup() {
        long startTime = System.currentTimeMillis();
        String appName = "Glue";
//        String appName = environment.get(ENV_KEY_APP_NAME, "Blade");

        log.info("Environment: jdk.version    => {}", System.getProperty("java.version"));
        log.info("Environment: user.dir       => {}", System.getProperty("user.dir"));
        log.info("Environment: java.io.tmpdir => {}", System.getProperty("java.io.tmpdir"));
        log.info("Environment: user.timezone  => {}", System.getProperty("user.timezone"));
        log.info("Environment: file.encoding  => {}", System.getProperty("file.encoding"));
        log.info("Environment: classpath      => {}", SystemConstant.CLASSPATH);

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
        String bootConf = env.get(SystemConstant.ENV_KEY_BOOT_CONF, "classpath:app.properties");
        log.info("current environment file is: {}", bootConf);
        Environment bootEnv = Environment.of(bootConf);
        if (bootEnv != null) {
            bootEnv.props().forEach((key, value) -> env.set(key.toString(), value));
        }
    }

    private void initIoc() {
    }

    private void startServer() throws InterruptedException {
        int backlog = env.getInt(SystemConstant.ENV_KEY_NETTY_SO_BACKLOG, SystemConstant.DEFAULT_KEY_NETTY_SO_BACKLOG);
        int acceptThreadCount = env.getInt(SystemConstant.ENC_KEY_NETTY_ACCEPT_THREAD_COUNT, SystemConstant.DEFAULT_NETTY_ACCEPT_THREAD_COUNT);
        int ioThreadCount = env.getInt(SystemConstant.ENV_KEY_NETTY_IO_THREAD_COUNT, SystemConstant.DEFAULT_NETTY_IO_THREAD_COUNT);
        String address = env.get(SystemConstant.ENV_KEY_SERVER_ADDRESS, SystemConstant.DEFAULT_SERVER_ADDRESS);
        int port = env.getInt(SystemConstant.ENV_KEY_SERVER_PORT, SystemConstant.DEFAULT_SERVER_PORT);
        boolean enableSsl = env.getBoolean(SystemConstant.ENV_KEY_SSL_ENABLE, Boolean.FALSE);
        boolean enableCors = env.getBoolean(SystemConstant.ENV_KEY_GZIP_ENABLE, Boolean.FALSE);
        boolean enableGzip = env.getBoolean(SystemConstant.ENV_KEY_CORS_ENABLE,Boolean.FALSE);

        EventLoopGroup bossGroup = new NioEventLoopGroup(acceptThreadCount, new NamedThreadFactory("nio-boss@"));
        EventLoopGroup workerGroup = new NioEventLoopGroup(ioThreadCount, new NamedThreadFactory("nio-worker@"));

        ServerBootstrap b = new ServerBootstrap();
        b.option(ChannelOption.SO_BACKLOG, backlog);
        b.option(ChannelOption.SO_REUSEADDR, true);
        b.childOption(ChannelOption.SO_REUSEADDR, true);
        b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class);
        b.handler(new LoggingHandler(LogLevel.INFO)).childHandler(
                new ServerInitializer(enableSsl,enableGzip,enableCors));

        Channel channel = b.bind(address, port).sync().channel();
        log.info("⬢ Glue start with {}:{}", address, port);
        log.info("⬢ Open your web browser and navigate to {}://{}:{} ⚡", "http",
                address.replace(SystemConstant.DEFAULT_SERVER_ADDRESS,SystemConstant. LOCAL_IP_ADDRESS), port);

        channel.closeFuture().sync();
    }

    public static void main(String[] args) {
        NettyServer server = new NettyServer();
        server.startup();
    }
}
