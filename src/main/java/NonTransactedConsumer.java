import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;

import javax.jms.*;
import java.time.Duration;
import java.time.Instant;


public class NonTransactedConsumer {
    public static void main(String[] args) throws JMSException {
        NonTransactedConsumer.receive();
    }
    public static void receive() throws JMSException {
        ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
        Connection connection = cf.createConnection();
        connection.start();

        Session session =
                connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        Destination destination = session.createQueue("message.queue");
        MessageConsumer consumer = session.createConsumer(destination);

        Instant start = Instant.now();

        for (int i = 0; i < 100_000; i++) {
            TextMessage message = (TextMessage) consumer.receive();
        }

        Instant finish = Instant.now();
        long elapsed = Duration.between(start, finish).toMillis();
        System.out.println("Прошло времени, мс: " + elapsed);

        session.close();
        connection.close();
    }
}
