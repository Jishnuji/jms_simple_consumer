import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQQueue;

import javax.jms.*;
import java.time.Duration;
import java.time.Instant;


public class NonTransactedConsumer {
    public static void main(String[] args) throws Exception {
        NonTransactedConsumer.receive();
    }
    public static void receive() throws Exception {
        BrokerService broker = new BrokerService();
        broker.setBrokerName("service");
        broker.setPersistent(false);
        broker.addConnector("tcp://localhost:61616");
        broker.start();

        ActiveMQConnectionFactory cf =
                new ActiveMQConnectionFactory("vm://service");
//        ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory("tcp://localhost:61616");
        Connection connection = cf.createConnection();
        connection.start();

        Session session =
                connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        Destination destination = session.createQueue("message.queue");
        MessageConsumer consumer = session.createConsumer(destination);

        Instant start = Instant.now();

        for (int i = 0; i < 100_000; i++) {
            TextMessage message = (TextMessage) consumer.receive();
//            System.out.println(message.getText());
        }

        Instant finish = Instant.now();
        long elapsed = Duration.between(start, finish).toMillis();
        System.out.println("Прошло времени, мс: " + elapsed);

        session.close();
        connection.close();
        broker.stop();
    }
}
