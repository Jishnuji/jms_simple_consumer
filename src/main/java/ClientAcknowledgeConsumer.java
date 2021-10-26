import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQMessageConsumer;
import org.apache.activemq.ActiveMQSession;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQTextMessage;

import javax.jms.*;
import java.time.Duration;
import java.time.Instant;

public class ClientAcknowledgeConsumer {
    public static void main(String[] args) throws Exception {
        BrokerService broker = new BrokerService();
        broker.setBrokerName("service");
        broker.setPersistent(false);
        broker.addConnector("tcp://localhost:61616");
        broker.start();

        ActiveMQConnectionFactory cf =
                new ActiveMQConnectionFactory("vm://service");
        Connection connection = cf.createConnection();
        connection.start();
        Session session =
                connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
        Destination destination = session.createQueue("message.queue");
        ActiveMQMessageConsumer consumer = (ActiveMQMessageConsumer) session.createConsumer(destination);
        MessageProducer producer = session.createProducer(destination);
        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

        // Producer
        Instant start = Instant.now();
        for (int i = 0; i <= 100_000; i++) {
            Message message = session.createTextMessage("message " + i);
            producer.send(message);
        }
        Instant finish = Instant.now();
        long elapsed = Duration.between(start, finish).toMillis();
        System.out.println("Прошло времени, мс: " + elapsed);

        //        Consumer
        Instant start2 = Instant.now();
        for (int i = 0; i <= 100_000; i++) {
            TextMessage message = (TextMessage) consumer.receive();
            if (i != 0 && i % 1000 == 0) {
                message.acknowledge();
            }
        }
        Instant finish2 = Instant.now();
        long elapsed2 = Duration.between(start2, finish2).toMillis();
        System.out.println("Прошло времени, мс: " + elapsed2);

        session.close();
        connection.close();
        broker.stop();
    }
}
