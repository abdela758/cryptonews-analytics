package org.ulpgc.dacd.eventstore.subscriber;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.ulpgc.dacd.eventstore.store.EventStore;

import javax.jms.*;

public class ActiveMqSubscriber implements Subscriber {

    private final String brokerUrl;
    private final String[] topics;
    private final String clientId;
    private final EventStore store;

    public ActiveMqSubscriber(String brokerUrl, String[] topics, String clientId, EventStore store) {
        this.brokerUrl = brokerUrl;
        this.topics = topics;
        this.clientId = clientId;
        this.store = store;
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
                Topic topic = session.createTopic(topicName);
                MessageConsumer consumer = session.createDurableSubscriber(topic, clientId + "-" + topicName);

                consumer.setMessageListener(message -> {
                    try {
                        if (message instanceof TextMessage textMessage) {
                            String json = textMessage.getText();
                            store.save(topicName, json);
                        }
                    } catch (JMSException e) {
                        System.err.println("Error processing message: " + e.getMessage());
                    }
                });

                System.out.println("Subscribed to topic: " + topicName);
            }

        } catch (JMSException e) {
            throw new RuntimeException("Failed to start subscriber", e);
        }
    }
}
