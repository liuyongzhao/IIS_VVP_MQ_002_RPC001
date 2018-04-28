package iis.consumer.rpc.consumer;

import com.rabbitmq.client.*;

public class RPCReceiver {
    private Connection connection;
    private Channel channel;
    private static final String RPC_QUEUE_NAME = "rpc_queue";
    private String replyQueueName;
    private QueueingConsumer consumer;

    //建立消息消费者
    public RPCReceiver() throws Exception {
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
        consumer = new QueueingConsumer(channel);
        //关闭应答机制，使用自动应答autoAck=true
        channel.basicConsume(replyQueueName, true, consumer);

    }

    //发送RPC请求
    public String call(String message) throws Exception {
        String response = null;
        String corrId = java.util.UUID.randomUUID().toString();
        //发送请求消息，消息使用了两个属性：replyto和correlationId
        BasicProperties props = new AMQP.BasicProperties.Builder()
                .correlationId(corrId).replyTo(replyQueueName).build();
        //发送消息到rpc_queue队列
        channel.basicPublish("", RPC_QUEUE_NAME, (AMQP.BasicProperties) props, message.getBytes());
        //等待接收结果
        while (true) {
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            //检查它的correlationId是否是我们所要找的那个
            if (delivery.getProperties().getCorrelationId().equals(corrId)) {
                System.out.println("关联ID是："+ delivery.getProperties().getCorrelationId());
                System.out.println("回调队列是："+ props.getReplyTo());
                response = new String(delivery.getBody());
                break;
            }
        }
        return response;
    }
    public void close() throws Exception {
        connection.close();
    }
}
