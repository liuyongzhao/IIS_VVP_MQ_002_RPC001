package iis.consumer.rpc.provider;

import com.rabbitmq.client.*;

import java.io.IOException;

public class RPC_P_DEMO {
    private static final String RPC_QUEUE_NAME = "rpc_queue";
    public Connection connection;
    public static void main(String[] argv) throws Exception {
        //创建连接
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("Provider");
        factory.setPassword("123");
        factory.setHost("localhost");
        factory.setPort(5672);
        factory.setVirtualHost("/");
        Connection connection = factory.newConnection();
        final Channel channel = connection.createChannel();
        //声明队列
        channel.queueDeclare(RPC_QUEUE_NAME, false, false, false, null);

        //限制：每次最多给一个消费者发送1条消息
        channel.basicQos(1);

        //为rpc_queue队列创建消费者，用于处理请求
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                        .Builder()
                        .correlationId(properties.getCorrelationId())
                        .build();
                System.out.println("请求消息的关联ID：" + properties.getCorrelationId());
                System.out.println("发送消息的关联ID：" + replyProps.getCorrelationId());
                System.out.println("接收的消息是：" + body.toString());
                String message = new String(body, "UTF-8");
                //获取结果
                String response = null;
                try {
                    response = "" + change(message);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    channel.basicPublish("", properties.getReplyTo(), replyProps, response.getBytes("UTF-8"));
                    channel.basicAck(envelope.getDeliveryTag(), false);
                    System.out.println("发布的消息是 " + response);
                    synchronized (this) {
                        this.notify();
                    }
                }
            }
        };
        //打开应答机制autoAck=false
        channel.basicConsume(RPC_QUEUE_NAME, false, consumer);

        System.out.println(" 等待来自RPC消费者的请求……");

        while (true) {
            synchronized (consumer) {
                try {
                    consumer.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private static char change(String para) throws Exception {
        //字符型的KeyNumber转化成int型的ASCII十进制数
        byte[] bytestr = para.getBytes();
        int sum = 0;
        for(int i = 0;i<bytestr.length;i++){
            sum += bytestr[i] + 16;
        }
        //ASCII十进制数转化成ASCII字符
        char ch = (char)sum;
        return ch;
    }
}