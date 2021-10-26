import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

public class ConsumerMessageListener implements MessageListener{
    private String consumerName;
    int count = 0;

    public ConsumerMessageListener(String consumerName) {
        this.consumerName = consumerName;
    }

    public void onMessage(Message message) {

        TextMessage textMessage = (TextMessage) message;
        count++;
        try {
            if (count == 100_000) {
                System.out.println(textMessage.getText());
            }
            } catch (JMSException jmsException) {
            jmsException.printStackTrace();
        }
    }
}
