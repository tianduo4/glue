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
    String       HTTP_DATE_FORMAT           = "EEE, dd MMM yyyy HH:mm:ss zzz";

}
