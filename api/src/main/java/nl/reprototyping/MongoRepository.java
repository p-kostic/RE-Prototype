package nl.reprototyping;


import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import javax.ejb.Singleton;
import javax.ejb.Stateful;
import java.util.Collections;
import java.util.Date;

@Singleton
public class MongoRepository {
    private static final String        DATABASE_NAME   = "prototype";
    private static final String        COLLECTION_NAME = "collection";
    private static final String        HOST            = "mongodb";
    private static final int           PORT            = 27017;
    private              MongoClient   mongoClient;
    private              MongoDatabase database;

    public MongoRepository() {
        MongoCredential credential = MongoCredential.createCredential(
                "vincent", "admin", "GeR7gnqjQYw5a5s3".toCharArray());

        mongoClient = MongoClients.create(
                MongoClientSettings.builder()
                                   .applyToClusterSettings(
                                           builder -> builder.hosts(Collections.singletonList(new ServerAddress(HOST, PORT))))
                                   .credential(credential)
                                   .build());

        database = mongoClient.getDatabase(DATABASE_NAME);
    }

    public void saveRequest(String uuid, String domain, Date date, String stylesheet, String enabled) {
        MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);

        Document doc = new Document("uuid", uuid)
                .append("domain", domain)
                .append("date", date)
                .append("stylesheet", stylesheet)
                .append("enabled", enabled);

        collection.insertOne(doc);
    }
}
