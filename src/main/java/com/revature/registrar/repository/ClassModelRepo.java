package com.revature.registrar.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.TypeResolutionContext;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;
import com.revature.registrar.exceptions.DataSourceException;
import com.revature.registrar.models.ClassModel;
import com.revature.registrar.models.Faculty;
import com.revature.registrar.models.Student;
import com.revature.registrar.util.MongoClientFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.print.Doc;
import java.util.*;

/**
 * Provides methods to communicate and interact with the MongoDB classes collection
 */
public class ClassModelRepo implements CrudRepository<ClassModel>{
    private final Logger logger = LogManager.getLogger(ClassModelRepo.class);


    /**
     * Searches the Database and returns a ClassModel with a matching ID
     * @param id
     * @return
     */
    @Override
    public ClassModel findById(int id) {
        try {
            MongoClient mongoClient = MongoClientFactory.getInstance().getConnection();

            MongoDatabase bookstoreDb = mongoClient.getDatabase("project0");
            MongoCollection<Document> usersCollection = bookstoreDb.getCollection("classes");
            Document queryDoc = new Document("id", id);

            Document authClassDoc = usersCollection.find(queryDoc).first();

            if (authClassDoc == null) {
                return null;
            } else {
                //Convert from millis to Calendar
                Date d = new Date((long)authClassDoc.get("openWindow"));
                Calendar openDate = new Calendar.Builder()
                        .setInstant(d)
                        .build();
                d = new Date((long)authClassDoc.get("closeWindow"));
                Calendar closeDate = new Calendar.Builder()
                        .setInstant(d)
                        .build();
                authClassDoc.remove("openWindow");
                authClassDoc.remove("closeWindow");
                //authClassDoc.append("openWindow", openDate);
                //authClassDoc.append("closeWindow", closeDate);
                ObjectMapper mapper = new ObjectMapper();
                ClassModel auth = mapper.readValue(authClassDoc.toJson(), ClassModel.class);
                auth.setOpenWindow(openDate);
                auth.setCloseWindow(closeDate);
                return auth;
            }

        } catch (Exception e) {
            logger.error(e.getStackTrace() + "\n");
            throw new DataSourceException("An unexpected exception occurred.", e);
        }
    }

    /**
     * Stores a ClassModel, newResource, in the database
     * @param newResource
     * @return
     */
    @Override
    public ClassModel save(ClassModel newResource) {
        Document newUserDoc = new Document("name", newResource.getName())
                .append("capacity", newResource.getCapacity())
                .append("description", newResource.getDescription())
                .append("openWindow", newResource.getOpenWindow().getTimeInMillis())
                .append("closeWindow", newResource.getCloseWindow().getTimeInMillis())
                .append("id", newResource.getId())
                .append("students", newResource.getStudentsAsDoc())
                .append("faculty", newResource.getFacultyAsDoc());


        try {
            MongoClient mongoClient = MongoClientFactory.getInstance().getConnection();

            MongoDatabase bookstoreDb = mongoClient.getDatabase("project0");
            MongoCollection<Document> usersCollection = bookstoreDb.getCollection("classes");

            usersCollection.insertOne(newUserDoc);
            logger.info("Created " + newResource + "\n");
            return newResource;

        } catch (Exception e) {
            logger.error(e.getStackTrace() + "\n");
            throw new DataSourceException("An unexpected exception occurred.", e);
        }
    }

    /**
     * Searches the database and returns a list of ClassModels where the current time falls between
     * the openDate and closeDate
     * @return
     */
    public List<ClassModel> findOpenClasses() {
        try {
            MongoClient mongoClient = MongoClientFactory.getInstance().getConnection();

            MongoDatabase bookstoreDb = mongoClient.getDatabase("project0");
            MongoCollection<Document> usersCollection = bookstoreDb.getCollection("classes");

            long current = Calendar.getInstance().getTimeInMillis();

            Document query = new Document()
                    .append("openWindow", new Document("$lt", current))
                    .append("closeWindow", new Document("$gt", current));

            List<ClassModel> result = new ArrayList<>();
            for (Document doc: usersCollection.find(query)) {
                Date d = new Date((long)doc.get("openWindow"));
                Calendar openDate = new Calendar.Builder()
                        .setInstant(d)
                        .build();
                d = new Date((long)doc.get("closeWindow"));
                Calendar closeDate = new Calendar.Builder()
                        .setInstant(d)
                        .build();

                ObjectMapper mapper = new ObjectMapper();
                ClassModel classModel = mapper.readValue(doc.toJson(), ClassModel.class);
                classModel.setOpenWindow(openDate);
                classModel.setCloseWindow(closeDate);
                result.add(classModel);
            }

            if (result.size() == 0) {
                return null;
            } else {
                return result;
            }

        } catch (Exception e) {
            logger.error(e.getStackTrace() + "\n");
            throw new DataSourceException("An unexpected exception occurred.", e);
        }
    }

    /**
     * Updates the fields of a database element with new data
     * @param updatedResource
     * @return
     */
    @Override
    public boolean update(ClassModel updatedResource) {
        try {
            MongoClient mongoClient = MongoClientFactory.getInstance().getConnection();

            MongoDatabase bookstoreDb = mongoClient.getDatabase("project0");
            MongoCollection<Document> usersCollection = bookstoreDb.getCollection("classes");

            Bson updates = Updates.combine(
                    Updates.set("capacity", updatedResource.getCapacity()),
                    Updates.set("description", updatedResource.getDescription()),
                    Updates.set("openWindow", updatedResource.getOpenWindow().getTimeInMillis()),
                    Updates.set("closeWindow", updatedResource.getCloseWindow().getTimeInMillis()),
                    Updates.set("students", updatedResource.getStudentsAsDoc()),
                    Updates.set("faculty", updatedResource.getFacultyAsDoc()));

            Document query = new Document().append("id",  updatedResource.getId());
            usersCollection.updateOne(query, updates);
            return true;

        } catch (Exception e) {
            logger.error(e.getStackTrace());
            throw new DataSourceException("An unexpected exception occurred.", e);
        }
    }


    /**
     * Deletes the classModel with the corresponding id from the database
     * @param id
     * @return
     */
    @Override
    public boolean deleteById(int id) {
        try {
            MongoClient mongoClient = MongoClientFactory.getInstance().getConnection();

            MongoDatabase bookstoreDb = mongoClient.getDatabase("project0");
            MongoCollection<Document> usersCollection = bookstoreDb.getCollection("classes");
            Document queryDoc = new Document("id", id);
            usersCollection.deleteOne(queryDoc);
            return true;

        } catch (Exception e) {
            logger.error(e.getStackTrace() + "\n");
            throw new DataSourceException("An unexpected exception occurred.", e);
        }

    }

    @Override
    public List<ClassModel> findAll(){
        return null; //TODO: fix this method
    }
}
