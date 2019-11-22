package rabbitmq;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * 生产者
 * 客户端代码首先与RabbitMQ服务器建立一个链接Connection
 * 然后在这个连接上创建一个信道Channel
 * 之后创建一个交换器Exchange和一个队列Queue
 * 并通过路由键进行绑定
 * 发送一条消息
 * 最后关闭资源
 */
public class RabbitProducer {

    private static final String EXCHANGE_NAME = "exchange_demo";
    private static final String ROUTING_KEY = "routingkey_demo";
    private static final String QUEUE_NAME = "queue_demo";
    private static final String IP_ADDRESS = "192.168.0.2";
    private static final Boolean MANDATORY = true;
    private static final int PORT = 5672;//RabbitMQd服务端默认端口号为5672

    public static void main(String[] args) throws IOException, TimeoutException {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(IP_ADDRESS);
        factory.setPort(PORT);
        factory.setUsername("root");
        factory.setPassword("123456");
        //创建连接
        Connection connection = factory.newConnection();
        //创建信道
        Channel channel = connection.createChannel();
        //创建一个type="direct"、持久化的、非自动删除的交换机
        channel.exchangeDeclare(EXCHANGE_NAME,"direct",true,false,null);



        //设置了mandatory参数，又不想复杂化逻辑代码---备份交换机
        Map<String, Object> arg = new HashMap<String, Object>();
        //备份交换机
        arg.put("alternate-exchange", "myAe");

        /*Map<String, Object> argg = new HashMap<String, Object>();
        //设置消息TTL过期时间 毫秒级
        argg.put("x-message-ttl",6000);
        channel.queueDeclare("queueName", durable, exclusive, autoDelete, argg);*/

        /*//设置队列的TTL 过期时间为30分钟的队列
        arg.put("x-expires", 1800000);
        channel.queueDeclare("myQueue", false, false, false, arg);*/

        channel.exchangeDeclare("normalExchange","direct", true, false, arg);
        channel.exchangeDeclare("myAe","fanout", true, false, null);
        channel.queueDeclare("normalQueue", true, false, false, null);
        channel.queueBind("normalQueue", "normalExchange", "normalKey");
        channel.queueDeclare("unroutedQueue", true, false, false, null);
        channel.queueBind("unroutedQueue","myAe", "");



        //创建一个持久化、非排他的，非自动删除的队列
        channel.queueDeclare(QUEUE_NAME,true,false,false,null);
        //将交换机与队列通过路由键绑定
        channel.queueBind(QUEUE_NAME,EXCHANGE_NAME,ROUTING_KEY);
        //发送一条持久化的消息：hello world!
        String message = "hello world!";
        channel.basicPublish(EXCHANGE_NAME,ROUTING_KEY,
                MessageProperties.PERSISTENT_TEXT_PLAIN,
                message.getBytes());



        /*//设置了mandatory参数的basicPublish 需要添加addReturnListener
        channel.basicPublish(EXCHANGE_NAME,"",true,
                MessageProperties.PERSISTENT_TEXT_PLAIN,
                "mandatory test".getBytes());
        channel.addReturnListener(new ReturnListener() {
            @Override
            public void handleReturn(int replyCode, String replyText, String exchange,
                                     String routingKey, AMQP.BasicProperties basicProperties, byte[] body) throws IOException {
                String message = new String(body);
                System.out.println("Basic.Return返回的结果是: " + message);
            }
        });
        channel.addReturnListener((int replyCode, String replyText, String exchange,
                                   String routingKey, AMQP.BasicProperties basicProperties, byte[] body) -> {
                String message1 = new String(body);
                System.out.println("Basic.Return返回的结果是: " + message1);
        });*/
        //关闭资源



        /*//针对每条消息设置TTL的方法是在channel.basicPublish方法中加入expiration的属性参数
        AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties.Builder();
        //持久化消息
        builder.deliveryMode(2);
        //设置TTL=6000ms
        builder.expiration("6000");
        AMQP.BasicProperties properties = builder.build();
        channel.basicPublish(EXCHANGE_NAME,ROUTING_KEY,MANDATORY,properties,"ttlTestMessage".getBytes());*/

        //创建DLX:dlx_exchange Dead-Letter-Exchange(死信队列)
        channel.exchangeDeclare("dlx_exchange", "direct");
        Map<String, Object> dlxMap = new HashMap<String, Object>();
        dlxMap.put("x-dead-letter-exchange", "dlx_exchange");
        //为队列myqueue添加DLX
        channel.queueDeclare("myqueue", false, false, false, dlxMap);



        channel.close();
        connection.close();
        channel.isOpen();
    }
}
