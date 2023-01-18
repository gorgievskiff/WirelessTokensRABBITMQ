package WirelessTokens;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.StringTokenizer;
import java.util.concurrent.TimeoutException;

public class StudentCsvService {
    private static final String EXCHANGE_NAME = "finki";
    private static Path fileName = Path.of("src/WirelessTokens/Student_log.csv");

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("51.83.68.66");
        factory.setPort(5672);

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME,"topic");
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName,EXCHANGE_NAME,"student");
        channel.queueBind(queueName,EXCHANGE_NAME,"student.kancelarija");
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");


        DeliverCallback deliverCallback = (consumerTag, delivery ) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            StringTokenizer st = new StringTokenizer(message,"#");
            var name = st.nextToken();
            var userType = st.nextToken();
            var roomType = st.nextToken();
            var roomNumber = st.nextToken();
            var time = LocalDateTime.now().format(DateTimeFormatter
                    .ofPattern("dd/MM/yyyy HH:mm"));


            var csvString = "\n" + time + "," + roomType + "-" + roomNumber + "," + name;

            Files.writeString(fileName,csvString, StandardOpenOption.APPEND);
        };

        channel.basicConsume(queueName,true,deliverCallback,consumerTag -> {});
    }
}
