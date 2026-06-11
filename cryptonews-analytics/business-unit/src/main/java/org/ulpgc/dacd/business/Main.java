package org.ulpgc.dacd.business;

import org.ulpgc.dacd.business.api.RestApi;
import org.ulpgc.dacd.business.controller.BusinessController;
import org.ulpgc.dacd.business.datamart.SqliteDatamart;
import org.ulpgc.dacd.business.eventstore.EventStoreReader;
import org.ulpgc.dacd.business.subscriber.ActiveMqSubscriber;

public class Main {

    public static void main(String[] args) {
        Config config = new Config();

        SqliteDatamart datamart = new SqliteDatamart(config.get("datamart.path"));

        BusinessController controller = new BusinessController(
                new EventStoreReader(config.get("eventstore.path"), datamart),
                new ActiveMqSubscriber(
                        config.get("broker.url"),
                        config.get("broker.client.id"),
                        config.getArray("broker.topics"),
                        datamart
                ),
                new RestApi(
                        datamart,
                        config.getInt("api.port"),
                        config.getDouble("alert.threshold.percent")
                )
        );
        controller.start();
    }
}
