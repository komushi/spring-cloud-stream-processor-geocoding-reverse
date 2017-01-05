package io.pivotal.spring.cloud.stream.processor.geocoding;

import java.util.Arrays;

import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;



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
        // Set credentials      

        // MongoClientURI uri = new MongoClientURI("mongodb://" + properties.getCredential()  + properties.getHostName() + ":" + properties.getPort() + "/" + properties.getDatabase());
        // return new MongoClient(uri);

        // MongoCredential credential = MongoCredential.createScramSha1Credential(properties.getUser(), properties.getDatabase(), properties.getPassword().toCharArray());
        MongoCredential credential = MongoCredential.createCredential(properties.getUser(), properties.getDatabase(), properties.getPassword().toCharArray());
        ServerAddress serverAddress = new ServerAddress(properties.getHostName(), properties.getPort());

        // Mongo Client
        return new MongoClient(serverAddress, Arrays.asList(credential)); 
    }

}