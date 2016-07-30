package io.pivotal.spring.cloud.stream.processor.geocoding;

import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

/**
 * Created by lei_xu on 7/29/16.
 */
@Configuration
@EnableConfigurationProperties(MongoProperties.class)
public class MongoConfiguration extends AbstractMongoConfiguration {

    @Autowired
    private MongoProperties properties;

    @Override
    protected String getDatabaseName() {
        return properties.getDatabase();
    }

    @Override
    public Mongo mongo() throws Exception {
        MongoClientURI uri = new MongoClientURI("mongodb://" + properties.getCredential()  + properties.getHostName() + ":" + properties.getPort() + "/" + properties.getDatabase());

        return new MongoClient(uri);
//        return new MongoClient(properties.getHostName(), properties.getPort());
    }

}