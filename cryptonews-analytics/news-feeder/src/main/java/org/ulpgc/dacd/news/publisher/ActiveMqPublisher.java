package org.ulpgc.dacd.news.publisher;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class ActiveMqPublisher implements Publisher {

    private final Connection connection;
    private final Session session;
    private final MessageProducer producer;

    public ActiveMqPublisher(String brokerUrl, String topicName) {
        try {
            ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(brokerUrl);
            connection = factory.createConnection();
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic topic = session.createTopic(topicName);
            producer = session.createProducer(topic);
        } catch (JMSException e) {
            throw new RuntimeException("Failed to connect to ActiveMQ", e);
        }
    }

    @Override
    public void publish(String json) {
        try {
            TextMessage message = session.createTextMessage(json);
            producer.send(message);
        } catch (JMSException e) {
            System.err.println("Failed to publish message: " + e.getMessage());
        }
    }

    @Override
    public void close() {
        try {
            producer.close();
            session.close();
            connection.close();
        } catch (JMSException e) {
            System.err.println("Failed to close ActiveMQ connection: " + e.getMessage());
        }
    }
}
