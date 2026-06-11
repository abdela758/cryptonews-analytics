package org.ulpgc.dacd.business.api;

import com.google.gson.Gson;
import io.javalin.Javalin;
import org.ulpgc.dacd.business.datamart.Datamart;

public class RestApi {

    private final Datamart datamart;
    private final int port;
    private final double alertThreshold;
    private final Gson gson = new Gson();

    public RestApi(Datamart datamart, int port, double alertThreshold) {
        this.datamart = datamart;
        this.port = port;
        this.alertThreshold = alertThreshold;
    }

    public void start() {
        Javalin app = Javalin.create().start(port);

        app.get("/api/cryptos", ctx -> {
            ctx.json(datamart.getLatestCryptos().stream()
                    .map(obj -> gson.fromJson(obj, Object.class))
                    .toList());
        });

        app.get("/api/cryptos/{id}/history", ctx -> {
            String coinId = ctx.pathParam("id");
            ctx.json(datamart.getCryptoHistory(coinId).stream()
                    .map(obj -> gson.fromJson(obj, Object.class))
                    .toList());
        });

        app.get("/api/news", ctx -> {
            ctx.json(datamart.getLatestNews().stream()
                    .map(obj -> gson.fromJson(obj, Object.class))
                    .toList());
        });

        app.get("/api/analysis/{coinId}", ctx -> {
            String coinId = ctx.pathParam("coinId");
            ctx.json(datamart.getNewsAndPriceAt(coinId).stream()
                    .map(obj -> gson.fromJson(obj, Object.class))
                    .toList());
        });

        app.get("/api/alerts", ctx -> {
            ctx.json(datamart.getPriceAlerts(alertThreshold).stream()
                    .map(obj -> gson.fromJson(obj, Object.class))
                    .toList());
        });

        System.out.println("REST API running at http://localhost:" + port);
        System.out.println("Endpoints:");
        System.out.println("  GET /api/cryptos          - Latest crypto prices");
        System.out.println("  GET /api/cryptos/{id}/history - Price history");
        System.out.println("  GET /api/news             - Latest news");
        System.out.println("  GET /api/analysis/{coinId} - News + price correlation");
        System.out.println("  GET /api/alerts           - Price movement alerts with related news");
    }
}