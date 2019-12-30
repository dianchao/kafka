package com.system.kafka.clients.handle;

/**
 * <ul>
 * <li>业务处理接口类</li>
 * <li>User: weiwei Date:16/5/11 <li>
 * </ul>
 */
public interface BizHandleInterface<O> {

    /**
     * 业务处理接口
     */
     void doBiz(O obj);
}
