package io.pivotal.spring.cloud.stream.processor.geocoding;

import com.fasterxml.jackson.databind.deser.Deserializers;
import com.mongodb.Mongo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Processor;;

import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.transformer.MessageTransformationException;
import org.springframework.messaging.Message;

import org.springframework.tuple.Tuple;
import org.springframework.tuple.TupleBuilder;

import java.util.Calendar;
import java.util.UUID;
import java.util.List;

import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.MongoOperations;

import io.pivotal.spring.cloud.stream.processor.geocoding.model.Block;

/**
 * Created by lei_xu on 7/28/16.
 */
@EnableBinding(Processor.class)
@EnableConfigurationProperties(MongoProperties.class)
public class ReverseGeocodingConfiguration {

    private static final String delims = "[,]";

    @Autowired
    private MongoProperties properties;

    @Autowired
    private MongoOperations mongoOperations;

    @Transformer(inputChannel = Processor.INPUT, outputChannel = Processor.OUTPUT)
    public Tuple transform(Message<?> message) {
        Object payloadObj = message.getPayload();
        String payload = null;

        if (payloadObj instanceof String) {
            try
            {
                payload = payloadObj.toString();
            }
            catch (Exception e)
            {
                throw new MessageTransformationException(message, e.getMessage(), e);
            }
        }

        if (payload == null) {
            throw new MessageTransformationException(message, "payload empty");
        }

        String[] tokens = payload.split(delims);

        System.out.println("payload:" + payload);

        double pickupLatitude = java.lang.Double.parseDouble(tokens[9]);
        double pickupLongitude = java.lang.Double.parseDouble(tokens[8]);
        double dropoffLatitude = java.lang.Double.parseDouble(tokens[7]);
        double dropoffLongitude = java.lang.Double.parseDouble(tokens[6]);
        String dropoffDatetime = tokens[3];
        String pickupDatetime = tokens[2];


        // get block name
        Query pickupQuery = Query.query(Criteria.where("geometry").intersects(new GeoJsonPoint(pickupLongitude, pickupLatitude)));
        Query dropoffQuery = Query.query(Criteria.where("geometry").intersects(new GeoJsonPoint(dropoffLongitude, dropoffLatitude)));

        List<Block> pickupBlocks =  mongoOperations.find(pickupQuery, Block.class, properties.getCollection());
        List<Block> dropoffBlocks =  mongoOperations.find(dropoffQuery, Block.class, properties.getCollection());

        if (pickupBlocks.size() == 0 || dropoffBlocks.size() == 0) {
            throw new MessageTransformationException(message, "coordinates out of scope");
        }

        String route = pickupBlocks.get(0).properties.getBlockCode() + "_" + dropoffBlocks.get(0).properties.getBlockCode();
        String pickupAddress = pickupBlocks.get(0).properties.getDistrict() + " " + pickupBlocks.get(0).properties.getBlock();
        pickupAddress = pickupAddress.trim();
        String dropoffAddress = dropoffBlocks.get(0).properties.getDistrict() + " " + dropoffBlocks.get(0).properties.getBlock();
        dropoffAddress = dropoffAddress.trim();

        Tuple tuple = null;

        tuple = TupleBuilder.tuple()
            .put("uuid", UUID.randomUUID())
            .put("route", route)
            .put("timestamp", Calendar.getInstance().getTimeInMillis())
            .put("pickupAddress", pickupAddress)
            .put("dropoffAddress", dropoffAddress)
            .put("pickupLatitude", pickupLatitude)
            .put("pickupLongitude", pickupLongitude)
            .put("dropoffLatitude", dropoffLatitude)
            .put("dropoffLongitude", dropoffLongitude)
            .put("pickupDatetime", pickupDatetime)
            .put("dropoffDatetime", dropoffDatetime)
            .build();

        System.out.println("tuple:" + tuple.toString());

        return tuple;

    }

}
