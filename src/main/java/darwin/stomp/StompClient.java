package darwin.stomp;

import javax.jms.JMSException;
import javax.jms.Session;
import org.fusesource.stomp.jms.StompJmsConnectionFactory;

public class StompClient implements Runnable {

    public static void main(String[] args) throws Exception {
        new StompClient().run();
    }
    
    // Get the *** values from http://opendata.nationalrail.co.uk/feeds

    @Override
    public void run() {
        var brokerUri = "tcp://***HOST***:61613?connection.watchTopicAdvisories=false";
        var TOPIC = "darwin.pushport-v16";

        var connectionFactory = new StompJmsConnectionFactory();
        connectionFactory.setBrokerURI(brokerUri);

        try {
            var connection = connectionFactory.createConnection("***USERNAME***", "***PASSWORD***");
            connection.start();

            var session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            var topic = session.createTopic(TOPIC);
            var consumer = session.createConsumer(topic);

            System.out.println("Connected to STOMP " + brokerUri);

            consumer.setMessageListener(new MessageHandler());

            while (!Thread.interrupted()) {}

            try {
                if (consumer != null) {
                    consumer.close();
                }

                if (session != null) {
                    session.close();
                }

                if (connection != null) {
                    connection.close();
                    connection = null;
                }
            } catch (JMSException ex) {
                System.out.println("Got exception on shutdown");
                ex.printStackTrace();
            }

            System.out.println("Thread was interrupted!");


        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}