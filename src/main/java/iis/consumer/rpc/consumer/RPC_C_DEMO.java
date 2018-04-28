package iis.consumer.rpc.consumer;

import com.rabbitmq.client.*;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class RPC_C_DEMO {
    private Connection connection;
    private Channel channel;
    private static final String RPC_QUEUE_NAME = "rpc_queue";
    private String replyQueueName;
    //private Consumer consumer;

    //建立消息消费者
    public RPC_C_DEMO() throws Exception {
        //创建连接
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("Consumer");
        factory.setPassword("123");
        factory.setHost("localhost");
        factory.setPort(5672);
        factory.setVirtualHost("/");
        connection = factory.newConnection();
        channel = connection.createChannel();
        //注册'回调'队列，用以接收RPC响应
        replyQueueName = channel.queueDeclare().getQueue();
    }
    public String call(String message) throws Exception{
        //JsonObject message = new JsonObject().put("body", "1");
        //设置消息属性：关联id和replyTo等
        final String corrId = java.util.UUID.randomUUID().toString();
        AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
                .correlationId(corrId).replyTo(replyQueueName).build();
        channel.basicPublish("", RPC_QUEUE_NAME, (AMQP.BasicProperties) props,message.toString().getBytes());
        final BlockingQueue<String> response = new ArrayBlockingQueue<String>(1);
        //关闭应答机制，使用自动应答autoAck=true
        channel.basicConsume(replyQueueName, true, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws
                    IOException {
                if (properties.getCorrelationId().equals(corrId)) {
                    response.offer(new String(body, "UTF-8"));
                }
            }
        });
        //System.out.println("输出值是"+response.take());
        return response.take();

    }
    public void close() throws Exception {
        connection.close();
        System.out.println("关闭连接");
    }
}
