package com.system.kafka.clients.factory;

import org.apache.kafka.clients.producer.Producer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class NormalRoundRobin {
    private final static Logger logger = LoggerFactory.getLogger(NormalRoundRobin.class);

    private List<Producer> producer = new ArrayList<>();

    private volatile int currentIndex;
    private volatile int totalServer;

    // 计时器
    private long nextTime = System.currentTimeMillis();

    // 触发次数
    private int touch_count = 0;

    // 用于计算并发数
    public AtomicInteger count = new AtomicInteger(0);

    public NormalRoundRobin(ProducerConnPool producerConnPool) throws Exception {
        // 初始化【生产者】连接对象
        if (this.size() == 0)
            for (int i = 0; i < producerConnPool.getMinIdle(); i++)
                this.add(producerConnPool.getProducerConn());

        // 异步维护连接大小对象
        new AI(this, producerConnPool).start();
        logger.info("kafka发生者轮询连接池加载完成,初始连接为:{}", producer.size());
    }

    public void add(Producer kafkaProducer) {
        producer.add(kafkaProducer);

        totalServer = producer.size();
        currentIndex = totalServer - 1;
    }

    // 轮询
    public Producer round() {
        count.addAndGet(1);
        currentIndex = (currentIndex + 1) % totalServer;
        return producer.get(currentIndex);
    }

    // 释放,用于并发计算
    public int release() {
        return count.addAndGet(-1);
    }

    // 移除生产者,用于维护最小连接
    public void remove(Producer p) {
        totalServer = this.producer.size() - 1;
        currentIndex = totalServer - 1;

        this.producer.remove(p);
    }

    // 获取队列大小
    public int size() {
        return producer.size();
    }

    /**
     * 用于维护最优连接数大小
     */
    class AI extends Thread {

        private NormalRoundRobin normalRoundRobin;
        private ProducerConnPool producerConnPool;

        AI(NormalRoundRobin normalRoundRobin, ProducerConnPool producerConnPool) {
            this.normalRoundRobin = normalRoundRobin;
            this.producerConnPool = producerConnPool;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(5000);

                    int size = normalRoundRobin.size();
                    long startTime = System.currentTimeMillis();

                    logger.debug("maintain Connection Thread Poll start,current connection size:{}", size);

                    // 最优连接数
                    int optimizeSize = normalRoundRobin.count.get() / 30;

                    // 如果等于最优,则无任何动作
                    if (size == optimizeSize)
                        continue;

                    // 最优=0  &&  当前连接数 <= 最小连接数
                    if (optimizeSize == 0 && size <= producerConnPool.getMinIdle())
                        continue;

                    // 如果小于最优数时
                    if (size < optimizeSize && size < producerConnPool.getMaxTotal()) {
                        try {
                            normalRoundRobin.add(producerConnPool.getProducerConn());
                            logger.debug("increase a Producer Connection,current connection size:{}:", normalRoundRobin.size());
                        } catch (Exception e) {
                            logger.error("add a conn fatal,reason:", e);
                        }
                    }

                    // 如果连接数大于最优数
                    if (size > optimizeSize) {

                        // 如果超过了50秒,则重新计数(说明50秒内没有满足3次)
                        if (System.currentTimeMillis() - nextTime >= 30000) {
                            touch_count++;
                            nextTime = System.currentTimeMillis();
                            continue;
                        }

                        // 第一次触发,则开始计时和计数
                        if (touch_count == 0) {
                            touch_count++;
                            nextTime = System.currentTimeMillis();
                            continue;
                        }

                        // 如果小于5次,则不递减
                        if (touch_count < 3) {
                            touch_count++;
                            continue;
                        }

                        if (touch_count >= 3) {
                            Producer producer = normalRoundRobin.producer.get(size - 1);
                            normalRoundRobin.remove(producer);
                            producerConnPool.releaseConn(producer);

                            // 重新计算
                            touch_count = 0;
                            nextTime = System.currentTimeMillis();
                            logger.debug("decrease a Producer Connection,current connection size:{}:", normalRoundRobin.size());
                        }
                    }
                    logger.debug("maintained Connection Thread Poll end,current connection size:{},time-consuming:{}.......", normalRoundRobin.size(), (System.currentTimeMillis() - startTime));
                } catch (Exception e) {
                    logger.error("Exception poll error:", e);
                } catch (Throwable e) {
                    logger.error("Throwable poll fatal:", e);
                }
            }
        }
    }
}