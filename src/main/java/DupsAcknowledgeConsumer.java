import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.time.Duration;
import java.time.Instant;

public class DupsAcknowledgeConsumer {
    public static void main(String[] args) throws JMSException {
        DupsAcknowledgeConsumer.receive();
    }
    public static void receive() throws JMSException {
        ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
        Connection connection = cf.createConnection();
        connection.start();

        Session session =
                connection.createSession(false, Session.DUPS_OK_ACKNOWLEDGE);
    //        Пытался увеличивать для увеличения производительности consumer.prefetchSize=3000, но ничего не помогало
        Destination destination = session.createQueue("message.queue");
        MessageConsumer consumer = session.createConsumer(destination);

        Instant start = Instant.now();

        for (int i = 0; i < 100_000; i++) {
            TextMessage message = (TextMessage) consumer.receive();
        }

        Instant finish = Instant.now();
        long elapsed = Duration.between(start, finish).toMillis();
        System.out.println("Прошло времени, мс: " + elapsed);

        consumer.close();
        session.close();
        connection.close();
    }
}
