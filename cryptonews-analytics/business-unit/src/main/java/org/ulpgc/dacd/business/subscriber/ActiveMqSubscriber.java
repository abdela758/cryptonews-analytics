package org.ulpgc.dacd.business.subscriber;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.ulpgc.dacd.business.datamart.Datamart;

import javax.jms.*;

public class ActiveMqSubscriber implements Subscriber {

    private final String brokerUrl;
    private final String clientId;
    private final String[] topics;
    private final Datamart datamart;

    public ActiveMqSubscriber(String brokerUrl, String clientId, String[] topics, Datamart datamart) {
        this.brokerUrl = brokerUrl;
        this.clientId = clientId;
        this.topics = topics;
        this.datamart = datamart;
    }

    @Override
    public void start() {
        try {
            ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(brokerUrl);
            Connection connection = factory.createConnection();
            connection.setClientID(clientId);
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            for (String topicName : topics) {
                subscribeTo(session, topicName);
            }

        } catch (JMSException e) {
            throw new RuntimeException("Failed to start business subscriber", e);
        }
    }

    private void subscribeTo(Session session, String topicName) throws JMSException {
        Topic topic = session.createTopic(topicName);
        MessageConsumer consumer = session.createDurableSubscriber(topic, clientId + "-" + topicName);

        consumer.setMessageListener(message -> {
            try {
                if (message instanceof TextMessage textMessage) {
                    String json = textMessage.getText();
                    JsonObject event = JsonParser.parseString(json).getAsJsonObject();

                    if (topicName.equals("CryptoPrice")) {
                        datamart.upsertCrypto(event);
                    } else {
                        datamart.insertNews(event);
                    }
                    System.out.println("Business unit processed event from: " + topicName);
                }
            } catch (JMSException e) {
                System.err.println("Error processing message: " + e.getMessage());
            }
        });

        System.out.println("Business unit subscribed to: " + topicName);
    }
}