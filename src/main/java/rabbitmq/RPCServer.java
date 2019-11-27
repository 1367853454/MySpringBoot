package rabbitmq;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RPCServer {

    private static final String RPC_QUEUE_NAME = "rpc_queue";
    private static final String ROUTING_KEY = "routingkey_demo";
    private static final String QUEUE_NAME = "queue_demo";
    private static final String IP_ADDRESS = "10.10.12.79";
    private static final Boolean MANDATORY = true;
    private static final int PORT = 5672;

    public static void main(String[] args) throws IOException, TimeoutException {

        Address [] addresses = new Address[]{
                new Address(IP_ADDRESS,PORT)
        };
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("root");
        factory.setPassword("123456");

        Connection connection = factory.newConnection(addresses);
        Channel channel = connection.createChannel();

        channel.queueDeclare(RPC_QUEUE_NAME,false,false,false,null);
        channel.basicQos(1);
        System.out.println(" [x] Awaiting RPC requests");

        Consumer consumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String comsumeTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body) throws IOException{
                AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                        .Builder()
                        .correlationId(properties.getCorrelationId())
                        .build();
                String response = "";
                try {
                    String message = new String(body, "UTF-8");
                    int n = Integer.parseInt(message);
                    System.out.println(" [.] fib(" + message + ")");
                    response += fib(n);
                }catch (RuntimeException e){
                    System.out.println(" [.] " + e.toString());
                }finally {
                    channel.basicPublish("", properties.getReplyTo(),replyProps, response.getBytes("UTF-8"));
                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
            }
        };
        channel.basicConsume(RPC_QUEUE_NAME,false,consumer);
    }

    private static int fib(int n){
        if (n == 0)
            return 0;
        if (n == 1)
            return 1;
        return fib(n - 1) + fib(n - 2);
    }

}
