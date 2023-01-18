package WirelessTokens;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.StringTokenizer;
import java.util.concurrent.TimeoutException;

public class Door {
    private static final String EXCHANGE_NAME = "finki";
    public static void main(String[] args) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("51.83.68.66");
        factory.setPort(5672);
        try {
            //Address addr = new Address("51.83.68.66",5672);
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.exchangeDeclare(EXCHANGE_NAME, "topic");
            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName,EXCHANGE_NAME,"profesor");
            channel.queueBind(queueName,EXCHANGE_NAME,"student");
            channel.queueBind(queueName,EXCHANGE_NAME,"student.kancelarija");

            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                //  name + "#" + userType + "#" + roomType + "#" +  roomNumber;
                StringTokenizer st = new StringTokenizer(message,"#");
                var name = st.nextToken();
                var userType = st.nextToken();
                var roomType = st.nextToken();
                var roomNumber = st.nextToken();



                System.out.println(" [x] Log - room unlocked by");
                System.out.println("\tName: " + name);
                System.out.println("\tUser Type: " + userType);
                System.out.println("\tRoom Type: " + roomType);
                System.out.println("\tRoom Number: " + roomNumber);


               //delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
            };
            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });

        } catch (IOException e) {
            System.out.println("Problem so konekcijata");
        } catch (TimeoutException e) {
            e.printStackTrace();
        }


    }
}
