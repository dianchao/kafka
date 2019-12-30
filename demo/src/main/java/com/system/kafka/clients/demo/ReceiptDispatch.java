package com.system.kafka.clients.demo;

import com.system.kafka.clients.handle.BizHandleInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * <ul>
 * <li>接收派发处理类</li>
 * <li>接收到获取的消息,进行响应处理.<li>
 * <li>User: duan Date:16/5/12 <li>
 * </ul>
 */
@Service
public class ReceiptDispatch implements BizHandleInterface<SayHello> {

    private final static Logger logger = LoggerFactory.getLogger(ReceiptDispatch.class);

    @Override
    public void doBiz(SayHello o) {
        // TODO: 16/5/14
        // [你需要在这里写逻辑处理]
//        logger.info("receipt a message:[{}]", o.toString());
    }
}
