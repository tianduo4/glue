package com.glue.ioc;

import java.util.Set;

/**
 * 一个类读取器的接口
 */
public interface ClassReader {

    Set<ClassInfo> readClasses(Scanner scanner);

}