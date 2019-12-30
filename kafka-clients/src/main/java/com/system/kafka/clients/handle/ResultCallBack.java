package com.system.kafka.clients.handle;

/**
 * ResultCallBack
 *
 * @author weiwei(Duan.Yu)
 * @version 1.0.0 createTime: 2017/10/30 下午2:19
 */
public interface ResultCallBack<T> {

    void callback(T message, Exception e);
}
