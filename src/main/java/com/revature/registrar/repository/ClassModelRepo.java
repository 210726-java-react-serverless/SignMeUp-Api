package com.revature.registrar.repository;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;
import com.revature.registrar.exceptions.DataSourceException;
import com.revature.registrar.models.ClassModel;

import com.revature.registrar.models.Student;
import com.revature.registrar.util.MongoClientFactory;
import com.revature.registrar.web.dtos.ClassModelDTO;
import com.revature.registrar.web.servlets.AuthServlet;
import org.apache.logging.log4j.LogManager;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.print.Doc;
import java.util.*;

/**
 * Provides methods to communicate and interact with the MongoDB classes collection
 */
public class ClassModelRepo implements CrudRepository<ClassModel>{
    private final Logger logger = LoggerFactory.getLogger(ClassModelRepo.class);


    /**
     * Searches the Database and returns a ClassModel with a matching ID
     * @param id
     * @return
     */
    @Override
    public ClassModel findById(String id) {
        try {
            MongoClient mongoClient = MongoClientFactory.getInstance().getConnection();

            MongoDatabase project1db = mongoClient.getDatabase("project0");
            MongoCollection<Document> classesCollection = project1db.getCollection("classes");
            Document queryDoc = new Document("id", id);

            Document authClassDoc = classesCollection.find(queryDoc).first();

            if (authClassDoc == null) {
                return null;
            } else {

                ObjectMapper mapper = new ObjectMapper();
                ClassModel auth = mapper.readValue(authClassDoc.toJson(), ClassModel.class);

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
        System.out.println("in Save");

        Document newUserDoc = new Document("name", newResource.getName())
                .append("capacity", newResource.getCapacity())
                .append("description", newResource.getDescription())
                .append("openWindow", newResource.getOpenWindow())
                .append("closeWindow", newResource.getCloseWindow())
                .append("id", newResource.getId())
                .append("students", newResource.getStudentsAsDoc())
                .append("faculty", newResource.getFacultyAsDoc());

        System.out.println("After doc build");

        try {
            MongoClient mongoClient = MongoClientFactory.getInstance().getConnection();

            MongoDatabase project1db = mongoClient.getDatabase("project0");
            MongoCollection<Document> usersCollection = project1db.getCollection("classes");

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
    public List<ClassModelDTO> findOpenClasses() {
        try {
            MongoClient mongoClient = MongoClientFactory.getInstance().getConnection();

            MongoDatabase project1db = mongoClient.getDatabase("project0");
            MongoCollection<Document> usersCollection = project1db.getCollection("classes");

            long current = Calendar.getInstance().getTimeInMillis();

            Document query = new Document()
                    .append("openWindow", new Document("$lt", current))
                    .append("closeWindow", new Document("$gt", current));

            List<ClassModelDTO> result = new ArrayList<>();
            for (Document doc: usersCollection.find(query)) {
                //Date d = new Date((long)doc.get("openWindow"));
                //Calendar openDate = new Calendar.Builder()
                //        .setInstant(d)
                //        .build();
                //d = new Date((long)doc.get("closeWindow"));
                //Calendar closeDate = new Calendar.Builder()
                //        .setInstant(d)
                //        .build();

                ObjectMapper mapper = new ObjectMapper();
                ClassModel classModel = mapper.readValue(doc.toJson(), ClassModel.class);
                //classModel.setOpenWindow(openDate);
                //classModel.setCloseWindow(closeDate);
                result.add(new ClassModelDTO(classModel));
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

            MongoDatabase project1db = mongoClient.getDatabase("project0");
            MongoCollection<Document> usersCollection = project1db.getCollection("classes");
            Bson updates = Updates.combine(
                    Updates.set("capacity", updatedResource.getCapacity()),
                    Updates.set("description", updatedResource.getDescription()),
                    Updates.set("openWindow", updatedResource.getOpenWindow()),
                    Updates.set("closeWindow", updatedResource.getCloseWindow()),
                    Updates.set("students", updatedResource.getStudentsAsDoc()),
                    Updates.set("faculty", updatedResource.getFacultyAsDoc()));

            Document query = new Document().append("id",  updatedResource.getId());
            usersCollection.updateOne(query, updates);
            return true;

        } catch (Exception e) {
            logger.error(e.getStackTrace().toString());
            throw new DataSourceException("An unexpected exception occurred.", e);
        }
    }


    /**
     * Deletes the classModel with the corresponding id from the database
     * @param id
     * @return
     */
    @Override
    public boolean deleteById(String id) {
        try {
            MongoClient mongoClient = MongoClientFactory.getInstance().getConnection();

            MongoDatabase project1db = mongoClient.getDatabase("project0");
            MongoCollection<Document> usersCollection = project1db.getCollection("classes");
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
