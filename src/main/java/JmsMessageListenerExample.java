import org.apache.activemq.ActiveMQConnectionFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.Instant;

import javax.jms.*;

public class JmsMessageListenerExample {
    public static void main(String[] args) throws URISyntaxException, Exception {

        Connection connection = null;
        try {
            // Producer
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
            connection = connectionFactory.createConnection();
            connection.start();
            Session session = connection.createSession(false,
                    Session.AUTO_ACKNOWLEDGE);
            Queue queue = session.createQueue("customerQueue");
            MessageProducer producer = session.createProducer(queue);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            Instant start = Instant.now();
            for (int i =0; i < 100_000; i++) {
                Message message = session.createTextMessage("message " + i);
                producer.send(message);
            }
            Instant finish = Instant.now();
            long elapsed = Duration.between(start, finish).toMillis();
            System.out.println("Прошло времени (1), мс: " + elapsed);

            // Consumer
            MessageConsumer consumer = session.createConsumer(queue);
            consumer.setMessageListener(new ConsumerMessageListener("Consumer"));
            //            Примерно за такое время вычитывает все сообщения из очереди
            Thread.sleep(3800);
            session.close();

        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }
}
