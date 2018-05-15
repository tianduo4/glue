package com.glue.server.netty;

import com.glue.ioc.Ioc;
import com.glue.router.RouteHandler;
import com.glue.environment.Environment;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author xushipeng
 * @create 2018-05-11 17:02
 */
@NoArgsConstructor
@Data
public class ServerContext {

    /**
     * ContextPath, are currently /
     */
    private String contextPath;

    /**
     * The default IOC container implementation
     */
    private static Ioc ioc;

    /**
     * All need to be scanned by the package, when you do not set the time will scan com.blade.plugin package
     */
    private Set<String> packages = new LinkedHashSet<>();

    /**
     *  environment, which stores the parameters of the app.properties configuration file
     */
    private Environment environment = Environment.empty();

    private static RouteHandler routeHandler = RouteHandler.empty();

    public static RouteHandler getRouteHandler(){
        return routeHandler;
    }

    public static void setRouteHandler(RouteHandler myRouteHandler ){
        routeHandler=myRouteHandler;
    }

    public static Ioc getIoc(){
        return ioc;
    }

    public static void setIoc(Ioc myIoc ){
        ioc=myIoc;
    }


    /**
     * When set to start blade scan packages
     *
     * @param packages package name
     * @return blade
     */
    public void setScanPackages(@NonNull String... packages) {
        this.packages.addAll(Arrays.asList(packages));
    }


    public void addBean(@NonNull Class<?> cls) {
        ioc.addBean(cls);
    }

    /**
     * Get bean instance by class type
     *
     * @param type class type
     * @param <T>  type
     * @return return bean instance
     */
    public <T> T getBean(Class<T> type) {
        return ioc.getBean(type);
    }


    public void addRouter(Class<?> clazz, Object controller) {
        routeHandler.addRouter(clazz, controller);
    }
}
