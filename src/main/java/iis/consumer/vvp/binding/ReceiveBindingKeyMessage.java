package iis.consumer.vvp.binding;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ReceiveBindingKeyMessage {
    private final static String EXCHANGE_NAME = "Translation";
    private final static String EXCHANGE_TYPE = "direct";
    private final static String BindingKey = "Consumer A";

    public static void main(String[] args) throws IOException, TimeoutException {
        // 创建连接和频道
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        // 声明交换机
        channel.exchangeDeclare(EXCHANGE_NAME, EXCHANGE_TYPE);

        //生成随机队列名
        String queueName = channel.queueDeclare().getQueue();
        //指定Binding Key
       // String equipmentTypes = "EquipmentInformation Transaction Types";

        channel.queueBind(queueName, EXCHANGE_NAME, BindingKey);
        System.out.println("开始等待MQ中间件消息.XXXX...");
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(" 接收到的消息是： '" + message + "'");
            }
        };
        channel.basicConsume(queueName, true, consumer);
    }
}

