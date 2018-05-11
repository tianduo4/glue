package com.glue.ioc;

import java.util.Set;

/**
 * 一个类读取器的接口
 *
 * @author <a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since 1.0
 */
public interface ClassReader {

    Set<ClassInfo> readClasses(Scanner scanner);

}