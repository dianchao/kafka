package com.system.kafka.clients;

import com.system.kafka.clients.factory.NormalRoundRobin;
import com.system.kafka.clients.handle.ResultCallBack;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;

/**
 * <ul>
 * <li>生产者服务类</li>
 * <li>此类封装生产者发送实体,对外提供统一入口<li>
 * <li>User: weiwei Date:16/5/11 <li>
 * </ul>
 */
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    /**
     * 生产者连接池
     */
    private NormalRoundRobin normalRoundRobin;


    private Boolean isBlock = true;

    public void setBlock(Boolean block) {
        isBlock = block;
    }

    public void setNormalRoundRobin(NormalRoundRobin normalRoundRobin) {
        this.normalRoundRobin = normalRoundRobin;
    }

    /**
     * 从[生产者连接池]获取一条通道,然后发送[message]到broker集群
     *
     * @param topicName 主题名称
     * @param message   消息
     * @param <T>       消息类型
     */
    public <T> void sendMessage(String topicName, final T message, final ResultCallBack callback) throws Exception {

        // 发送生产者
        Producer producer;

        try {
            producer = normalRoundRobin.round();
            // 阻塞
            if (isBlock == true)
                producer.send(new ProducerRecord<String, T>(topicName, message)).get();
                // 非阻塞
            else if (callback == null)
                producer.send(new ProducerRecord<String, T>(topicName, message));
            else {
                producer.send(new ProducerRecord<String, T>(topicName, message), new Callback() {
                    public void onCompletion(RecordMetadata metadata, Exception e) {
                        callback.callback(message, e);
                    }
                });
            }
        } catch (InterruptedException e) {
            logger.error("send a message InterruptedException:{}", e);
            throw e;
        } catch (ExecutionException e) {
            logger.error("send a message ExecutionException:{}", e);
            throw e;
        } catch (Exception e) {
            logger.error("fetch a producer connection Exception:{}", e);
        } finally {
            normalRoundRobin.release();
        }
    }
}
