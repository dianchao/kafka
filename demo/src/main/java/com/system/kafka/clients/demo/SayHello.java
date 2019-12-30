package com.system.kafka.clients.demo;

import java.io.Serializable;

/**
 * <ul>
 * <li>消息对象</li>
 * <li>生产者和消费者共用此类作为消息传输对象<li>
 * <li>User: weiwei Date:16/5/12 <li>
 * </ul>
 */
public class SayHello implements Serializable {

    private static final long serialVersionUID = 5338219201055652888L;

    private String name;
    private String say;

    /**
     * 注意,必须保留空构造
     */
    public SayHello() {
    }

    public SayHello(String name, String say) {
        this.name = name;
        this.say = say;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSay() {
        return say;
    }

    public void setSay(String say) {
        this.say = say;
    }

    @Override
    public String toString() {
        return "SayHello{" +
                "name='" + name + '\'' +
                ", say='" + say + '\'' +
                '}';
    }
}
