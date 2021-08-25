package com.revature.registrar.util;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.revature.registrar.exceptions.DataSourceException;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

/**
 * Singleton Factory which creates and manages the connection to our MongoDB
 */
public class MongoClientFactory {

    private final MongoClient mongoClient;
    private static final MongoClientFactory mongoClientFactory = new MongoClientFactory();


    private MongoClientFactory() {

        Properties appProperties = new Properties();

        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            appProperties.load(loader.getResourceAsStream("application.properties"));

            System.out.println(appProperties.getProperty("ipAddress"));

            String ipAddress = appProperties.getProperty("ipAddress");
            int port = Integer.parseInt(appProperties.getProperty("port"));
            String dbName = appProperties.getProperty("dbName"); //System.getProperty("dbName");
            String username = appProperties.getProperty("username");
            char[] password = appProperties.getProperty("password").toCharArray();

            System.out.println("AAAAAAAAAAAAAAAAAAH: " + dbName);

            List<ServerAddress> hosts = Collections.singletonList(new ServerAddress(ipAddress, port));
            MongoCredential credentials = MongoCredential.createScramSha1Credential(username, dbName, password);

            MongoClientSettings settings = MongoClientSettings.builder()
                                                              .applyToClusterSettings(builder -> builder.hosts(hosts))
                                                              .credential(credentials)
                                                              .build();

            this.mongoClient = MongoClients.create(settings);

        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace(); // TODO log this to a file
            throw new DataSourceException("Unable to load database properties file.", fnfe);
        } catch(Exception e){
            e.printStackTrace(); // TODO log this to a file
            throw new DataSourceException("An unexpected exception occurred.", e);
        }

    }

    public void cleanUp(){
        mongoClient.close();
    }

    public static MongoClientFactory getInstance(){
        return mongoClientFactory;
    }

    public MongoClient getConnection(){
        return mongoClient;
    }

}
