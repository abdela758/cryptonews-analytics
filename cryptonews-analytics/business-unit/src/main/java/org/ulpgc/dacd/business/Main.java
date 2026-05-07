package org.ulpgc.dacd.business;

import org.ulpgc.dacd.business.api.RestApi;
import org.ulpgc.dacd.business.controller.BusinessController;
import org.ulpgc.dacd.business.datamart.SqliteDatamart;
import org.ulpgc.dacd.business.eventstore.EventStoreReader;
import org.ulpgc.dacd.business.subscriber.ActiveMqSubscriber;

public class Main {

    public static void main(String[] args) {
        SqliteDatamart datamart = new SqliteDatamart("datamart.db");

        BusinessController controller = new BusinessController(
                new EventStoreReader("eventstore", datamart),
                new ActiveMqSubscriber("tcp://localhost:61616", "business-unit", datamart),
                new RestApi(datamart)
        );
        controller.start();
    }
}
