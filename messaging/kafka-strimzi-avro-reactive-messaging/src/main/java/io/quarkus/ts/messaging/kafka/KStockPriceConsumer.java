package io.quarkus.ts.messaging.kafka;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import io.smallrye.reactive.messaging.annotations.Broadcast;
import io.vertx.core.json.JsonObject;

@ApplicationScoped
public class KStockPriceConsumer {

    @Incoming("channel-stock-price")
    @Outgoing("price-stream")
    @Broadcast
    public String process(StockPrice next) {
        eventCompleted(next);
        return toJson(next);
    }

    // TODO Kafka bulk Not supported on Quarkus 2.2.4.Final
    //    @Incoming("channel-stock-price-batch")
    //    @Outgoing("price-stream-batch")
    //    @Broadcast
    //    public List<String> processBatch(List<StockPrice> next) {
    //        return next.stream()
    //                .map(KStockPriceConsumer::eventCompleted)
    //                .map(KStockPriceConsumer::toJson)
    //                .collect(Collectors.toList());
    //    }

    private static StockPrice eventCompleted(StockPrice price) {
        price.setStatus(status.COMPLETED);
        return price;
    }

    private static String toJson(StockPrice price) {
        return new JsonObject().put("id", price.getId()).put("price", price.getPrice()).encode();
    }
}
