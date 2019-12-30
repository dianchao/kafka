package com.system.kafka.clients.demo.producer;

import com.system.kafka.clients.demo.BaseSpringTest;
import com.system.kafka.clients.demo.SayHello;
import com.system.kafka.clients.handle.ProducerHandler;
import com.system.kafka.clients.handle.ResultCallBack;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <ul>
 * <li>生产者测试类</li>
 * <li>调用生产者处理类,发送消息<li>
 * <li>注意:消费者是通过spring-context.xml控制的,是默认启动的<li>
 * <li>User: weiwei Date:16/5/12 <li>
 * </ul>
 */
public class ProducerTest extends BaseSpringTest {

    private final static Logger logger = LoggerFactory.getLogger(ProducerTest.class);
    public static long currentTime = System.currentTimeMillis();

    @Autowired
    ProducerHandler producerHandler;

    @Autowired()
    @Qualifier("producerHandler2")
    ProducerHandler producerHandler2;

//    @Autowired
//    CustomCallBack customCallBack;

    /**
     * 一、同步发送消息用例
     * 在spring-kafka.xml默认启动消费者
     *
     * @throws InterruptedException
     */
    @Test
    public void concurrentSendMessageTest() throws InterruptedException {
        long startTime = System.currentTimeMillis();
        ExecutorService executorService = Executors.newFixedThreadPool(100);

        for (int i = 0; i < 100000; i++) {
//            SayHello sayHello = new SayHello("name:" + i, "message:" + i);
//            SayHello sayHello = new SayHello("name:" + i, "我要说话,说到够400bety,400bety到底有多长,1byte是8个字节,一个中文占二个字节,那我得说二百多字,但是格式也占了一些,不知道够了没,现在才说到一般,我要把刚才的话全部复制过去, 我要说话,说到够400bety,400bety到底有多长,1byte是8个字节,一个中文占二个字节,那我得说二百多字,但是格式也我要说话,说到够400bety,400bety到底有多长,1byte是8个字节,一个中文占二个字节,那我得说二百多字,但是格式也占了一些,不知道够了没,现在才说到一般,我要把刚才的话全部复制过去, 我要说话,说到够400bety,400bety到底有多长,1byte是8个字节,一个中文占二个字节,那我得说二百多字,但是格式也我要说话,说到够400bety,400bety到底有多长,1byte是8个字节,一个中文占二个字节,那我得说二百多字,但是格式也我要说话,说到够400bety,400bety到底有多长,1byte是8个字节," + i);
            String sayHello = "{\"logId\":\"b90c8664-5fa3-4dd5-bd77-6f00001554c2\",\"reqDTO\":{\"cardName\":\"武永\",\"cardNo\":\"4367484443836911\",\"cardType\":\"CREDIT\",\"functionId\":\"YHKRZ_SJ_FOUR\",\"idCardNo\":\"110226198908042312\",\"idCardType\":\"0\",\"industryType\":\"A1\",\"memberId\":\"8150710732\",\"memberTransDate\":1532630062000,\"memberTransId\":\"e34532e4be20425a97e97de3dd735828\",\"mobile\":\"13313123107\",\"noExistCardBin\":false,\"orgBankCode\":\"000000\",\"productType\":\"AUTH_SERVICE\",\"productTypeEnum\":\"COMMON\",\"subProductType\":\"YHKRZ_SJ_FOUR\",\"terminalId\":\"8150710732\",\"verifyElement\":\"FOUR\",\"version\":\"v1\"},\"resDTO\":{\"cacheFlag\":true,\"cardName\":\"武永\",\"errCode\":\"0\",\"errMsg\":\"亲，认证成功\",\"fee\":true,\"tradeNo\":\"20180703210210000000010145179141\",\"transId\":\"e34532e4be20425a97e97de3dd735828\",\"verifyElement\":\"1234\"},\"tCreditOrder\":{\"authState\":\"S\",\"businessNo\":\"0\",\"channelId\":\"0\",\"createTime\":1532630062841,\"createdBy\":\"GATEWAY-BANKCARD\",\"feeFlag\":\"N\",\"functionCode\":\"YHKRZ_SJ_FOUR\",\"id\":502256680,\"industryType\":\"A1\",\"memberId\":\"8150710732\",\"memberTransDate\":1532630062000,\"memberTransId\":\"e34532e4be20425a97e97de3dd735828\",\"orderMoney\":12,\"orderState\":\"INIT\",\"orderType\":\"TYPE_BANK_CARD\",\"productType\":\"AUTH_SERVICE\",\"subProductType\":\"YHKRZ_SJ_FOUR\",\"terminalId\":\"8150710732\",\"tradeNo\":\"20180703210210000000010145179141\",\"tradeState\":\"PRE_FEE_SUC\",\"updateTime\":1532630062861,\"useLocallib\":\"Y\"}";
            try {
                executorService.execute(new Send<>(producerHandler, sayHello));
            } catch (Exception e) {
                logger.error("send a message:{},fatal,Exception:", sayHello, e);
            }
        }
        System.out.println("发送完成,耗时:" + (System.currentTimeMillis() - startTime) + " ms");
        Thread.sleep(12000000);
    }

    /**
     * 二、异步发送用例
     * 注意:需要在spring-kafka.xml中,修改block=false,否则默认为同步发送
     *
     * @throws InterruptedException
     */
    @Test
    public void concurrentAsyncSendMessageTest() throws InterruptedException {
        long startTime = System.currentTimeMillis();
        ExecutorService executorService = Executors.newFixedThreadPool(100);

        for (int i = 0; i < 10000; i++) {
            SayHello sayHello = new SayHello("name:" + i, "message:" + i);
            try {
                executorService.execute(new AsyncSend<>(producerHandler, sayHello, null));
            } catch (Exception e) {
                logger.error("send a message:{},fatal,Exception:", sayHello, e);
            }
        }
        System.out.println("发送完成,耗时:" + (System.currentTimeMillis() - startTime) + " ms");
        Thread.sleep(12000000);
    }


    /**
     * 发送对象,用于同步并发发送模拟
     */
    class Send<T> implements Runnable {

        ProducerHandler producerHandler;
        T message;

        public Send(ProducerHandler producerHandler, T message) {
            this.producerHandler = producerHandler;
            this.message = message;
        }

        @Override
        public void run() {
            try {
                producerHandler.sendMessage(message);
                System.out.println("say..." + (System.currentTimeMillis() - ProducerTest.currentTime));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 发送对象,用于异步并发发送模拟
     */
    class AsyncSend<T> implements Runnable {

        ProducerHandler producerHandler;
        T message;
        ResultCallBack callback;

        public AsyncSend(ProducerHandler producerHandler, T message, ResultCallBack callback) {
            this.producerHandler = producerHandler;
            this.message = message;
            this.callback = callback;
        }

        @Override
        public void run() {
            try {
                producerHandler.sendMessage(message, callback);
                System.out.println("say..." + (System.currentTimeMillis() - ProducerTest.currentTime));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
