package WirelessTokens;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import javax.sound.midi.Soundbank;
import java.io.IOException;
import java.sql.SQLOutput;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class Unlocker {
    private static final String EXCHANGE_NAME = "finki";

    public static void main(String[] args) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("51.83.68.66");
        factory.setPort(5672);

        Scanner in = new Scanner(System.in);
        System.out.println("Vnesete go vaseto ime");
        String name = in.nextLine();
        System.out.println("1 za profesor, 2 za student");
        var userType = in.nextLine();
        System.out.println("1 za ucilnica, 2 za laboratorija, 3 za kancelarija");
        var roomType = in.nextLine();
        System.out.println("Vnesete broj na prostorija");
        var roomNumber = in.nextLine();

        userType = userType.equals("1") ? "profesor" : "student";
        if(roomType.equals("1"))
            roomType = "ucilnica";
        if(roomType.equals("2"))
            roomType = "laboratorija";
        if(roomType.equals("3"))
            roomType = "kancelarija";

        try(Connection connection = factory.newConnection()){
            Channel channel = connection.createChannel();
            channel.exchangeDeclare(EXCHANGE_NAME,"topic");
            String routingKey = "";

            if(userType.toLowerCase().startsWith("profesor")){
                routingKey = "profesor";
            }else{
                routingKey = "student";
                if(roomType.startsWith("kancelarija")){
                    routingKey+=".kancelarija";
                }
            }

            var message = name + "#" + userType + "#" + roomType + "#" +  roomNumber;

            channel.basicPublish(EXCHANGE_NAME,routingKey, null, message.getBytes("UTF-8"));
            System.out.println(" [x] Sent '" + routingKey + "':'" + message + "'");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
