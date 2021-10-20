import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.time.Duration;
import java.time.Instant;

public class TransactedConsumer {
    public static void main(String[] args) throws JMSException {
        TransactedConsumer.receive();

    }
    public static void receive() throws JMSException {
        ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
        Connection connection = cf.createConnection();
        connection.start();

        Session session =
                connection.createSession(true, Session.SESSION_TRANSACTED);

        Destination destination = session.createQueue("message.queue");
        MessageConsumer consumer = session.createConsumer(destination);

        Instant start = Instant.now();

        for (int i = 0; i <= 100_000; i++) {
            TextMessage message = (TextMessage) consumer.receive();

            if (i != 0 && i% 1000 == 0) {
                session.commit();
            }
        }
        session.commit();

        Instant finish = Instant.now();
        long elapsed = Duration.between(start, finish).toMillis();
        System.out.println("Прошло времени, мс: " + elapsed);

        session.close();
        connection.close();
    }
}
