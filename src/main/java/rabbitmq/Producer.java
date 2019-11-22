package rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 *延迟队列根据死信队列
 * 消费者订阅死信队列
 * 在normal中设置不同级别的TTL routingKey
 * 形成不同等级的延迟队列
 */
public class Producer {

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
        //创建一个队列，为其设置TTL，DLX
        channel.exchangeDeclare("exchange.dlx", "direct", true);
        channel.exchangeDeclare("exchange.normal", "fanout", true);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("x-message-ttl", 10000);
        map.put("x-dead-letter-exchange", "exchange.dlx");
        map.put("x-dead-letter-routing-key", "routingKey");
        channel.queueDeclare("queue.normal", true, false, false, map);
        channel.queueBind("queue.normal", "exchange.normal","");
        channel.queueDeclare("queue.dlx", true, false, false, null);
        channel.queueBind("queue.dlx", "exchange.dlx", "routingKey");
        channel.basicPublish("exchange.normal","rk",MessageProperties.PERSISTENT_TEXT_PLAIN,"dlx".getBytes()) ;

        channel.close();
        connection.close();
        channel.isOpen();
    }
}
