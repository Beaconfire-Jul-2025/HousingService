package org.beaconfire.housing.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.beaconfire.housing.config.RabbitMQConfig.*;

@Component
public class HouseAssignProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendAssignmentMessage(String email, String userId, String houseId) {
        String subject = "House Assignment Notification";
        String body = "Your housing has been assigned to house " + houseId + ".";
        String message = "{ \"to\": "+ email + " ,\"subject\": \"" + subject + "\", \"body\": \"" + body + "\" }";
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, message);
    }
}