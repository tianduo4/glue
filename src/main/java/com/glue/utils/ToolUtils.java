package com.glue.utils;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;

/**
 * @author xushipeng
 * @create 2018-05-03 16:56
 */
@Slf4j
@NoArgsConstructor
public class ToolUtils {

    public static void closeQuietly(Closeable closeable) {
        try {
            if (null == closeable) {
                return;
            }
            closeable.close();
        } catch (Exception e) {
            log.error("Close closeable error", e);
        }
    }
}
