package com.system.kafka.clients.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;

/**
 * <ul>
 * <li>关闭流公共包</li>
 * <li>User: weiwei Date:16/5/14 <li>
 * </ul>
 */
public class CloseUtils {
    private final static Logger logger = LoggerFactory.getLogger(CloseUtils.class);

    public static void CloseAll(Closeable... io) {
        for (Closeable temp : io) {
            if (null != temp) {
                try {
                    temp.close();
                } catch (IOException e) {
                    logger.error("close stream Exception:{}", e);
                }
            }
        }
    }
}
