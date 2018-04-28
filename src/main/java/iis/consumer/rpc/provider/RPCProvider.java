package iis.consumer.rpc.provider;

import com.rabbitmq.client.*;

public class RPCProvider {
    private static final String RPC_QUEUE_NAME = "rpc_queue";
    public static void main(String[] argv) throws Exception {
        //创建连接
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("Provider");
        factory.setPassword("123");
        factory.setHost("localhost");
        factory.setPort(5672);
        factory.setVirtualHost("/");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        //声明队列
        channel.queueDeclare(RPC_QUEUE_NAME, false, false, false, null);

        //限制：每次最多给一个消费者发送1条消息
        channel.basicQos(1);

        //为rpc_queue队列创建消费者，用于处理请求
        QueueingConsumer consumer = new QueueingConsumer(channel);
        //打开应答机制autoAck=false
        channel.basicConsume(RPC_QUEUE_NAME, false, consumer);

        System.out.println(" 等待来自RPC消费者的请求……");

        while (true) {
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();

            //获取请求中的correlationId属性值，并将其设置到结果消息的correlationId属性中
            AMQP.BasicProperties props = delivery.getProperties();
            BasicProperties replyProps = new AMQP.BasicProperties.Builder().correlationId(props.getCorrelationId()).build();
            //获取回调队列名字
            String callQueueName = props.getReplyTo();

            String message = new String(delivery.getBody(),"UTF-8");

            System.out.println(" 要转换的输入是：" + message );

            //获取结果
            String response = "" + change(message);
            //先发送回调结果
            channel.basicPublish("", callQueueName, (AMQP.BasicProperties) replyProps,response.getBytes());
            //后手动发送消息反馈
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
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

