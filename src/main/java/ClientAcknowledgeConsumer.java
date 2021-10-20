import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQMessageConsumer;
import org.apache.activemq.ActiveMQSession;
import org.apache.activemq.command.ActiveMQTextMessage;

import javax.jms.*;
import java.time.Duration;
import java.time.Instant;

public class ClientAcknowledgeConsumer {
    public static void main(String[] args) throws JMSException {
        ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
        Connection connection = cf.createConnection();
        connection.start();
        Session session =
                connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
        Destination destination = session.createQueue("message.queue");
        ActiveMQMessageConsumer consumer = (ActiveMQMessageConsumer) session.createConsumer(destination);

        Instant start = Instant.now();

        for (int i = 0; i <= 100_000; i++) {
            TextMessage message = (TextMessage) consumer.receive();
            if (i != 0 && i% 1000 == 0) {
                message.acknowledge();
            }
        }

        Instant finish = Instant.now();
        long elapsed = Duration.between(start, finish).toMillis();
        System.out.println("Прошло времени, мс: " + elapsed);

        session.close();
        connection.close();
    }
}
