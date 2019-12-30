package com.system.kafka.clients;

import com.system.kafka.clients.factory.ConsumerFactory;
import com.system.kafka.clients.process.ConsumerForward;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <ul>
 * <li>消费者服务类</li>
 * <li>封装消费实体,对外统计提供入口<li>
 * <li>User: weiwei Date:16/5/11 <li>
 * </ul>
 */
public class ConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(ConsumerService.class);

    /**
     * 消费者工厂,获取一条消费者通道
     */
    private ConsumerFactory consumerFactory;

    // 公共方法,设置销毁调用
    public volatile static Boolean flag = true;

    public void setConsumerFactory(ConsumerFactory consumerFactory) {
        this.consumerFactory = consumerFactory;
    }

    /**
     * 根据[topicName]获取消费者通道,"长等待"主动去broker拉取消息.
     * 获取到的消息,丢给[obj]处理人进行处理.
     * @param topicName 主题名称
     * @param obj 业务处理对象
     * @param transObj 消息类型
     */
    public void consumerMessages(final String topicName, final Object obj, final Class transObj) {
        logger.info("start a kafka consumer,subscribe topic:{}", topicName);
        new Thread(new Runnable() {
            @Override
            public void run() {
                new ConsumerForward(consumerFactory.getConsumer(topicName)).poll(topicName, obj, transObj);
            }
        }, topicName + "-consumerThread").start();
    }

    public void destroy() {
        logger.info("收到停机信息,开始关闭kafka消费者连接!");
        flag = false;
        this.consumerFactory.closeAll();
    }
}
