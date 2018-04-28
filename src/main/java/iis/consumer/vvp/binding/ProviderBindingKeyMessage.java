package iis.consumer.vvp.binding;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ProviderBindingKeyMessage {
    private final static String EXCHANGE_NAME = "Translation";
    private final static String EXCHANGE_TYPE = "direct";
    private final static String BindingKey = "Consumer A";

    public static void main(String[] args)throws IOException,TimeoutException{
        // 创建连接和频道
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        // 声明交换机
        channel.exchangeDeclare(EXCHANGE_NAME, EXCHANGE_TYPE);

            String message = "A";
            channel.basicPublish(EXCHANGE_NAME,BindingKey,null,message.getBytes());
            System.out.println("发送的消息是："+message);

        // 关闭频道和连接
        channel.close();
        connection.close();
    }


}
