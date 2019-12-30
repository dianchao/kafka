# Kafka客户端


kafka版本:0.10.0.0


## 运行Demo

#### 第一步
打开 `demo`中的 `test/resources/spring/spring-kafka.xml` 配置文件,修改kafka集群地址.

#### 第二步
启动 demo模块中的`com.system.kafka.clients.demo.producer.ProducerTest.concurrentSendMessageTest()` 方法，即可发送消息到kafka集群。
默认情况下，消费者也同时启动。
<br/><br/><br/>

## 项目中引入

#### 第一步
在项目中,需要引入

```
        <dependency>
            <groupId>com.system.kafka</groupId>
            <artifactId>kafka-clients</artifactId>
            <version>2.8.0-RELEASE</version>
        </dependency>
```
#### 第二步
引入`test/resources/spring/spring-kafka.xml`配置。

### 第三步
修改集群地址。

```
    <property name="bootstrapServers" value="address1:port,address2:port"/>
```


### 第四步
替换`消息传输对象` ，在 `<bean name =consumerHandler`中的属性【<b>transObj</b>】替换为你自己的传输对象。

```
<property name="transObj" value="com.system.kafka.clients.demo.SayHello"/>
替换为
<property name="transObj" value="你的传输对象"/>
```

同样替换`注入消息处理类对象` 的属性【receiptObj】为你自己的处理对象。

```
<property name="receiptObj" ref="receiptDispatch"/>
替换为
<property name="receiptObj" ref="消息处理对象"/>

```

注意： receiptObj必须实现`BizHandleInterface`接口.

### 第五步

修改`test/resources/spring/spring-context.xml`中的扫描路径。

```
<context:component-scan base-package="扫描第四步【receiptObj】消息处理对象的地址"/>

```