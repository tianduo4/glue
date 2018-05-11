package com.glue.server.netty;

import com.glue.ioc.Ioc;
import com.glue.ioc.SimpleIoc;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private  String contextPath;

    /**
     * The default IOC container implementation
     */
    private Ioc ioc;






}
