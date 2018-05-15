package com.glue.test2;

import com.glue.ioc.annotation.Path;
import com.glue.router.annotation.GetRoute;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by King on 2018/5/12.
 */
@Slf4j
@Path
public class HelloController {

    @GetRoute(value = {"hello"})
    public String hello(){
        return "Hello World";
    }
}
