package rabbitmq;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

public class RPCClient {

    private static final String RPC_QUEUE_NAME = "rpc_queue";
    private static final String ROUTING_KEY = "routingkey_demo";
    private static final String QUEUE_NAME = "queue_demo";
    private static final String IP_ADDRESS = "10.10.12.79";
    private static final Boolean MANDATORY = true;
    private static final int PORT = 5672;

    private Connection connection;
    private Channel channel;
    private String requestQueueName = "rpc_queue";
    private String replyQueueName;
    private QueueingConsumer consumer;

    public RPCClient() throws IOException, TimeoutException {

        Address [] addresses = new Address[]{
                new Address(IP_ADDRESS,PORT)
        };
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("root");
        factory.setPassword("123456");

        connection = factory.newConnection(addresses);
        channel = connection.createChannel();

        replyQueueName = channel.queueDeclare().getQueue();
        consumer = new QueueingConsumer(channel);
        channel.basicConsume(replyQueueName,true,consumer);
    }

    public String call(String message) throws IOException{

        String response = null;
        String corrId = UUID.randomUUID().toString();

        return null;
    }

}
