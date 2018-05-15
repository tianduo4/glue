package com.glue.server.netty;

import com.glue.constant.SystemConstant;
import com.glue.ioc.ClassInfo;
import com.glue.ioc.DynamicContext;
import com.glue.ioc.Ioc;
import com.glue.ioc.SimpleIoc;
import com.glue.ioc.annotation.Bean;
import com.glue.ioc.annotation.Path;
import com.glue.ioc.annotation.Value;
import com.glue.router.RouteHandler;
import com.glue.server.ILifeCycle;
import com.glue.environment.Environment;
import com.glue.utils.NamedThreadFactory;
import com.glue.utils.ReflectUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

/**
 * @author xushipeng
 * @create 2018-05-03 16:20
 */
@Slf4j
public class NettyServer implements ILifeCycle {
    private final static String DEFAULT_CONTENT_CONTEXT="/";
    private Environment env = Environment.empty();

    private ServerContext serverContext;

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

        this.initServerContext();

        this.initConfig();

        this.printBanner();

        this.initIoc();

//        this.shutdownHook();

        this.watchEnv();

          this.startServer();

        log.info("⬢ {} initialize successfully, Time elapsed: {} ms", appName, (System.currentTimeMillis() - startTime));

    }

    private void watchEnv() {
        boolean watchEnv = env.getBoolean(SystemConstant.ENV_KEY_APP_WATCH_ENV, true);
        log.info("⬢ Watched environment: {}", watchEnv);

//        if (watchEnv) {
//            Thread t = new Thread(new EnvironmentWatcher());
//            t.setName("watch@thread");
//            t.start();
//        }
    }


    @Override
    public void shutdown() {

    }

    private void initServerContext() {
        serverContext=new ServerContext();
        serverContext.setContextPath(DEFAULT_CONTENT_CONTEXT);

        Ioc ioc = new SimpleIoc();
        ServerContext.setIoc(ioc);

        ServerContext.setRouteHandler(RouteHandler.empty());
    }

    private void initConfig() {
        String bootConf = env.get(SystemConstant.ENV_KEY_BOOT_CONF, "classpath:app.properties");
        log.info("current environment file is: {}", bootConf);
        Environment bootEnv = Environment.of(bootConf);
        if (bootEnv != null) {
            bootEnv.props().forEach((key, value) -> env.set(key.toString(), value));
        }
        serverContext.setEnvironment(bootEnv);

        Optional<String> scanPackage = env.get(SystemConstant.ENV_KEY_SCAN_PACKAGE);
        if(scanPackage.isPresent()){
            String[] scanPackages = scanPackage.get().split(";");
            serverContext.setScanPackages(scanPackages);
        }
    }

    private void printBanner() {
    }

    private void initIoc() {

        serverContext.getPackages().stream()
                .flatMap(DynamicContext::recursionFindClasses)
                .map(ClassInfo::getClazz)
                .filter(ReflectUtils::isNormalClass)
                .forEach(this::parseCls);

        ServerContext.getRouteHandler().register();
//        Ioc ioc = blade.ioc();
//        if (BladeKit.isNotEmpty(ioc.getBeans())) {
//            log.info("⬢ Register bean: {}", ioc.getBeans());
//        }
//
//        List<BeanDefine> beanDefines = ioc.getBeanDefines();
//        if (BladeKit.isNotEmpty(beanDefines)) {
//            beanDefines.forEach(b -> {
//                BladeKit.injection(ioc, b);
//                BladeKit.injectionValue(environment,b);
//            });
//        }
    }

    private void parseCls(Class<?> clazz) {
        if (null != clazz.getAnnotation(Bean.class) || null != clazz.getAnnotation(Value.class)) {
            serverContext.addBean(clazz);
        }
        if (null != clazz.getAnnotation(Path.class)) {
            if (null == serverContext.getBean(clazz)) {
                serverContext.addBean(clazz);
            }
            Object controller = serverContext.getBean(clazz);
            serverContext.addRouter(clazz, controller);
        }
//        if (ReflectKit.hasInterface(clazz, WebHook.class) && null != clazz.getAnnotation(Bean.class)) {
//            Object     hook       = blade.ioc().getBean(clazz);
//            UrlPattern urlPattern = clazz.getAnnotation(UrlPattern.class);
//            if (null == urlPattern) {
//                routeBuilder.addWebHook(clazz, "/.*", hook);
//            } else {
//                Stream.of(urlPattern.values())
//                        .forEach(pattern -> routeBuilder.addWebHook(clazz, pattern, hook));
//            }
//        }
//        if (ReflectKit.hasInterface(clazz, BeanProcessor.class) && null != clazz.getAnnotation(Bean.class)) {
//            this.processors.add((BeanProcessor) blade.ioc().getBean(clazz));
//        }
//        if (isExceptionHandler(clazz)) {
//            ExceptionHandler exceptionHandler = (ExceptionHandler) blade.ioc().getBean(clazz);
//            blade.exceptionHandler(exceptionHandler);
//        }
    }

    private void startServer() {
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
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.option(ChannelOption.SO_BACKLOG, backlog);
            b.option(ChannelOption.SO_REUSEADDR, true);
            b.childOption(ChannelOption.SO_REUSEADDR, true);
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class);
            b.handler(new LoggingHandler(LogLevel.INFO)).childHandler(
                    new ServerInitializer(enableSsl, enableGzip, enableCors));

            Channel channel = b.bind(address, port).sync().channel();
            log.info("⬢ Glue start with {}:{}", address, port);
            log.info("⬢ Open your web browser and navigate to {}://{}:{} ⚡", "http",
                    address.replace(SystemConstant.DEFAULT_SERVER_ADDRESS, SystemConstant.LOCAL_IP_ADDRESS), port);

            channel.closeFuture().sync();
        }catch(InterruptedException e){
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        NettyServer server = new NettyServer();
        server.startup();
    }
}
