package com.system.kafka.clients.demo;

import com.system.kafka.clients.handle.ResultCallBack;
import org.springframework.stereotype.Service;

/**
 * CustomCallBack
 *
 * @author weiwei(Duan.Yu)
 * @version 1.0.0 createTime: 2017/10/30 下午2:23
 */
@Service
public class CustomCallBack implements ResultCallBack<SayHello> {
    @Override
    public void callback(SayHello message, Exception e) {
        if (e != null)
            e.printStackTrace();

        System.out.println("收到的消息确认结果:" + message);
    }
}
