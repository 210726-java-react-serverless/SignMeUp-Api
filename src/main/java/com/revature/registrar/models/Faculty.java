package com.revature.registrar.models;

import java.util.HashSet;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.bson.Document;

/**
 * Extension of the User class
 * Adds the classes field and provides Faculty specific helper methods
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Faculty extends User{
    Set<ClassModel> classes = new HashSet<>(); //contains ids of taught classes OR should it store actual objects?


    public Faculty() {
        super();
    }

    public Faculty(String firstName, String lastName, String email, String username, String password) {
        super(firstName, lastName, email, username, password, true);
    }

    public boolean isInClasses(ClassModel classModel) {
        for(ClassModel c : classes) {
            if(c.getId() == classModel.getId()) {
                return true;
            }
        }
        return false;
    }

    public Set<ClassModel> getClasses() {
        return classes;
    }

    public Set<Document> getClassesAsDoc() {
        Set<Document> docs = new HashSet<>();
        for(ClassModel classModel : classes) {
            Document doc = classModel.getAsDoc();
            docs.add(doc);
        }
        return docs;
    }

    public void addClass(ClassModel c) {
        classes.add(c);
    }

    public void removeClass(ClassModel classModel) {
        for(ClassModel c : classes) {
            if(c.getId() == classModel.getId()) {
                classes.remove(c);
                return;
            }
        }
    }

}
